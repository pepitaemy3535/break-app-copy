package com.example.breakapp.main.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.OrderStatus
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun OrderTrackingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel()
) {
    val activeOrder by viewModel.activeOrder.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val order = activeOrder ?: Order()
    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Seguimiento de Pedido",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(R.drawable.main_img_logo),
                contentDescription = "Break Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- STEPPER / TIMELINE ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            // Paso 1: Pedido Recibido
            TimelineStepItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                },
                title = "Pedido Recibido",
                subtitle = null,
                isCompleted = true,
                showBottomLine = true
            )

            // Paso 2: En Preparación
            TimelineStepItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                },
                title = "En Preparación",
                subtitle = "Tu pedido se está preparando... Tiempo estimado: 8 min",
                isCompleted = order.status == OrderStatus.EN_PREPARACION || order.status == OrderStatus.LISTO,
                showBottomLine = true
            )

            // Paso 3: Listo para Recoger
            TimelineStepItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                },
                title = "Listo para Recoger",
                subtitle = "Punto de recogida: Módulo 2 y 7 Planta Baja",
                isCompleted = order.status == OrderStatus.LISTO,
                showBottomLine = false
            )
        }

        Spacer(Modifier.height(24.dp))

        // --- TARJETA BLANCA RESUMEN DEL PEDIDO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Orden ${order.id}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))

                val itemsSummary = if (order.items.isNotEmpty()) {
                    order.items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
                } else {
                    "3x Tinto"
                }

                Text(
                    text = itemsSummary,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 13.sp,
                    color = Color(0xFF5D2EAF)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "Total: " + (if (order.total > 0) order.total.toCopPrice() else "$7.500"),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF5D2EAF)
                )
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BreakColors.Amber),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Ticket",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- NOTIFICACIÓN CENTER ---
        Text(
            text = "Notificación Center",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Notificación 1
            NotificationPill(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "¡Tu pedido ha sido recibido!"
            )

            // Notificación 2
            NotificationPill(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "Tiempo de preparación actualizado"
            )

            // Notificación 3
            NotificationPill(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = "Tu pedido está LISTO para recoger (hace 1 min)"
            )

            // Notificación 4: Calificación
            NotificationPill(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = if (uiState.ratingSent) "¡Gracias por calificar tu pedido! (${uiState.selectedRating} ⭐)" else "Califica tu pedido",
                onClick = { if (!uiState.ratingSent) viewModel.openRatingDialog() }
            )
        }

        Spacer(Modifier.height(24.dp))
    }

    // Modal para calificar el pedido
    if (uiState.showRatingDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeRatingDialog,
            title = {
                Text(
                    text = "Califica tu pedido",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Cómo estuvo tu experiencia con ${order.restaurantName}?",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$i estrellas",
                                tint = if (i <= uiState.selectedRating) BreakColors.Amber else Color.LightGray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { viewModel.setRating(i) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::submitRating,
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber)
                ) {
                    Text("Enviar", color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeRatingDialog) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun TimelineStepItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    isCompleted: Boolean,
    showBottomLine: Boolean
) {
    val textMuted = Color(0xFFB3A5D9)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) BreakColors.Amber else Color(0xFF4C2A9E)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            if (showBottomLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(34.dp)
                        .background(if (isCompleted) BreakColors.Amber else Color(0xFF4C2A9E))
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showBottomLine) 16.dp else 4.dp)
        ) {
            Text(
                text = title,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = textMuted
                )
            }
        }
    }
}

@Composable
private fun NotificationPill(
    icon: @Composable () -> Unit,
    text: String,
    onClick: (() -> Unit)? = null
) {
    val deepPurpleCard = Color(0xFF381B7A)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(deepPurpleCard)
            .border(1.dp, Color(0xFF4C2A9E), RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = BreakFonts.Inter,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun OrderTrackingPreview() {
    OrderTrackingScreen(onBack = {})
}
