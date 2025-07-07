package com.example.frontendapk.data

data class EditarPoliticasRequest(
    val permite_cancelar: Boolean,
    val tiempo_limite_cancelacion: Int,
    val maximo_reservas_diarias: Int
)