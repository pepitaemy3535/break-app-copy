package com.example.breakapp.ui.perfil

enum class TipoMetodoPago {
    TARJETA,
    EFECTIVO
}

data class PerfilUiState(
    val usuario: UsuarioPerfil = UsuarioPerfil(),
    val cargando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val mensajeError: String? = null,

    // Campos temporales para la pantalla de edición
    val nombreEdit: String = "Katy Lozano",
    val correoEdit: String = "correo@gmail.com",
    val direccionEdit: String = "Cra 4 #22-61, Bogotá",

    // Diálogos modales
    val mostrarDialogoFoto: Boolean = false,
    val mostrarDialogoCerrarSesion: Boolean = false,
    val mostrarDialogoEliminarCuenta: Boolean = false,
    val sesionCerrada: Boolean = false,
    val cuentaEliminada: Boolean = false,

    // Métodos de pago
    val metodoPagoSeleccionado: TipoMetodoPago = TipoMetodoPago.TARJETA,
    val numeroTarjeta: String = "",
    val errorTarjeta: String? = null,
    val tarjetaGuardadaExito: Boolean = false,

    // Configuración
    val modoOscuro: Boolean = true,
    val idiomaSeleccionado: String = "Español",
    val notiPromociones: Boolean = true,
    val notiEstadoPedidos: Boolean = true,
    val notiNovedadesMenu: Boolean = false,
    val notiRecordatorios: Boolean = true,
    val notiMensajesRestaurante: Boolean = false,

    // Privacidad
    val privCompartirRestaurantes: Boolean = true,
    val privHistorialVisible: Boolean = false,
    val privUbicacionSegundoPlano: Boolean = true,
    val privCompartirRepartidor: Boolean = true
)
