package com.example.frontendapk.data

data class DetalleTicket(
    val ticket_id: Int,
    val nombre: String,
    val correo: String,
    val estado: String,
    val fecha_hora_registro: String,
    val fecha_hora_atencion: String,
    val posicion: Int,
    val fila_nombre: String,
    val negocio_nombre: String,
    val permitir_cancelacion: Boolean,
    val minutos_restantes_cancelacion: Int
)