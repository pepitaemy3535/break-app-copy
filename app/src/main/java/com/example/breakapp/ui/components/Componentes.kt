package com.example.breakapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.breakapp.R
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BreakText

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
