package com.example.frontendapk.data
import com.google.gson.annotations.SerializedName
data class LoginResponse(
    @SerializedName("access")
    val accessToken: String,

    @SerializedName("refresh")
    val refreshToken: String,

    @SerializedName("usuario_id")
    val usuarioId: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("suscripcion")
    val suscripcion: String
)