package com.dsm.miprofeenlinea.presentacion.chart

import com.dsm.miprofeenlinea.model.Tarea
import java.util.Calendar

enum class DashboardRole(val firestoreField: String, val title: String) {
    ALUMNO("alumnoId", "Dashboard alumno"),
    DOCENTE("docenteId", "Dashboard docente");

    companion object {
        fun from(value: String): DashboardRole =
            if (value.equals("docente", ignoreCase = true)) DOCENTE else ALUMNO
    }
}

enum class DashboardPeriod(val label: String) {
    LAST_7_DAYS("Ultimos 7 dias"),
    LAST_30_DAYS("Ultimos 30 dias"),
    THIS_MONTH("Este mes")
}

data class DateRange(
    val startMillis: Long,
    val endMillis: Long
)

data class DashboardKpi(
    val title: String,
    val value: String,
    val variation: String
)

data class DashboardData(
    val role: DashboardRole,
    val period: DashboardPeriod,
    val kpis: List<DashboardKpi>,
    val finalizadasPorDia: Map<String, Int>,
    val estadoCount: Map<String, Int>,
    val tarifaPorDia: Map<String, Double>,
    val lastUpdatedAt: Long,
    val fromLocalCache: Boolean = false
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Content(val data: DashboardData) : DashboardUiState
    data class Empty(val message: String) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

fun DashboardPeriod.currentRange(now: Long = System.currentTimeMillis()): DateRange {
    val calendar = Calendar.getInstance().apply { timeInMillis = now }
    val end = calendar.endOfDay()

    val start = Calendar.getInstance().apply { timeInMillis = now }
    when (this) {
        DashboardPeriod.LAST_7_DAYS -> start.add(Calendar.DAY_OF_YEAR, -6)
        DashboardPeriod.LAST_30_DAYS -> start.add(Calendar.DAY_OF_YEAR, -29)
        DashboardPeriod.THIS_MONTH -> {
            start.set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    return DateRange(start.startOfDay(), end)
}

fun DashboardPeriod.previousRange(now: Long = System.currentTimeMillis()): DateRange {
    val current = currentRange(now)
    val span = current.endMillis - current.startMillis
    return DateRange(
        startMillis = current.startMillis - span - 1,
        endMillis = current.startMillis - 1
    )
}

fun Tarea.completedTimestamp(): Long? = completedAt ?: finalizadoAt

private fun Calendar.startOfDay(): Long {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return timeInMillis
}

private fun Calendar.endOfDay(): Long {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
    return timeInMillis
}
