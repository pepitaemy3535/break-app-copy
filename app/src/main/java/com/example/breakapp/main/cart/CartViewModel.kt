package com.example.breakapp.main.cart

import androidx.lifecycle.ViewModel
import com.example.breakapp.main.data.CartItem
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.CartState
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import com.example.breakapp.main.data.StockRepository

data class CartUiState(
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val cardNumber: String = "4532 8901 2345 6789",
    val isProcessing: Boolean = false,
    val orderCreated: Order? = null
)

class CartViewModel(
    private val cartRepo: CartRepository = CartRepository
) : ViewModel() {

    val cartState: StateFlow<CartState> = cartRepo.cartState

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun onCardNumberChange(number: String) {
        _uiState.update { it.copy(cardNumber = number) }
    }

    fun updateQuantity(item: CartItem, newQty: Int) {
        val availableStock = StockRepository.getStock(item.product.id)
        if (newQty > availableStock) {
            return
        }
        cartRepo.updateQuantity(item.product.id, newQty)
    }

    fun removeItem(item: CartItem) {
        cartRepo.removeItem(item.product.id)
    }

    fun confirmOrder(
        onSuccess: (Order) -> Unit,
        onInsufficientFunds: (Order) -> Unit = {},
        onCardDeclined: (Order) -> Unit = {}
    ) {
        if (cartState.value.items.isEmpty()) return

        _uiState.update { it.copy(isProcessing = true) }

        // Simulación controlada para pruebas:
        // Tarjeta terminada en 0000 -> Fondos Insuficientes
        // Tarjeta terminada en 1111 -> Pago Rechazado
        // Cualquier otra -> Pago Exitoso
        val trimmed = _uiState.value.cardNumber.trim().replace(" ", "")
        if (_uiState.value.selectedPaymentMethod == PaymentMethod.CARD && trimmed.endsWith("0000")) {
            val order = cartRepo.createOrder(_uiState.value.selectedPaymentMethod)
            _uiState.update { it.copy(isProcessing = false) }
            onInsufficientFunds(order)
            return
        }

        if (_uiState.value.selectedPaymentMethod == PaymentMethod.CARD && trimmed.endsWith("1111")) {
            val order = cartRepo.createOrder(_uiState.value.selectedPaymentMethod)
            _uiState.update { it.copy(isProcessing = false) }
            onCardDeclined(order)
            return
        }

        val itemsToBuy = cartState.value.items.toList()
        val order = cartRepo.createOrder(_uiState.value.selectedPaymentMethod)
        StockRepository.decrementStock(itemsToBuy)
        _uiState.update { it.copy(isProcessing = false, orderCreated = order) }
        onSuccess(order)
    }
}
