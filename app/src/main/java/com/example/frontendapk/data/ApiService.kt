package com.example.frontendapk.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

import retrofit2.http.PATCH
import retrofit2.http.Path


interface ApiService {
    @POST("api/usuarios/registro/")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/usuarios/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/usuarios/perfil/")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfileResponse>

    @GET("api/negocios/negocios/mis_negocios/")
    fun getMisNegocios(@Header("Authorization") token: String): Call<List<Negocio>>

    @GET("api/negocios/negocios/{id}/")
    fun getNegocioPorId(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") id: Int
    ): Call<Negocio>

    @POST("api/negocios/negocios/")
    fun crearNegocio(
        @Header("Authorization") token: String,
        @Body negocio: NegocioRequest
    ): Call<Negocio>


    @PATCH("api/negocios/negocios/{id}/editar-parcial/")
    fun editarNegocioParcial(
        @Header("Authorization") token: String,
        @Path("id") negocioId: Int,
        @Body negocio: NegocioRequest
    ): Call<Negocio>

}