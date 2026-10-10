package com.example.breakapp.ui.olvidocontrasena



enum class PasoContrasena { Correo, Codigo, NuevaContrasena, Exito }

/** Por qué falló el intento de guardar la nueva contraseña. */
enum class ErrorContrasena { Ninguno, NoCoinciden, Debil }

data class OlvidoContrasenaUiState(
    val paso: PasoContrasena = PasoContrasena.Correo,
    val correo: String = "",
    val correoInvalido: Boolean = false,
    val codigo: String = "",
    val codigoInvalido: Boolean = false,
    val nuevaContrasena: String = "",
    val confirmarContrasena: String = "",
    val error: ErrorContrasena = ErrorContrasena.Ninguno
)
