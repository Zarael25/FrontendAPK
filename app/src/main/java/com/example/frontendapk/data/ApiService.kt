package com.example.frontendapk.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/usuarios/registro/")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/usuarios/login/")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

}