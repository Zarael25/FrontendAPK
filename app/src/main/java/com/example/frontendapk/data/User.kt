package com.example.frontendapk.data

// Esta es la clase User que será utilizada para registrar el usuario en la API
data class User(
    val username: String,
    val password: String,
    val nombre: String,
    val correo: String
)