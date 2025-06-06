package com.example.frontendapk.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/registro/")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/perfil/")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfileResponse>

    @GET("api/negocios/mis_negocios/") //Antes estaba con esto ()
    fun getMisNegocios(@Header("Authorization") token: String): Call<List<Negocio>>

    @GET("api/negocios/{id}/")
    fun getNegocioPorId(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: Int
    ): Call<Negocio>

    @POST("api/negocios/")
    fun crearNegocio(
        @Header("Authorization") token: String,
        @Body negocio: NegocioRequest
    ): Call<Negocio>


    @PATCH("api/negocios/{id}/editar-parcial/")
    fun editarNegocioParcial(
        @Header("Authorization") token: String,
        @Path("id") negocioId: Int,
        @Body negocio: NegocioRequest
    ): Call<Negocio>

    @PATCH("api/negocios/{id}/ocultar/")
    fun ocultarNegocio(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Map<String, String>>

    @POST("api/filas/")
    fun crearFila(
        @Header("Authorization") token: String,
        @Body fila: FilaRequest
    ): Call<Unit>

    @GET("api/negocios/{id_negocio}/mis_filas/")
    fun getFilasPorNegocio(
        @Header("Authorization") token: String,
        @Path("id_negocio") idNegocio: Int
    ): Call<List<FilaAtencion>>

    @GET("api/filas/{id}/")
    fun getFilaPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<FilaAtencion>

    @PATCH("api/filas/{id_fila}/editar-parcial/")
    fun editarFilaParcial(
        @Header("Authorization") token: String,
        @Path("id_fila") filaId: Int,
        @Body datos: Map<String, @JvmSuppressWildcards Any>
    ): Call<Void>

    @GET("api/negocios/verificados/")
    fun getNegociosVerificados(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Call<List<Negocio>>

    @GET("api/negocios/{id}/filas_visibles/")
    fun getFilasVisibles(
        @Path("id") negocioId: Int,
        @Header("Authorization") token: String
    ): Call<List<FilaAtencion>>




}