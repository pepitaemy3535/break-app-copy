package com.example.breakapp.main.cart

import androidx.lifecycle.ViewModel
import com.example.breakapp.main.data.CartItem
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.CartState
import com.example.breakapp.main.data.Order
import com.example.breakapp.main.data.PaymentMethod
import com.example.breakapp.main.data.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartUiState(
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,
    val cardNumber: String = "",
    val cardError: String? = null,
    val isProcessing: Boolean = false,
    val orderCreated: Order? = null
) {
    val rawCardNumber: String get() = cardNumber.filter { it.isDigit() }
    val isCardValid: Boolean get() = rawCardNumber.length == 16
    val canConfirm: Boolean
        get() = when (selectedPaymentMethod) {
            PaymentMethod.CARD -> isCardValid
            PaymentMethod.CASH_NEQUI -> true
        }
}

class CartViewModel(
    private val cartRepo: CartRepository = CartRepository
) : ViewModel() {

    val cartState: StateFlow<CartState> = cartRepo.cartState

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method, cardError = null) }
    }

    fun onCardNumberChange(number: String) {
        val digits = number.filter { it.isDigit() }.take(16)
        val formatted = digits.chunked(4).joinToString(" ")
        _uiState.update {
            it.copy(
                cardNumber = formatted,
                cardError = if (digits.length in 1..15) "Debe tener 16 dígitos" else null
            )
        }
    }

    fun updateQuantity(item: CartItem, newQty: Int) {
        val availableStock = StockRepository.getStock(item.product.id)
        if (newQty > availableStock) {
            return
        }
        cartRepo.updateQuantity(item.product.id, newQty)
    }

    fun updateNotes(item: CartItem, notes: String) {
        cartRepo.updateNotes(item.product.id, notes)
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

        val method = _uiState.value.selectedPaymentMethod
        val digits = _uiState.value.rawCardNumber

        if (method == PaymentMethod.CARD && digits.length != 16) {
            _uiState.update { it.copy(cardError = "Ingresa los 16 dígitos de la tarjeta") }
            return
        }

        _uiState.update { it.copy(isProcessing = true) }

        // Simulación controlada para pruebas:
        // Tarjeta terminada en 0000 -> Fondos Insuficientes
        // Tarjeta terminada en 1111 -> Pago Rechazado
        // Cualquier otra -> Pago Exitoso
        if (method == PaymentMethod.CARD && digits.endsWith("0000")) {
            val last4 = digits.takeLast(4)
            val draft = cartRepo.createDraftOrder(method, last4)
            cartRepo.setFailedOrder(draft)
            _uiState.update { it.copy(isProcessing = false) }
            onInsufficientFunds(draft)
            return
        }

        if (method == PaymentMethod.CARD && digits.endsWith("1111")) {
            val last4 = digits.takeLast(4)
            val draft = cartRepo.createDraftOrder(method, last4)
            cartRepo.setFailedOrder(draft)
            _uiState.update { it.copy(isProcessing = false) }
            onCardDeclined(draft)
            return
        }

        val last4 = if (method == PaymentMethod.CARD) digits.takeLast(4) else null
        val itemsToBuy = cartState.value.items.toList()
        val order = cartRepo.createOrder(method, last4)
        StockRepository.decrementStock(itemsToBuy)
        _uiState.update { it.copy(isProcessing = false, orderCreated = order) }
        onSuccess(order)
    }
}
