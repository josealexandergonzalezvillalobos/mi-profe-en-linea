package com.dsm.miprofeenlinea.presentacion.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChartScreen() {

    val db = FirebaseFirestore.getInstance()

    var data by remember { mutableStateOf(mapOf<String, Int>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {

        try {

            // 🔥 CAMBIO IMPORTANTE: colección correcta
            val snapshot = db.collection("tareas")
                .whereEqualTo("estado", "finalizado")
                .get()
                .await()

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val grouped = snapshot.documents
                .mapNotNull { doc ->

                    // 🔥 CAMPO CORRECTO
                    val timestamp = doc.getLong("tarifaTimestamp")
                        ?: return@mapNotNull null

                    val date = formatter.format(Date(timestamp))
                    date
                }
                .groupingBy { it }
                .eachCount()
                .toSortedMap()

            data = grouped
            loading = false

        } catch (e: Exception) {
            e.printStackTrace()
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "📊 Consultas finalizadas por día",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (loading) {
            CircularProgressIndicator()
            return
        }

        if (data.isEmpty()) {
            Text("No hay datos aún")
            return
        }

        BarChart(data)
    }
}

@Composable
fun BarChart(data: Map<String, Int>) {

    val maxValue = (data.values.maxOrNull() ?: 1).toFloat()
    val barColor = Color(0xFF2563EB)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp)
    ) {

        val barWidth = size.width / (data.size * 2f)
        var x = barWidth / 2f

        data.forEach { (_, value) ->

            val barHeight = (value / maxValue) * size.height

            drawRect(
                color = barColor,
                topLeft = Offset(
                    x,
                    size.height - barHeight
                ),
                size = Size(barWidth, barHeight)
            )

            x += barWidth * 2
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Column {
        data.forEach { (date, value) ->
            Text("📅 $date → $value consultas")
        }
    }
}