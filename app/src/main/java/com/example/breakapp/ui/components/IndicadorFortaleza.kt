package com.example.breakapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.FortalezaApagado
import com.example.breakapp.ui.theme.FortalezaDebil
import com.example.breakapp.ui.theme.FortalezaFuerte
import com.example.breakapp.ui.theme.FortalezaMedia

enum class NivelFortaleza { Debil, Media, Fuerte }

/** Tres rayitas y una palabra (Débil / Media / Fuerte) que van dentro del campo de contraseña. */
@Composable
fun IndicadorFortaleza(
    nivel: NivelFortaleza,
    modifier: Modifier = Modifier
) {
    val encendidos = when (nivel) {
        NivelFortaleza.Debil -> 1
        NivelFortaleza.Media -> 2
        NivelFortaleza.Fuerte -> 3
    }
    val colores = listOf(FortalezaDebil, FortalezaMedia, FortalezaFuerte)
    val texto = stringResource(
        when (nivel) {
            NivelFortaleza.Debil -> R.string.fortaleza_debil
            NivelFortaleza.Media -> R.string.fortaleza_media
            NivelFortaleza.Fuerte -> R.string.fortaleza_fuerte
        }
    )
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        colores.forEachIndexed { indice, color ->
            if (indice > 0) Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(if (indice < encendidos) color else FortalezaApagado)
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = texto,
            style = BreakText.MensajeError.copy(fontSize = 10.sp),
            color = colores[encendidos - 1]
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IndicadorFortalezaPreview() {
    BreakAppTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IndicadorFortaleza(NivelFortaleza.Debil)
            IndicadorFortaleza(NivelFortaleza.Media)
            IndicadorFortaleza(NivelFortaleza.Fuerte)
        }
    }
}
