package com.example.breakapp.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUIState())
    val uiState: StateFlow<RegistroUIState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor) }
    }

    fun onUsuarioChange(valor: String) {
        _uiState.update { it.copy(usuario = valor) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor) }
    }

    fun onConfirmarContrasenaChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor) }
    }

    fun registrarUsuario() {
        val state = _uiState.value

        if (state.contrasena != state.confirmarContrasena) {
            _uiState.update { it.copy(error = ErrorRegistro.ContrasenasNoCoinciden) }
            return
        }

        _uiState.update { it.copy(cargando = true, error = ErrorRegistro.Ninguno) }

        viewModelScope.launch {
            val exito = true // Cambia a false para simular error de servidor

            if (exito) {
                _uiState.update { it.copy(cargando = false, registroExitoso = true) }
            } else {
                _uiState.update { it.copy(cargando = false, error = ErrorRegistro.ErrorServidor) }
            }
        }
    }

    fun reintentar() {
        _uiState.update { it.copy(error = ErrorRegistro.Ninguno) }
    }
}