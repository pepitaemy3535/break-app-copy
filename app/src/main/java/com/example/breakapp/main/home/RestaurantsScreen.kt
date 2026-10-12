package com.example.breakapp.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.main.components.BreakHeader
import com.example.breakapp.main.components.RestaurantCardHorizontal
import com.example.breakapp.main.data.MenuRepository
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

/** Figma: "Cafeterías y restaurantes" (se abre con "Ver todos" del Inicio). */
@Composable
fun RestaurantsScreen(
    modifier: Modifier = Modifier,
    onRestaurantClick: (Restaurant) -> Unit = {}, // sin pantalla de detalle todavía
    repository: MenuRepository = menuRepository
) {
    val restaurants = remember { repository.restaurants() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BreakColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        BreakHeader()

        Spacer(Modifier.height(19.dp))

        Text(
            text = "Cafeterías y Restaurantes",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Color.White
        )

        Spacer(Modifier.height(22.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(19.dp)
        ) {
            restaurants.forEach { restaurant ->
                RestaurantCardHorizontal(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
