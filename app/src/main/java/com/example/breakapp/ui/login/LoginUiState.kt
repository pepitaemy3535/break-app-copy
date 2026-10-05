package com.example.breakapp.ui.login

/** Qué campo falló en el último intento de ingreso. */
enum class ErrorLogin { Ninguno, Usuario, Contrasena }

data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val error: ErrorLogin = ErrorLogin.Ninguno,
    val ingresoExitoso: Boolean = false
)
