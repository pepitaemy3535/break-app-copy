package com.example.breakapp.main.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.breakapp.R

/**
 * Familias tipográficas de las pantallas principales.
 *
 * - ImFell: título "¿Qué quieres pedir hoy?" -> res/font/im_fell_double_pica.ttf (archivo nuevo).
 * - Inter, Jersey25 y Poppins: ya están en tu proyecto de las pantallas anteriores.
 *   SI TUS ARCHIVOS SE LLAMAN DISTINTO, cambia solo los nombres R.font.xxx de ESTE archivo.
 */
object BreakFonts {

    val ImFell = FontFamily(
        Font(R.font.im_fell_double_pica, FontWeight.Normal)
    )

    val Inter = FontFamily(
        Font(R.font.inter_medium, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_bold, FontWeight.SemiBold),
        Font(R.font.inter_bold, FontWeight.Bold),
        Font(R.font.inter_black, FontWeight.ExtraBold)
    )

    val Jersey25 = FontFamily(
        Font(R.font.jersey_25, FontWeight.Normal)
    )

    val Poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium)
    )
}
