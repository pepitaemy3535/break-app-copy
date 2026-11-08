package com.example.breakapp.ui.ai

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts
import com.example.breakapp.model.AiComboDto
import com.example.breakapp.model.AiRecommendationDto

@Composable
fun AiRecommendationsScreen(
    viewModel: AiViewModel,
    onBack: () -> Unit,
    onExploreRestaurants: () -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    val chips = listOf(
        "🍜 ¿Qué almuerzo hoy?",
        "⚡ Algo rápido para receso",
        "☕ Café y snack",
        "🍰 Antojo dulce",
        "💰 Menos de $10.000",
        "🥗 Opción ligera o saludable",
        "🍕 Antojo de pizza"
    )

    fun sendPrompt(prompt: String) {
        val clean = prompt.trim()
        if (clean.isBlank()) return
        inputText = ""
        focusManager.clearFocus()
        viewModel.enviarMensaje(clean)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Break IA",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BreakColors.Amber.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BreakColors.Amber,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Agente Híbrido",
                                fontFamily = BreakFonts.Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BreakColors.Amber
                            )
                        }
                    }
                }
                Text(
                    text = "Recomendaciones personalizadas del campus",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Image(
                painter = painterResource(R.drawable.main_img_logo),
                contentDescription = "Break Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        // --- CHIPS HORIZONTALES CON SUGERENCIAS RÁPIDAS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chipText ->
                Surface(
                    color = Color(0xFF381E72),
                    shape = RoundedCornerShape(20.dp),
                    border = null,
                    modifier = Modifier.clickable {
                        sendPrompt(chipText)
                    }
                ) {
                    Text(
                        text = chipText,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // --- CONTENIDO PRINCIPAL (SCROLLABLE) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isLoading -> {
                    LoadingView()
                }

                uiState.rawText.isNotBlank() -> {
                    ResponseCard(
                        prompt = uiState.lastPrompt,
                        response = uiState.rawText,
                        banner = uiState.banner,
                        clusterLabel = uiState.clusterLabel,
                        recommendations = uiState.recommendations,
                        suggestedCombo = uiState.suggestedCombo,
                        sourceLabel = uiState.activeSource?.label,
                        onAddToCart = { dto ->
                            val matched = findCatalogProduct(dto.productId, dto.name)
                            if (matched != null) {
                                CartRepository.addItem(matched, 1)
                                Toast.makeText(context, "${matched.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "${dto.name} añadido a tus preferencias", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onAddComboToCart = { combo ->
                            val mainProd = findCatalogProductByName(combo.mainName)
                            val pairProd = findCatalogProductByName(combo.pairingName)
                            if (mainProd != null) CartRepository.addItem(mainProd, 1)
                            if (pairProd != null) CartRepository.addItem(pairProd, 1)
                            Toast.makeText(context, "Combo ${combo.mainName} + ${combo.pairingName} agregado", Toast.LENGTH_SHORT).show()
                        },
                        onExplore = onExploreRestaurants
                    )
                }

                else -> {
                    InitialWelcomeCard(
                        onQuickStart = {
                            sendPrompt("Recomiéndame opciones deliciosas para pedir hoy en el campus")
                        }
                    )
                }
            }
        }

        // --- CAMPO INFERIOR PARA ESCRIBIR A LA IA ---
        Surface(
            color = Color(0xFF1E103E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Pregúntale a Break IA qué pedir...",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2C1462),
                        unfocusedContainerColor = Color(0xFF2C1462),
                        focusedBorderColor = BreakColors.Amber,
                        unfocusedBorderColor = Color(0xFF4C2A8A)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { sendPrompt(inputText) })
                )

                Spacer(Modifier.width(10.dp))

                IconButton(
                    onClick = { sendPrompt(inputText) },
                    enabled = inputText.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !uiState.isLoading) BreakColors.Amber else Color(0xFF4C2A8A))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (inputText.isNotBlank() && !uiState.isLoading) Color(0xFF26184C) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = BreakColors.Amber,
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "El Agente Break IA está analizando hábitos y menús...",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Filtrado híbrido • Reglas de horario • Clustering de estudiantes",
            fontFamily = BreakFonts.Inter,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InitialWelcomeCard(onQuickStart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF381E72)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4C2A8A))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BreakColors.Amber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BreakColors.Amber,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "¡Hola! ¿Qué se te antoja hoy?",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Soy el agente inteligente de Break. Puedo sugerirte platos fuertes, bebidas y snacks según tu horario, tu presupuesto y las opciones favoritas del campus.",
                fontFamily = BreakFonts.Inter,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onQuickStart,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BreakColors.Amber,
                    contentColor = Color(0xFF26184C)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF26184C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Dame sugerencias para hoy",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF26184C)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponseCard(
    prompt: String,
    response: String,
    banner: String?,
    clusterLabel: String?,
    recommendations: List<AiRecommendationDto>,
    suggestedCombo: AiComboDto?,
    sourceLabel: String?,
    onAddToCart: (AiRecommendationDto) -> Unit,
    onAddComboToCart: (AiComboDto) -> Unit,
    onExplore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        if (prompt.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                    .background(Color(0xFF4C2A8A))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = prompt,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        // --- TARJETA PRINCIPAL DEL AGENTE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF381E72)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BreakColors.Amber.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BreakColors.Amber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Agente Break IA",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BreakColors.Amber
                    )

                    Spacer(Modifier.weight(1f))

                    if (sourceLabel != null) {
                        Surface(
                            color = Color(0xFF281157),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (sourceLabel.contains("On-Device")) "On-Device" else "En Línea",
                                fontFamily = BreakFonts.Inter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BreakColors.Amber,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                if (!banner.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF281157))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 $banner",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = response,
                    fontFamily = BreakFonts.Inter,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                // --- TARJETAS ESTRUCTURADAS DE PRODUCTOS RECOMENDADOS ---
                if (recommendations.isNotEmpty()) {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "Platos y Bebidas Recomendadas",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BreakColors.Amber
                    )
                    Spacer(Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        recommendations.forEach { dto ->
                            RecommendationProductItem(
                                dto = dto,
                                onAddToCart = { onAddToCart(dto) }
                            )
                        }
                    }
                }

                // --- COMBO SUGERIDO DESTACADO ---
                if (suggestedCombo != null) {
                    Spacer(Modifier.height(18.dp))
                    SuggestedComboCard(
                        combo = suggestedCombo,
                        onAddCombo = { onAddComboToCart(suggestedCombo) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onExplore) {
                        Text(
                            text = "Explorar todos los restaurantes",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            color = BreakColors.Amber,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationProductItem(
    dto: AiRecommendationDto,
    onAddToCart: () -> Unit
) {
    val matched = findCatalogProduct(dto.productId, dto.name)
    val imageRes = matched?.imageRes ?: R.drawable.main_img_food_bandeja_paisa

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1462)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4C2A8A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = dto.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dto.name,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )

                if (!dto.restaurantName.isNullOrBlank()) {
                    Text(
                        text = dto.restaurantName,
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = BreakColors.Amber
                    )
                }

                val explanation = dto.explanations?.firstOrNull()
                if (!explanation.isNullOrBlank()) {
                    Text(
                        text = "• $explanation",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1
                    )
                }

                Spacer(Modifier.height(2.dp))
                Text(
                    text = dto.price.toCopPrice(),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            IconButton(
                onClick = onAddToCart,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BreakColors.Amber)
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Agregar al carrito",
                    tint = Color(0xFF26184C),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SuggestedComboCard(
    combo: AiComboDto,
    onAddCombo: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF281157)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BreakColors.Amber),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BreakColors.Amber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Combo Recomendado por Break IA",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BreakColors.Amber
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${combo.mainName} + ${combo.pairingName}",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${combo.totalPrice.toCopPrice()}",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BreakColors.Amber
                )

                Button(
                    onClick = onAddCombo,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Pedir Combo",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF26184C)
                    )
                }
            }
        }
    }
}

private fun findCatalogProduct(id: String, name: String): Product? {
    val all = menuRepository.restaurants().flatMap { menuRepository.productsForRestaurant(it.id) }
    return all.firstOrNull { it.id.equals(id, ignoreCase = true) }
        ?: all.firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: all.firstOrNull { name.contains(it.name, ignoreCase = true) || it.name.contains(name, ignoreCase = true) }
}

private fun findCatalogProductByName(name: String): Product? {
    val all = menuRepository.restaurants().flatMap { menuRepository.productsForRestaurant(it.id) }
    return all.firstOrNull { it.name.equals(name, ignoreCase = true) }
        ?: all.firstOrNull { name.contains(it.name, ignoreCase = true) || it.name.contains(name, ignoreCase = true) }
}
