package com.example.breakapp.ui.perfil.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.breakapp.R
import com.example.breakapp.ui.components.BotonPizzaAtras
import com.example.breakapp.ui.components.BotonPrimario
import com.example.breakapp.ui.theme.Ambar
import com.example.breakapp.ui.theme.BreakText
import com.example.breakapp.ui.theme.Fondo

/**
 * TopBar superior estándar para las pantallas del módulo de perfil.
 */
@Composable
fun PerfilTopBar(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Pizza/Logo en la esquina superior derecha
        BotonPizzaAtras(
            onClick = { onVolver?.invoke() },
            modifier = Modifier.size(width = 65.dp, height = 45.dp)
        )
    }
}

/**
 * Fila seleccionable tipo tarjeta para menús (Mis pedidos, Direcciones, etc.).
 */
@Composable
fun PerfilMenuItem(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorTexto: Color = Color.White,
    colorIcono: Color = Ambar
) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(forma)
            .background(Color(0xFF381B7A))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = colorIcono,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = titulo,
            style = BreakText.Cuerpo.copy(fontSize = 15.sp),
            color = colorTexto,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f)
        )
    }
}

/**
 * Elemento con interruptor Switch (Modo oscuro, Notificaciones, Privacidad).
 */
@Composable
fun PerfilSwitchItem(
    titulo: String,
    activo: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(forma)
            .background(Color(0xFF381B7A))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            style = BreakText.Cuerpo.copy(fontSize = 15.sp),
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = activo,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Ambar,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0xFF2C1462)
            )
        )
    }
}

/**
 * Diálogo modal de éxito al guardar cambios.
 */
@Composable
fun ExitoGuardadoDialog(
    mensaje: String = "¡Se guardaron correctamente los cambios!",
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Fondo)
        ) {
            Column(
                modifier = Modifier
                    .padding(26.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_check_verde),
                    contentDescription = "Éxito",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = mensaje,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))
                BotonPrimario(
                    texto = "Continuar",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Diálogo modal de confirmación (Cerrar sesión o Eliminar cuenta).
 */
@Composable
fun ConfirmacionAccionDialog(
    titulo: String,
    subtitulo: String? = null,
    textoConfirmar: String,
    colorConfirmar: Color = Ambar,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    Dialog(onDismissRequest = onCancelar) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Fondo)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = colorConfirmar,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = titulo,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (subtitulo != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitulo,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onCancelar,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF432282))
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                    Button(
                        onClick = onConfirmar,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorConfirmar)
                    ) {
                        Text(textoConfirmar, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Diálogo / BottomSheet para cambiar la foto de perfil.
 */
@Composable
fun EditarFotoDialog(
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Fondo)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Editar foto de perfil",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF432282)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_usuario),
                        contentDescription = null,
                        modifier = Modifier.size(54.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                PerfilMenuItem(
                    titulo = "Tomar foto\nAbre la cámara de tu dispositivo",
                    icono = Icons.Default.CameraAlt,
                    onClick = onTomarFoto
                )
                Spacer(modifier = Modifier.height(10.dp))
                PerfilMenuItem(
                    titulo = "Elegir de la galería\nSelecciona una foto guardada",
                    icono = Icons.Default.PhotoLibrary,
                    onClick = onElegirGaleria
                )
                Spacer(modifier = Modifier.height(20.dp))
                BotonPrimario(
                    texto = "Cerrar",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ExitoDialogPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        ExitoGuardadoDialog(onDismiss = {})
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ConfirmacionDialogPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        ConfirmacionAccionDialog(
            titulo = "¿Seguro que quieres cerrar sesión?",
            textoConfirmar = "Cerrar sesión",
            onConfirmar = {},
            onCancelar = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun EditarFotoDialogPreview() {
    com.example.breakapp.ui.theme.BreakAppTheme {
        EditarFotoDialog(
            onTomarFoto = {},
            onElegirGaleria = {},
            onDismiss = {}
        )
    }
}
