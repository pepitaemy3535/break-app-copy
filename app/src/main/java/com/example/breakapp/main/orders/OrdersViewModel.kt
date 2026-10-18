package com.example.breakapp.main.orders

import androidx.lifecycle.ViewModel
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OrdersUiState(
    val selectedRating: Int = 5,
    val showRatingDialog: Boolean = false,
    val ratingSent: Boolean = false
)

class OrdersViewModel(
    private val cartRepo: CartRepository = CartRepository
) : ViewModel() {

    val activeOrder: StateFlow<Order?> = cartRepo.activeOrder
    val orderHistory: StateFlow<List<Order>> = cartRepo.orderHistory

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    fun updateStatus(status: OrderStatus) {
        cartRepo.advanceOrderStatus(status)
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
}
