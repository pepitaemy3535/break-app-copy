package com.example.breakapp.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
        val nombreUsuario = actual.usuario.trim()
        val contrasena = actual.contrasena

        // Validaciones locales antes de enviar a Firebase
        if (nombreUsuario.isEmpty()) {
            _uiState.update { it.copy(error = ErrorLogin.Usuario) }
            return
        }

        if (contrasena.isEmpty()) {
            _uiState.update { it.copy(error = ErrorLogin.Contrasena) }
            return
        }

        // 1. Buscamos en Cloud Firestore el correo asociado al nombre de usuario ingresado
        db.collection("usuarios")
            .whereEqualTo("usuario", nombreUsuario)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documento = documents.documents[0]
                    val correoAsociado = documento.getString("correo") ?: ""

                    if (correoAsociado.isNotEmpty()) {
                        // 2. Autenticamos en Firebase Auth usando el correo real encontrado y la contraseña
                        auth.signInWithEmailAndPassword(correoAsociado, contrasena)
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
                    } else {
                        _uiState.update { it.copy(error = ErrorLogin.Usuario) }
                    }
                } else {
                    // Si el usuario no existe en Firestore
                    _uiState.update { it.copy(error = ErrorLogin.Usuario) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(error = ErrorLogin.Contrasena) }
            }
    }

    fun ingresoConsumido() {
        _uiState.update { it.copy(ingresoExitoso = false) }
    }
}