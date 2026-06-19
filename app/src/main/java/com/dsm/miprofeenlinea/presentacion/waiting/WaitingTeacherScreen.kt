package com.dsm.miprofeenlinea.presentacion.waiting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

// COLORES
val PrimaryBlue = Color(0xFF2563EB)
val SecondaryBlue = Color(0xFF1E3A8A)
val LightBlue = Color(0xFFDBEAFE)
val White = Color(0xFFFFFFFF)
val GrayText = Color(0xFF6B7280)

@Composable
fun WaitingTeacherScreen(
    taskId: String,
    navigateToChat: (String) -> Unit = {}
) {

    // CONTADOR
    var seconds by remember {
        mutableStateOf(0)
    }

    var tarifa by remember { mutableStateOf<Double?>(null) }
    var docenteNombre by remember { mutableStateOf("") }
    var tiempoOferta by remember { mutableStateOf(10) }
    var mostrarOferta by remember { mutableStateOf(false) }

    // TIMER
    LaunchedEffect(Unit) {

        while (true) {

            delay(1000)

            seconds++
        }
    }

    // ESCUCHAR FIRESTORE
    LaunchedEffect(Unit) {

        FirebaseFirestore.getInstance()
            .collection("tareas")
            .document(taskId)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val estado = snapshot.getString("estado") ?: ""

                if (estado == "aceptado") {
                    navigateToChat(taskId)
                }

                if (estado == "tarifa_propuesta") {

                    tarifa = snapshot.getDouble("tarifa")

                    docenteNombre =
                        snapshot.getString("docenteNombre")
                            ?: "Docente"

                    mostrarOferta = true
                }

                if (
                    estado == "pendiente" ||
                    estado == "expirada"
                ) {
                    mostrarOferta = false
                }
            }
    }

    LaunchedEffect(mostrarOferta) {

        if (mostrarOferta) {

            tiempoOferta = 10

            while (tiempoOferta > 0) {

                delay(1000)

                tiempoOferta--
            }

            mostrarOferta = false
        }
    }

    // FORMATO MINUTOS Y SEGUNDOS
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    val formattedTime =
        String.format(
            "%02d:%02d",
            minutes,
            remainingSeconds
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LightBlue,
                        White,
                        Color(0xFFE0F2FE)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator(
                    color = PrimaryBlue,
                    strokeWidth = 6.dp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Buscando Docente",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Esperando a que un docente acepte tu solicitud...",
                    color = GrayText,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // CONTADOR
                Text(
                    text = formattedTime,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Tiempo de espera",
                    color = GrayText,
                    fontSize = 16.sp
                )

                if (mostrarOferta) {

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "📢 Oferta recibida",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Docente: $docenteNombre"
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Monto: S/ $tarifa",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "⏳ Expira en ${tiempoOferta}s",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Button(
                                    onClick = {

                                        val db = FirebaseFirestore.getInstance()

                                        db.collection("tareas")
                                            .document(taskId)
                                            .update(
                                                mapOf(
                                                    "estado" to "aceptado"
                                                )
                                            )
                                            .addOnSuccessListener {

                                                // 👉 CREAR CHAT
                                                db.collection("chats")
                                                    .document(taskId)
                                                    .set(
                                                        mapOf(
                                                            "taskId" to taskId,
                                                            "createdAt" to System.currentTimeMillis()
                                                        )
                                                    )

                                                navigateToChat(taskId)
                                            }
                                    }
                                ) {
                                    Text("Aceptar")
                                }

                                OutlinedButton(
                                    onClick = {

                                        FirebaseFirestore.getInstance()
                                            .collection("tareas")
                                            .document(taskId)
                                            .update(
                                                mapOf(
                                                    "estado" to "pendiente",
                                                    "tarifa" to null
                                                )
                                            )

                                        mostrarOferta = false
                                    }
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}