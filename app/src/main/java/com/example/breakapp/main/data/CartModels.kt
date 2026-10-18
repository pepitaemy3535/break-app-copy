package com.example.breakapp.main.data

/**
 * Representa un producto dentro del carrito de compras.
 */
data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val notes: String = "Sin azúcar"
) {
    val totalPrice: Int get() = product.price * quantity
}

/**
 * Estado general del carrito de compras.
 */
data class CartState(
    val items: List<CartItem> = emptyList(),
    val restaurantId: String? = null,
    val restaurantName: String = "Catering Gourmet",
    val restaurantLocation: String = "Módulo 2 y 7 en Planta Baja",
    val estimatedTime: String = "3 - 5 min"
) {
    val subtotal: Int get() = items.sumOf { it.totalPrice }
    val total: Int get() = subtotal
    val totalItemsCount: Int get() = items.sumOf { it.quantity }
    val isEmpty: Boolean get() = items.isEmpty()
}
