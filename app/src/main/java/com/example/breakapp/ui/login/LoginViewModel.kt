package com.example.breakapp.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
        val correo = actual.usuario.trim()
        val contrasena = actual.contrasena

        // Validaciones locales antes de enviar a Firebase
        if (correo.isEmpty()) {
            _uiState.update { it.copy(error = ErrorLogin.Usuario) }
            return
        }

        if (contrasena.isEmpty()) {
            _uiState.update { it.copy(error = ErrorLogin.Contrasena) }
            return
        }

        // Autenticación con Firebase Auth
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        ingresoExitoso = true,
                        error = ErrorLogin.Ninguno
                    )
                }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(
                        ingresoExitoso = false,
                        error = ErrorLogin.Contrasena
                    )
                }
            }
    }

    fun ingresoConsumido() {
        _uiState.update { it.copy(ingresoExitoso = false) }
    }
}