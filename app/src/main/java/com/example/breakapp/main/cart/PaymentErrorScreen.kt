package com.example.breakapp.main.cart

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.breakapp.R
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

enum class PaymentErrorType {
    RECHAZADO,
    FONDOS_INSUFICIENTES
}

@Composable
fun PaymentErrorScreen(
    errorType: PaymentErrorType,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeOrder by CartRepository.activeOrder.collectAsState()
    val order = activeOrder ?: Order()

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    val isRechazado = errorType == PaymentErrorType.RECHAZADO

    val title = if (isRechazado) "¡Pago Rechazado!" else "¡Fondos Insuficientes!"
    val description = if (isRechazado) {
        "Tu tarjeta fue rechazada. Por favor verifica los datos e inténtalo de nuevo."
    } else {
        "Tu tarjeta no tiene fondos suficientes para completar esta compra de " +
                (if (order.total > 0) order.total.toCopPrice() else "$7.500") + "."
    }

    val primaryButtonText = if (isRechazado) "Intentar de Nuevo" else "Usar Otro Método de Pago"
    val secondaryButtonText = if (isRechazado) "Cambiar Método de Pago" else "Cancelar Pedido"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "Pago de Pedido",
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

        Spacer(Modifier.height(30.dp))

        // --- CÍRCULO ROJO 3D CON CRUZ (X) ---
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935))
                .border(4.dp, Color(0xFFFF8A80), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error de Pago",
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = title,
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = description,
            fontFamily = BreakFonts.Inter,
            fontSize = 13.sp,
            color = textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(28.dp))

        // --- TARJETA DE RESUMEN DEL PEDIDO ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(deepPurpleCard)
                .padding(16.dp)
        ) {
            // Fila 1: Restaurante y código
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
                    fontSize = 14.sp,
                    color = BreakColors.Amber
                )
            }

            Spacer(Modifier.height(12.dp))

            // Fila 2: Ítems y total
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.main_img_food_capuchino),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(12.dp))

                val summaryText = if (order.items.isNotEmpty()) {
                    order.items.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
                } else {
                    "3x Tinto (Sin Azúcar)"
                }

                Text(
                    text = summaryText,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (order.total > 0) order.total.toCopPrice() else "$7.500",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BreakColors.Amber
                )
            }

            Spacer(Modifier.height(14.dp))

            // Fila 3: Método de pago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Método de pago",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = textMuted
                )
                Text(
                    text = "Visa •••• 4242",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(36.dp))

        // --- BOTONES DE ACCIÓN ---
        Button(
            onClick = onPrimaryAction,
            colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = primaryButtonText,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E1B26)
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSecondaryAction,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF23104C)),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF4C2A9E))),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = secondaryButtonText,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PagoRechazadoPreview() {
    PaymentErrorScreen(
        errorType = PaymentErrorType.RECHAZADO,
        onPrimaryAction = {},
        onSecondaryAction = {},
        onBack = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun FondosInsuficientesPreview() {
    PaymentErrorScreen(
        errorType = PaymentErrorType.FONDOS_INSUFICIENTES,
        onPrimaryAction = {},
        onSecondaryAction = {},
        onBack = {}
    )
}
