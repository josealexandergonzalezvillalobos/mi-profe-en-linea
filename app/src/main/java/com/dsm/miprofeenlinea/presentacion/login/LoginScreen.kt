package com.dsm.miprofeenlinea.presentacion.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miprofeenlinea.R
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    navigateToSignUp: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    navigateToHomeDocente: () -> Unit = {}
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()

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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            // LOGO
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.teacher),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(18.dp)
                        .size(90.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TITULO
            Text(
                text = "Mi Profe en Línea",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SecondaryBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SUBTITULO
            Text(
                text = "Accede a tus cursos y continúa\naprendiendo desde cualquier lugar",
                color = GrayText,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // CARD LOGIN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {

                    // EMAIL
                    Text(
                        text = "Correo electrónico",
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                text = "Ingrese su correo",
                                color = GrayText
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedTextColor = DarkText,
                            unfocusedTextColor = DarkText,
                            cursorColor = PrimaryBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // PASSWORD
                    Text(
                        text = "Contraseña",
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                text = "Ingrese su contraseña",
                                color = GrayText
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        trailingIcon = {

                            val image =
                                if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff

                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {

                                Icon(
                                    imageVector = image,
                                    contentDescription = "",
                                    tint = PrimaryBlue
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedTextColor = DarkText,
                            unfocusedTextColor = DarkText,
                            cursorColor = PrimaryBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTON LOGIN
                    Button(
                        onClick = {

                            Log.d("AUTH", "Email: $email")
                            Log.d("AUTH", "Password: $password")

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        val user = task.result?.user

                                        Toast.makeText(
                                            context,
                                            "Bienvenido ${user?.email}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Log.d(
                                            "AUTH",
                                            "Login correcto: ${user?.email}"
                                        )

                                        val uid = user?.uid ?: ""

                                        db.collection("usuarios")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener { document ->

                                                val modo = document.getString("modo") ?: ""

                                                Log.d("FIRESTORE", "Modo: $modo")

                                                if (modo == "Docente") {

                                                    navigateToHomeDocente()

                                                } else {

                                                    navigateToHome()
                                                }
                                            }
                                            .addOnFailureListener {

                                                Toast.makeText(
                                                    context,
                                                    "No se pudo verificar el perfil",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Correo o contraseña incorrectos",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        Log.e("AUTH",              "Error: ${task.exception?.message}"
                                        )
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {

                        Text(
                            text = "Iniciar Sesión",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // TEXTO REGISTRO
            val annotatedText = buildAnnotatedString {

                append("¿No tienes cuenta? ")

                pushStringAnnotation(
                    tag = "signup",
                    annotation = "signup"
                )

                withStyle(
                    style = SpanStyle(
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                ) {

                    append("Regístrate gratis")
                }

                pop()
            }

            ClickableText(
                text = annotatedText,
                onClick = { offset ->

                    annotatedText.getStringAnnotations(
                        tag = "signup",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {

                        navigateToSignUp()
                    }
                },
                modifier = Modifier.padding(bottom = 32.dp),
                style = TextStyle(
                    color = GrayText,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Aprende fácil, rápido y desde casa",
                color = GrayText,
                fontSize = 14.sp
            )
        }
    }
}
