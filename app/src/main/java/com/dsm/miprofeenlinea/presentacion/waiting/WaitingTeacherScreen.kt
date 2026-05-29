package com.dsm.miprofeenlinea.presentacion.waiting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
    navigateToChat: () -> Unit = {}
) {

    // CONTADOR
    var seconds by remember {
        mutableStateOf(0)
    }

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

                if (snapshot != null) {

                    val estado =
                        snapshot.getString("estado")

                    if (estado == "aceptado") {

                        navigateToChat()
                    }
                }
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
            }
        }
    }
}