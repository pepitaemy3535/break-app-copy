package com.example.breakapp.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.breakapp.R

// Fuentes en app/src/main/res/font (ver instrucciones del paso).
val Inter = FontFamily(
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_black, FontWeight.Black)
)
val Jersey25 = FontFamily(Font(R.font.jersey_25, FontWeight.Normal))
val Poppins = FontFamily(Font(R.font.poppins_medium, FontWeight.Medium))

/** Estilos de texto del diseño de Break. El color se define en cada uso. */
object BreakText {
    val Marca = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Black, fontSize = 48.sp, letterSpacing = (-1).sp)
    val Eslogan = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 24.sp)
    val TituloPantalla = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 33.sp)
    val TituloLogin = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 48.sp, letterSpacing = (-1).sp)
    val Cuerpo = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 21.sp)
    val Enlace = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    val Pie = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    val Etiqueta = TextStyle(fontFamily = Jersey25, fontWeight = FontWeight.Normal, fontSize = 32.sp)
    val Boton = TextStyle(fontFamily = Jersey25, fontWeight = FontWeight.Normal, fontSize = 32.sp)
    val Campo = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 14.sp)

    // De tu recuperación de usuario (los dejo igual)
    val TituloRecuperacion = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 44.sp, letterSpacing = (-1).sp)
    val TextoGuia = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 20.sp)

    // Compartidos (cada uno una sola vez)
    val TituloExito = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 40.sp, letterSpacing = (-1).sp)
    val MensajeError = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp)

    // Del flujo "olvidé mi contraseña" / "olvidé mi usuario" nuevo
    val TituloOlvido = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 44.sp, letterSpacing = (-1).sp)
    val Instruccion = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium, fontSize = 20.sp)
}