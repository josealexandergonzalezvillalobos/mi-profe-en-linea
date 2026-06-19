package com.dsm.miprofeenlinea.presentacion.signup

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.dsm.miprofeenlinea.R
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.SoftBlue
import com.dsm.miprofeenlinea.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(auth: FirebaseAuth) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nombres by remember { mutableStateOf("") }

    val opcionesModo = listOf("Estudiante", "Docente")
    var modoSeleccionado by remember { mutableStateOf(opcionesModo[0]) }
    var expanded by remember { mutableStateOf(false) }

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

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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

            Text(
                text = "Crea tu cuenta y empieza\na aprender con los mejores docentes",
                color = GrayText,
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // CARD FORMULARIO
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

                    Text(
                        text = "Apellidos y Nombres",
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nombres,
                        onValueChange = { nombres = it },
                        placeholder = {
                            Text(
                                text = "Ingrese sus apellidos y nombres",
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

                    Text(
                        text = "Modalidad",
                        color = DarkText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {

                        OutlinedTextField(
                            value = modoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFD1D5DB)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {

                            opcionesModo.forEach { opcion ->

                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        modoSeleccionado = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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

                    // BOTON REGISTRO
                    Button(
                        onClick = {

                            Log.d("AUTH", "Email: $email")
                            Log.d("AUTH", "Password: $password")

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        val user = task.result?.user
                                        val uid = user?.uid ?: ""

                                        val datosUsuario = hashMapOf(
                                            "uid" to uid,
                                            "nombres" to nombres,
                                            "email" to email,
                                            "modo" to modoSeleccionado,
                                            "fechaRegistro" to System.currentTimeMillis()
                                        )

                                        db.collection("usuarios")
                                            .document(uid)
                                            .set(datosUsuario)
                                            .addOnSuccessListener {

                                                Toast.makeText(
                                                    context,
                                                    "Usuario registrado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                Log.d("FIRESTORE", "Usuario guardado")

                                            }
                                            .addOnFailureListener { e ->

                                                Toast.makeText(
                                                    context,
                                                    "Usuario creado pero no guardado en Firestore",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                Log.e("FIRESTORE", e.message ?: "")
                                            }
                                    } else {

                                        Toast.makeText(
                                            context,
                                            "Usuario ya existe...",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        Log.e(
                                            "AUTH",
                                            "Error: ${task.exception?.message}"
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
                            text = "Crear Cuenta",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Educación moderna para todos",
                color = GrayText,
                fontSize = 15.sp
            )
        }
    }
}