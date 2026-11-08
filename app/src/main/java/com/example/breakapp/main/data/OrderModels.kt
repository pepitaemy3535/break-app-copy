package com.example.breakapp.main.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        description = "Tu pedido se está preparando en la cocina..."
    ),
    LISTO(
        title = "Listo para Recoger",
        description = "Punto de recogida: Módulo 2 y 7 Planta Baja"
    ),
    ENTREGADO(
        title = "Pedido Entregado",
        description = "¡Entrega confirmada! Disfruta tu comida."
    )
}

data class OrderNotification(
    val id: String,
    val message: String,
    val isRating: Boolean = false,
    val timestamp: String = ""
)

data class Order(
    val id: String = "#B-204",
    val restaurantId: String = "catering_gourmet",
    val restaurantName: String = "Catering Gourmet",
    val restaurantLocation: String = "Módulo 2 y 7 en Planta Baja",
    val items: List<CartItem> = emptyList(),
    val total: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_NEQUI,
    val cardLast4: String? = null,
    val paymentMethodDetail: String = "",
    val isPaid: Boolean = false,
    val status: OrderStatus = OrderStatus.RECIBIDO,
    val createdAt: Long = System.currentTimeMillis(),
    val date: String = formatOrderDate(createdAt),
    val time: String = formatOrderTime(createdAt),
    val estimatedTime: String = "15 min",
    val prepTimeMinutes: Int = parsePrepTimeToMinutes(estimatedTime),
    val pickupPoint: String = "Módulo 2 y 7 en Planta Baja Ext. 1933",
    val studentName: String = "Katy Lozano",
    val studentEmail: String = "correo@gmail.com",
    val isDeliveryConfirmed: Boolean = false,
    val deliveryConfirmedAt: Long? = null,
    val rating: Int = 0,
    val notifications: List<OrderNotification> = defaultNotifications()
) {
    val formattedPaymentMethod: String
        get() = when {
            paymentMethodDetail.isNotBlank() -> paymentMethodDetail
            paymentMethod == PaymentMethod.CARD -> "Visa •••• ${cardLast4 ?: "4242"}"
            else -> paymentMethod.label
        }

    val formattedDateTime: String
        get() = "$date • $time"
}

fun defaultNotifications(): List<OrderNotification> = listOf(
    OrderNotification("1", "¡Tu pedido ha sido recibido!"),
    OrderNotification("2", "Tiempo de preparación actualizado"),
    OrderNotification("3", "Tu pedido está LISTO para recoger (hace 1 min)"),
    OrderNotification("4", "Califica tu pedido", isRating = true)
)

/** Formatea una marca de tiempo en fecha legible (ej: 07 Nov 2026). */
fun formatOrderDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "CO"))
    return sdf.format(Date(timestamp))
}

/** Formatea una marca de tiempo en hora legible (ej: 08:30 PM). */
fun formatOrderTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale("es", "CO"))
    return sdf.format(Date(timestamp))
}

/**
 * Parsea el texto de tiempo de preparación de un producto o restaurante a minutos enteros.
 * Ejemplos:
 *  - "1 hora" -> 60
 *  - "2 horas" -> 120
 *  - "15-20 min" -> 20
 *  - "3 - 5 min" -> 5
 *  - "12 min" -> 12
 *  - "1 min" -> 1
 */
fun parsePrepTimeToMinutes(prepTime: String): Int {
    val lower = prepTime.lowercase().trim()
    return try {
        when {
            lower.contains("hora") -> {
                val digits = lower.filter { it.isDigit() }
                val hours = digits.toIntOrNull() ?: 1
                hours * 60
            }
            lower.contains("-") -> {
                // Tomar el límite superior: ej. "15-20 min" -> 20
                val parts = lower.split("-")
                val lastPart = parts.lastOrNull()?.filter { it.isDigit() }
                lastPart?.toIntOrNull() ?: 15
            }
            else -> {
                val digits = lower.filter { it.isDigit() }
                digits.toIntOrNull() ?: 10
            }
        }
    } catch (_: Exception) {
        10
    }
}
