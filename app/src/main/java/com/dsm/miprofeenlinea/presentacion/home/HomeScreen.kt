package com.dsm.miprofeenlinea.presentacion.home

import HomeViewModel
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miprofeenlinea.R
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsm.miprofeenlinea.model.Tarea
import com.dsm.miprofeenlinea.utils.bitmapToUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onRequestTeacherClick: (String) -> Unit = {},
    onGoToCharts: () -> Unit = {}, // agregamos un callback
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    var tareas by remember { mutableStateOf(listOf<Tarea>()) }
    var countdownMap by remember { mutableStateOf(mapOf<String, Int>()) }
    val scope = rememberCoroutineScope()

    // VARIABLE PARA FOTO
    var imageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    // CAMARA
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {
                imageBitmap = bitmap
            }
        }

    LaunchedEffect(Unit) {
        viewModel.escucharTareas { lista ->
            tareas = lista
        }
    }

    val ofertasActivas = tareas.filter {
        it.estado == "tarifa_propuesta"
    }

    fun startCountdown(tarea: Tarea) {

        if (countdownMap.containsKey(tarea.id)) return

        scope.launch {

            var time = 10

            countdownMap = countdownMap + (tarea.id to time)

            while (time > 0) {
                delay(1000)
                time--

                countdownMap = countdownMap.toMutableMap().apply {
                    put(tarea.id, time)
                }
            }

            // ⛔ expira
            countdownMap = countdownMap - tarea.id

            FirebaseFirestore.getInstance()
                .collection("tareas")
                .document(tarea.id)
                .update(
                    mapOf(
                        "estado" to "expirada"
                    )
                )
        }
    }

    ofertasActivas.forEach { tarea ->

        val countdown = countdownMap[tarea.id]

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "📢 Oferta de docente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFFF6F00)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Docente: ${tarea.docenteNombre}",
                    fontSize = 14.sp
                )

                Text(
                    text = "Monto propuesto: S/ ${tarea.tarifa}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (countdown != null) {
                    Text(
                        text = "⏳ Expira en ${countdown}s",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {

                            FirebaseFirestore.getInstance()
                                .collection("tareas")
                                .document(tarea.id)
                                .update(
                                    mapOf(
                                        "estado" to "aceptado"
                                    )
                                )

                            // 👉 aquí luego navegas al chat
                            Toast.makeText(context, "Abriendo chat...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(PrimaryBlue)
                    ) {
                        Text("Aceptar")
                    }

                    OutlinedButton(
                        onClick = {
                            FirebaseFirestore.getInstance()
                                .collection("tareas")
                                .document(tarea.id)
                                .update(
                                    mapOf(
                                        "estado" to "pendiente",
                                        "tarifa" to null
                                    )
                                )
                        }
                    ) {
                        Text("Rechazar")
                    }
                }

                // 🚀 iniciar countdown automático
                LaunchedEffect(tarea.id) {
                    startCountdown(tarea)
                }
            }
        }
    }

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

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.teacher),
                contentDescription = "Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // TITULO
            Text(
                text = "Mi Profe en Línea",
                color = SecondaryBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // SUBTITULO
            Text(
                text = "Toma una foto de tu tarea y\nsolicita ayuda personalizada",
                color = GrayText,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    onGoToCharts()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ver estadísticas")
            }

            // CARD FOTO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // FOTO O ICONO
                    if (imageBitmap != null) {

                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Foto",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    2.dp,
                                    PrimaryBlue,
                                    RoundedCornerShape(20.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Toma foto a tu tarea",
                        fontSize = 24.sp,
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Sube una imagen clara de tu ejercicio o tarea para recibir apoyo de un docente.",
                        color = GrayText,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // BOTON CAMARA
                    Button(
                        onClick = {
                            cameraLauncher.launch(null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "",
                            tint = White
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Abrir Cámara",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // BOTON SOLICITAR DOCENTE
            Button(
                onClick = {

                    // VALIDAR FOTO
                    if (imageBitmap == null) {

                        Toast.makeText(
                            context,
                            "Primero toma una foto",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    val storage =
                        FirebaseStorage.getInstance()

                    val firestore =
                        FirebaseFirestore.getInstance()

                    val imageUri =
                        bitmapToUri(
                            context,
                            imageBitmap!!
                        )

                    val fileName = "tareas/${UUID.randomUUID()}.jpg"

                    val storageRef = storage.reference.child(fileName)

                    // SUBIR IMAGEN
                    storageRef.putFile(imageUri)
                        .addOnSuccessListener {

                            // OBTENER URL
                            storageRef.downloadUrl
                                .addOnSuccessListener { downloadUrl ->

                                    // DATOS FIRESTORE
                                    val tarea = Tarea(
                                        imagen = downloadUrl.toString(),
                                        estado = "pendiente"
                                    )

                                    viewModel.guardarTarea(
                                        tarea = tarea,
                                        onSuccess = { taskId ->

                                            Toast.makeText(
                                                context,
                                                "Solicitud enviada correctamente",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            onRequestTeacherClick(taskId)
                                        },
                                        onError = {

                                            Toast.makeText(
                                                context,
                                                "Error guardando datos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                }
                        }
                        .addOnFailureListener {

                            Toast.makeText(
                                context,
                                "Error subiendo imagen",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryBlue
                )
            ) {

                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "",
                    tint = White
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Solicitar Docente",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Aprender nunca fue tan fácil",
                color = GrayText,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}