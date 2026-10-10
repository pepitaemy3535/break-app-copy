package com.example.breakapp.ui.olvidocontrasena

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.example.breakapp.ui.components.IndicadorFortaleza
import com.example.breakapp.ui.recuperacion.MensajeErrorCampo
import com.example.breakapp.ui.recuperacion.RecuperacionLayout
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValidoBrillante
import com.example.breakapp.ui.theme.BreakAppTheme

@Composable
fun PasoNuevaContrasenaScreen(
    state: OlvidoContrasenaUiState,
    onNuevaContrasenaChange: (String) -> Unit,
    onConfirmarContrasenaChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onIntentarDeNuevo: () -> Unit,
    onAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val enError = state.error != ErrorContrasena.Ninguno
    val borde = if (enError) BordeError else BordeValidoBrillante

    RecuperacionLayout(
        titulo = R.string.olvido_contrasena_titulo,
        onAtras = onAtras,
        textoBoton = stringResource(
            if (enError) R.string.olvido_intentar_de_nuevo else R.string.olvido_guardar
        ),
        anchoBoton = if (enError) 250.dp else 204.dp,
        onBoton = if (enError) onIntentarDeNuevo else onGuardar,
        modifier = modifier
    ) {
        Spacer(Modifier.height(49.dp))
        CampoBreak(
            etiqueta = stringResource(R.string.olvido_nueva_contrasena_etiqueta),
            valor = state.nuevaContrasena,
            onValorChange = onNuevaContrasenaChange,
            placeholder = stringResource(R.string.login_contrasena_placeholder),
            icono = R.drawable.ic_candado,
            colorBorde = borde,
            enmascarado = true,
            habilitado = !enError,
            alturaEtiqueta = 40.dp,
            teclado = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                autoCorrectEnabled = false
            ),
            contenidoDerecha = {
                if (state.nuevaContrasena.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    IndicadorFortaleza(ReglasContrasena.nivel(state.nuevaContrasena))
                }
            }
        )
        Spacer(Modifier.height(27.dp))
        CampoBreak(
            etiqueta = stringResource(R.string.olvido_confirmar_contrasena_etiqueta),
            valor = state.confirmarContrasena,
            onValorChange = onConfirmarContrasenaChange,
            placeholder = stringResource(R.string.login_contrasena_placeholder),
            icono = R.drawable.ic_candado,
            colorBorde = borde,
            enmascarado = true,
            habilitado = !enError,
            alturaEtiqueta = 40.dp,
            teclado = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false
            ),
            acciones = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onGuardar()
            }),
            contenidoDerecha = {
                if (state.confirmarContrasena.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    IndicadorFortaleza(ReglasContrasena.nivel(state.confirmarContrasena))
                }
            }
        )
        if (enError) {
            Spacer(Modifier.height(13.dp))
            MensajeErrorCampo(
                stringResource(
                    if (state.error == ErrorContrasena.NoCoinciden) {
                        R.string.olvido_contrasenas_no_coinciden
                    } else {
                        R.string.olvido_contrasena_debil
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PasoNuevaContrasenaPreview() {
    BreakAppTheme {
        PasoNuevaContrasenaScreen(
            OlvidoContrasenaUiState(
                paso = PasoContrasena.NuevaContrasena,
                nuevaContrasena = "Break123!",
                confirmarContrasena = "Break123!"
            ),
            {}, {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PasoNuevaContrasenaErrorPreview() {
    BreakAppTheme {
        PasoNuevaContrasenaScreen(
            OlvidoContrasenaUiState(
                paso = PasoContrasena.NuevaContrasena,
                nuevaContrasena = "Break123!",
                confirmarContrasena = "Break123",
                error = ErrorContrasena.NoCoinciden
            ),
            {}, {}, {}, {}, {}
        )
    }
}
