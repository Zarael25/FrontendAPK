package com.example.frontendapk.data
import com.google.gson.annotations.SerializedName

data class AdminLoginResponse(
    val refresh: String,
    val access: String,
    @SerializedName("usuario_id") val usuarioId: Int,
    val nombre: String,
    val suscripcion: String,
    @SerializedName("tipo_usuario") val tipoUsuario: String
)