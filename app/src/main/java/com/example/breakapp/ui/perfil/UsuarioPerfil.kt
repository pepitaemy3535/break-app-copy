package com.example.breakapp.ui.perfil

/**
 * Modelo de datos del perfil de usuario en Break-app.
 */
data class UsuarioPerfil(
    val uid: String = "",
    val nombre: String = "Katy Lozano",
    val correo: String = "correo@gmail.com",
    val universidad: String = "Universidad Jorge Tadeo Lozano - Bogotá",
    val direccion: String = "Cra 4 #22-61, Bogotá",
    val fotoUrl: String? = null
)
