package com.example.frontendapk.data

data class Ticket(
    val ticket_id: Int,
    val nombre_usuario: String,
    val estado: String,
    val fecha_hora_registro: String,
    val fecha_hora_atencion: String,
    val posicion: Int,
    val fila_atencion: Int
)