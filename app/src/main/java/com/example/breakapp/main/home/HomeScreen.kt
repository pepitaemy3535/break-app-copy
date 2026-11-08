package com.example.breakapp.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.components.BreakHeader
import com.example.breakapp.main.components.ProductCard
import com.example.breakapp.main.components.RestaurantCardVertical
import com.example.breakapp.main.data.FoodCategory
import com.example.breakapp.main.data.MenuRepository
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

/**
 * Pantalla de Inicio (Figma: Home - Almuerzos / Snacks / Bebidas / Postres).
 * Las 4 variantes son la misma pantalla; solo cambia la categoría seleccionada.
 */
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSeeAllRestaurants: () -> Unit,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},       // sin pantalla de detalle todavía
    onRestaurantClick: (Restaurant) -> Unit = {}, // sin pantalla de detalle todavía
    onRecommendationsClick: () -> Unit = {},
    repository: MenuRepository = menuRepository
) {
    var selected by rememberSaveable { mutableStateOf(FoodCategory.ALMUERZOS) }
    val products = remember(selected) { repository.featuredProducts(selected) }
    val restaurants = remember { repository.restaurants() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        BreakHeader()

        HomeSearchPill(onClick = onSearchClick)

        Spacer(Modifier.height(31.dp))

        CategoryRow(selected = selected, onSelect = { selected = it })

        Spacer(Modifier.height(24.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(product = product, onClick = { onProductClick(product) })
            }
        }

        Spacer(Modifier.height(24.dp))

        AiAssistantHomeBanner(onClick = onRecommendationsClick)

        Spacer(Modifier.height(18.dp))

        FastOrderBanner()

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cafeterías y Restaurantes",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Ver todos",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = BreakColors.Purple,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeAllRestaurants)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(restaurants, key = { it.id }) { restaurant ->
                RestaurantCardVertical(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

/** Barra "Buscar restaurantes o platos..." del Inicio: al tocarla se abre la pantalla de búsqueda. */
@Composable
private fun HomeSearchPill(onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .height(46.dp)
            .clip(shape)
            .background(Color.White)
            .border(4.dp, BreakColors.Purple, shape)
            .clickable(onClick = onClick)
            .padding(start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.main_ic_search_purple),
            contentDescription = "Buscar",
            modifier = Modifier.size(width = 24.dp, height = 23.dp)
        )
        Spacer(Modifier.width(15.dp))
        Text(
            text = "Buscar restaurantes o platos...",
            fontFamily = BreakFonts.Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryRow(
    selected: FoodCategory,
    onSelect: (FoodCategory) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FoodCategory.values().forEach { category ->
            val isSelected = category == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(40.dp))
                    .background(if (isSelected) BreakColors.Amber else BreakColors.Purple)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.label,
                    fontFamily = BreakFonts.Jersey25,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

/** "¿Tienes poco tiempo entre clases?" — por ahora no abre nada (no hay pantalla de destino). */
@Composable
private fun FastOrderBanner() {
    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BreakColors.Banner)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.main_ic_clock_badge),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "¿Tienes poco tiempo entre clases?",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = BreakColors.Ink
            )
            Text(
                text = "Pide Snacks Express listos para retirar en 5 minutos.",
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = BreakColors.GrayText
            )
        }
    }
}

@Composable
private fun AiAssistantHomeBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF381E72))
            .border(1.dp, BreakColors.Amber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BreakColors.Amber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = BreakColors.Amber,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Asistente Break IA",
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BreakColors.Amber
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BreakColors.Amber.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Recomendaciones",
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = BreakColors.Amber
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "No sabes qué pedir? Toca aquí para ver sugerencias personalizadas de la IA.",
                fontFamily = BreakFonts.Inter,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 15.sp
            )
        }
    }
}
