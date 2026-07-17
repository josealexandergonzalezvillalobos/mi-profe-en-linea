package com.dsm.miprofeenlinea.presentacion.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val localDataSource: ProfileLocalDataSource
) {

    suspend fun loadProfile(uid: String, email: String?): Pair<UserProfile, Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val document = firestore.collection("usuarios").document(uid).get().await()
                val profile = UserProfile(
                    uid = uid,
                    email = document.getString("email") ?: email.orEmpty(),
                    modo = document.getString("modo").orEmpty(),
                    nombres = document.getString("nombres").orEmpty(),
                    apellidos = document.getString("apellidos").orEmpty(),
                    institucion = document.getString("institucion").orEmpty(),
                    telefono = document.getString("telefono").orEmpty(),
                    descripcion = document.getString("descripcion").orEmpty()
                )
                localDataSource.save(profile)
                profile to false
            } catch (exception: Exception) {
                val cached = localDataSource.read(uid)
                if (cached != null) {
                    cached to true
                } else {
                    UserProfile(uid = uid, email = email.orEmpty()) to true
                }
            }
        }

    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        localDataSource.save(profile)

        val data = mapOf(
            "uid" to profile.uid,
            "email" to profile.email,
            "modo" to profile.modo,
            "nombres" to profile.nombres,
            "apellidos" to profile.apellidos,
            "institucion" to profile.institucion,
            "telefono" to profile.telefono,
            "descripcion" to profile.descripcion,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("usuarios")
            .document(profile.uid)
            .set(data, SetOptions.merge())
            .await()
    }
}
