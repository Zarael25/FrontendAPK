package com.example.frontendapk.data

data class Usuario(
    val usuario_id: Int,
    val username: String,
    val correo: String,
    val nombre: String,
    val estado: String?,
    val suscripcion: String?,
    val tipo_usuario: String?
    // Puedes agregar más campos si necesitas
)