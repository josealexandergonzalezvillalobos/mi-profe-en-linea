package com.dsm.miprofeenlinea.presentacion.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.dsm.miprofeenlinea.ui.theme.*

@Composable
fun ChatScreen(
    taskId: String,
    currentUserRole: String,
    onGoToRating: (String,Boolean) -> Unit
) {

    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var message by remember { mutableStateOf("") }

    val messages = remember { mutableStateListOf<Map<String, Any>>() }

    var navigatedToRating by remember { mutableStateOf(false) }

    // ---------------------------
    // MENSAJES EN TIEMPO REAL
    // ---------------------------
    LaunchedEffect(taskId) {

        db.collection("chats")
            .document(taskId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                messages.clear()

                for (doc in snapshot.documents) {
                    messages.add(doc.data ?: emptyMap())
                }
            }
    }

    // ---------------------------
    // DETECTAR FINALIZACIÓN CHAT
    // ---------------------------
    LaunchedEffect(taskId) {

        db.collection("chats")
            .document(taskId)
            .addSnapshotListener { snapshot, _ ->

                val estado = snapshot?.getString("estado")

                if (estado == "finalizado" && !navigatedToRating) {
                    navigatedToRating = true
                    onGoToRating(taskId, currentUserRole == "docente")
                }
            }
    }

    val sentColor = Color(0xFF2563EB)
    val receivedColor = Color(0xFFE5E7EB)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---------------------------
            // HEADER
            // ---------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {

                        Text(
                            text = "Chat",
                            color = White,
                            fontSize = 26.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )

                        Text(
                            text = "Tarea: $taskId",
                            color = White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }

                    // ---------------------------
                    // BOTÓN FINALIZAR CHAT
                    // ---------------------------
                    IconButton(
                        onClick = {

                            if (navigatedToRating) return@IconButton
                            val finishedAt = System.currentTimeMillis()

                            db.collection("chats")
                                .document(taskId)
                                .update(
                                    mapOf(
                                        "estado" to "finalizado",
                                        "finalizadoAt" to finishedAt
                                    )
                                )
                                .addOnSuccessListener {
                                    db.collection("tareas")
                                        .document(taskId)
                                        .update(
                                            mapOf(
                                                "estado" to "finalizado",
                                                "completedAt" to finishedAt
                                            )
                                        )
                                    navigatedToRating = true
                                    onGoToRating(taskId, currentUserRole == "docente")
                                }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Finalizar chat",
                            tint = Color.White
                        )
                    }
                }
            }

            // ---------------------------
            // MENSAJES
            // ---------------------------
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(messages) { msg ->

                    val text = msg["text"] as? String ?: ""
                    val senderId = msg["senderId"] as? String ?: ""

                    val isMe = senderId == currentUserId

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (isMe) Arrangement.End else Arrangement.Start
                    ) {

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) sentColor else receivedColor
                            ),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {

                            Text(
                                text = text,
                                modifier = Modifier.padding(12.dp),
                                color = if (isMe) Color.White else Color(0xFF111827),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // ---------------------------
            // INPUT
            // ---------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") },
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {

                            if (message.isBlank() || currentUserId == null) return@FloatingActionButton

                            val msg = hashMapOf(
                                "text" to message,
                                "senderId" to currentUserId,
                                "timestamp" to System.currentTimeMillis()
                            )

                            db.collection("chats")
                                .document(taskId)
                                .collection("messages")
                                .add(msg)

                            message = ""
                        },
                        containerColor = PrimaryBlue
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = White
                        )
                    }
                }
            }
        }
    }
}
