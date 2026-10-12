package com.example.breakapp.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breakapp.R
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.toCopPrice
import com.example.breakapp.main.theme.BreakColors
import com.example.breakapp.main.theme.BreakFonts

// ----------------------------------------------------------------------------------------------
// Tarjeta de producto (carrusel del Inicio)
// ----------------------------------------------------------------------------------------------

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {} // todavía no hay pantalla de detalle
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .width(320.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, BreakColors.MenuCardBorder, shape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(product.imageRes),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    modifier = Modifier.weight(1f, fill = false),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BreakColors.Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                AvailabilityBadge(available = product.available)
            }
            Text(
                text = product.description,
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = BreakColors.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.price.toCopPrice(),
                    fontFamily = BreakFonts.Inter,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = BreakColors.Purple
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.main_ic_clock_small),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = product.prepTime,
                        fontFamily = BreakFonts.Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = BreakColors.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailabilityBadge(available: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (available) BreakColors.BadgeAvailableBg else BreakColors.BadgeSoldOutBg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (available) "Disponible" else "Agotado",
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = if (available) BreakColors.BadgeAvailableText else BreakColors.BadgeSoldOutText,
            maxLines = 1
        )
    }
}

// ----------------------------------------------------------------------------------------------
// Tarjetas de restaurante
// ----------------------------------------------------------------------------------------------

/** Tarjeta vertical (carrusel "Cafeterías y Restaurantes" del Inicio). */
@Composable
fun RestaurantCardVertical(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {} // todavía no hay pantalla de detalle
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .width(210.dp)
            .restaurantCardStyle(shape)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(restaurant.logoRes),
            contentDescription = restaurant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(900f / 490f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RestaurantName(restaurant.name)
            RatingAndTime(restaurant)
        }
    }
}

/** Tarjeta horizontal (pantalla "Cafeterías y Restaurantes"). */
@Composable
fun RestaurantCardHorizontal(
    restaurant: Restaurant,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {} // todavía no hay pantalla de detalle
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(119.dp)
            .restaurantCardStyle(shape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(restaurant.logoRes),
            contentDescription = restaurant.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RestaurantName(restaurant.name)
            RatingAndTime(restaurant)
        }
    }
}

private fun Modifier.restaurantCardStyle(shape: RoundedCornerShape): Modifier =
    this
        .shadow(
            elevation = 2.dp,
            shape = shape,
            ambientColor = BreakColors.Purple.copy(alpha = 0.10f),
            spotColor = BreakColors.Purple.copy(alpha = 0.10f)
        )
        .clip(shape)
        .background(Color.White)
        .border(1.dp, BreakColors.RestaurantCardBorder, shape)

@Composable
private fun RestaurantName(name: String) {
    Text(
        text = name,
        modifier = Modifier.fillMaxWidth(),
        fontFamily = BreakFonts.Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = BreakColors.Ink,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun RatingAndTime(restaurant: Restaurant) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.main_ic_star),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = restaurant.rating.toString(),
                fontFamily = BreakFonts.Inter,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BreakColors.Ink
            )
        }
        Text(
            text = restaurant.timeRange,
            fontFamily = BreakFonts.Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = BreakColors.GrayText,
            maxLines = 1
        )
    }
}
