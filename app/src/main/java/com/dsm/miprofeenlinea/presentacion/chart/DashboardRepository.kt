package com.dsm.miprofeenlinea.presentacion.chart

import com.dsm.miprofeenlinea.model.Tarea
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class DashboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val localDataSource: DashboardLocalDataSource
) {

    suspend fun loadDashboard(
        userId: String,
        role: DashboardRole,
        period: DashboardPeriod
    ): DashboardData = withContext(Dispatchers.IO) {
        val currentRange = period.currentRange()
        val previousRange = period.previousRange()

        try {
            val tareas = firestore.collection("tareas")
                .whereEqualTo(role.firestoreField, userId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toTarea() }

            localDataSource.replaceForUser(role, userId, tareas)
            buildDashboard(tareas, role, period, fromLocalCache = false)
        } catch (exception: Exception) {
            val cached = localDataSource.readForUser(role, userId)
            if (cached.isNotEmpty()) {
                buildDashboard(cached, role, period, fromLocalCache = true)
            } else {
                throw exception
            }
        }
    }

    private fun buildDashboard(
        tareas: List<Tarea>,
        role: DashboardRole,
        period: DashboardPeriod,
        fromLocalCache: Boolean
    ): DashboardData {
        val currentRange = period.currentRange()
        val previousRange = period.previousRange()

        val current = tareas.filter { it.isInRange(currentRange, role) }
        val previous = tareas.filter { it.isInRange(previousRange, role) }
        val completed = current.filter { it.estado == "finalizado" && it.completedTimestamp() != null }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val finalizadasPorDia = completed
            .mapNotNull { tarea -> tarea.completedTimestamp()?.let { formatter.format(Date(it)) } }
            .groupingBy { it }
            .eachCount()
            .toSortedMap()

        val estadoCount = current
            .map { it.estado.ifBlank { "pendiente" } }
            .groupingBy { it }
            .eachCount()

        val tarifaPorDia = completed
            .mapNotNull { tarea ->
                val completedAt = tarea.completedTimestamp() ?: return@mapNotNull null
                val tarifa = tarea.tarifa ?: return@mapNotNull null
                formatter.format(Date(completedAt)) to tarifa
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, tarifas) -> tarifas.average() }
            .toSortedMap()

        return DashboardData(
            role = role,
            period = period,
            kpis = buildKpis(current, previous, role),
            finalizadasPorDia = finalizadasPorDia,
            estadoCount = estadoCount,
            tarifaPorDia = tarifaPorDia,
            lastUpdatedAt = System.currentTimeMillis(),
            fromLocalCache = fromLocalCache
        )
    }

    private fun buildKpis(current: List<Tarea>, previous: List<Tarea>, role: DashboardRole): List<DashboardKpi> {
        val currentFinished = current.count { it.estado == "finalizado" }
        val previousFinished = previous.count { it.estado == "finalizado" }
        val pending = current.count { it.estado == "pendiente" || it.estado == "tarifa_propuesta" }
        val amount = current
            .filter { it.estado == "finalizado" || it.estado == "aceptado" }
            .sumOf { it.tarifa ?: 0.0 }
        val previousAmount = previous
            .filter { it.estado == "finalizado" || it.estado == "aceptado" }
            .sumOf { it.tarifa ?: 0.0 }
        val average = current.mapNotNull { it.tarifa }.averageOrZero()

        return if (role == DashboardRole.ALUMNO) {
            listOf(
                DashboardKpi("Consultas realizadas", current.size.toString(), variation(current.size, previous.size)),
                DashboardKpi("Consultas finalizadas", currentFinished.toString(), variation(currentFinished, previousFinished)),
                DashboardKpi("Consultas pendientes", pending.toString(), ""),
                DashboardKpi("Gasto total", amount.asMoney(), variation(amount, previousAmount)),
                DashboardKpi("Tarifa promedio", average.asMoney(), ""),
                DashboardKpi("Docentes consultados", current.map { it.docenteId.ifBlank { it.docenteNombre } }.filter { it.isNotBlank() }.distinct().size.toString(), "")
            )
        } else {
            val accepted = current.count { it.estado == "aceptado" || it.estado == "finalizado" }
            val proposed = current.count { it.tarifaTimestamp != null }
            val responseAverage = current.mapNotNull { tarea ->
                val createdAt = tarea.createdAt ?: return@mapNotNull null
                val proposedAt = tarea.tarifaTimestamp ?: return@mapNotNull null
                proposedAt - createdAt
            }.map { it.toDouble() }.averageOrZero()
            val rating = current.mapNotNull { it.ratingTeacher }.averageOrZero()

            listOf(
                DashboardKpi("Consultas atendidas", currentFinished.toString(), variation(currentFinished, previousFinished)),
                DashboardKpi("Ingresos", amount.asMoney(), variation(amount, previousAmount)),
                DashboardKpi("Tarifa promedio", average.asMoney(), ""),
                DashboardKpi("Tasa de aceptacion", percentage(accepted, proposed), ""),
                DashboardKpi("Tiempo prom. respuesta", responseAverage.asMinutes(), ""),
                DashboardKpi("Calificacion promedio", if (rating == 0.0) "Sin datos" else "%.1f/5".format(rating), "")
            )
        }
    }

    private fun Tarea.isInRange(range: DateRange, role: DashboardRole): Boolean {
        val timestamp = if (estado == "finalizado") completedTimestamp() ?: createdAt else createdAt
        val belongsToUser = if (role == DashboardRole.ALUMNO) alumnoId.isNotBlank() else docenteId.isNotBlank()
        return belongsToUser && timestamp != null && timestamp in range.startMillis..range.endMillis
    }

    private fun DocumentSnapshot.toTarea(): Tarea? =
        toObject(Tarea::class.java)?.copy(id = id)

    private fun Iterable<Double>.averageOrZero(): Double =
        filter { it.isFinite() }.let { values -> if (values.isEmpty()) 0.0 else values.average() }

    private fun Double.asMoney(): String = "S/ %.2f".format(this)

    private fun Double.asMinutes(): String =
        if (this <= 0.0) "Sin datos" else "${(this / 60000.0).roundToInt()} min"

    private fun percentage(value: Int, total: Int): String =
        if (total == 0) "Sin datos" else "${((value.toDouble() / total) * 100).roundToInt()}%"

    private fun variation(current: Int, previous: Int): String =
        variation(current.toDouble(), previous.toDouble())

    private fun variation(current: Double, previous: Double): String {
        if (previous <= 0.0) return if (current > 0.0) "+100% vs periodo anterior" else "0% vs periodo anterior"
        val percent = ((current - previous) / previous * 100).roundToInt()
        val sign = if (percent > 0) "+" else ""
        return "$sign$percent% vs periodo anterior"
    }
}
