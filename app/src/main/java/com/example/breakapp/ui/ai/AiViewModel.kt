package com.example.breakapp.ui.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakapp.data.network.RetrofitClient
import com.example.breakapp.model.PromptRequest
import kotlinx.coroutines.launch

class AiViewModel : ViewModel() {

    // Estado para guardar la respuesta de la IA y mostrarla en la UI
    var respuestaIa by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun enviarMensaje(promptTexto: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Creamos el objeto con el mensaje
                val request = PromptRequest(prompt = promptTexto)

                // Llamamos a la API usando Retrofit
                val response = RetrofitClient.apiService.enviarConsulta(request)

                // Guardamos la respuesta que nos devolvió Python
                respuestaIa = response.response
            } catch (e: Exception) {
                respuestaIa = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}