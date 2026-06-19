package com.dsm.miprofeenlinea.data.repository

import com.dsm.miprofeenlinea.model.Tarea
import com.google.firebase.firestore.FirebaseFirestore

class TareaRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun guardarTarea(
        tarea: Tarea,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {

        firestore.collection("tareas")
            .add(tarea)
            .addOnSuccessListener { document ->
                onSuccess(document.id)
            }
            .addOnFailureListener {
                onError(it)
            }
    }
}