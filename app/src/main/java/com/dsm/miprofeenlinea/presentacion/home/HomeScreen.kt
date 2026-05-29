package com.dsm.miprofeenlinea.presentacion.home

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
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// FUNCION PARA CONVERTIR BITMAP A URI
fun bitmapToUri(
    context: Context,
    bitmap: Bitmap
): Uri {

    val file = File(
        context.cacheDir,
        "${UUID.randomUUID()}.jpg"
    )

    val outputStream = FileOutputStream(file)

    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        100,
        outputStream
    )

    outputStream.flush()
    outputStream.close()

    return Uri.fromFile(file)
}

@Composable
fun HomeScreen(
    onRequestTeacherClick: (String) -> Unit = {}
) {

    val context = LocalContext.current

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

                    val fileName =
                        "tareas/${UUID.randomUUID()}.jpg"

                    val storageRef =
                        storage.reference.child(fileName)

                    // SUBIR IMAGEN
                    storageRef.putFile(imageUri)
                        .addOnSuccessListener {

                            // OBTENER URL
                            storageRef.downloadUrl
                                .addOnSuccessListener { downloadUrl ->

                                    // DATOS FIRESTORE
                                    val data = hashMapOf(
                                        "imagen" to downloadUrl.toString(),
                                        "fecha" to System.currentTimeMillis(),
                                        "estado" to "pendiente"
                                    )

                                    // GUARDAR EN FIRESTORE
                                    firestore.collection("tareas")
                                        .add(data)
                                        .addOnSuccessListener { document ->
                                            val taskId = document.id

                                            Toast.makeText(
                                                context,
                                                "Solicitud enviada correctamente",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            onRequestTeacherClick(taskId)
                                        }
                                        .addOnFailureListener {

                                            Toast.makeText(
                                                context,
                                                "Error guardando datos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
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