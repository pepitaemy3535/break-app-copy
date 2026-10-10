package com.example.breakapp.ui.perfil.subpantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.PerfilEvent
import com.example.breakapp.ui.perfil.PerfilUiState
import com.example.breakapp.ui.perfil.components.PerfilSwitchItem
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.Fondo

@Composable
fun PrivacidadScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onVolver: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PerfilTopBar(
            titulo = "Privacidad",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sección DATOS PERSONALES
            Text(
                text = "DATOS PERSONALES",
                color = Ambar,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            PerfilSwitchItem(
                titulo = "Compartir datos con restaurantes",
                activo = state.privCompartirRestaurantes,
                onCheckedChange = { onEvent(PerfilEvent.OnPrivacidadToggle("restaurantes", it)) }
            )

            PerfilSwitchItem(
                titulo = "Historial de pedidos visible",
                activo = state.privHistorialVisible,
                onCheckedChange = { onEvent(PerfilEvent.OnPrivacidadToggle("historial", it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sección UBICACIÓN
            Text(
                text = "UBICACIÓN",
                color = Ambar,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            PerfilSwitchItem(
                titulo = "Permitir ubicación en segundo plano",
                activo = state.privUbicacionSegundoPlano,
                onCheckedChange = { onEvent(PerfilEvent.OnPrivacidadToggle("segundo_plano", it)) }
            )

            PerfilSwitchItem(
                titulo = "Compartir ubicación con repartidor",
                activo = state.privCompartirRepartidor,
                onCheckedChange = { onEvent(PerfilEvent.OnPrivacidadToggle("repartidor", it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace política de privacidad
            Text(
                text = "Ver política de privacidad",
                color = Ambar,
                fontSize = 13.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { /* Abrir enlace web */ }
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PrivacidadScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        PrivacidadScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {}
        )
    }
}
