package com.example.breakapp.ui.recuperacion

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPizzaAtras
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo

/** Última pantalla de ambos flujos: confirmación con el check verde. */
@Composable
fun RecuperacionExitoScreen(
    @StringRes titulo: Int,
    @StringRes mensaje: Int,
    onIrAIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(57.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = stringResource(titulo),
                    style = BreakText.TituloExito,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(57.dp))
            Image(
                painter = painterResource(R.drawable.img_check),
                contentDescription = stringResource(R.string.exito_check_descripcion),
                modifier = Modifier.size(300.dp)
            )
            Text(
                text = stringResource(mensaje),
                style = BreakText.Cuerpo,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 282.dp)
            )
            Spacer(Modifier.height(161.dp))
            BotonPrimario(
                texto = stringResource(R.string.exito_ir_a_login),
                onClick = onIrAIniciarSesion,
                modifier = Modifier.width(304.dp)
            )
            Spacer(Modifier.height(35.dp))
            PieUniversidad()
            Spacer(Modifier.height(16.dp))
        }
        BotonPizzaAtras(
            onClick = onIrAIniciarSesion,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun ExitoUsuarioPreview() {
    BreakAppTheme {
        RecuperacionExitoScreen(R.string.exito_titulo, R.string.exito_mensaje, {})
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun ExitoContrasenaPreview() {
    BreakAppTheme {
        RecuperacionExitoScreen(R.string.exito_contrasena_titulo, R.string.exito_contrasena_mensaje, {})
    }
}
