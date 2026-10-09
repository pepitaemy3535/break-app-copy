package com.example.breakapp.ui.recuperarusuario

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarUsuarioViewModel : ViewModel() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(RecuperarUsuarioUiState())
    val uiState: StateFlow<RecuperarUsuarioUiState> = _uiState.asStateFlow()

    // ID del documento encontrado en Firestore
    private var usuarioDocId: String? = null

    // PIN de 6 dígitos generado dinámicamente para la validación
    private var codigoGenerado: String = ""

    fun onCorreoChange(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = false) }
    }

    fun onCodigoChange(valor: String) {
        // Solo dígitos y máximo 6
        _uiState.update { it.copy(codigo = valor.filter { c -> c.isDigit() }.take(LONGITUD_CODIGO)) }
    }

    fun onNuevoUsuarioChange(valor: String) {
        _uiState.update { it.copy(nuevoUsuario = valor, errorUsuarios = false) }
    }

    fun onConfirmarUsuarioChange(valor: String) {
        _uiState.update { it.copy(confirmarUsuario = valor, errorUsuarios = false) }
    }

    fun onSiguiente() {
        val actual = _uiState.value
        when (actual.paso) {
            PasoRecuperacion.Correo -> validarCorreoEnFirestore(actual.correo.trim())

            PasoRecuperacion.Codigo -> {
                // Compara el código ingresado por el usuario con el código generado
                if (actual.codigo == codigoGenerado && actual.codigo.isNotEmpty()) {
                    _uiState.update { it.copy(paso = PasoRecuperacion.NuevoUsuario) }
                } else {
                    // Si el código no coincide, borra el campo para reintentar
                    _uiState.update { it.copy(codigo = "") }
                }
            }

            PasoRecuperacion.NuevoUsuario -> actualizarUsuarioEnFirestore()

            PasoRecuperacion.Exito -> Unit
        }
    }

    /** Consulta en Firestore si el correo ingresado existe en la colección "usuarios" */
    private fun validarCorreoEnFirestore(correo: String) {
        if (correo.isEmpty()) {
            _uiState.update { it.copy(errorCorreo = true) }
            return
        }

        db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    usuarioDocId = querySnapshot.documents[0].id

                    // Genera un PIN aleatorio de 6 dígitos
                    codigoGenerado = (100000..999999).random().toString()
                    Log.d("RecuperarUsuario", "Código de verificación generado: $codigoGenerado")

                    _uiState.update { it.copy(paso = PasoRecuperacion.Codigo, errorCorreo = false) }
                } else {
                    _uiState.update { it.copy(errorCorreo = true) }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(errorCorreo = true) }
            }
    }

    /** Actualiza el campo "usuario" en Cloud Firestore */
    private fun actualizarUsuarioEnFirestore() {
        val actual = _uiState.value
        val nuevo = actual.nuevoUsuario.trim()
        val confirmar = actual.confirmarUsuario.trim()

        if (nuevo.isEmpty() || confirmar.isEmpty() || nuevo != confirmar) {
            _uiState.update { it.copy(errorUsuarios = true) }
            return
        }

        val docId = usuarioDocId
        if (docId != null) {
            db.collection("usuarios").document(docId)
                .update("usuario", nuevo)
                .addOnSuccessListener {
                    _uiState.update { it.copy(paso = PasoRecuperacion.Exito, errorUsuarios = false) }
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(errorUsuarios = true) }
                }
        } else {
            _uiState.update { it.copy(errorUsuarios = true) }
        }
    }

    /** Botón "Intentar de nuevo": vuelve a la pantalla normal del mismo paso */
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

    /** Regenera el código de verificación de 6 dígitos */
    fun onReenviarCodigo() {
        codigoGenerado = (100000..999999).random().toString()
        Log.d("RecuperarUsuario", "Nuevo código reenviado: $codigoGenerado")
        _uiState.update { it.copy(codigo = "") }
    }

    /** Retrocede un paso del flujo (la X de pizza) */
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
    }
}