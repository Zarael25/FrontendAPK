package com.example.frontendapk.data

data class FilaAtencion(
    val fila_atencion_id: Int,
    val nombre: String,
    val cantidad_tickets: Int,
    val visible: Boolean,
    val periodo_atencion: String,
    val apertura: String,
    val finalizacion: String,
    val numero_ticket_actual: Int,
    val negocio: Int,
)