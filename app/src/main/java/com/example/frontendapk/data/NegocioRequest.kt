package com.example.frontendapk.data

data class NegocioRequest(
    val nombre: String,
    val direccion: String,
    val categoria: String,
    val num_referencia: String,
    val detalle: String,
    val doc_respaldo: ByteArray? = null
)