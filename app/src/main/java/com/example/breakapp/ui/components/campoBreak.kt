package com.example.breakapp.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Morado

/** Campo de texto con etiqueta, ícono y borde de color (login y recuperación). */
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
    alturaEtiqueta: Dp = 45.dp,
    enmascarado: Boolean = false,
    habilitado: Boolean = true,
    acciones: KeyboardActions = KeyboardActions.Default,
    contenidoDerecha: @Composable () -> Unit = {}
) {
    val forma = RoundedCornerShape(10.dp)
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 33.dp)) {
        Text(
            text = etiqueta,
            style = BreakText.Etiqueta,
            color = Color.White,
            modifier = Modifier.height(alturaEtiqueta)
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
                visualTransformation = if (enmascarado) PasswordVisualTransformation() else VisualTransformation.None,
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
            contenidoDerecha()
        }
    }
}