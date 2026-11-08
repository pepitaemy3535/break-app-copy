package com.example.breakapp.main.orders

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.OrderStatus
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit = {},
    viewModel: OrdersViewModel = viewModel()
) {
    val activeOrder by viewModel.activeOrder.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
    ) {
        // --- CABECERA PRINCIPAL ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToHome, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al inicio",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mis Pedidos",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = "Historial y seguimiento del estudiante",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = textMuted
                )
            }
        }

        // --- PESTAÑAS: EN CURSO vs HISTORIAL ---
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = Color(0xFF261254),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = BreakColors.Amber,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.selectTab(0) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "En curso",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = if (uiState.selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (uiState.selectedTab == 0) BreakColors.Amber else Color.White
                        )
                        if (activeOrder != null && activeOrder?.status != OrderStatus.ENTREGADO) {
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BreakColors.Amber)
                            )
                        }
                    }
                }
            )

            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.selectTab(1) },
                text = {
                    Text(
                        text = "Historial (${orderHistory.size})",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = if (uiState.selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (uiState.selectedTab == 1) BreakColors.Amber else Color.White
                    )
                }
            )
        }

        // --- CONTENIDO SEGÚN LA PESTAÑA ---
        if (uiState.selectedTab == 0) {
            // PESTAÑA 1: SEGUIMIENTO EN CURSO
            if (activeOrder != null) {
                OrderTrackingScreen(
                    onBack = onBackToHome,
                    viewModel = viewModel
                )
            } else {
                EmptyActiveOrderView(onExploreMenu = onBackToHome)
            }
        } else {
            // PESTAÑA 2: HISTORIAL DE PEDIDOS DEL ESTUDIANTE
            OrderHistoryView(
                orders = orderHistory,
                studentName = activeOrder?.studentName ?: "Katy Lozano",
                studentEmail = activeOrder?.studentEmail ?: "correo@gmail.com",
                onOrderClick = { viewModel.selectHistoryOrder(it) },
                onReorder = { viewModel.reorder(it) },
                onExploreMenu = onBackToHome
            )
        }
    }

    // Modal de Detalle completo de un pedido del Historial
    uiState.selectedHistoryOrder?.let { order ->
        OrderDetailDialog(
            order = order,
            onDismiss = { viewModel.selectHistoryOrder(null) }
        )
    }
}

/**
 * Pantalla que lista el historial de pedidos realizados por el estudiante
 * mostrando el día, la hora exacta, el restaurante, productos, precio y estado.
 */
