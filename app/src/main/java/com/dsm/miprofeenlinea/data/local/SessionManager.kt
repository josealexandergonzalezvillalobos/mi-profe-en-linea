package com.dsm.miprofeenlinea.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        "session",
        Context.MODE_PRIVATE
    )

    fun saveSession(uid: String, email: String?, modo: String) {
        preferences.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_EMAIL, email.orEmpty())
            .putString(KEY_ROLE, normalizeRole(modo))
            .apply()
    }

    fun getRole(): String? = preferences.getString(KEY_ROLE, null)

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    private fun normalizeRole(modo: String): String =
        if (modo.equals("Docente", ignoreCase = true) || modo.equals("docente", ignoreCase = true)) {
            "docente"
        } else {
            "alumno"
        }

    companion object {
        private const val KEY_UID = "uid"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
    }
}
