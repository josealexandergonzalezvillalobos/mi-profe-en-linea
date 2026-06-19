package com.dsm.miprofeenlinea.model

data class Tarea(
    val id: String = "",
    val imagen: String = "",
    val estado: String = "",
    val tarifa: Double? = null,
    val docenteNombre: String = "",
    val tarifaTimestamp: Long? = null
)