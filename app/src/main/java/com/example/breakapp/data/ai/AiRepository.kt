package com.example.breakapp.data.ai

import android.util.Log
import com.example.breakapp.data.network.RetrofitClient
import com.example.breakapp.model.IaResponse
import com.example.breakapp.model.PromptRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AiSource(val label: String) {
    REMOTE_API("Servidor Python Break-AI"),
    LOCAL_ENGINE("Agente On-Device (Modo Autónomo)")
}

data class AiQueryResult(
    val response: IaResponse,
    val source: AiSource,
    val isFallback: Boolean = false
)

interface AiRepository {
    suspend fun getRecommendations(prompt: String, userId: String = "usr_estudiante_01"): AiQueryResult
}

object DefaultAiRepository : AiRepository {

    private const val TAG = "BreakAiRepository"

    override suspend fun getRecommendations(prompt: String, userId: String): AiQueryResult = withContext(Dispatchers.IO) {
        val cleanPrompt = prompt.trim()

        try {
            Log.d(TAG, "Consultando servidor de IA remoto en RetrofitClient...")
            val req = PromptRequest(prompt = cleanPrompt, userId = userId)
            val remoteResp = RetrofitClient.apiService.enviarConsulta(req)
            Log.d(TAG, "Respuesta exitosa recibida del servidor remoto.")
            return@withContext AiQueryResult(
                response = remoteResp,
                source = AiSource.REMOTE_API,
                isFallback = false
            )
        } catch (e: Exception) {
            Log.w(TAG, "Servidor remoto no disponible (${e.localizedMessage}). Activando agente local BreakAiLocalEngine...")
            val localResp = BreakAiLocalEngine.decide(prompt = cleanPrompt, userId = userId)
            return@withContext AiQueryResult(
                response = localResp,
                source = AiSource.LOCAL_ENGINE,
                isFallback = true
            )
        }
    }
}
