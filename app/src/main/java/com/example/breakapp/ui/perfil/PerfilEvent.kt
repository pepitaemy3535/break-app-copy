package com.example.breakapp.ui.perfil

sealed interface PerfilEvent {
    // Edición de perfil
    data class OnNombreEditChange(val valor: String) : PerfilEvent
    data class OnCorreoEditChange(val valor: String) : PerfilEvent
    data class OnDireccionEditChange(val valor: String) : PerfilEvent
    object OnGuardarCambiosPerfil : PerfilEvent

    // Métodos de pago
    data class OnMetodoPagoChange(val tipo: TipoMetodoPago) : PerfilEvent
    data class OnNumeroTarjetaChange(val valor: String) : PerfilEvent
    object OnGuardarMetodoPago : PerfilEvent
    object OnDismissExitoTarjeta : PerfilEvent

    // Modales y diálogos
    data class SetMostrarDialogoFoto(val mostrar: Boolean) : PerfilEvent
    data class SetMostrarDialogoCerrarSesion(val mostrar: Boolean) : PerfilEvent
    data class SetMostrarDialogoEliminarCuenta(val mostrar: Boolean) : PerfilEvent
    object OnConfirmarCerrarSesion : PerfilEvent
    object OnConfirmarEliminarCuenta : PerfilEvent
    object OnDismissExitoGuardado : PerfilEvent

    // Configuración
    data class OnModoOscuroToggle(val activo: Boolean) : PerfilEvent
    data class OnIdiomaSelected(val idioma: String) : PerfilEvent
    data class OnNotificacionToggle(val campo: String, val activo: Boolean) : PerfilEvent
    data class OnPrivacidadToggle(val campo: String, val activo: Boolean) : PerfilEvent
}
