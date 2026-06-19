package com.dsm.miprofeenlinea.presentacion.rating

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RatingScreen(
    taskId: String,
    isTeacher: Boolean,
    onGoHomeTeacher: () -> Unit,
    onGoHomeStudent: () -> Unit
) {

    var rating by remember { mutableStateOf(0f) }
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (isTeacher)
                "Califica al estudiante"
            else
                "Califica al docente",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = rating,
            onValueChange = { rating = it },
            valueRange = 0f..5f,
            steps = 4
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val field = if (isTeacher) "ratingStudent" else "ratingTeacher"

                db.collection("chats")
                    .document(taskId)
                    .update(
                        mapOf(
                            field to rating,
                            "estado" to "finalizado"
                        )
                    )
                    .addOnSuccessListener {

                        if (isTeacher) {
                            onGoHomeTeacher()
                        } else {
                            onGoHomeStudent()
                        }
                    }
            }
        ) {
            Text("Calificar")
        }
    }
}