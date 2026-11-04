package com.example.breakapp.data.network

import com.example.breakapp.model.IaResponse
import com.example.breakapp.model.PromptRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface IaApiService {
    @POST("/api/chat")
    suspend fun enviarConsulta(@Body request: PromptRequest): IaResponse
}