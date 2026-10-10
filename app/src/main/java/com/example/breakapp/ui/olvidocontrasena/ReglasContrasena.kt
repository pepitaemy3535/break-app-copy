package com.example.breakapp.ui.olvidocontrasena

import com.example.breakapp.ui.components.NivelFortaleza

/**
 * Reglas de la contraseña. PROPUESTA mía: el diseño solo muestra el nivel "Fuerte".
 * - Mínimo para poder guardar: 8 caracteres, mayúscula, minúscula y número.
 * - Fuerte: cumple el mínimo y además tiene un símbolo o 12 caracteres o más.
 */
object ReglasContrasena {
    private const val LONGITUD_MINIMA = 8
    private const val LONGITUD_FUERTE = 12

    fun cumpleMinimos(contrasena: String): Boolean =
        contrasena.length >= LONGITUD_MINIMA &&
            contrasena.any { it.isLowerCase() } &&
            contrasena.any { it.isUpperCase() } &&
            contrasena.any { it.isDigit() }

    fun nivel(contrasena: String): NivelFortaleza = when {
        !cumpleMinimos(contrasena) -> NivelFortaleza.Debil
        contrasena.any { !it.isLetterOrDigit() } || contrasena.length >= LONGITUD_FUERTE -> NivelFortaleza.Fuerte
        else -> NivelFortaleza.Media
    }
}
