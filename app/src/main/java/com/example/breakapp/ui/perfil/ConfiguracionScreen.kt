package com.example.breakapp.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.components.ConfirmacionAccionDialog
import com.example.breakapp.ui.perfil.components.PerfilMenuItem
import com.example.breakapp.ui.perfil.components.PerfilSwitchItem
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.Fondo

@Composable
fun ConfiguracionScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onVolver: () -> Unit,
    onNavigateToIdioma: () -> Unit,
    onNavigateToNotificaciones: () -> Unit,
    onNavigateToPrivacidad: () -> Unit,
    onNavigateToAyuda: () -> Unit,
    onCuentaEliminada: () -> Unit
) {
    if (state.mostrarDialogoEliminarCuenta) {
        ConfirmacionAccionDialog(
            titulo = "¿Estás seguro de que quieres eliminar tu cuenta?",
            subtitulo = "Esta acción no se puede deshacer",
            textoConfirmar = "Aceptar",
            colorConfirmar = BordeError,
            onConfirmar = {
                onEvent(PerfilEvent.OnConfirmarEliminarCuenta)
                onCuentaEliminada()
            },
            onCancelar = { onEvent(PerfilEvent.SetMostrarDialogoEliminarCuenta(false)) }
        )
    }

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
            titulo = "Configuración",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PerfilMenuItem(
                titulo = "Idioma",
                icono = Icons.Default.Language,
                onClick = onNavigateToIdioma
            )

            PerfilMenuItem(
                titulo = "Notificaciones",
                icono = Icons.Default.Notifications,
                onClick = onNavigateToNotificaciones
            )

            PerfilMenuItem(
                titulo = "Privacidad",
                icono = Icons.Default.Lock,
                onClick = onNavigateToPrivacidad
            )

            PerfilSwitchItem(
                titulo = "Modo oscuro",
                activo = state.modoOscuro,
                onCheckedChange = { onEvent(PerfilEvent.OnModoOscuroToggle(it)) }
            )

            PerfilMenuItem(
                titulo = "Ayuda y soporte",
                icono = Icons.Default.Help,
                onClick = onNavigateToAyuda
            )

            PerfilMenuItem(
                titulo = "Acerca de",
                icono = Icons.Default.Info,
                onClick = { /* Información de versión */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Eliminar cuenta en color rojo
            PerfilMenuItem(
                titulo = "Eliminar cuenta",
                icono = Icons.Default.DeleteOutline,
                onClick = { onEvent(PerfilEvent.SetMostrarDialogoEliminarCuenta(true)) },
                colorTexto = BordeError,
                colorIcono = BordeError
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun ConfiguracionScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        ConfiguracionScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {},
            onNavigateToIdioma = {},
            onNavigateToNotificaciones = {},
            onNavigateToPrivacidad = {},
            onNavigateToAyuda = {},
            onCuentaEliminada = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun ConfiguracionEliminarCuentaDialogPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        ConfiguracionScreen(
            state = PerfilUiState(mostrarDialogoEliminarCuenta = true),
            onEvent = {},
            onVolver = {},
            onNavigateToIdioma = {},
            onNavigateToNotificaciones = {},
            onNavigateToPrivacidad = {},
            onNavigateToAyuda = {},
            onCuentaEliminada = {}
        )
    }
}
