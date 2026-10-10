package com.example.breakapp.ui.perfil.subpantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.PerfilEvent
import com.example.breakapp.ui.perfil.PerfilUiState
import com.example.breakapp.ui.perfil.components.PerfilSwitchItem
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Fondo

@Composable
fun NotificacionesScreen(
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
            titulo = "Notificaciones",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PerfilSwitchItem(
                titulo = "Promociones y ofertas",
                activo = state.notiPromociones,
                onCheckedChange = { onEvent(PerfilEvent.OnNotificacionToggle("promos", it)) }
            )

            PerfilSwitchItem(
                titulo = "Estado de pedidos",
                activo = state.notiEstadoPedidos,
                onCheckedChange = { onEvent(PerfilEvent.OnNotificacionToggle("pedidos", it)) }
            )

            PerfilSwitchItem(
                titulo = "Novedades del menú",
                activo = state.notiNovedadesMenu,
                onCheckedChange = { onEvent(PerfilEvent.OnNotificacionToggle("novedades", it)) }
            )

            PerfilSwitchItem(
                titulo = "Recordatorios",
                activo = state.notiRecordatorios,
                onCheckedChange = { onEvent(PerfilEvent.OnNotificacionToggle("recordatorios", it)) }
            )

            PerfilSwitchItem(
                titulo = "Mensajes del restaurante",
                activo = state.notiMensajesRestaurante,
                onCheckedChange = { onEvent(PerfilEvent.OnNotificacionToggle("mensajes", it)) }
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun NotificacionesScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        NotificacionesScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {}
        )
    }
}
