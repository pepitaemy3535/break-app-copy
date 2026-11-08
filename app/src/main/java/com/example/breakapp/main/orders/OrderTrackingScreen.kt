package com.example.breakapp.main.orders

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val context = LocalContext.current
    val activeOrder by viewModel.activeOrder.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val order = activeOrder ?: Order()
    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    // Solicitar permiso de notificaciones en Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permiso concedido o denegado */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Seguimiento de Pedido",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "${order.date} • ${order.time}",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = textMuted
                )
            }

            Image(
                painter = painterResource(R.drawable.main_img_logo),
                contentDescription = "Break Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // --- STEPPER / TIMELINE (4 PASOS) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(deepPurpleCard)
                .padding(16.dp)
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
                subtitle = "¡Tu pedido fue recibido y confirmado en caja!",
                isCompleted = true,
                isActive = order.status == OrderStatus.RECIBIDO,
                showBottomLine = true
            )

            // Paso 2: En Preparación
            val isPrepDone = order.status == OrderStatus.EN_PREPARACION ||
                    order.status == OrderStatus.LISTO ||
                    order.status == OrderStatus.ENTREGADO
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
                subtitle = when {
                    order.status == OrderStatus.EN_PREPARACION -> "Preparando en cocina... Tiempo restante: ${uiState.formattedRemainingTime}"
                    order.status == OrderStatus.RECIBIDO -> "Tiempo estimado de preparación: ${order.estimatedTime}"
                    else -> "Preparación completada (${order.estimatedTime})"
                },
                isCompleted = isPrepDone,
                isActive = order.status == OrderStatus.EN_PREPARACION,
                showBottomLine = true
            )

            // Paso 3: Listo para Recoger
            val isReadyDone = order.status == OrderStatus.LISTO || order.status == OrderStatus.ENTREGADO
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
                subtitle = "Punto de entrega: ${order.pickupPoint}",
                isCompleted = isReadyDone,
                isActive = order.status == OrderStatus.LISTO,
                showBottomLine = true
            )

            // Paso 4: Pedido Entregado
            TimelineStepItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                },
                title = "Pedido Entregado",
                subtitle = if (order.status == OrderStatus.ENTREGADO) "¡Entrega confirmada por ti! Buen provecho." else "Pendiente de confirmación de entrega",
                isCompleted = order.status == OrderStatus.ENTREGADO,
                isActive = order.status == OrderStatus.ENTREGADO,
                showBottomLine = false
            )
        }

        Spacer(Modifier.height(16.dp))

        // --- TARJETAS DE ACCIÓN SEGÚN ESTADO ---

        // ESTADO 1: EN PREPARACIÓN -> BOTÓN PARA ACELERAR / PROBAR NOTIFICACIONES
        if (order.status == OrderStatus.RECIBIDO || order.status == OrderStatus.EN_PREPARACION) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2D1664))
                    .border(1.dp, BreakColors.Amber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Tiempo de preparación: ${order.estimatedTime}",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Al cumplirse el tiempo, recibirás una notificación de aviso para recogerlo.",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = textMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.simulateTimeComplete(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = null,
                        tint = Color(0xFF1E1B26),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Simular tiempo cumplido (Avisar ya)",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E1B26)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ESTADO 2: LISTO PARA RECOGER -> ALERTA DESTACADA Y BOTÓN "CONFIRMAR ENTREGA"
        if (order.status == OrderStatus.LISTO) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF4C2210))
                    .border(2.dp, BreakColors.Amber, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "¡Tu pedido ya está listo!",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = BreakColors.Amber
                    )
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Pasa a recogerlo en: ${order.pickupPoint}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                // BOTÓN PRINCIPAL REQUERIDO: CONFIRMAR QUE YA TIENE EL PEDIDO
                Button(
                    onClick = { viewModel.openConfirmDeliveryDialog(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1E1B26)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Confirmar que ya tengo el pedido",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E1B26)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ESTADO 3: ENTREGADO -> CONFIRMACIÓN Y BOTÓN CALIFICAR
        if (order.status == OrderStatus.ENTREGADO) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF143B22))
                    .border(1.5.dp, Color(0xFF4CAF50), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "¡Pedido Entregado con Éxito!",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Confirmaste la recepción de tu comida. ¡Esperamos que la disfrutes!",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = Color(0xFFC8E6C9),
                    textAlign = TextAlign.Center
                )

                if (!uiState.ratingSent) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = viewModel::openRatingDialog,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakColors.Amber),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BreakColors.Amber),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = BreakColors.Amber)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Calificar experiencia ⭐",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Orden ${order.id} • ${order.restaurantName}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))

                val itemsSummary = if (order.items.isNotEmpty()) {
                    order.items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
                } else {
                    "Productos variados"
                }

                Text(
                    text = itemsSummary,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 13.sp,
                    color = Color(0xFF5D2EAF)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "Total: ${order.total.toCopPrice()} (${order.formattedPaymentMethod})",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF5D2EAF)
                )
            }

            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BreakColors.Amber),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
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
            // Mostrar notificaciones dinámicas del pedido
            order.notifications.forEach { notif ->
                NotificationPill(
                    icon = {
                        Icon(
                            imageVector = if (notif.isRating) Icons.Default.Star else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = BreakColors.Amber,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    text = if (notif.isRating && uiState.ratingSent) {
                        "¡Gracias por calificar tu pedido! (${uiState.selectedRating} ⭐)"
                    } else {
                        notif.message
                    },
                    onClick = if (notif.isRating && !uiState.ratingSent) {
                        { viewModel.openRatingDialog() }
                    } else null
                )
            }

            // Si está listo y no entregado, mostrar pill para confirmar
            if (order.status == OrderStatus.LISTO) {
                NotificationPill(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BreakColors.Amber,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    text = "¿Ya recibiste tu pedido? Toca aquí para confirmar",
                    onClick = { viewModel.openConfirmDeliveryDialog(order) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // --- MODAL DIÁLOGO DE CONFIRMACIÓN DE ENTREGA ---
    if (uiState.showConfirmDeliveryDialog) {
        val target = uiState.orderToConfirm ?: order
        AlertDialog(
            onDismissRequest = viewModel::closeConfirmDeliveryDialog,
            title = {
                Text(
                    text = "¿Ya recibiste tu pedido?",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Confirma que ya tienes tu orden ${target.id} de ${target.restaurantName} contigo.",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Al confirmar, el pedido pasará a tu historial como entregado exitosamente.",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDelivery(target.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber)
                ) {
                    Text("Sí, ya lo tengo", color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeConfirmDeliveryDialog) {
                    Text("Aún no", color = Color.Gray)
                }
            }
        )
    }

    // --- MODAL PARA CALIFICAR EL PEDIDO ---
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
                    Text("Cerrar", color = Color.Gray)
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
    isActive: Boolean = false,
    showBottomLine: Boolean
) {
    val textMuted = Color(0xFFB3A5D9)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> BreakColors.Amber
                            isCompleted -> Color(0xFF4CAF50)
                            else -> Color(0xFF4C2A9E)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            if (showBottomLine) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(34.dp)
                        .background(if (isCompleted) Color(0xFF4CAF50) else Color(0xFF4C2A9E))
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
                color = if (isActive) BreakColors.Amber else Color.White
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
