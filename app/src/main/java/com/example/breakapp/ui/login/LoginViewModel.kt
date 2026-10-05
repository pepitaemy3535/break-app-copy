package com.example.breakapp.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsuarioChange(valor: String) {
        _uiState.update { it.copy(usuario = valor, error = ErrorLogin.Ninguno) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = ErrorLogin.Ninguno) }
    }

    fun onIngresar() {
        val actual = _uiState.value
        val usuario = actual.usuario.trim()
        val error = when {
            usuario.isEmpty() -> ErrorLogin.Usuario
            actual.contrasena.isEmpty() -> ErrorLogin.Contrasena
            else -> verificarTemporal(usuario, actual.contrasena)
        }
        _uiState.update { it.copy(error = error, ingresoExitoso = error == ErrorLogin.Ninguno) }
    }

    fun ingresoConsumido() {
        _uiState.update { it.copy(ingresoExitoso = false) }
    }

    // TEMPORAL: solo sirve para ver el flujo de error en pantalla.
    // Se elimina cuando conectemos Firebase Auth (paso de autenticación).
    private fun verificarTemporal(usuario: String, contrasena: String): ErrorLogin = when {
        usuario != USUARIO_TEMPORAL -> ErrorLogin.Usuario
        contrasena != CONTRASENA_TEMPORAL -> ErrorLogin.Contrasena
        else -> ErrorLogin.Ninguno
    }

    private companion object {
        const val USUARIO_TEMPORAL = "break"
        const val CONTRASENA_TEMPORAL = "Break123"
    }
}
