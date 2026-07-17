package com.dsm.miprofeenlinea.presentacion.home

import HomeViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.dsm.miprofeenlinea.model.Tarea
import com.dsm.miprofeenlinea.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun HomeDocenteScreen(
    viewModel: HomeViewModel = viewModel(),
    onGoToCharts: () -> Unit = {},
    onGoToProfile: () -> Unit = {},
    onGoToChat: (String) -> Unit = {}
){

    val context = LocalContext.current

    var tareas by remember { mutableStateOf(listOf<Tarea>()) }

    var showDialog by remember { mutableStateOf(false) }
    var selectedTarea by remember { mutableStateOf<Tarea?>(null) }
    var tarifaInput by remember { mutableStateOf("") }

    // ⏱️ countdown por tarea
    var countdownMap by remember { mutableStateOf(mapOf<String, Int>()) }
    val scope = rememberCoroutineScope()
    var navigatedToChatMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // 📡 ESCUCHAR TAREAS
    LaunchedEffect(Unit) {
        viewModel.escucharTareas { lista ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

            tareas = lista.filter { tarea ->
                tarea.estado == "pendiente" ||
                        (tarea.docenteId == currentUserId &&
                                (tarea.estado == "tarifa_propuesta" ||
                                        tarea.estado == "aceptado"))
            }
        }
    }

    // ⏱️ FUNCIÓN COUNTDOWN
    fun startCountdown(tareaId: String) {

        if (countdownMap.containsKey(tareaId)) return

        scope.launch {

            var time = 10

            countdownMap = countdownMap + (tareaId to time)

            while (time > 0) {
                delay(1000)
                time--

                countdownMap = countdownMap.toMutableMap().apply {
                    put(tareaId, time)
                }
            }

            // ⛔ expira
            countdownMap = countdownMap - tareaId

            FirebaseFirestore.getInstance()
                .collection("tareas")
                .document(tareaId)
                .update(
                    mapOf(
                        "estado" to "pendiente",
                        "tarifa" to null,
                        "docenteNombre" to "",
                        "docenteId" to "",
                        "cancelledAt" to System.currentTimeMillis(),
                        "tarifaTimestamp" to null
                    )
                )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightBlue, White, Color(0xFFE0F2FE))
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Panel Docente",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SecondaryBlue
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Solicitudes de tareas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📄 LISTA
            Button(
                onClick = onGoToCharts,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Ver estadisticas")
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onGoToProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mi perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            tareas.forEach { tarea ->

                val countdown = countdownMap[tarea.id]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Image(
                            painter = rememberAsyncImagePainter(tarea.imagen),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Estado: ${tarea.estado}",
                            fontSize = 14.sp,
                            color = GrayText
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 🟢 CASO 1: PENDIENTE
                        if (tarea.estado == "pendiente") {

                            Button(
                                onClick = {
                                    selectedTarea = tarea
                                    tarifaInput = ""
                                    showDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(SecondaryBlue)
                            ) {
                                Text("Asignar tarifa")
                            }
                        }

                        // 🟠 CASO 2: TARIFA PROPUESTA
                        if (tarea.estado == "tarifa_propuesta") {

                            Text(
                                text = "⏳ Esperando aceptación...",
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )

                            if (countdown != null) {
                                Text(
                                    text = "Tiempo restante: ${countdown}s",
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))


                            Button(
                                onClick = {
                                    FirebaseFirestore.getInstance()
                                        .collection("tareas")
                                        .document(tarea.id)
                                        .update(
                                            mapOf(
                                                "estado" to "pendiente",
                                                "tarifa" to null,
                                                "docenteNombre" to "",
                                                "docenteId" to "",
                                                "cancelledAt" to System.currentTimeMillis(),
                                                "tarifaTimestamp" to null
                                            )
                                        )
                                }
                            ) {
                                Text("Cancelar propuesta")
                            }

                            // 🚀 INICIAR COUNTDOWN
                            LaunchedEffect(tarea.id, tarea.estado) {

                                if (tarea.estado != "tarifa_propuesta") return@LaunchedEffect

                                startCountdown(tarea.id)
                            }
                        }
                    }
                }

                LaunchedEffect(tarea.id, tarea.estado) {

                    val alreadyNavigated = navigatedToChatMap[tarea.id] == true

                    if (tarea.estado == "aceptado" && !alreadyNavigated) {

                        navigatedToChatMap = navigatedToChatMap + (tarea.id to true)

                        FirebaseFirestore.getInstance()
                            .collection("chats")
                            .document(tarea.id)
                            .set(
                                mapOf(
                                    "taskId" to tarea.id,
                                    "createdAt" to System.currentTimeMillis()
                                )
                            )

                        onGoToChat(tarea.id)
                    }
                }
            }


            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Mi Profe en Línea",
                color = GrayText,
                fontSize = 13.sp
            )
        }
    }

    // 💰 DIALOG TARIFA
    if (showDialog && selectedTarea != null) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Asignar tarifa") },
            text = {

                Column {

                    Text("Ingrese el monto")

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tarifaInput,
                        onValueChange = { tarifaInput = it },
                        label = { Text("Tarifa (S/)") }
                    )
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        val tarifa = tarifaInput.toDoubleOrNull()

                        if (tarifa == null) {
                            Toast.makeText(context, "Monto inválido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        FirebaseFirestore.getInstance()
                            .collection("tareas")
                            .document(selectedTarea!!.id)
                            .update(
                                mapOf(
                                    "tarifa" to tarifa,
                                    "estado" to "tarifa_propuesta",
                                    "docenteId" to FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                                    "docenteNombre" to "Juan Pérez",
                                    "tarifaTimestamp" to System.currentTimeMillis()
                                )
                            )

                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
