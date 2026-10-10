package com.example.breakapp.ui.recuperacion

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.CampoBreak
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValidoBrillante
import com.example.breakapp.ui.theme.BreakAppTheme

/** Primer paso de ambos flujos: confirmar el correo (o mostrar que no coincide). */
@Composable
fun PasoCorreoScreen(
    @StringRes titulo: Int,
    correo: String,
    enError: Boolean,
    onCorreoChange: (String) -> Unit,
    onSiguiente: () -> Unit,
    onIntentarDeNuevo: () -> Unit,
    onAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    RecuperacionLayout(
        titulo = titulo,
        onAtras = onAtras,
        textoBoton = stringResource(
            if (enError) R.string.olvido_intentar_de_nuevo else R.string.olvido_siguiente
        ),
        anchoBoton = if (enError) 250.dp else 204.dp,
        onBoton = if (enError) onIntentarDeNuevo else onSiguiente,
        modifier = modifier
    ) {
        Spacer(Modifier.height(23.dp))
        TextoInstruccion(stringResource(R.string.olvido_correo_instruccion), alto = 90.dp)
        CampoBreak(
            etiqueta = stringResource(R.string.olvido_correo_etiqueta),
            valor = correo,
            onValorChange = onCorreoChange,
            placeholder = stringResource(R.string.olvido_correo_placeholder),
            icono = R.drawable.ic_correo,
            colorBorde = if (enError) BordeError else BordeValidoBrillante,
            habilitado = !enError,
            alturaEtiqueta = 40.dp,
            teclado = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false
            ),
            acciones = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSiguiente()
            })
        )
        if (enError) {
            Spacer(Modifier.height(15.dp))
            MensajeErrorCampo(stringResource(R.string.olvido_correo_error))
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PasoCorreoPreview() {
    BreakAppTheme {
        PasoCorreoScreen(R.string.olvido_usuario_titulo, "", false, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PasoCorreoErrorPreview() {
    BreakAppTheme {
        PasoCorreoScreen(R.string.olvido_contrasena_titulo, "otro@gmail.com", true, {}, {}, {}, {})
    }
}
