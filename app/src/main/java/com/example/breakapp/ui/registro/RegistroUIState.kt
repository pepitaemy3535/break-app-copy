package com.example.breakapp.ui.registro

enum class ErrorRegistro {
    Ninguno,
    ContrasenasNoCoinciden,
    ErrorServidor
}

data class RegistroUIState(
    val correo: String = "",
    val usuario: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val fortalezaContrasena: String = "Fuerte",
    val error: ErrorRegistro = ErrorRegistro.Ninguno,
    val registroExitoso: Boolean = false,
    val cargando: Boolean = false
)