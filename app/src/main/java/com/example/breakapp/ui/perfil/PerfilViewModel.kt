package com.example.breakapp.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarDatosUsuario()
    }

    fun cargarDatosUsuario() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            _uiState.update { it.copy(cargando = true) }
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    val nombre = doc.getString("usuario") ?: user.displayName ?: "Katy Lozano"
                    val correo = doc.getString("correo") ?: user.email ?: "correo@gmail.com"
                    val direccion = doc.getString("direccion") ?: "Cra 4 #22-61, Bogotá"
                    val universidad = doc.getString("universidad") ?: "Universidad Jorge Tadeo Lozano - Bogotá"
                    val fotoUrl = doc.getString("fotoUrl")

                    val perfil = UsuarioPerfil(
                        uid = uid,
                        nombre = nombre,
                        correo = correo,
                        universidad = universidad,
                        direccion = direccion,
                        fotoUrl = fotoUrl
                    )

                    _uiState.update {
                        it.copy(
                            usuario = perfil,
                            nombreEdit = nombre,
                            correoEdit = correo,
                            direccionEdit = direccion,
                            cargando = false
                        )
                    }
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(cargando = false) }
                }
        }
    }

    fun onEvent(event: PerfilEvent) {
        when (event) {
            is PerfilEvent.OnNombreEditChange -> _uiState.update { it.copy(nombreEdit = event.valor) }
            is PerfilEvent.OnCorreoEditChange -> _uiState.update { it.copy(correoEdit = event.valor) }
            is PerfilEvent.OnDireccionEditChange -> _uiState.update { it.copy(direccionEdit = event.valor) }
            PerfilEvent.OnGuardarCambiosPerfil -> guardarCambiosPerfil()

            is PerfilEvent.OnMetodoPagoChange -> _uiState.update {
                it.copy(metodoPagoSeleccionado = event.tipo, errorTarjeta = null)
            }
            is PerfilEvent.OnNumeroTarjetaChange -> {
                // Formateo o filtrado de sólo dígitos (hasta 16)
                val filtrado = event.valor.filter { it.isDigit() }.take(16)
                _uiState.update { it.copy(numeroTarjeta = filtrado, errorTarjeta = null) }
            }
            PerfilEvent.OnGuardarMetodoPago -> validarYGuardarMetodoPago()
            PerfilEvent.OnDismissExitoTarjeta -> _uiState.update { it.copy(tarjetaGuardadaExito = false) }

            is PerfilEvent.SetMostrarDialogoFoto -> _uiState.update { it.copy(mostrarDialogoFoto = event.mostrar) }
            is PerfilEvent.SetMostrarDialogoCerrarSesion -> _uiState.update { it.copy(mostrarDialogoCerrarSesion = event.mostrar) }
            is PerfilEvent.SetMostrarDialogoEliminarCuenta -> _uiState.update { it.copy(mostrarDialogoEliminarCuenta = event.mostrar) }
            PerfilEvent.OnConfirmarCerrarSesion -> cerrarSesion()
            PerfilEvent.OnConfirmarEliminarCuenta -> eliminarCuenta()
            PerfilEvent.OnDismissExitoGuardado -> _uiState.update { it.copy(guardadoExitoso = false) }

            is PerfilEvent.OnModoOscuroToggle -> _uiState.update { it.copy(modoOscuro = event.activo) }
            is PerfilEvent.OnIdiomaSelected -> _uiState.update { it.copy(idiomaSeleccionado = event.idioma) }
            is PerfilEvent.OnNotificacionToggle -> actualizarNotificacion(event.campo, event.activo)
            is PerfilEvent.OnPrivacidadToggle -> actualizarPrivacidad(event.campo, event.activo)
        }
    }

    private fun guardarCambiosPerfil() {
        val state = _uiState.value
        val nombre = state.nombreEdit.trim()
        val correo = state.correoEdit.trim()
        val direccion = state.direccionEdit.trim()

        if (nombre.isBlank() || correo.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor completa todos los campos requeridos.") }
            return
        }

        _uiState.update { it.copy(cargando = true, mensajeError = null) }

        val uid = auth.currentUser?.uid
        if (uid != null) {
            val actualizacion = mapOf(
                "usuario" to nombre,
                "correo" to correo,
                "direccion" to direccion
            )
            db.collection("usuarios").document(uid)
                .update(actualizacion)
                .addOnSuccessListener {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            guardadoExitoso = true,
                            usuario = it.usuario.copy(
                                nombre = nombre,
                                correo = correo,
                                direccion = direccion
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    // Si falla Firestore (o es offline), actualizamos en memoria y mostramos éxito
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            guardadoExitoso = true,
                            usuario = it.usuario.copy(
                                nombre = nombre,
                                correo = correo,
                                direccion = direccion
                            )
                        )
                    }
                }
        } else {
            // Usuario en sesión local/demo
            _uiState.update {
                it.copy(
                    cargando = false,
                    guardadoExitoso = true,
                    usuario = it.usuario.copy(
                        nombre = nombre,
                        correo = correo,
                        direccion = direccion
                    )
                )
            }
        }
    }

    private fun validarYGuardarMetodoPago() {
        val state = _uiState.value
        if (state.metodoPagoSeleccionado == TipoMetodoPago.TARJETA) {
            if (state.numeroTarjeta.length < 16) {
                _uiState.update { it.copy(errorTarjeta = "Ingresa un número de tarjeta válido.") }
                return
            }
        }
        _uiState.update { it.copy(errorTarjeta = null, tarjetaGuardadaExito = true) }
    }

    private fun cerrarSesion() {
        auth.signOut()
        _uiState.update { it.copy(mostrarDialogoCerrarSesion = false, sesionCerrada = true) }
    }

    private fun eliminarCuenta() {
        viewModelScope.launch {
            val user = auth.currentUser
            val uid = user?.uid
            if (uid != null) {
                db.collection("usuarios").document(uid).delete()
                user.delete()
            }
            auth.signOut()
            _uiState.update { it.copy(mostrarDialogoEliminarCuenta = false, cuentaEliminada = true) }
        }
    }

    private fun actualizarNotificacion(campo: String, activo: Boolean) {
        _uiState.update {
            when (campo) {
                "promos" -> it.copy(notiPromociones = activo)
                "pedidos" -> it.copy(notiEstadoPedidos = activo)
                "novedades" -> it.copy(notiNovedadesMenu = activo)
                "recordatorios" -> it.copy(notiRecordatorios = activo)
                else -> it.copy(notiMensajesRestaurante = activo)
            }
        }
    }

    private fun actualizarPrivacidad(campo: String, activo: Boolean) {
        _uiState.update {
            when (campo) {
                "restaurantes" -> it.copy(privCompartirRestaurantes = activo)
                "historial" -> it.copy(privHistorialVisible = activo)
                "segundo_plano" -> it.copy(privUbicacionSegundoPlano = activo)
                else -> it.copy(privCompartirRepartidor = activo)
            }
        }
    }
}
