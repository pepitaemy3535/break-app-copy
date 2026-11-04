package com.example.breakapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Usa "http://10.0.2.2:8000/" si estás en el emulador de Android Studio.
    // Si usas un celular físico en la misma red Wi-Fi, cambia "10.0.2.2" por la IP local del computador de tu compañero.
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val apiService: IaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IaApiService::class.java)
    }
}