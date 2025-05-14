package com.example.frontendapk.data

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val usuario_id: Int,
    val nombre: String,
    val suscripcion: String
)