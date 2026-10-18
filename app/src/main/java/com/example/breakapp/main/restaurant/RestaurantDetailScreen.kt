package com.example.breakapp.main.restaurant

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breakapp.R
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RestaurantDetailViewModel = viewModel()
) {
    LaunchedEffect(restaurantId) {
        viewModel.loadRestaurant(restaurantId)
    }

    val state by viewModel.uiState.collectAsState()
    val cartState by viewModel.cartState.collectAsState()

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .statusBarsPadding()
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BreakColors.Amber)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (cartState.totalItemsCount > 0) 90.dp else 16.dp)
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

                    Spacer(Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.restaurant?.name ?: "Catering Gourmet",
                            fontFamily = BreakFonts.Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${state.restaurant?.rating ?: 4.8}",
                                fontFamily = BreakFonts.Inter,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = state.restaurant?.schedule ?: "Lun - Vie • 7:00 AM - 8:00 PM",
                                fontFamily = BreakFonts.Inter,
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }
                    }

                    Image(
                        painter = painterResource(R.drawable.main_img_logo),
                        contentDescription = "Break Logo",
                        modifier = Modifier.size(44.dp)
                    )
                }

                // --- BANNER FOTOGRÁFICO DE LA TIENDA ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    val bannerRes = state.restaurant?.bannerRes ?: R.drawable.main_img_rest_catering_gourmet
                    Image(
                        painter = painterResource(bannerRes),
                        contentDescription = "Foto Restaurante",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Ubicación del local
                Text(
                    text = state.restaurant?.location ?: "Módulo 2 y 7 en Planta Baja Ext. 1933",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(14.dp))

                // --- BUSCADOR DE PRODUCTOS ---
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Buscar productos...",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 13.sp,
                            color = textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = textMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = deepPurpleCard,
                        unfocusedContainerColor = deepPurpleCard,
                        focusedBorderColor = Color(0x44FFFFFF),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(14.dp))

                // --- CHIPS DE CATEGORÍAS ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.categories.forEach { cat ->
                        val isSelected = cat == state.selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color.White else deepPurpleCard)
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White else Color(0x44FFFFFF),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.onCategorySelect(cat) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontFamily = BreakFonts.Inter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) Color(0xFF2C1462) else Color.White
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- GRID DE PRODUCTOS (2 COLUMNAS) ---
                val products = state.filteredProducts
                val chunkedProducts = products.chunked(2)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    chunkedProducts.forEach { rowProducts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProducts.forEach { product ->
                                val qty = viewModel.getProductQuantity(product.id)
                                ProductGridCard(
                                    product = product,
                                    quantity = qty,
                                    onIncrease = { viewModel.increaseQuantity(product) },
                                    onDecrease = { viewModel.decreaseQuantity(product) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // --- BARRA FLOTANTE "AGREGAR AL CARRITO" ---
        AnimatedVisibility(
            visible = cartState.totalItemsCount > 0,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = onNavigateToCart,
                colors = ButtonDefaults.buttonColors(containerColor = BreakColors.Amber),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agregar al carrito",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1B26)
                    )
                    Text(
                        text = cartState.total.toCopPrice(),
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E1B26)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductGridCard(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = Color(0xFF3B1D7E)
    val textMuted = Color(0xFFB3A5D9)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .padding(10.dp)
    ) {
        // Carga directa de la imagen local del producto
        Image(
            painter = painterResource(if (product.imageRes != 0) product.imageRes else R.drawable.main_img_food_chocoramo),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = product.name,
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = product.price.toCopPrice(),
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = BreakColors.Amber
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⏱ ${product.prepTime}",
                    fontFamily = BreakFonts.Inter,
                    fontSize = 10.sp,
                    color = textMuted
                )
                if (product.calories.isNotBlank()) {
                    Text(
                        text = product.calories,
                        fontFamily = BreakFonts.Inter,
                        fontSize = 9.sp,
                        color = textMuted
                    )
                }
            }

            if (quantity == 0) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(BreakColors.Amber)
                        .clickable(onClick = onIncrease),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2C1462))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF03012))
                            .clickable(onClick = onDecrease),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Restar",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = "$quantity",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(BreakColors.Amber)
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
}