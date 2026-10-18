package com.example.breakapp.main.data

enum class PaymentMethod(val label: String) {
    CARD("Tarjeta de crédito o débito"),
    CASH_NEQUI("Efectivo o Nequi")
}

enum class OrderStatus(val title: String, val description: String) {
    RECIBIDO(
        title = "Pedido Recibido",
        description = "¡Tu pedido ha sido recibido!"
    ),
    EN_PREPARACION(
        title = "En Preparación",
        description = "Tu pedido se está preparando... Tiempo estimado: 8 min"
    ),
    LISTO(
        title = "Listo para Recoger",
        description = "Punto de recogida: Módulo 2 y 7 Planta Baja"
    )
}

data class OrderNotification(
    val id: String,
    val message: String,
    val isRating: Boolean = false
)

data class Order(
    val id: String = "#B-204",
    val restaurantId: String = "catering_gourmet",
    val restaurantName: String = "Catering Gourmet",
    val restaurantLocation: String = "Módulo 2 y 7 en Planta Baja",
    val items: List<CartItem> = emptyList(),
    val total: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_NEQUI,
    val isPaid: Boolean = false,
    val status: OrderStatus = OrderStatus.RECIBIDO,
    val date: String = "Oct 2026",
    val estimatedTime: String = "3 - 5 min",
    val pickupPoint: String = "Módulo 2 y 7 en Planta Baja Ext. 1933",
    val notifications: List<OrderNotification> = listOf(
        OrderNotification("1", "¡Tu pedido ha sido recibido!"),
        OrderNotification("2", "Tiempo de preparación actualizado"),
        OrderNotification("3", "Tu pedido está LISTO para recoger (hace 1 min)"),
        OrderNotification("4", "Califica tu pedido", isRating = true)
    )
)
