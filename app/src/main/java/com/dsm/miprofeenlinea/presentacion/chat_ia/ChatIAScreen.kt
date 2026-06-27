package com.dsm.miprofeenlinea.presentacion.chat_ia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// COLORES
val PrimaryBlue = Color(0xFF2563EB)
val SecondaryBlue = Color(0xFF1E3A8A)
val LightBlue = Color(0xFFDBEAFE)
val White = Color(0xFFFFFFFF)
val GrayText = Color(0xFF6B7280)
val DarkText = Color(0xFF111827)

@Composable
fun ChatIAScreen(
    viewModel: ChatIAViewModel = viewModel(),
    onContactProfessor: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LightBlue,
                        White,
                        Color(0xFFF8FAFC)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // HEADER
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        top = 52.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                ) {

                    Text(
                        text = "Tutor IA",
                        color = White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Gemini conectado ●",
                        color = White.copy(alpha = 0.85f),
                        fontSize = 15.sp
                    )
                }
            }

            // MENSAJES
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    top = 20.dp,
                    bottom = 20.dp
                )
            ) {

                items(uiState.messages) { msg ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (msg.isUser)
                                Arrangement.End
                            else
                                Arrangement.Start
                    ) {

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (msg.isUser)
                                        PrimaryBlue
                                    else
                                        White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            modifier = Modifier.widthIn(
                                max = 320.dp
                            )
                        ) {

                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 14.dp
                                ),
                                color =
                                    if (msg.isUser)
                                        White
                                    else
                                        DarkText,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // INDICADOR DE ESCRITURA
                if (uiState.isTyping) {

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.Start
                        ) {

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = White
                                )
                            ) {

                                Text(
                                    text = "Tutor IA está escribiendo...",
                                    modifier = Modifier.padding(
                                        horizontal = 18.dp,
                                        vertical = 14.dp
                                    ),
                                    color = DarkText
                                )
                            }
                        }
                    }
                }
            }

            // BOTÓN CONTACTAR PROFESOR
            if (uiState.showProfessorButton) {

                Button(
                    onClick = onContactProfessor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryBlue
                    )
                ) {

                    Text(
                        text = "Contactar Profesor"
                    )
                }
            }

            // INPUT
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        bottom = 18.dp
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = uiState.currentMessage,
                        onValueChange = {
                            viewModel.onMessageChange(it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Pregúntale algo al Tutor IA...",
                                color = GrayText
                            )
                        },
                        shape = RoundedCornerShape(18.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedTextColor = DarkText,
                            unfocusedTextColor = DarkText,
                            cursorColor = PrimaryBlue
                        )
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    FloatingActionButton(
                        onClick = {
                            viewModel.sendMessage()
                        },
                        containerColor = PrimaryBlue
                    ) {

                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar",
                            tint = White
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}