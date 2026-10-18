package com.example.breakapp.main.data

import androidx.annotation.DrawableRes
import java.text.NumberFormat
import java.util.Locale

enum class FoodCategory(val label: String) {
    ALMUERZOS("Almuerzos"),
    SNACKS("Snacks"),
    BEBIDAS("Bebidas"),
    POSTRES("Postres")
}

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val timeRange: String,
    @DrawableRes val logoRes: Int,
    val schedule: String = "Lun - Vie • 7:00 AM - 8:00 PM",
    val location: String = "Módulo 2 y 7 en Planta Baja Ext. 1933",
    @DrawableRes val bannerRes: Int = logoRes
)

data class Product(
    val id: String,
    val name: String,
    val description: String,
    /** Precio en pesos colombianos, sin decimales. */
    val price: Int,
    val prepTime: String,
    val available: Boolean,
    @DrawableRes val imageRes: Int,
    val category: FoodCategory,
    val restaurantId: String,
    /** true = aparece en las tarjetas del Inicio. Todos los productos entran a la búsqueda. */
    val featured: Boolean = true,
    val calories: String = ""
)

/** Un producto encontrado junto con el restaurante que lo vende. */
data class ProductMatch(
    val product: Product,
    val restaurant: Restaurant
)

data class SearchResults(
    val products: List<ProductMatch>,
    val restaurants: List<Restaurant>
) {
    val isEmpty: Boolean get() = products.isEmpty() && restaurants.isEmpty()
}

/** 12000 -> "$12.000" */
fun Int.toCopPrice(): String =
    "\$" + NumberFormat.getIntegerInstance(Locale("es", "CO")).format(this)
