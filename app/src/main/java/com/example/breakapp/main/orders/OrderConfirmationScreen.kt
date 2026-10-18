package com.example.breakapp.main.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.PaymentMethod
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    onNavigateToTracking: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel()
) {
    val activeOrder by viewModel.activeOrder.collectAsState()
    val order = activeOrder ?: Order(id = orderId.ifBlank { "#B-204" })

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // --- MEDALLA DE CELEBRACIÓN CON CHECKMARK ---
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Fondo púrpura con anillo dorado
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED))
                    .border(6.dp, Color(0xFFF9C72F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Éxito",
                    tint = Color(0xFFF9C72F),
                    modifier = Modifier.size(54.dp)
                )
            }
            // Partículas festivas decorativas
            Text("🎉", fontSize = 24.sp, modifier = Modifier.align(Alignment.TopStart))
            Text("✨", fontSize = 20.sp, modifier = Modifier.align(Alignment.TopEnd))
            Text("🎊", fontSize = 22.sp, modifier = Modifier.align(Alignment.BottomEnd))
            Text("⭐", fontSize = 18.sp, modifier = Modifier.align(Alignment.BottomStart))
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "¡Pedido Exitoso!",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        // --- TARJETA DE RESUMEN DEL PEDIDO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(deepPurpleCard)
                .padding(16.dp)
        ) {
            // Fila 1: Restaurante y código de orden
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BreakColors.Amber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Tienda",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.restaurantName,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = order.restaurantLocation,
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = textMuted
                    )
                }

                Text(
                    text = order.id,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0x33FFFFFF))
            Spacer(Modifier.height(12.dp))

            // Fila 2: Ítems del pedido
            if (order.items.isNotEmpty()) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(item.product.imageRes),
                            contentDescription = item.product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.quantity}x ${item.product.name}",
                                fontFamily = BreakFonts.Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = item.notes,
                                    fontFamily = BreakFonts.Inter,
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.main_img_food_capuchino),
                        contentDescription = "Tinto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "3x Tinto",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Sin azúcar",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0x33FFFFFF))
            Spacer(Modifier.height(12.dp))

            // Fila 3: Código de orden y Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = if (order.total > 0) order.total.toCopPrice() else "$7.500",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BreakColors.Amber
                )
            }

            Spacer(Modifier.height(10.dp))

            // Fila 4: Método de pago y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(BreakColors.Amber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Pago",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.paymentMethod.label,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (order.isPaid) "¡Pagado!" else "¡Pendiente de Pago!",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (order.isPaid) Color(0xFF4CAF50) else Color(0xFFFF5252)
                    )
                }

                Text(
                    text = order.date,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = textMuted
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // --- INFORMACIÓN DE ENTREGA ---
        Text(
            text = "¡Tu pedido estará listo pronto!",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tiempo estimado de entrega",
                fontFamily = BreakFonts.Inter,
                fontSize = 12.sp,
                color = textMuted
            )
            Text(
                text = "⏱ ${order.estimatedTime}",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Punto de recogida:",
                fontFamily = BreakFonts.Inter,
                fontSize = 12.sp,
                color = textMuted
            )
            Text(
                text = "📍 ${order.pickupPoint}",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(30.dp))

        // --- BOTÓN IR A SEGUIMIENTO ---
        Button(
            onClick = onNavigateToTracking,
            colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Ir a Seguimiento de Pedido",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E1B26)
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun OrderConfirmationPreview() {
    OrderConfirmationScreen(
        orderId = "#B-204",
        onNavigateToTracking = {}
    )
}
