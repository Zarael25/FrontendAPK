package com.example.frontendapk.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @POST("api/usuarios/registro/")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/usuarios/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/usuarios/perfil/")
    fun getUserProfile(@Header("Authorization") token: String): Call<UserProfileResponse>


}