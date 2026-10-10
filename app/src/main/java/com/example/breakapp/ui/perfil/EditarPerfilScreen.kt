package com.example.breakapp.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.components.CampoBreak
import com.example.breakapp.ui.components.PieUniversidad
import com.example.breakapp.ui.perfil.components.EditarFotoDialog
import com.example.breakapp.ui.perfil.components.ExitoGuardadoDialog
import com.example.breakapp.ui.perfil.components.PerfilTopBar
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BordeError
import com.example.breakapp.ui.theme.BordeValido
import com.example.breakapp.ui.theme.Fondo

@Composable
fun EditarPerfilScreen(
    state: PerfilUiState,
    onEvent: (PerfilEvent) -> Unit,
    onVolver: () -> Unit
) {
    if (state.guardadoExitoso) {
        ExitoGuardadoDialog(
            mensaje = "¡Se guardaron correctamente los cambios!",
            onDismiss = {
                onEvent(PerfilEvent.OnDismissExitoGuardado)
                onVolver()
            }
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
        PerfilTopBar(
            titulo = "Editar perfil",
            onVolver = onVolver
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar con icono de cámara
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
                    contentDescription = null,
                    modifier = Modifier.size(54.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Ambar)
                    .clickable { onEvent(PerfilEvent.SetMostrarDialogoFoto(true)) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de edición
        CampoBreak(
            etiqueta = "Nombre",
            valor = state.nombreEdit,
            onValorChange = { onEvent(PerfilEvent.OnNombreEditChange(it)) },
            placeholder = "Ingresa tu nombre",
            icono = R.drawable.ic_usuario,
            colorBorde = if (state.nombreEdit.isNotBlank()) BordeValido else BordeError,
            teclado = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoBreak(
            etiqueta = "Correo electrónico",
            valor = state.correoEdit,
            onValorChange = { onEvent(PerfilEvent.OnCorreoEditChange(it)) },
            placeholder = "nombre@correo.com",
            icono = R.drawable.ic_correo,
            colorBorde = if (state.correoEdit.contains("@")) BordeValido else BordeError,
            teclado = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoBreak(
            etiqueta = "Dirección de entrega",
            valor = state.direccionEdit,
            onValorChange = { onEvent(PerfilEvent.OnDireccionEditChange(it)) },
            placeholder = "Cra 4 #22-61, Bogotá",
            icono = R.drawable.ic_codigo,
            colorBorde = if (state.direccionEdit.isNotBlank()) BordeValido else BordeError,
            teclado = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        if (state.mensajeError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.mensajeError,
                color = BordeError,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (state.cargando) {
            CircularProgressIndicator(color = Ambar)
        } else {
            BotonPrimario(
                texto = "Guardar cambios",
                onClick = { onEvent(PerfilEvent.OnGuardarCambiosPerfil) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        PieUniversidad(modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun EditarPerfilScreenPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        EditarPerfilScreen(
            state = PerfilUiState(),
            onEvent = {},
            onVolver = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun EditarPerfilExitoPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        EditarPerfilScreen(
            state = PerfilUiState(guardadoExitoso = true),
            onEvent = {},
            onVolver = {}
        )
    }
}
