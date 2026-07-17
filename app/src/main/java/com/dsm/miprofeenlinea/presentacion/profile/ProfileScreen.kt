package com.dsm.miprofeenlinea.presentacion.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsm.miprofeenlinea.ui.theme.DarkText
import com.dsm.miprofeenlinea.ui.theme.GrayText
import com.dsm.miprofeenlinea.ui.theme.LightBlue
import com.dsm.miprofeenlinea.ui.theme.PrimaryBlue
import com.dsm.miprofeenlinea.ui.theme.SecondaryBlue
import com.dsm.miprofeenlinea.ui.theme.White
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ProfileScreen(
    uid: String,
    email: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context, uid, email)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LightBlue, White, Color(0xFFE0F2FE))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = SecondaryBlue)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mi perfil", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = SecondaryBlue)
                    Text("Datos visibles para tu cuenta", fontSize = 14.sp, color = GrayText)
                }
                IconButton(onClick = viewModel::load) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (val state = uiState) {
                ProfileUiState.Loading -> LoadingProfile()
                is ProfileUiState.Error -> ErrorProfile(state.message, viewModel::load)
                is ProfileUiState.Content -> ProfileForm(
                    state = state,
                    onSave = viewModel::save,
                    onMessage = { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun ProfileForm(
    state: ProfileUiState.Content,
    onSave: (UserProfile) -> Unit,
    onMessage: (String) -> Unit
) {
    var nombres by remember { mutableStateOf(state.profile.nombres) }
    var apellidos by remember { mutableStateOf(state.profile.apellidos) }
    var institucion by remember { mutableStateOf(state.profile.institucion) }
    var telefono by remember { mutableStateOf(state.profile.telefono) }
    var descripcion by remember { mutableStateOf(state.profile.descripcion) }

    LaunchedEffect(state.profile) {
        nombres = state.profile.nombres
        apellidos = state.profile.apellidos
        institucion = state.profile.institucion
        telefono = state.profile.telefono
        descripcion = state.profile.descripcion
    }

    if (state.fromLocalCache) {
        AssistChip(onClick = {}, label = { Text("Mostrando datos guardados") })
        Spacer(modifier = Modifier.height(12.dp))
    }

    LaunchedEffect(state.savedMessage) {
        state.savedMessage?.let { onMessage(it) }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ReadOnlyField("Correo", state.profile.email)
            ReadOnlyField("Rol", state.profile.modo.ifBlank { "No definido" })
            ProfileField("Nombres", nombres) { nombres = it.uppercase(Locale.getDefault()) }
            ProfileField("Apellidos", apellidos) { apellidos = it.uppercase(Locale.getDefault()) }
            ProfileField("Institucion", institucion) { institucion = it.uppercase(Locale.getDefault()) }
            ProfileField(
                label = "Telefono",
                value = telefono,
                keyboardType = KeyboardType.Phone
            ) { value ->
                telefono = value.filter { it.isDigit() }.take(9)
            }
            ProfileField("Descripcion", descripcion, maxLines = 4) { descripcion = it }

            Button(
                onClick = {
                    if (telefono.length != 9) {
                        onMessage("El telefono debe tener 9 digitos.")
                        return@Button
                    }

                    onSave(
                        state.profile.copy(
                            nombres = nombres.trim(),
                            apellidos = apellidos.trim(),
                            institucion = institucion.trim(),
                            telefono = telefono.trim(),
                            descripcion = descripcion.trim()
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Guardar cambios")
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = profileTextFieldColors()
    )
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        enabled = false,
        colors = profileTextFieldColors()
    )
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DarkText,
    unfocusedTextColor = DarkText,
    disabledTextColor = DarkText.copy(alpha = 0.85f),
    focusedLabelColor = PrimaryBlue,
    unfocusedLabelColor = GrayText,
    disabledLabelColor = GrayText.copy(alpha = 0.85f),
    focusedBorderColor = PrimaryBlue,
    unfocusedBorderColor = Color(0xFF6B7280),
    disabledBorderColor = Color(0xFFD1D5DB),
    cursorColor = PrimaryBlue
)

@Composable
private fun LoadingProfile() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
    }
}

@Composable
private fun ErrorProfile(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, color = DarkText, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}
