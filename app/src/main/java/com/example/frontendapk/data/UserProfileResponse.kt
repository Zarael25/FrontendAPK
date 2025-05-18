package com.example.frontendapk.data

data class UserProfileResponse(
    val usuario_id: Int,
    val username: String,
    val correo: String,
    val nombre: String,
    val estado: String,
    val suscripcion: String
)