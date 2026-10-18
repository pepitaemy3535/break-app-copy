package com.example.breakapp.main.cart

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.data.CartItem
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.PaymentMethod
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onOrderConfirmed: (Order) -> Unit,
    modifier: Modifier = Modifier,
    onInsufficientFunds: (Order) -> Unit = {},
    onCardDeclined: (Order) -> Unit = {},
    viewModel: CartViewModel = viewModel()
) {
    val cartState by viewModel.cartState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFF9E92BA)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Carrito",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.main_img_logo),
                contentDescription = "Break Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        if (cartState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🛒",
                        fontSize = 48.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Agrega deliciosos productos desde el menú de los restaurantes.",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 13.sp,
                        color = textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // --- TARJETA DE INFORMACIÓN DEL RESTAURANTE ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(deepPurpleCard)
                        .border(1.dp, Color(0xFF4C2A9E), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BreakColors.Amber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Restaurante",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = cartState.restaurantName,
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = cartState.restaurantLocation,
                            fontFamily = BreakFonts.Inter,
                            fontSize = 12.sp,
                            color = textMuted
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // --- PRODUCTOS EN EL CARRITO ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    cartState.items.forEachIndexed { index, item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { viewModel.updateQuantity(item, item.quantity + 1) },
                            onDecrease = { viewModel.updateQuantity(item, item.quantity - 1) },
                            onDelete = { viewModel.removeItem(item) }
                        )
                        if (index < cartState.items.size - 1) {
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // --- RESUMEN DE COSTOS ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 13.sp,
                            color = Color(0xFF555555)
                        )
                        Text(
                            text = cartState.subtotal.toCopPrice(),
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tiempo estimado de entrega",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 12.sp,
                            color = Color(0xFF777777)
                        )
                        Text(
                            text = "⏱ ${cartState.estimatedTime}",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF4C2A9E)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total a pagar",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = cartState.total.toCopPrice(),
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- MÉTODOS DE PAGO ---
                // Opción 1: Tarjeta de crédito o débito
                val isCardSelected = uiState.selectedPaymentMethod == PaymentMethod.CARD
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(deepPurpleCard)
                        .border(
                            1.dp,
                            if (isCardSelected) BreakColors.Amber else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.selectPaymentMethod(PaymentMethod.CARD) }
                        .padding(14.dp)
                ) {
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
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Tarjeta",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Tarjeta de crédito o débito",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = isCardSelected,
                            onClick = { viewModel.selectPaymentMethod(PaymentMethod.CARD) },
                            colors = RadioButtonDefaults.colors(selectedColor = BreakColors.Amber)
                        )
                    }

                    if (isCardSelected) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Número de tarjeta",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 11.sp,
                            color = textMuted
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = uiState.cardNumber,
                            onValueChange = viewModel::onCardNumberChange,
                            placeholder = { Text("xxxx xxxx xxxx xxxx", color = Color(0x66FFFFFF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF281157),
                                unfocusedContainerColor = Color(0xFF281157),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BreakColors.Amber,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Opción 2: Efectivo o Nequi
                val isCashSelected = uiState.selectedPaymentMethod == PaymentMethod.CASH_NEQUI
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(deepPurpleCard)
                        .border(
                            1.dp,
                            if (isCashSelected) BreakColors.Amber else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.selectPaymentMethod(PaymentMethod.CASH_NEQUI) }
                        .padding(14.dp)
                ) {
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
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Efectivo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Efectivo o Nequi",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = isCashSelected,
                            onClick = { viewModel.selectPaymentMethod(PaymentMethod.CASH_NEQUI) },
                            colors = RadioButtonDefaults.colors(selectedColor = BreakColors.Amber)
                        )
                    }

                    if (isCashSelected) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF281157))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = "Pagar en caja",
                                fontFamily = BreakFonts.Inter,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = BreakColors.Amber
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- BOTÓN CONFIRMAR Y PAGAR ---
                Button(
                    onClick = {
                        viewModel.confirmOrder(
                            onSuccess = onOrderConfirmed,
                            onInsufficientFunds = onInsufficientFunds,
                            onCardDeclined = onCardDeclined
                        )
                    },
                    enabled = !uiState.isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            text = "Confirmar y Pagar • ${cartState.total.toCopPrice()}",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E1B26)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.product.imageRes),
            contentDescription = item.product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.product.name,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )
            if (item.notes.isNotBlank()) {
                Text(
                    text = item.notes,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.totalPrice.toCopPrice(),
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF2C1462)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF4F4F6))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onDecrease),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Restar",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = "${item.quantity}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C1462))
                        .clickable(onClick = onIncrease),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Sumar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
