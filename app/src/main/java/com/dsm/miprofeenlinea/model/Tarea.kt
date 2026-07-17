package com.dsm.miprofeenlinea.model

data class Tarea(
    val id: String = "",
    val imagen: String = "",
    val estado: String = "",
    val tarifa: Double? = null,
    val docenteNombre: String = "",
    val alumnoId: String = "",
    val docenteId: String = "",
    val createdAt: Long? = null,
    val acceptedAt: Long? = null,
    val completedAt: Long? = null,
    val cancelledAt: Long? = null,
    val tarifaTimestamp: Long? = null,
    val finalizadoAt: Long? = null,
    val ratingTeacher: Double? = null,
    val ratingStudent: Double? = null
)
