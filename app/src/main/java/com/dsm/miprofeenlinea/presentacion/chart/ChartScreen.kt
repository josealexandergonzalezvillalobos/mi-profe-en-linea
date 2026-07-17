package com.dsm.miprofeenlinea.presentacion.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val stateColors = mapOf(
    "pendiente" to Color(0xFFF59E0B),
    "tarifa_propuesta" to Color(0xFF2563EB),
    "aceptado" to Color(0xFF10B981),
    "finalizado" to Color(0xFF16A34A),
    "expirada" to Color(0xFFEF4444)
)

@Composable
fun ChartScreen(
    userId: String,
    role: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dashboardRole = DashboardRole.from(role)
    val viewModel: ChartViewModel = viewModel(
        factory = ChartViewModelFactory(context, userId, dashboardRole)
    )
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.period.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LightBlue, White, Color(0xFFE0F2FE))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            DashboardHeader(
                title = dashboardRole.title,
                onBack = onBack,
                onRefresh = viewModel::refresh
            )

            PeriodSelector(
                selected = selectedPeriod,
                onSelected = viewModel::selectPeriod
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                DashboardUiState.Loading -> LoadingState()
                is DashboardUiState.Empty -> MessageState(state.message, onRetry = viewModel::refresh)
                is DashboardUiState.Error -> MessageState(state.message, onRetry = viewModel::refresh)
                is DashboardUiState.Content -> DashboardContent(state.data)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader(
    title: String,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = SecondaryBlue)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SecondaryBlue)
            Text("Resumen de actividad", fontSize = 14.sp, color = GrayText)
        }
        IconButton(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = PrimaryBlue)
        }
    }
}

@Composable
private fun PeriodSelector(
    selected: DashboardPeriod,
    onSelected: (DashboardPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashboardPeriod.entries.forEach { period ->
            FilterChip(
                selected = selected == period,
                onClick = { onSelected(period) },
                label = { Text(period.label) }
            )
        }
    }
}

@Composable
private fun DashboardContent(data: DashboardData) {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    if (data.fromLocalCache) {
        AssistChip(
            onClick = {},
            label = { Text("Mostrando datos guardados sin conexion") }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    Text(
        text = "Ultima actualizacion: ${formatter.format(Date(data.lastUpdatedAt))}",
        fontSize = 12.sp,
        color = GrayText
    )

    Spacer(modifier = Modifier.height(16.dp))

    KpiGrid(data.kpis)

    Spacer(modifier = Modifier.height(20.dp))

    ChartCard(title = "Consultas finalizadas por dia") {
        if (data.finalizadasPorDia.isEmpty()) EmptyState() else BarChart(data.finalizadasPorDia)
    }

    Spacer(modifier = Modifier.height(20.dp))

    ChartCard(title = "Distribucion de estados") {
        if (data.estadoCount.isEmpty()) EmptyState() else DonutChart(data.estadoCount)
    }

    Spacer(modifier = Modifier.height(20.dp))

    ChartCard(title = "Tarifa promedio por dia") {
        if (data.tarifaPorDia.isEmpty()) EmptyState() else BarChartDouble(data.tarifaPorDia)
    }
}

@Composable
private fun KpiGrid(kpis: List<DashboardKpi>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        kpis.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { kpi ->
                    KpiCard(kpi, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiCard(kpi: DashboardKpi, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(kpi.title, fontSize = 12.sp, color = GrayText, lineHeight = 16.sp)
            Text(kpi.value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(kpi.variation.ifBlank { "Periodo actual" }, fontSize = 11.sp, color = PrimaryBlue, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun ChartCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun MessageState(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, color = DarkText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry, contentPadding = PaddingValues(horizontal = 18.dp)) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Sin actividad para este periodo", color = GrayText, fontSize = 14.sp)
    }
}

@Composable
private fun BarChart(data: Map<String, Int>) {
    val visibleData = data.entries.toList().takeLast(30).associate { entry -> entry.key to entry.value }
    val maxValue = (visibleData.values.maxOrNull() ?: 1).toFloat()

    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Canvas(
            modifier = Modifier
                .widthIn(min = 320.dp)
                .width((visibleData.size * 46).coerceAtLeast(320).dp)
                .height(180.dp)
        ) {
            val barWidth = 24.dp.toPx()
            val gap = 22.dp.toPx()
            var x = 12.dp.toPx()

            visibleData.forEach { (_, value) ->
                val barHeight = (value / maxValue) * size.height
                drawRect(
                    color = PrimaryBlue,
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
                x += barWidth + gap
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        visibleData.forEach { (date, value) ->
            LegendRow(color = PrimaryBlue, label = date, detail = "$value consultas")
        }
    }
}

@Composable
private fun DonutChart(data: Map<String, Int>) {
    val orderedData = data.toSortedMap(compareBy<String> { stateOrder(it) }.thenBy { it })
    val total = orderedData.values.sum().toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(180.dp)) {
                var startAngle = -90f
                orderedData.entries.forEach { (estado, value) ->
                    val sweep = (value / total) * 360f
                    drawArc(
                        color = stateColors[estado] ?: Color(0xFF64748B),
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                        size = Size(size.width - 20.dp.toPx(), size.height - 20.dp.toPx()),
                        style = Stroke(width = 28.dp.toPx())
                    )
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(total.toInt().toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text("total", fontSize = 12.sp, color = GrayText)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            orderedData.forEach { (estado, value) ->
                val percentage = if (total > 0) (value / total * 100).toInt() else 0
                LegendRow(
                    color = stateColors[estado] ?: Color(0xFF64748B),
                    label = estadoLabel(estado),
                    detail = "$value ($percentage%)"
                )
            }
        }
    }
}

@Composable
private fun BarChartDouble(data: Map<String, Double>) {
    val visibleData = data.entries.toList().takeLast(30).associate { entry -> entry.key to entry.value }
    val maxValue = (visibleData.values.maxOrNull() ?: 1.0).toFloat()

    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Canvas(
            modifier = Modifier
                .widthIn(min = 320.dp)
                .width((visibleData.size * 46).coerceAtLeast(320).dp)
                .height(180.dp)
        ) {
            val barWidth = 24.dp.toPx()
            val gap = 22.dp.toPx()
            var x = 12.dp.toPx()

            visibleData.forEach { (_, value) ->
                val barHeight = (value.toFloat() / maxValue) * size.height
                drawRect(
                    color = Color(0xFF10B981),
                    topLeft = Offset(x, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
                x += barWidth + gap
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        visibleData.forEach { (date, value) ->
            LegendRow(color = Color(0xFF10B981), label = date, detail = "S/ ${"%.2f".format(value)}")
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String, detail: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 13.sp, color = DarkText, modifier = Modifier.weight(1f))
        Text(text = detail, fontSize = 13.sp, color = GrayText, fontWeight = FontWeight.Medium)
    }
}

private fun estadoLabel(estado: String): String =
    when (estado) {
        "pendiente" -> "Pendiente"
        "tarifa_propuesta" -> "Tarifa propuesta"
        "aceptado" -> "En curso"
        "finalizado" -> "Finalizada"
        "expirada" -> "Expirada"
        else -> estado.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

private fun stateOrder(estado: String): Int =
    when (estado) {
        "pendiente" -> 0
        "tarifa_propuesta" -> 1
        "aceptado" -> 2
        "finalizado" -> 3
        "expirada" -> 4
        else -> 5
    }
