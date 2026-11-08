package com.example.breakapp.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.collectAsState
import com.example.breakapp.main.data.FavoritesRepository
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailBottomSheet(
    product: Product,
    stock: Int,
    initialQuantity: Int = 1,
    initialNotes: String = "",
    onDismiss: () -> Unit,
    onAddToCart: (Product, Int, String) -> Unit
) {
    var quantity by remember(product) { mutableIntStateOf(if (initialQuantity > 0) initialQuantity else 1) }
    var notes by remember(product) { mutableStateOf(initialNotes) }

    val deepPurpleCard = Color(0xFF381B7A)
    val textMuted = Color(0xFFB3A5D9)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF241052),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Fila superior: Título, Favorito y Botón Cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle del producto",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val favoriteProducts by FavoritesRepository.favoriteProductIds.collectAsState()
                    val isFavorite = product.id in favoriteProducts

                    IconButton(
                        onClick = { FavoritesRepository.toggleProductFavorite(product.id) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(deepPurpleCard)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(deepPurpleCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Imagen del producto
            Image(
                painter = painterResource(
                    if (product.imageRes != 0) product.imageRes else R.drawable.main_img_food_chocoramo
                ),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.height(14.dp))

            // Nombre y Precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = product.price.toCopPrice(),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BreakColors.Amber
                )
            }

            Spacer(Modifier.height(10.dp))

            // Badges informativos (Tiempo, Calorías, Stock)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tiempo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(deepPurpleCard)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⏱ ${product.prepTime}",
                        fontFamily = BreakFonts.Inter,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                // Calorías si están presentes
                if (product.calories.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(deepPurpleCard)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔥 ${product.calories}",
                            fontFamily = BreakFonts.Inter,
                            fontSize = 11.sp,
                            color = BreakColors.Amber
                        )
                    }
                }

                // Stock
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (stock > 0) Color(0x334CAF50) else Color(0x33F44336))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (stock > 0) "Disponibles: $stock" else "Agotado",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = if (stock > 0) Color(0xFF81C784) else Color(0xFFE57373)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Descripción detallada
            Text(
                text = "Descripción",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = textMuted
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.description.ifBlank { "Sin descripción disponible." },
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            // Campo de notas / instrucciones especiales
            Text(
                text = "Instrucciones especiales",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = textMuted
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = {
                    Text(
                        text = "Ej. Salsa aparte, sin cebolla, bien caliente...",
                        color = textMuted.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = deepPurpleCard,
                    unfocusedContainerColor = deepPurpleCard,
                    focusedBorderColor = BreakColors.Amber,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3
            )

            Spacer(Modifier.height(20.dp))

            // Selector de cantidad y Botón de añadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector de cantidad (- N +)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(deepPurpleCard)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (quantity > 1) Color(0x33FFFFFF) else Color(0x11FFFFFF))
                            .clickable(enabled = quantity > 1) { quantity-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = if (quantity > 1) Color.White else textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (quantity < stock) BreakColors.Amber else Color(0x11FFFFFF))
                            .clickable(enabled = quantity < stock) { quantity++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = if (quantity < stock) Color.White else textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Botón Agregar
                Button(
                    onClick = {
                        onAddToCart(product, quantity, notes)
                        onDismiss()
                    },
                    enabled = stock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreakColors.Amber,
                        disabledContainerColor = Color(0xFF555555)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (stock > 0) "Agregar • ${(product.price * quantity).toCopPrice()}" else "Agotado",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (stock > 0) Color(0xFF1E1B26) else Color.LightGray
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
