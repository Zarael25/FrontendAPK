package com.example.frontendapk.data

data class TicketGenerado(
    val ticket_id: Int,
    val estado: String,
    val fecha_hora_registro: String,
    val fecha_hora_atencion: String,
    val posicion: Int,
    val fila_atencion: Int
)