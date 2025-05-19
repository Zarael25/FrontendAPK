package com.example.frontendapk.data

data class Negocio(
    val negocio_id: Int,
    val nombre: String,
    val direccion: String,
    val estado: String,
    val categoria: String,
    val doc_respaldo: String?,
    val num_referencia: String,
    val detalle: String,
    val usuario: Int
)