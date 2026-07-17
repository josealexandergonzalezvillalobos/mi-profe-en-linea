package com.dsm.miprofeenlinea.presentacion.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val uid: String,
    private val email: String?,
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        if (uid.isBlank()) {
            _uiState.value = ProfileUiState.Error("No se encontro una sesion activa.")
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            runCatching { repository.loadProfile(uid, email) }
                .onSuccess { (profile, fromCache) ->
                    _uiState.value = ProfileUiState.Content(profile, fromLocalCache = fromCache)
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Error("No se pudo cargar el perfil.")
                }
        }
    }

    fun save(profile: UserProfile) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            runCatching { repository.saveProfile(profile) }
                .onSuccess {
                    _uiState.value = ProfileUiState.Content(
                        profile = profile,
                        savedMessage = "Perfil actualizado correctamente."
                    )
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Content(
                        profile = profile,
                        fromLocalCache = true,
                        savedMessage = "Sin conexion: no se pudo sincronizar con Firestore."
                    )
                }
        }
    }
}

class ProfileViewModelFactory(
    private val context: Context,
    private val uid: String,
    private val email: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val localDataSource = ProfileLocalDataSource(context)
        val repository = ProfileRepository(localDataSource = localDataSource)
        return ProfileViewModel(uid, email, repository) as T
    }
}
