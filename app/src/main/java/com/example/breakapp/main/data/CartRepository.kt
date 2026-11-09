package com.example.breakapp.main.data

import android.util.Log
import com.example.breakapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private val _orderHistory = MutableStateFlow<List<Order>>(generateInitialHistory())
    val orderHistory: StateFlow<List<Order>> = _orderHistory.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun setRestaurant(id: String, name: String, location: String, estimatedTime: String = "3-5 min") {
        // Solo actualizamos la información si el carrito está vacío o pertenece al mismo restaurante.
        // Nunca se borran los productos existentes simplemente por navegar o entrar a la pantalla de otro restaurante.
        if (_cartState.value.items.isEmpty() || _cartState.value.restaurantId == id) {
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

    fun canAddItem(product: Product): Boolean {
        val currentRestId = _cartState.value.restaurantId
        return currentRestId == null || _cartState.value.items.isEmpty() || currentRestId == product.restaurantId
    }

    fun clearAndAdd(product: Product, quantity: Int = 1, notes: String = "", restaurant: Restaurant? = null) {
        val rest = restaurant ?: menuRepository.restaurantById(product.restaurantId)
        _cartState.value = CartState(
            items = if (quantity > 0) listOf(CartItem(product = product, quantity = quantity, notes = notes)) else emptyList(),
            restaurantId = rest?.id ?: product.restaurantId,
            restaurantName = rest?.name ?: "Restaurante",
            restaurantLocation = rest?.location ?: "",
            estimatedTime = rest?.timeRange ?: "3-5 min"
        )
    }

    fun addItem(product: Product, quantity: Int = 1, notes: String = "") {
        if (quantity <= 0) {
            removeItem(product.id)
            return
        }
        _cartState.update { current ->
            val isFirstItem = current.items.isEmpty() || current.restaurantId == null
            val rest = if (isFirstItem) menuRepository.restaurantById(product.restaurantId) else null

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
            current.copy(
                items = updatedItems,
                restaurantId = if (isFirstItem && rest != null) rest.id else current.restaurantId,
                restaurantName = if (isFirstItem && rest != null) rest.name else current.restaurantName,
                restaurantLocation = if (isFirstItem && rest != null) rest.location else current.restaurantLocation,
                estimatedTime = if (isFirstItem && rest != null) rest.timeRange else current.estimatedTime
            )
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
     * Calcula el tiempo de preparación del pedido en base al producto con mayor tiempo de preparación del carrito.
     * Si un producto indica "1 hora", el pedido indicará "1 hora" (60 min).
     */
    private fun calculatePrepTime(items: List<CartItem>, fallbackTime: String): Pair<String, Int> {
        if (items.isEmpty()) {
            val minutes = parsePrepTimeToMinutes(fallbackTime)
            return fallbackTime to minutes
        }
        var maxMinutes = 0
        var maxString = fallbackTime
        for (item in items) {
            val minutes = parsePrepTimeToMinutes(item.product.prepTime)
            if (minutes > maxMinutes) {
                maxMinutes = minutes
                maxString = item.product.prepTime
            }
        }
        if (maxMinutes <= 0) {
            maxMinutes = parsePrepTimeToMinutes(fallbackTime)
            maxString = fallbackTime
        }
        return maxString to maxMinutes
    }

    /**
     * Genera un borrador de pedido basado en el carrito actual para pantallas de error o previsualización.
     */
    fun createDraftOrder(paymentMethod: PaymentMethod, cardLast4: String? = null): Order {
        val current = _cartState.value
        val orderCode = "#B-${Random.nextInt(100, 999)}"
        val paymentDetail = if (paymentMethod == PaymentMethod.CARD) {
            "Visa •••• ${cardLast4 ?: "4242"}"
        } else {
            "Efectivo o Nequi"
        }
        val (calcTimeStr, calcMinutes) = calculatePrepTime(current.items, current.estimatedTime)
        val now = System.currentTimeMillis()
        val user = auth.currentUser

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
            createdAt = now,
            date = formatOrderDate(now),
            time = formatOrderTime(now),
            estimatedTime = calcTimeStr,
            prepTimeMinutes = calcMinutes,
            pickupPoint = "${current.restaurantLocation} Ext. 1933",
            studentName = user?.displayName ?: "Katy Lozano",
            studentEmail = user?.email ?: "correo@gmail.com"
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
        val (calcTimeStr, calcMinutes) = calculatePrepTime(current.items, current.estimatedTime)
        val now = System.currentTimeMillis()
        val user = auth.currentUser

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
            createdAt = now,
            date = formatOrderDate(now),
            time = formatOrderTime(now),
            estimatedTime = calcTimeStr,
            prepTimeMinutes = calcMinutes,
            pickupPoint = "${current.restaurantLocation} Ext. 1933",
            studentName = user?.displayName ?: "Katy Lozano",
            studentEmail = user?.email ?: "correo@gmail.com",
            isDeliveryConfirmed = false,
            notifications = listOf(
                OrderNotification("1", "¡Tu pedido ha sido recibido!", timestamp = formatOrderTime(now)),
                OrderNotification("2", "En preparación (Tiempo estimado: $calcTimeStr)", timestamp = formatOrderTime(now))
            )
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
        val itemsMapList = order.items.map {
            mapOf(
                "productId" to it.product.id,
                "productName" to it.product.name,
                "price" to it.product.price,
                "quantity" to it.quantity,
                "notes" to it.notes,
                "prepTime" to it.product.prepTime
            )
        }

        val orderData = hashMapOf(
            "id" to order.id,
            "userId" to (user?.uid ?: "anonimo"),
            "studentName" to order.studentName,
            "studentEmail" to order.studentEmail,
            "restaurantId" to order.restaurantId,
            "restaurantName" to order.restaurantName,
            "restaurantLocation" to order.restaurantLocation,
            "total" to order.total,
            "paymentMethod" to order.paymentMethod.name,
            "cardLast4" to (order.cardLast4 ?: ""),
            "paymentMethodDetail" to order.paymentMethodDetail,
            "isPaid" to order.isPaid,
            "status" to order.status.name,
            "createdAt" to order.createdAt,
            "date" to order.date,
            "time" to order.time,
            "estimatedTime" to order.estimatedTime,
            "prepTimeMinutes" to order.prepTimeMinutes,
            "pickupPoint" to order.pickupPoint,
            "isDeliveryConfirmed" to order.isDeliveryConfirmed,
            "itemCount" to order.items.sumOf { it.quantity },
            "items" to itemsMapList
        )

        db.collection("pedidos")
            .document(order.id)
            .set(orderData)
            .addOnSuccessListener {
                Log.d("CartRepository", "Pedido ${order.id} guardado en Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("CartRepository", "Error al guardar pedido en Firestore", e)
            }
    }

    /**
     * Avanza el estado del pedido activo y actualiza la lista del historial y Firestore.
     */
    fun advanceOrderStatus(status: OrderStatus) {
        val current = _activeOrder.value ?: return
        val now = System.currentTimeMillis()
        val updatedNotifs = current.notifications.toMutableList()

        if (status == OrderStatus.LISTO) {
            val exists = updatedNotifs.any { it.message.contains("LISTO", ignoreCase = true) }
            if (!exists) {
                updatedNotifs.add(
                    OrderNotification(
                        id = "${updatedNotifs.size + 1}",
                        message = "Tu pedido está LISTO para recoger en ${current.pickupPoint}",
                        timestamp = formatOrderTime(now)
                    )
                )
            }
        }

        val updatedOrder = current.copy(
            status = status,
            notifications = updatedNotifs
        )

        _activeOrder.value = updatedOrder
        _orderHistory.update { list ->
            list.map { if (it.id == updatedOrder.id) updatedOrder else it }
        }

        db.collection("pedidos").document(updatedOrder.id)
            .update("status", status.name)
            .addOnFailureListener { /* Fallback silencioso */ }
    }

    /**
     * Confirma que el estudiante ha recibido el pedido.
     * Cambia el estado a ENTREGADO, guarda la fecha/hora de confirmación y actualiza Firestore.
     */
    fun confirmDelivery(orderId: String) {
        val now = System.currentTimeMillis()
        val currentActive = _activeOrder.value
        val targetOrder = if (currentActive?.id == orderId) currentActive else _orderHistory.value.find { it.id == orderId } ?: return

        val updatedNotifs = targetOrder.notifications.toMutableList().apply {
            add(
                OrderNotification(
                    id = "${size + 1}",
                    message = "¡Confirmaste la entrega de tu pedido! Disfruta tu comida.",
                    timestamp = formatOrderTime(now)
                )
            )
            add(
                OrderNotification(
                    id = "${size + 2}",
                    message = "Califica tu pedido",
                    isRating = true,
                    timestamp = formatOrderTime(now)
                )
            )
        }

        val completedOrder = targetOrder.copy(
            status = OrderStatus.ENTREGADO,
            isDeliveryConfirmed = true,
            deliveryConfirmedAt = now,
            notifications = updatedNotifs
        )

        if (_activeOrder.value?.id == orderId) {
            _activeOrder.value = completedOrder
        }

        _orderHistory.update { list ->
            list.map { if (it.id == orderId) completedOrder else it }
        }

        db.collection("pedidos").document(orderId)
            .update(
                mapOf(
                    "status" to OrderStatus.ENTREGADO.name,
                    "isDeliveryConfirmed" to true,
                    "deliveryConfirmedAt" to now
                )
            )
            .addOnFailureListener { /* Fallback */ }
    }

    /**
     * Carga el historial de pedidos desde Firestore para el estudiante conectado.
     */
    fun loadOrderHistoryFromFirestore() {
        val user = auth.currentUser ?: return
        db.collection("pedidos")
            .whereEqualTo("userId", user.uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val loaded = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: doc.id
                            val restaurantId = doc.getString("restaurantId") ?: "catering_gourmet"
                            val restaurantName = doc.getString("restaurantName") ?: "Catering Gourmet"
                            val restaurantLocation = doc.getString("restaurantLocation") ?: "Módulo 2 y 7 en Planta Baja"
                            val total = doc.getLong("total")?.toInt() ?: 0
                            val paymentMethodStr = doc.getString("paymentMethod") ?: "CASH_NEQUI"
                            val paymentMethod = try { PaymentMethod.valueOf(paymentMethodStr) } catch (_: Exception) { PaymentMethod.CASH_NEQUI }
                            val cardLast4 = doc.getString("cardLast4")
                            val paymentMethodDetail = doc.getString("paymentMethodDetail") ?: ""
                            val isPaid = doc.getBoolean("isPaid") ?: false
                            val statusStr = doc.getString("status") ?: "RECIBIDO"
                            val status = try { OrderStatus.valueOf(statusStr) } catch (_: Exception) { OrderStatus.RECIBIDO }
                            val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            val date = doc.getString("date") ?: formatOrderDate(createdAt)
                            val time = doc.getString("time") ?: formatOrderTime(createdAt)
                            val estimatedTime = doc.getString("estimatedTime") ?: "15 min"
                            val prepTimeMinutes = doc.getLong("prepTimeMinutes")?.toInt() ?: parsePrepTimeToMinutes(estimatedTime)
                            val pickupPoint = doc.getString("pickupPoint") ?: "$restaurantLocation Ext. 1933"
                            val studentName = doc.getString("studentName") ?: (user.displayName ?: "Katy Lozano")
                            val studentEmail = doc.getString("studentEmail") ?: (user.email ?: "correo@gmail.com")
                            val isDeliveryConfirmed = doc.getBoolean("isDeliveryConfirmed") ?: (status == OrderStatus.ENTREGADO)

                            Order(
                                id = id,
                                restaurantId = restaurantId,
                                restaurantName = restaurantName,
                                restaurantLocation = restaurantLocation,
                                items = emptyList(), // Items básicos del historial
                                total = total,
                                paymentMethod = paymentMethod,
                                cardLast4 = cardLast4,
                                paymentMethodDetail = paymentMethodDetail,
                                isPaid = isPaid,
                                status = status,
                                createdAt = createdAt,
                                date = date,
                                time = time,
                                estimatedTime = estimatedTime,
                                prepTimeMinutes = prepTimeMinutes,
                                pickupPoint = pickupPoint,
                                studentName = studentName,
                                studentEmail = studentEmail,
                                isDeliveryConfirmed = isDeliveryConfirmed
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (loaded.isNotEmpty()) {
                        _orderHistory.value = loaded
                    }
                }
            }
            .addOnFailureListener {
                // Si la consulta compuesta falla o está offline, conserva el historial en memoria
            }
    }

    /**
     * Pedidos de ejemplo iniciales para que el estudiante siempre vea historial detallado con fecha y hora.
     */
    private fun generateInitialHistory(): List<Order> {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - 24 * 60 * 60 * 1000
        val twoDaysAgo = now - 48 * 60 * 60 * 1000

        val item1 = CartItem(
            product = Product(
                id = "bandeja_paisa_break",
                name = "Bandeja Paisa Break",
                description = "Arroz, frijoles, carne molida, huevo, arepa y plátano",
                price = 18500,
                prepTime = "12 min",
                available = true,
                imageRes = R.drawable.main_img_food_bandeja_paisa,
                category = FoodCategory.ALMUERZOS,
                restaurantId = "catering_gourmet"
            ),
            quantity = 1
        )
        val item2 = CartItem(
            product = Product(
                id = "cocacola",
                name = "Coca-cola personal",
                description = "Bebida refrescante",
                price = 3500,
                prepTime = "2 min",
                available = true,
                imageRes = R.drawable.main_img_food_cocacola,
                category = FoodCategory.BEBIDAS,
                restaurantId = "catering_gourmet"
            ),
            quantity = 1
        )
        val item3 = CartItem(
            product = Product(
                id = "pizza_pepperoni",
                name = "Pizza Pepperoni Personal",
                description = "Queso mozzarella y abundante pepperoni",
                price = 14000,
                prepTime = "15 min",
                available = true,
                imageRes = R.drawable.img_pizza_pepperoni,
                category = FoodCategory.ALMUERZOS,
                restaurantId = "peoples_pizza"
            ),
            quantity = 1
        )

        return listOf(
            Order(
                id = "#B-189",
                restaurantId = "catering_gourmet",
                restaurantName = "Catering Gourmet",
                restaurantLocation = "Módulo 2 y 7 en Planta Baja",
                items = listOf(item1, item2),
                total = 22000,
                paymentMethod = PaymentMethod.CARD,
                cardLast4 = "4242",
                paymentMethodDetail = "Visa •••• 4242",
                isPaid = true,
                status = OrderStatus.ENTREGADO,
                createdAt = oneDayAgo,
                date = formatOrderDate(oneDayAgo),
                time = "01:25 PM",
                estimatedTime = "12 min",
                prepTimeMinutes = 12,
                pickupPoint = "Módulo 2 y 7 en Planta Baja Ext. 1933",
                studentName = "Katy Lozano",
                studentEmail = "correo@gmail.com",
                isDeliveryConfirmed = true,
                deliveryConfirmedAt = oneDayAgo + 15 * 60 * 1000
            ),
            Order(
                id = "#B-142",
                restaurantId = "peoples_pizza",
                restaurantName = "People’s pizza",
                restaurantLocation = "Módulo 2, Planta Baja",
                items = listOf(item3),
                total = 14000,
                paymentMethod = PaymentMethod.CASH_NEQUI,
                paymentMethodDetail = "Efectivo o Nequi",
                isPaid = true,
                status = OrderStatus.ENTREGADO,
                createdAt = twoDaysAgo,
                date = formatOrderDate(twoDaysAgo),
                time = "06:10 PM",
                estimatedTime = "15 min",
                prepTimeMinutes = 15,
                pickupPoint = "Módulo 2, Planta Baja",
                studentName = "Katy Lozano",
                studentEmail = "correo@gmail.com",
                isDeliveryConfirmed = true,
                deliveryConfirmedAt = twoDaysAgo + 18 * 60 * 1000
            )
        )
    }
}
