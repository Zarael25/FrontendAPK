package com.example.frontendapk.data

data class FilaRequest(
    val nombre: String,
    val cantidad_tickets: Int,
    val visible: Boolean,
    val periodo_atencion: String, // formato: hh:mm:ss
    val apertura: String,         // formato: hh:mm:ss
    val finalizacion: String,     // formato: hh:mm:ss
    val negocio: Int,
    val numero_ticket_actual: Int
)