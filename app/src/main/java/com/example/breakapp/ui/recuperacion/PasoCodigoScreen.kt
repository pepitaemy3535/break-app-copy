package com.example.breakapp.ui.recuperacion

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.components.CampoBreak
import com.example.breakapp.ui.theme.BordeAdvertencia
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BreakAppTheme
import com.example.breakapp.ui.theme.BreakText

/** Segundo paso de ambos flujos: código de 6 dígitos enviado al correo. */
@Composable
fun PasoCodigoScreen(
    @StringRes titulo: Int,
    codigo: String,
    codigoInvalido: Boolean,
    onCodigoChange: (String) -> Unit,
    onSiguiente: () -> Unit,
    onReenviar: () -> Unit,
    onAtras: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    RecuperacionLayout(
        titulo = titulo,
        onAtras = onAtras,
        textoBoton = stringResource(R.string.olvido_siguiente),
        anchoBoton = 204.dp,
        onBoton = onSiguiente,
        modifier = modifier
    ) {
        Spacer(Modifier.height(24.dp))
        TextoInstruccion(stringResource(R.string.olvido_codigo_instruccion), alto = 90.dp)
        Spacer(Modifier.height(25.dp))
        CampoBreak(
            etiqueta = stringResource(R.string.olvido_codigo_etiqueta),
            valor = codigo,
            onValorChange = onCodigoChange,
            placeholder = stringResource(R.string.olvido_codigo_placeholder),
            icono = R.drawable.ic_codigo,
            // En el diseño el campo del código lleva borde ámbar; el rojo es mío (código incompleto).
            colorBorde = if (codigoInvalido) BordeError else BordeAdvertencia,
            alturaEtiqueta = 40.dp,
            teclado = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false
            ),
            acciones = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSiguiente()
            })
        )
        Spacer(Modifier.height(17.dp))
        Text(
            text = stringResource(R.string.olvido_reenviar_codigo),
            style = BreakText.Instruccion,
            color = Color.White,
            modifier = Modifier
                .padding(start = 31.dp)
                .clickable(role = Role.Button, onClick = onReenviar)
                .padding(horizontal = 3.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PasoCodigoPreview() {
    BreakAppTheme {
        PasoCodigoScreen(R.string.olvido_contrasena_titulo, "", false, {}, {}, {}, {})
    }
}
