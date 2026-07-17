package com.dsm.miprofeenlinea.presentacion.profile

import android.content.Context

class ProfileLocalDataSource(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        "profile_cache",
        Context.MODE_PRIVATE
    )

    fun save(profile: UserProfile) {
        preferences.edit()
            .putString("${profile.uid}_uid", profile.uid)
            .putString("${profile.uid}_email", profile.email)
            .putString("${profile.uid}_modo", profile.modo)
            .putString("${profile.uid}_nombres", profile.nombres)
            .putString("${profile.uid}_apellidos", profile.apellidos)
            .putString("${profile.uid}_institucion", profile.institucion)
            .putString("${profile.uid}_telefono", profile.telefono)
            .putString("${profile.uid}_descripcion", profile.descripcion)
            .apply()
    }

    fun read(uid: String): UserProfile? {
        if (uid.isBlank()) return null
        val cachedUid = preferences.getString("${uid}_uid", null) ?: return null
        return UserProfile(
            uid = cachedUid,
            email = preferences.getString("${uid}_email", "").orEmpty(),
            modo = preferences.getString("${uid}_modo", "").orEmpty(),
            nombres = preferences.getString("${uid}_nombres", "").orEmpty(),
            apellidos = preferences.getString("${uid}_apellidos", "").orEmpty(),
            institucion = preferences.getString("${uid}_institucion", "").orEmpty(),
            telefono = preferences.getString("${uid}_telefono", "").orEmpty(),
            descripcion = preferences.getString("${uid}_descripcion", "").orEmpty()
        )
    }
}
