package com.example.breakapp.ui.registro

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(RegistroUIState())
    val uiState: StateFlow<RegistroUIState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, error = ErrorRegistro.Ninguno) }
    }

    fun onUsuarioChange(valor: String) {
        _uiState.update { it.copy(usuario = valor, error = ErrorRegistro.Ninguno) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = ErrorRegistro.Ninguno) }
        validarCoincidenciaContrasenas()
    }

    fun onConfirmarContrasenaChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, error = ErrorRegistro.Ninguno) }
        validarCoincidenciaContrasenas()
    }

    private fun validarCoincidenciaContrasenas() {
        val state = _uiState.value
        if (state.confirmarContrasena.isNotEmpty() && state.contrasena != state.confirmarContrasena) {
            _uiState.update { it.copy(error = ErrorRegistro.ContrasenasNoCoinciden) }
        }
    }

    fun registrarUsuario() {
        val state = _uiState.value

        // Validar que los campos no estén vacíos
        if (state.correo.isBlank() || state.usuario.isBlank() || state.contrasena.isBlank()) {
            _uiState.update { it.copy(error = ErrorRegistro.ErrorServidor) }
            return
        }

        // Validar que las contraseñas coincidan
        if (state.contrasena != state.confirmarContrasena) {
            _uiState.update { it.copy(error = ErrorRegistro.ContrasenasNoCoinciden) }
            return
        }

        _uiState.update { it.copy(cargando = true, error = ErrorRegistro.Ninguno) }

        // 1. Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(state.correo.trim(), state.contrasena)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId == null) {
                    _uiState.update { it.copy(cargando = false, error = ErrorRegistro.ErrorServidor) }
                    return@addOnSuccessListener
                }

                // 2. Guardar datos del usuario en Cloud Firestore
                val datosUsuario = hashMapOf(
                    "uid" to userId,
                    "correo" to state.correo.trim(),
                    "usuario" to state.usuario.trim(),
                    "rol" to "estudiante",
                    "fechaRegistro" to System.currentTimeMillis()
                )

                db.collection("usuarios").document(userId)
                    .set(datosUsuario)
                    .addOnSuccessListener {
                        _uiState.update { it.copy(cargando = false, registroExitoso = true) }
                    }
                    .addOnFailureListener {
                        _uiState.update { it.copy(cargando = false, error = ErrorRegistro.ErrorServidor) }
                    }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(cargando = false, error = ErrorRegistro.ErrorServidor) }
            }
    }

    fun reintentar() {
        _uiState.update { it.copy(error = ErrorRegistro.Ninguno, cargando = false) }
    }
}