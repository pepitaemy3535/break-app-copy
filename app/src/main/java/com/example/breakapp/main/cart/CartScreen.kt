package com.example.breakapp.main.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.data.CartItem
import com.example.breakapp.main.data.FoodCategory
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.PaymentMethod
import com.example.breakapp.main.data.StockRepository
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@OptIn(ExperimentalLayoutApi::class)
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
    val stockMap by StockRepository.stockMap.collectAsState()

    var itemForNotesDialog by remember { mutableStateOf<CartItem?>(null) }

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
                        val availableStock = stockMap[item.product.id] ?: StockRepository.getStock(item.product.id)
                        CartItemRow(
                            item = item,
                            availableStock = availableStock,
                            onIncrease = { viewModel.updateQuantity(item, item.quantity + 1) },
                            onDecrease = { viewModel.updateQuantity(item, item.quantity - 1) },
                            onDelete = { viewModel.removeItem(item) },
                            onEditNotes = { itemForNotesDialog = item }
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
                            placeholder = { Text("0000 0000 0000 0000", color = Color(0x66FFFFFF)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF281157),
                                unfocusedContainerColor = Color(0xFF281157),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = if (uiState.cardError != null) Color(0xFFFF5252) else BreakColors.Amber,
                                unfocusedBorderColor = if (uiState.cardError != null) Color(0xFFFF5252) else Color(0x33FFFFFF)
                            )
                        )
                        if (uiState.cardError != null) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = uiState.cardError ?: "",
                                color = Color(0xFFFF5252),
                                fontSize = 11.sp,
                                fontFamily = BreakFonts.Inter
                            )
                        }
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
                val isPayEnabled = !uiState.isProcessing && uiState.canConfirm
                Button(
                    onClick = {
                        viewModel.confirmOrder(
                            onSuccess = onOrderConfirmed,
                            onInsufficientFunds = onInsufficientFunds,
                            onCardDeclined = onCardDeclined
                        )
                    },
                    enabled = isPayEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreakColors.Amber,
                        disabledContainerColor = BreakColors.Amber.copy(alpha = 0.4f)
                    ),
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
                            color = if (isPayEnabled) Color(0xFF1E1B26) else Color(0x661E1B26)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Modal para editar / agregar notas (especialmente bebidas calientes)
    itemForNotesDialog?.let { item ->
        var noteText by remember(item) { mutableStateOf(item.notes) }
        AlertDialog(
            onDismissRequest = { itemForNotesDialog = null },
            title = {
                Text(
                    text = "Nota para ${item.product.name}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Personaliza tu pedido (ej. nivel de azúcar, tipo de leche):",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Sin azúcar", "Poca azúcar", "Con azúcar", "Leche deslactosada").forEach { suggestion ->
                            SuggestionChip(
                                onClick = { noteText = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Escribe una indicación...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateNotes(item, noteText.trim())
                        itemForNotesDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber)
                ) {
                    Text("Guardar", color = Color(0xFF1E1B26), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.updateNotes(item, "")
                        itemForNotesDialog = null
                    }
                ) {
                    Text("Quitar nota", color = Color.Gray)
                }
            }
        )
    }
}

/**
 * Detecta si un ítem corresponde a una bebida caliente donde tiene sentido personalizar el azúcar/preparación.
 */
private fun isHotBeverage(item: CartItem): Boolean {
    val name = item.product.name.lowercase()
    return item.product.category == FoodCategory.BEBIDAS && (
        name.contains("café") || name.contains("cafe") ||
        name.contains("tinto") || name.contains("capuchino") ||
        name.contains("cappuccino") || name.contains("chocolate") ||
        name.contains("aromática") || name.contains("aromatica") ||
        name.contains("té") || name.contains("te") ||
        name.contains("latte") || name.contains("espresso")
    )
}

@Composable
private fun CartItemRow(
    item: CartItem,
    availableStock: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit,
    onEditNotes: () -> Unit
) {
    val isHot = isHotBeverage(item)

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

            // Mostrar notas o botón para añadir notas en bebidas calientes
            if (item.notes.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onEditNotes)
                ) {
                    Text(
                        text = item.notes,
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = Color(0xFF6B46C1),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nota",
                        tint = Color(0xFF6B46C1),
                        modifier = Modifier.size(12.dp)
                    )
                }
            } else if (isHot) {
                Text(
                    text = "+ Agregar nota (ej. azúcar)",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 11.sp,
                    color = Color(0xFF4C2A9E),
                    modifier = Modifier.clickable(onClick = onEditNotes)
                )
            }

            Spacer(Modifier.height(2.dp))
            Text(
                text = "Disponibles: $availableStock",
                fontFamily = BreakFonts.Inter,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (availableStock > 0) Color(0xFF388E3C) else Color(0xFFD32F2F)
            )
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

                val canIncrease = item.quantity < availableStock
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (canIncrease) Color(0xFF2C1462) else Color.LightGray)
                        .clickable(enabled = canIncrease, onClick = onIncrease),
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
