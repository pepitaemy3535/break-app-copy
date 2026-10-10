package com.example.breakapp.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.components.ConfirmacionAccionDialog
import com.example.breakapp.ui.perfil.components.EditarFotoDialog
import com.example.breakapp.ui.perfil.components.PerfilMenuItem
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.Fondo

@Composable
fun PerfilScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onNavigateToEditarPerfil: () -> Unit,
    onNavigateToMetodosPago: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onCerrarSesionCompleto: () -> Unit
) {
    if (state.mostrarDialogoCerrarSesion) {
        ConfirmacionAccionDialog(
            titulo = "¿Seguro que quieres cerrar sesión?",
            textoConfirmar = "Cerrar sesión",
            colorConfirmar = Ambar,
            onConfirmar = {
                onEvent(PerfilEvent.OnConfirmarCerrarSesion)
                onCerrarSesionCompleto()
            },
            onCancelar = { onEvent(PerfilEvent.SetMostrarDialogoCerrarSesion(false)) }
        )
    }

    if (state.mostrarDialogoFoto) {
        EditarFotoDialog(
            onTomarFoto = { onEvent(PerfilEvent.SetMostrarDialogoFoto(false)) },
            onElegirGaleria = { onEvent(PerfilEvent.SetMostrarDialogoFoto(false)) },
            onDismiss = { onEvent(PerfilEvent.SetMostrarDialogoFoto(false)) }
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
        // TopBar superior
        PerfilTopBar(titulo = "Mi perfil")

        Spacer(modifier = Modifier.height(12.dp))

        // Avatar con botón de editar
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF432282))
                    .clickable { onEvent(PerfilEvent.SetMostrarDialogoFoto(true)) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_usuario),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(54.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Ambar)
                    .clickable { onNavigateToEditarPerfil() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Nombre de usuario y universidad
        Text(
            text = state.usuario.nombre,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = state.usuario.universidad,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caja de correo electrónico
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF381B7A))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_correo),
                contentDescription = null,
                tint = Ambar,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = state.usuario.correo,
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de opciones de navegación
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PerfilMenuItem(
                titulo = "Mis pedidos",
                icono = Icons.Default.ShoppingBag,
                onClick = { /* Navegación a pedidos futuros */ }
            )
            PerfilMenuItem(
                titulo = "Direcciones",
                icono = Icons.Default.LocationOn,
                onClick = onNavigateToEditarPerfil
            )
            PerfilMenuItem(
                titulo = "Métodos de pago",
                icono = Icons.Default.CreditCard,
                onClick = onNavigateToMetodosPago
            )
            PerfilMenuItem(
                titulo = "Configuración",
                icono = Icons.Default.Settings,
                onClick = onNavigateToConfiguracion
            )
            PerfilMenuItem(
                titulo = "Cerrar sesión",
                icono = Icons.Default.ExitToApp,
                onClick = { onEvent(PerfilEvent.SetMostrarDialogoCerrarSesion(true)) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PerfilScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        PerfilScreen(
            state = PerfilUiState(),
            onEvent = {},
            onNavigateToEditarPerfil = {},
            onNavigateToMetodosPago = {},
            onNavigateToConfiguracion = {},
            onCerrarSesionCompleto = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PerfilCerrarSesionPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        PerfilScreen(
            state = PerfilUiState(mostrarDialogoCerrarSesion = true),
            onEvent = {},
            onNavigateToEditarPerfil = {},
            onNavigateToMetodosPago = {},
            onNavigateToConfiguracion = {},
            onCerrarSesionCompleto = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PerfilEditarFotoPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        PerfilScreen(
            state = PerfilUiState(mostrarDialogoFoto = true),
            onEvent = {},
            onNavigateToEditarPerfil = {},
            onNavigateToMetodosPago = {},
            onNavigateToConfiguracion = {},
            onCerrarSesionCompleto = {}
        )
    }
}
