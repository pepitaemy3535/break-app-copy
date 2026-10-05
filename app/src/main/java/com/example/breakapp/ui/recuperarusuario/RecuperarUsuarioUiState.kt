package com.example.breakapp.ui.recuperarusuario

/** Pasos del flujo "¿Olvidaste tu usuario?" (Forgott user 1 a 6 del Figma). */
enum class PasoRecuperacion { Correo, Codigo, NuevoUsuario, Exito }

data class RecuperarUsuarioUiState(
    val paso: PasoRecuperacion = PasoRecuperacion.Correo,
    val correo: String = "",
    val codigo: String = "",
    val nuevoUsuario: String = "",
    val confirmarUsuario: String = "",
    /** Pantalla "Forgott user 1": el correo no corresponde. */
    val errorCorreo: Boolean = false,
    /** Pantalla "Forgott user 5": los usuarios no coinciden. */
    val errorUsuarios: Boolean = false
)
