package com.dsm.miprofeenlinea.presentacion.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.SoftBlue
import com.dsm.miprofeenlinea.ui.theme.White

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
                        Color(0xFFF8FBFF),
                        LightBlue,
                        Color(0xFFE0F2FE)
                    )
                )
            )
    ) {

        // DECORACION SUPERIOR
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 160.dp, y = (-80).dp)
                .background(
                    color = SoftBlue.copy(alpha = 0.25f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-60).dp, y = 600.dp)
                .background(
                    color = PrimaryBlue.copy(alpha = 0.12f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(70.dp))

            // LOGO
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                ),
                modifier = Modifier.shadow(
                    elevation = 20.dp,
                    shape = CircleShape
                )
            ) {

                Box(
                    modifier = Modifier
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.teacher),
                        contentDescription = "Logo",
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // BADGE
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PrimaryBlue.copy(alpha = 0.10f)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Educación Inteligente",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // TITULO
            Text(
                text = "Mi Profe en Línea",
                color = SecondaryBlue,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SUBTITULO
            Text(
                text = "Resuelve tus tareas con ayuda\npersonalizada de docentes en tiempo real.",
                color = GrayText,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(36.dp))


            Spacer(modifier = Modifier.weight(1f))

            // BOTON INICIAR SESION
            Button(
                onClick = { navigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Text(
                    text = "Iniciar Sesión",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // BOTON CREAR CUENTA
            Button(
                onClick = { navigateToSignUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp
                )
            ) {

                Text(
                    text = "Crear Cuenta",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "",
                    tint = White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // GOOGLE
            CustomButton(
                modifier = Modifier.clickable { },
                painter = painterResource(id = R.drawable.google),
                title = "Continuar con Google"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Aprende desde cualquier lugar",
                color = GrayText,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun BenefitItem(text: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = text,
            color = DarkText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
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
            .height(58.dp)
            .background(
                color = White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(20.dp)
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