@Composable
private fun OrderHistoryView(
    orders: List<Order>,
    studentName: String,
    studentEmail: String,
    onOrderClick: (Order) -> Unit,
    onReorder: (Order) -> Unit,
    onExploreMenu: () -> Unit
) {
    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tarjeta con información del estudiante
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF261254))
                    .border(1.dp, Color(0xFF4C2A9E), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BreakColors.Amber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Estudiante",
                        tint = Color(0xFF1E1B26),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Estudiante: $studentName",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = studentEmail,
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = textMuted
                    )
                    Text(
                        text = "Universidad Jorge Tadeo Lozano • Bogotá",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = BreakColors.Amber
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Aún no tienes pedidos registrados",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tus pedidos aparecerán aquí detallados con la fecha, la hora y el restaurante.",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 13.sp,
                        color = textMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = onExploreMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber)
                    ) {
                        Text("Explorar Restaurantes", color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(orders) { order ->
                OrderHistoryCard(
                    order = order,
                    onClick = { onOrderClick(order) },
                    onReorder = { onReorder(order) }
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Tarjeta individual de pedido en el historial mostrando fecha, hora, restaurante, productos y estado.
 */
@Composable
private fun OrderHistoryCard(
    order: Order,
    onClick: () -> Unit,
    onReorder: () -> Unit
) {
    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(deepPurpleCard)
            .border(1.dp, Color(0xFF4C2A9E), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Fila 1: Restaurante, ID y Badge de Estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BreakColors.Amber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = Color(0xFF1E1B26),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = order.restaurantName,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = order.id,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = BreakColors.Amber
                    )
                }
            }

            OrderStatusBadge(status = order.status)
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFF4C2A9E), thickness = 0.8.dp)
        Spacer(Modifier.height(10.dp))

        // Fila 2: DÍA Y HORA DESTACADOS (Requerimiento explícito)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF261254))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Fecha",
                tint = BreakColors.Amber,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = order.date,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color.White
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Hora",
                tint = BreakColors.Amber,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = order.time,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(10.dp))

        // Fila 3: Productos comprados
        val itemsSummary = if (order.items.isNotEmpty()) {
            order.items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
        } else {
            "Productos varios del menú"
        }
        Text(
            text = itemsSummary,
            fontFamily = BreakFonts.Inter,
            fontSize = 13.sp,
            color = Color(0xFFDDD6FE),
            maxLines = 2
        )

        Spacer(Modifier.height(10.dp))

        // Fila 4: Total y Botón de Recibo
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total pagado",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = textMuted
                )
                Text(
                    text = order.total.toCopPrice(),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BreakColors.Amber
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6D28D9)),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Recibo", fontSize = 12.sp)
                }

                Button(
                    onClick = onReorder,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFF1E1B26),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Repetir", fontSize = 12.sp, color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: OrderStatus) {
    val (bgColor, textColor, label, icon) = when (status) {
        OrderStatus.ENTREGADO -> Quad(Color(0xFF1B5E20), Color(0xFF81C784), "Entregado", Icons.Default.CheckCircle)
        OrderStatus.LISTO -> Quad(Color(0xFF78350F), Color(0xFFFBBF24), "Listo", Icons.Default.ShoppingBag)
        OrderStatus.EN_PREPARACION -> Quad(Color(0xFF312E81), Color(0xFFA5B4FC), "Preparando", Icons.Default.Timer)
        OrderStatus.RECIBIDO -> Quad(Color(0xFF1E293B), Color(0xFF94A3B8), "Recibido", Icons.Default.Check)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = textColor
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun EmptyActiveOrderView(onExploreMenu: () -> Unit) {
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF381B7A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = BreakColors.Amber,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "No tienes ningún pedido activo",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Cuando realices una orden en una de las cafeterías del campus, aquí podrás ver el progreso en tiempo real y recibir avisos para recogerla.",
            fontFamily = BreakFonts.Inter,
            fontSize = 13.sp,
            color = textMuted,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onExploreMenu,
            colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Explorar Cafeterías del Campus",
                color = Color(0xFF1E1B26),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Diálogo que muestra el recibo/ticket detallado de un pedido del historial.
 */
@Composable
private fun OrderDetailDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Detalle del Pedido",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                OrderStatusBadge(status = order.status)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${order.restaurantName} • ${order.id}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BreakColors.Amber
                )
                Spacer(Modifier.height(6.dp))

                // Fecha y Hora
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Realizado el ${order.date} a las ${order.time}",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Estudiante
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Estudiante: ${order.studentName}",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFF4C2A9E))
                Spacer(Modifier.height(10.dp))

                // Lista de productos
                Text(
                    text = "Productos comprados:",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = textMuted
                )
                Spacer(Modifier.height(6.dp))

                if (order.items.isNotEmpty()) {
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.product.name}",
                                fontFamily = BreakFonts.Inter,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = item.totalPrice.toCopPrice(),
                                fontFamily = BreakFonts.Inter,
                                fontSize = 13.sp,
                                color = BreakColors.Amber
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                } else {
                    Text(
                        text = "Productos del menú registrado",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFF4C2A9E))
                Spacer(Modifier.height(10.dp))

                // Total y Método de pago
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Método:", fontSize = 12.sp, color = textMuted)
                    Text(text = order.formattedPaymentMethod, fontSize = 12.sp, color = Color.White)
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    Text(
                        text = order.total.toCopPrice(),
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BreakColors.Amber
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "📍 Punto de entrega: ${order.pickupPoint}",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = textMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber)
            ) {
                Text("Entendido", color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
            }
        }
    )
}
