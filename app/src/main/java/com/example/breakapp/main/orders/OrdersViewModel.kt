package com.example.breakapp.main.orders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.OrderStatus
import com.example.breakapp.main.notifications.OrderNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrdersUiState(
    val selectedRating: Int = 5,
    val showRatingDialog: Boolean = false,
    val ratingSent: Boolean = false,
    val selectedTab: Int = 0, // 0 = En Curso, 1 = Historial
    val showConfirmDeliveryDialog: Boolean = false,
    val orderToConfirm: Order? = null,
    val selectedHistoryOrder: Order? = null,
    val remainingSeconds: Long = 0L,
    val isReadyNotified: Boolean = false,
    val isConfirmNotified: Boolean = false
) {
    val formattedRemainingTime: String
        get() {
            if (remainingSeconds <= 0) return "¡Listo para entrega!"
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return String.format("%02d:%02d min", minutes, seconds)
        }
}

class OrdersViewModel(
    private val cartRepo: CartRepository = CartRepository
) : ViewModel() {

    val activeOrder: StateFlow<Order?> = cartRepo.activeOrder
    val orderHistory: StateFlow<List<Order>> = cartRepo.orderHistory

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Carga el historial inicial desde Firestore o datos locales
        cartRepo.loadOrderHistoryFromFirestore()

        // Observa cambios en la orden activa para iniciar el temporizador
        viewModelScope.launch {
            activeOrder.collect { order ->
                if (order != null && (order.status == OrderStatus.RECIBIDO || order.status == OrderStatus.EN_PREPARACION)) {
                    startPreparationTimer(order)
                } else if (order == null || order.status == OrderStatus.LISTO || order.status == OrderStatus.ENTREGADO) {
                    timerJob?.cancel()
                    if (order?.status == OrderStatus.LISTO) {
                        _uiState.update { it.copy(remainingSeconds = 0L) }
                    }
                }
            }
        }
    }

    private fun startPreparationTimer(order: Order) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // Pasamos a EN_PREPARACION tras 3 segundos si estaba en RECIBIDO
            if (order.status == OrderStatus.RECIBIDO) {
                delay(3000)
                cartRepo.advanceOrderStatus(OrderStatus.EN_PREPARACION)
            }

            val totalSeconds = (order.prepTimeMinutes * 60L).coerceAtLeast(60L)
            val elapsedSeconds = ((System.currentTimeMillis() - order.createdAt) / 1000).coerceAtLeast(0L)
            var remaining = (totalSeconds - elapsedSeconds).coerceAtLeast(0L)

            // Si es la primera vez que se carga y queda tiempo
            _uiState.update { it.copy(remainingSeconds = remaining) }

            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { it.copy(remainingSeconds = remaining) }
            }

            // Al cumplirse el tiempo, cambia a LISTO
            if (activeOrder.value?.status == OrderStatus.EN_PREPARACION || activeOrder.value?.status == OrderStatus.RECIBIDO) {
                cartRepo.advanceOrderStatus(OrderStatus.LISTO)
            }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectHistoryOrder(order: Order?) {
        _uiState.update { it.copy(selectedHistoryOrder = order) }
    }

    fun updateStatus(status: OrderStatus) {
        cartRepo.advanceOrderStatus(status)
    }

    /**
     * Permite simular de inmediato que el tiempo de preparación se completó.
     * Pone el pedido en estado LISTO y dispara las notificaciones del sistema.
     */
    fun simulateTimeComplete(context: Context) {
        val current = activeOrder.value ?: return
        cartRepo.advanceOrderStatus(OrderStatus.LISTO)
        _uiState.update {
            it.copy(
                remainingSeconds = 0L,
                isReadyNotified = true,
                isConfirmNotified = true
            )
        }

        // 1. Notificación: "Ya puedes recoger tu pedido"
        OrderNotificationHelper.showOrderReadyNotification(
            context = context,
            orderId = current.id,
            restaurantName = current.restaurantName,
            pickupPoint = current.pickupPoint
        )

        // 2. Notificación consecutiva: "Confirmar que ya tienes el pedido"
        viewModelScope.launch {
            delay(1500)
            OrderNotificationHelper.showDeliveryConfirmationNotification(
                context = context,
                orderId = current.id,
                restaurantName = current.restaurantName
            )
        }
    }

    /**
     * Abre el diálogo de confirmación de entrega para el pedido indicado.
     */
    fun openConfirmDeliveryDialog(order: Order) {
        _uiState.update {
            it.copy(
                showConfirmDeliveryDialog = true,
                orderToConfirm = order
            )
        }
    }

    fun closeConfirmDeliveryDialog() {
        _uiState.update {
            it.copy(
                showConfirmDeliveryDialog = false,
                orderToConfirm = null
            )
        }
    }

    /**
     * Confirma que el estudiante tiene el pedido en sus manos.
     * Pasa el estado a ENTREGADO y abre el diálogo de calificación.
     */
    fun confirmDelivery(orderId: String) {
        cartRepo.confirmDelivery(orderId)
        _uiState.update {
            it.copy(
                showConfirmDeliveryDialog = false,
                orderToConfirm = null,
                showRatingDialog = true,
                ratingSent = false
            )
        }
    }

    fun openRatingDialog() {
        _uiState.update { it.copy(showRatingDialog = true) }
    }

    fun closeRatingDialog() {
        _uiState.update { it.copy(showRatingDialog = false) }
    }

    fun setRating(rating: Int) {
        _uiState.update { it.copy(selectedRating = rating) }
    }

    fun submitRating() {
        _uiState.update { it.copy(showRatingDialog = false, ratingSent = true) }
    }

    fun reorder(order: Order) {
        if (order.items.isNotEmpty()) {
            cartRepo.setRestaurant(
                id = order.restaurantId,
                name = order.restaurantName,
                location = order.restaurantLocation,
                estimatedTime = order.estimatedTime
            )
            order.items.forEach { cartRepo.addItem(it.product, it.quantity, it.notes) }
        }
    }
}
