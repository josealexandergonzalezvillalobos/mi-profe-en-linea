package com.dsm.miprofeenlinea.presentacion.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsm.miprofeenlinea.R
import com.dsm.miprofeenlinea.ui.theme.Gray
import com.dsm.miprofeenlinea.ui.theme.Green

// NUEVOS COLORES
val PrimaryBlue = Color(0xFF2563EB)
val SecondaryBlue = Color(0xFF1E3A8A)
val LightBlue = Color(0xFFDBEAFE)
val White = Color(0xFFFFFFFF)
val DarkText = Color(0xFF111827)
val GrayText = Color(0xFF6B7280)

@Preview(showBackground = true)
@Composable
fun InitialScreen(
    navigateToLogin: () -> Unit = {},
    navigateToSignUp: () -> Unit = {}
) {

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // LOGO
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.teacher),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(20.dp)
                        .size(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TITULOS
            Text(
                text = "Mi Profe en Línea",
                color = SecondaryBlue,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Aprende desde cualquier lugar\ncon tus docentes favoritos",
                color = GrayText,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // INICIAR SESION
            Button(
                onClick = { navigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Text(
                    text = "Iniciar Sesión",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            //REGISTRARSE
            Button(
                onClick = { navigateToSignUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Text(
                    text = "Crear Cuenta",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GOOGLE
            CustomButton(
                modifier = Modifier.clickable { },
                painter = painterResource(id = R.drawable.google),
                title = "Continuar con Google"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier,
    painter: Painter,
    title: String
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = White,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFD1D5DB),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {

        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .padding(start = 20.dp)
                .size(22.dp)
        )

        Text(
            text = title,
            color = DarkText,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}