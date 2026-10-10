package com.example.breakapp.ui.olvidocontrasena

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OlvidoContrasenaViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(OlvidoContrasenaUiState())
    val uiState: StateFlow<OlvidoContrasenaUiState> = _uiState.asStateFlow()

    // PIN aleatorio de 6 dígitos generado
    private var codigoGenerado: String = ""

    // ---- Paso 1: correo ----
    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, correoInvalido = false) }
    }

    fun onCorreoSiguiente() {
        val correo = _uiState.value.correo.trim()
        if (correo.isEmpty()) {
            _uiState.update { it.copy(correoInvalido = true) }
            return
        }

        // Consultamos en Cloud Firestore si el correo existe
        db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Generamos PIN aleatorio de 6 dígitos
                    codigoGenerado = (100000..999999).random().toString()
                    Log.d("OlvidoContrasena", "Código PIN generado: $codigoGenerado")

                    _uiState.update {
                        it.copy(paso = PasoContrasena.Codigo, correoInvalido = false)
                    }
                } else {
                    _uiState.update { it.copy(correoInvalido = true) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(correoInvalido = true) }
            }
    }

    fun reintentarCorreo() {
        _uiState.update { it.copy(correo = "", correoInvalido = false) }
    }

    // ---- Paso 2: código ----
    fun onCodigoChange(valor: String) {
        val soloDigitos = valor.filter { it.isDigit() }.take(LONGITUD_CODIGO)
        _uiState.update { it.copy(codigo = soloDigitos, codigoInvalido = false) }
    }

    fun onCodigoSiguiente() {
        val actual = _uiState.value
        if (actual.codigo == codigoGenerado && actual.codigo.isNotEmpty()) {
            _uiState.update {
                it.copy(paso = PasoContrasena.NuevaContrasena, codigoInvalido = false)
            }
        } else {
            _uiState.update { it.copy(codigoInvalido = true) }
        }
    }

    fun onReenviarCodigo() {
        codigoGenerado = (100000..999999).random().toString()
        Log.d("OlvidoContrasena", "Nuevo código reenviado: $codigoGenerado")
        _uiState.update { it.copy(codigo = "", codigoInvalido = false) }
    }

    // ---- Paso 3: nueva contraseña ----
    fun onNuevaContrasenaChange(valor: String) {
        _uiState.update { it.copy(nuevaContrasena = valor, error = ErrorContrasena.Ninguno) }
    }

    fun onConfirmarContrasenaChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor, error = ErrorContrasena.Ninguno) }
    }

    fun onGuardar() {
        val actual = _uiState.value
        val error = when {
            actual.nuevaContrasena != actual.confirmarContrasena -> ErrorContrasena.NoCoinciden
            !ReglasContrasena.cumpleMinimos(actual.nuevaContrasena) -> ErrorContrasena.Debil
            else -> ErrorContrasena.Ninguno
        }

        if (error == ErrorContrasena.Ninguno) {
            val nuevaClave = actual.nuevaContrasena.trim()
            val usuarioActual = auth.currentUser

            if (usuarioActual != null) {
                usuarioActual.updatePassword(nuevaClave)
                    .addOnCompleteListener {
                        _uiState.update {
                            it.copy(
                                paso = PasoContrasena.Exito,
                                nuevaContrasena = "",
                                confirmarContrasena = "",
                                error = ErrorContrasena.Ninguno
                            )
                        }
                    }
            } else {
                // Envía el correo de restablecimiento oficial y pasa exitosamente
                auth.sendPasswordResetEmail(actual.correo.trim())
                _uiState.update {
                    it.copy(
                        paso = PasoContrasena.Exito,
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        error = ErrorContrasena.Ninguno
                    )
                }
            }
        } else {
            _uiState.update { it.copy(error = error) }
        }
    }

    /** "Intentar de nuevo" tras un error */
    fun reintentarContrasenas() {
        _uiState.update {
            it.copy(nuevaContrasena = "", confirmarContrasena = "", error = ErrorContrasena.Ninguno)
        }
    }

    /** Retroceder paso en el flujo */
    fun retrocederPaso(): Boolean {
        return when (_uiState.value.paso) {
            PasoContrasena.Correo, PasoContrasena.Exito -> false
            PasoContrasena.Codigo -> {
                _uiState.update { it.copy(paso = PasoContrasena.Correo, codigo = "", codigoInvalido = false) }
                true
            }
            PasoContrasena.NuevaContrasena -> {
                _uiState.update {
                    it.copy(
                        paso = PasoContrasena.Codigo,
                        nuevaContrasena = "",
                        confirmarContrasena = "",
                        error = ErrorContrasena.Ninguno
                    )
                }
                true
            }
        }
    }

    private companion object {
        const val LONGITUD_CODIGO = 6
    }
}