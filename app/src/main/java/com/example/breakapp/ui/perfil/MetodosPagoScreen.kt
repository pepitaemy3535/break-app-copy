package com.example.breakapp.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.components.ExitoGuardadoDialog
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValido
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo
import com.example.breakapp.ui.theme.Morado

@Composable
fun MetodosPagoScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onVolver: () -> Unit
) {
    if (state.tarjetaGuardadaExito) {
        ExitoGuardadoDialog(
            mensaje = "Tarjeta guardada correctamente",
            onDismiss = {
                onEvent(PerfilEvent.OnDismissExitoTarjeta)
                onVolver()
            }
        )
    }

    val forma = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PerfilTopBar(
            titulo = "Métodos de pago",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Opción 1: Tarjeta de crédito
            val esTarjeta = state.metodoPagoSeleccionado == TipoMetodoPago.TARJETA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(forma)
                    .background(Color(0xFF381B7A))
                    .clickable { onEvent(PerfilEvent.OnMetodoPagoChange(TipoMetodoPago.TARJETA)) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = esTarjeta,
                        onClick = { onEvent(PerfilEvent.OnMetodoPagoChange(TipoMetodoPago.TARJETA)) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Ambar,
                            unselectedColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tarjeta de crédito",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (esTarjeta) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Número de tarjeta",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                    )

                    // Campo de texto de la tarjeta
                    val colorBordeTarjeta = if (state.errorTarjeta != null) BordeError else if (state.numeroTarjeta.length == 16) BordeValido else Ambar

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(2.dp, colorBordeTarjeta, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = state.numeroTarjeta,
                            onValueChange = { onEvent(PerfilEvent.OnNumeroTarjetaChange(it)) },
                            singleLine = true,
                            textStyle = BreakText.Campo.copy(color = Morado),
                            cursorBrush = SolidColor(Morado),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            decorationBox = { campoInterno ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (state.numeroTarjeta.isEmpty()) {
                                        Text(
                                            text = "0000 0000 0000 0000",
                                            style = BreakText.Campo,
                                            color = Morado.copy(alpha = 0.5f)
                                        )
                                    }
                                    campoInterno()
                                }
                            }
                        )
                    }

                    if (state.errorTarjeta != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.errorTarjeta,
                            color = BordeError,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Opción 2: Efectivo
            val esEfectivo = state.metodoPagoSeleccionado == TipoMetodoPago.EFECTIVO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(forma)
                    .background(Color(0xFF381B7A))
                    .clickable { onEvent(PerfilEvent.OnMetodoPagoChange(TipoMetodoPago.EFECTIVO)) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = esEfectivo,
                        onClick = { onEvent(PerfilEvent.OnMetodoPagoChange(TipoMetodoPago.EFECTIVO)) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Ambar,
                            unselectedColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Efectivo",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pagar en caja",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BotonPrimario(
            texto = "Continuar",
            onClick = { onEvent(PerfilEvent.OnGuardarMetodoPago) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun MetodosPagoScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        MetodosPagoScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun MetodosPagoErrorPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        MetodosPagoScreen(
            state = PerfilUiState(
                numeroTarjeta = "4525 12",
                errorTarjeta = "Ingresa un número de tarjeta válido."
            ),
            onEvent = {},
            onVolver = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun MetodosPagoExitoPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        MetodosPagoScreen(
            state = PerfilUiState(tarjetaGuardadaExito = true),
            onEvent = {},
            onVolver = {}
        )
    }
}
