package com.example.breakapp.ui.olvidocontrasena

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OlvidoContrasenaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OlvidoContrasenaUiState())
    val uiState: StateFlow<OlvidoContrasenaUiState> = _uiState.asStateFlow()

    // ---- Paso 1: correo ----
    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor) }
    }

    fun onCorreoSiguiente() {
        val correo = _uiState.value.correo.trim()
        // TEMPORAL: se reemplaza por la consulta real cuando conectemos Firebase.
        if (correo.equals(CORREO_TEMPORAL, ignoreCase = true)) {
            _uiState.update { it.copy(paso = PasoContrasena.Codigo, correoInvalido = false) }
        } else {
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
        // TEMPORAL: acepta cualquier código de 6 dígitos.
        if (_uiState.value.codigo.length == LONGITUD_CODIGO) {
            _uiState.update { it.copy(paso = PasoContrasena.NuevaContrasena, codigoInvalido = false) }
        } else {
            _uiState.update { it.copy(codigoInvalido = true) }
        }
    }

    // ---- Paso 3: nueva contraseña ----
    fun onNuevaContrasenaChange(valor: String) {
        _uiState.update { it.copy(nuevaContrasena = valor) }
    }

    fun onConfirmarContrasenaChange(valor: String) {
        _uiState.update { it.copy(confirmarContrasena = valor) }
    }

    fun onGuardar() {
        val actual = _uiState.value
        val error = when {
            actual.nuevaContrasena != actual.confirmarContrasena -> ErrorContrasena.NoCoinciden
            !ReglasContrasena.cumpleMinimos(actual.nuevaContrasena) -> ErrorContrasena.Debil
            else -> ErrorContrasena.Ninguno
        }
        if (error == ErrorContrasena.Ninguno) {
            // TEMPORAL: todavía no se guarda la contraseña en ningún lado.
            _uiState.update {
                it.copy(paso = PasoContrasena.Exito, nuevaContrasena = "", confirmarContrasena = "", error = error)
            }
        } else {
            _uiState.update { it.copy(error = error) }
        }
    }

    /** "Intentar de nuevo" tras un error: campos vacíos otra vez. */
    fun reintentarContrasenas() {
        _uiState.update {
            it.copy(nuevaContrasena = "", confirmarContrasena = "", error = ErrorContrasena.Ninguno)
        }
    }

    /** Devuelve true si retrocedió un paso dentro del flujo; false si hay que salir de él. */
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
        const val CORREO_TEMPORAL = "break@gmail.com"
        const val LONGITUD_CODIGO = 6
    }
}
