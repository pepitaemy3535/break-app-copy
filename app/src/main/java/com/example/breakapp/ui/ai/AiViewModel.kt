package com.example.breakapp.ui.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakapp.data.ai.AiRepository
import com.example.breakapp.data.ai.AiSource
import com.example.breakapp.data.ai.DefaultAiRepository
import com.example.breakapp.model.AiComboDto
import com.example.breakapp.model.AiRecommendationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiUiState(
    val isLoading: Boolean = false,
    val rawText: String = "",
    val banner: String? = null,
    val clusterLabel: String? = null,
    val recommendations: List<AiRecommendationDto> = emptyList(),
    val suggestedCombo: AiComboDto? = null,
    val activeSource: AiSource? = null,
    val isFallback: Boolean = false,
    val lastPrompt: String = ""
)

class AiViewModel(
    private val repository: AiRepository = DefaultAiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    // Propiedades de compatibilidad directa
    var respuestaIa by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun enviarMensaje(promptTexto: String, userId: String = "usr_estudiante_01") {
        val clean = promptTexto.trim()
        if (clean.isBlank()) return

        isLoading = true
        _uiState.update { it.copy(isLoading = true, lastPrompt = clean) }

        viewModelScope.launch {
            try {
                val result = repository.getRecommendations(clean, userId)
                val resp = result.response

                respuestaIa = resp.response

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rawText = resp.response,
                        banner = resp.banner,
                        clusterLabel = resp.clusterLabel,
                        recommendations = resp.recommendations ?: emptyList(),
                        suggestedCombo = resp.suggestedCombo,
                        activeSource = result.source,
                        isFallback = result.isFallback,
                        lastPrompt = clean
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Error al procesar consulta: ${e.localizedMessage ?: "Error desconocido"}"
                respuestaIa = errorMsg
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rawText = errorMsg,
                        recommendations = emptyList(),
                        suggestedCombo = null
                    )
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun retry() {
        val last = _uiState.value.lastPrompt
        if (last.isNotBlank()) {
            enviarMensaje(last)
        }
    }
}
