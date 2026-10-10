package com.example.breakapp.ui.recuperacion

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPizzaAtras
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo
import com.example.breakapp.ui.theme.TextoError

/**
 * Estructura común de las pantallas de recuperación (usuario y contraseña):
 * título, cabito, contenido, botón y pie. Las alturas salen del Figma, así que
 * el botón queda siempre en el mismo sitio.
 */
@Composable
fun RecuperacionLayout(
    @StringRes titulo: Int,
    onAtras: () -> Unit,
    textoBoton: String,
    anchoBoton: Dp,
    onBoton: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(53.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .padding(start = 34.dp)
            ) {
                Text(
                    text = stringResource(titulo),
                    style = BreakText.TituloOlvido,
                    color = Color.White,
                    modifier = Modifier.widthIn(max = 345.dp)
                )
            }
            Image(
                painter = painterResource(R.drawable.img_cabito),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 7.dp)
                    .fillMaxWidth(315f / 412f)
                    .aspectRatio(4096f / 3071f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(325.dp),
                content = contenido
            )
            BotonPrimario(
                texto = textoBoton,
                onClick = onBoton,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(anchoBoton)
            )
            Spacer(Modifier.height(39.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
        // Va al final para quedar encima del contenido y recibir los toques.
        BotonPizzaAtras(
            onClick = onAtras,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

/** Texto de instrucción (Poppins 20) con altura fija para no mover lo que va debajo. */
@Composable
fun TextoInstruccion(
    texto: String,
    alto: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
            .padding(horizontal = 33.dp)
    ) {
        Text(text = texto, style = BreakText.Instruccion, color = Color.White)
    }
}

/** Mensaje de error rojo bajo los campos. */
@Composable
fun MensajeErrorCampo(
    texto: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = texto,
        style = BreakText.MensajeError,
        color = TextoError,
        modifier = modifier
            .padding(horizontal = 37.dp)
            .semantics { liveRegion = LiveRegionMode.Polite }
    )
}
