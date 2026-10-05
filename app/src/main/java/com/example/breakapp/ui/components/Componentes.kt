package com.example.breakapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Morado

/** Botón ámbar con forma de píldora (Siguiente, Ingresar). */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .height(59.dp)
            .clip(forma)
            .background(Ambar)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto, style = BreakText.Boton, color = Color.White)
    }
}

/** Texto del pie: "Universidad Jorge Tadeo Lozano • Bogotá". */
@Composable
fun PieUniversidad(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.pie_universidad),
        style = BreakText.Pie,
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Campo de texto del diseño: etiqueta arriba y caja blanca con ícono, borde de color y texto morado.
 * Igual al campo de la pantalla de login, para que las pantallas nuevas lo reutilicen.
 */
@Composable
fun CampoBreak(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes icono: Int,
    colorBorde: Color,
    teclado: KeyboardOptions,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    acciones: KeyboardActions = KeyboardActions.Default
) {
    val forma = RoundedCornerShape(10.dp)
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 33.dp)) {
        Text(
            text = etiqueta,
            style = BreakText.Etiqueta,
            color = Color.White,
            modifier = Modifier.height(40.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(forma)
                .background(Color.White)
                .border(4.dp, colorBorde, forma)
                .padding(start = 29.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(icono), contentDescription = null)
            Spacer(Modifier.width(13.dp))
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                enabled = habilitado,
                singleLine = true,
                textStyle = BreakText.Campo.copy(color = Morado),
                cursorBrush = SolidColor(Morado),
                keyboardOptions = teclado,
                keyboardActions = acciones,
                modifier = Modifier.weight(1f),
                decorationBox = { campoInterno ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (valor.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = BreakText.Campo,
                                color = Morado.copy(alpha = 0.6f)
                            )
                        }
                        campoInterno()
                    }
                }
            )
        }
    }
}
