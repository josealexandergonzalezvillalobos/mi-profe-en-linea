package com.dsm.miprofeenlinea.presentacion.profile

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val modo: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val institucion: String = "",
    val telefono: String = "",
    val descripcion: String = ""
)

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Content(
        val profile: UserProfile,
        val fromLocalCache: Boolean = false,
        val savedMessage: String? = null
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}
