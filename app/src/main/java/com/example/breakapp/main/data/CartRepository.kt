package com.example.breakapp.main.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

object CartRepository {

    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    private val _failedOrder = MutableStateFlow<Order?>(null)
    val failedOrder: StateFlow<Order?> = _failedOrder.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun setRestaurant(id: String, name: String, location: String, estimatedTime: String = "3-5 min") {
        if (_cartState.value.restaurantId != id && _cartState.value.items.isNotEmpty()) {
            // Si cambia de restaurante, limpiamos el carrito previo
            _cartState.value = CartState(
                restaurantId = id,
                restaurantName = name,
                restaurantLocation = location,
                estimatedTime = estimatedTime
            )
        } else {
            _cartState.update {
                it.copy(
                    restaurantId = id,
                    restaurantName = name,
                    restaurantLocation = location,
                    estimatedTime = estimatedTime
                )
            }
        }
    }

    fun addItem(product: Product, quantity: Int = 1, notes: String = "") {
        if (quantity <= 0) {
            removeItem(product.id)
            return
        }
        _cartState.update { current ->
            val existing = current.items.find { it.product.id == product.id }
            val updatedItems = if (existing != null) {
                current.items.map {
                    if (it.product.id == product.id) {
                        val finalNotes = if (notes.isNotBlank()) notes else existing.notes
                        it.copy(quantity = quantity, notes = finalNotes)
                    } else it
                }
            } else {
                current.items + CartItem(product = product, quantity = quantity, notes = notes)
            }
            current.copy(items = updatedItems)
        }
    }

    fun updateNotes(productId: String, notes: String) {
        _cartState.update { current ->
            current.copy(
                items = current.items.map {
                    if (it.product.id == productId) it.copy(notes = notes) else it
                }
            )
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(productId)
            return
        }
        _cartState.update { current ->
            current.copy(
                items = current.items.map {
                    if (it.product.id == productId) it.copy(quantity = quantity) else it
                }
            )
        }
    }

    fun removeItem(productId: String) {
        _cartState.update { current ->
            current.copy(items = current.items.filterNot { it.product.id == productId })
        }
    }

    fun clearCart() {
        _cartState.update { it.copy(items = emptyList()) }
    }

    fun setFailedOrder(order: Order?) {
        _failedOrder.value = order
    }

    /**
     * Genera un borrador de pedido basado en el carrito actual para pantallas de error o previsualización.
     * NO vacía el carrito ni lo registra en historial ni en Firestore.
     */
    fun createDraftOrder(paymentMethod: PaymentMethod, cardLast4: String? = null): Order {
        val current = _cartState.value
        val orderCode = "#B-${Random.nextInt(100, 999)}"
        val paymentDetail = if (paymentMethod == PaymentMethod.CARD) {
            "Visa •••• ${cardLast4 ?: "4242"}"
        } else {
            "Efectivo o Nequi"
        }
        return Order(
            id = orderCode,
            restaurantId = current.restaurantId ?: "catering_gourmet",
            restaurantName = current.restaurantName.ifBlank { "Catering Gourmet" },
            restaurantLocation = current.restaurantLocation.ifBlank { "Módulo 2 y 7 en Planta Baja" },
            items = current.items,
            total = current.total,
            paymentMethod = paymentMethod,
            cardLast4 = cardLast4,
            paymentMethodDetail = paymentDetail,
            isPaid = false,
            status = OrderStatus.RECIBIDO,
            estimatedTime = current.estimatedTime,
            pickupPoint = "${current.restaurantLocation} Ext. 1933"
        )
    }

    /**
     * Crea un pedido confirmado tras pago exitoso.
     * Guarda la orden, la añade al historial y a Firestore, y vacía el carrito.
     */
    fun createOrder(paymentMethod: PaymentMethod, cardLast4: String? = null): Order {
        val current = _cartState.value
        val orderCode = "#B-${Random.nextInt(100, 999)}"
        val paymentDetail = if (paymentMethod == PaymentMethod.CARD) {
            "Visa •••• ${cardLast4 ?: "4242"}"
        } else {
            "Efectivo o Nequi"
        }
        val newOrder = Order(
            id = orderCode,
            restaurantId = current.restaurantId ?: "catering_gourmet",
            restaurantName = current.restaurantName.ifBlank { "Catering Gourmet" },
            restaurantLocation = current.restaurantLocation.ifBlank { "Módulo 2 y 7 en Planta Baja" },
            items = current.items,
            total = current.total,
            paymentMethod = paymentMethod,
            cardLast4 = cardLast4,
            paymentMethodDetail = paymentDetail,
            isPaid = paymentMethod == PaymentMethod.CARD,
            status = OrderStatus.RECIBIDO,
            estimatedTime = current.estimatedTime,
            pickupPoint = "${current.restaurantLocation} Ext. 1933"
        )

        _failedOrder.value = null
        _activeOrder.value = newOrder
        _orderHistory.update { listOf(newOrder) + it }
        clearCart()

        // Registro de la orden en Firestore
        saveOrderToFirestore(newOrder)

        return newOrder
    }

    private fun saveOrderToFirestore(order: Order) {
        val user = auth.currentUser
        val orderData = hashMapOf(
            "id" to order.id,
            "userId" to (user?.uid ?: "anonimo"),
            "restaurantId" to order.restaurantId,
            "restaurantName" to order.restaurantName,
            "total" to order.total,
            "paymentMethod" to order.paymentMethod.name,
            "cardLast4" to (order.cardLast4 ?: ""),
            "paymentMethodDetail" to order.paymentMethodDetail,
            "isPaid" to order.isPaid,
            "status" to order.status.name,
            "date" to order.date,
            "itemCount" to order.items.sumOf { it.quantity }
        )

        db.collection("pedidos")
            .document(order.id)
            .set(orderData)
            .addOnSuccessListener { /* Pedido registrado exitosamente en Firestore */ }
            .addOnFailureListener { /* Fallback silencioso en caso de error de red */ }
    }

    fun advanceOrderStatus(status: OrderStatus) {
        _activeOrder.update { it?.copy(status = status) }
    }
}
