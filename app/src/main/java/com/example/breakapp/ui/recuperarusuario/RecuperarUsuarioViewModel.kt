package com.example.breakapp.ui.recuperarusuario

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarUsuarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperarUsuarioUiState())
    val uiState: StateFlow<RecuperarUsuarioUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor) }
    }

    fun onCodigoChange(valor: String) {
        // Solo dígitos y máximo 6.
        _uiState.update { it.copy(codigo = valor.filter { c -> c.isDigit() }.take(LONGITUD_CODIGO)) }
    }

    fun onNuevoUsuarioChange(valor: String) {
        _uiState.update { it.copy(nuevoUsuario = valor) }
    }

    fun onConfirmarUsuarioChange(valor: String) {
        _uiState.update { it.copy(confirmarUsuario = valor) }
    }

    fun onSiguiente() {
        val actual = _uiState.value
        when (actual.paso) {
            PasoRecuperacion.Correo -> {
                val correo = actual.correo.trim()
                if (correo.isEmpty()) return
                // TEMPORAL: solo para ver el flujo de error. Se reemplaza al conectar Firebase.
                if (correo.equals(CORREO_TEMPORAL, ignoreCase = true)) {
                    _uiState.update { it.copy(paso = PasoRecuperacion.Codigo, errorCorreo = false) }
                } else {
                    _uiState.update { it.copy(errorCorreo = true) }
                }
            }

            PasoRecuperacion.Codigo -> {
                // TEMPORAL: cualquier código de 6 dígitos avanza. Se reemplaza al conectar Firebase.
                if (actual.codigo.length == LONGITUD_CODIGO) {
                    _uiState.update { it.copy(paso = PasoRecuperacion.NuevoUsuario) }
                }
            }

            PasoRecuperacion.NuevoUsuario -> {
                val nuevo = actual.nuevoUsuario.trim()
                val confirmar = actual.confirmarUsuario.trim()
                if (nuevo.isEmpty() || confirmar.isEmpty()) return
                if (nuevo == confirmar) {
                    _uiState.update { it.copy(paso = PasoRecuperacion.Exito, errorUsuarios = false) }
                } else {
                    _uiState.update { it.copy(errorUsuarios = true) }
                }
            }

            PasoRecuperacion.Exito -> Unit
        }
    }

    /** Botón "Intentar de nuevo": vuelve a la pantalla normal del mismo paso. */
    fun onIntentarDeNuevo() {
        _uiState.update {
            when (it.paso) {
                PasoRecuperacion.Correo -> it.copy(correo = "", errorCorreo = false)
                PasoRecuperacion.NuevoUsuario ->
                    it.copy(nuevoUsuario = "", confirmarUsuario = "", errorUsuarios = false)
                else -> it
            }
        }
    }

    fun onReenviarCodigo() {
        // TODO: reenviar el código cuando se conecte Firebase.
    }

    /** Retrocede un paso del flujo (la X de pizza). Salir del flujo lo decide la ruta. */
    fun onVolver() {
        _uiState.update {
            when (it.paso) {
                PasoRecuperacion.Codigo -> it.copy(paso = PasoRecuperacion.Correo, codigo = "")
                PasoRecuperacion.NuevoUsuario -> it.copy(
                    paso = PasoRecuperacion.Codigo,
                    nuevoUsuario = "",
                    confirmarUsuario = "",
                    errorUsuarios = false
                )
                else -> it
            }
        }
    }

    private companion object {
        const val LONGITUD_CODIGO = 6
        const val CORREO_TEMPORAL = "break@gmail.com"
    }
}
