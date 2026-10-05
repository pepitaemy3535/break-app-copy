package com.example.breakapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Break usa un único esquema (fondo morado), sin color dinámico ni modo claro.
private val EsquemaBreak = darkColorScheme(
    primary = Ambar,
    onPrimary = Color.White,
    background = Fondo,
    onBackground = Color.White,
    surface = Fondo,
    onSurface = Color.White
)

@Composable
fun BreakAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaBreak, content = content)
}
