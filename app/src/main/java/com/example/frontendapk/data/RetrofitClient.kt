package com.example.frontendapk.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    //private const val BASE_URL = "http://10.0.2.2:8000/" // Cambia esta URL si es necesario
    //private const val BASE_URL = "http://192.168.1.101:8000/"
    private const val BASE_URL = "https://backend-fichasvirtuales.onrender.com/"
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}