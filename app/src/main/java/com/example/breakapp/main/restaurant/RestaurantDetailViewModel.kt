package com.example.breakapp.main.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.CartState
import com.example.breakapp.main.data.FoodCategory
import com.example.breakapp.main.data.MenuRepository
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.menuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import com.example.breakapp.main.data.StockRepository

data class RestaurantDetailUiState(
    val restaurant: Restaurant? = null,
    val allProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val isLoading: Boolean = false
) {
    val categories: List<String> = listOf("Todos", "Cafés", "Repostería", "Bebidas", "Snacks")

    val filteredProducts: List<Product>
        get() {
            var list = allProducts
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                list = list.filter { it.name.lowercase().contains(query) }
            }
            if (selectedCategory != "Todos") {
                list = when (selectedCategory) {
                    "Cafés" -> list.filter { it.category == FoodCategory.BEBIDAS && (it.name.contains("tinto", true) || it.name.contains("capuchino", true) || it.name.contains("café", true)) }
                    "Repostería" -> list.filter { it.category == FoodCategory.POSTRES }
                    "Bebidas" -> list.filter { it.category == FoodCategory.BEBIDAS }
                    "Snacks" -> list.filter { it.category == FoodCategory.SNACKS }
                    else -> list
                }
            }
            return list
        }
}

class RestaurantDetailViewModel(
    private val repository: MenuRepository = menuRepository,
    private val cartRepo: CartRepository = CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState: StateFlow<RestaurantDetailUiState> = _uiState.asStateFlow()

    val cartState: StateFlow<CartState> = cartRepo.cartState
    val stockMap: StateFlow<Map<String, Int>> = StockRepository.stockMap

    fun loadRestaurant(restaurantId: String, initialSearch: String? = null) {
        _uiState.update { it.copy(isLoading = true) }
        val rest = repository.restaurantById(restaurantId) ?: repository.restaurants().first()
        val products = repository.productsForRestaurant(rest.id)

        cartRepo.setRestaurant(
            id = rest.id,
            name = rest.name,
            location = rest.location,
            estimatedTime = rest.timeRange
        )

        _uiState.update {
            it.copy(
                restaurant = rest,
                allProducts = products,
                searchQuery = initialSearch ?: "",
                selectedCategory = "Todos",
                isLoading = false
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelect(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun increaseQuantity(product: Product) {
        val currentQty = cartState.value.items.find { it.product.id == product.id }?.quantity ?: 0
        val available = StockRepository.getStock(product.id)
        if (currentQty < available) {
            cartRepo.addItem(product, currentQty + 1)
        }
    }

    fun decreaseQuantity(product: Product) {
        val currentQty = cartState.value.items.find { it.product.id == product.id }?.quantity ?: 0
        if (currentQty > 0) {
            cartRepo.addItem(product, currentQty - 1)
        }
    }

    fun getProductQuantity(productId: String): Int {
        return cartState.value.items.find { it.product.id == productId }?.quantity ?: 0
    }

    fun getProductStock(productId: String): Int {
        return stockMap.value[productId] ?: StockRepository.getStock(productId)
    }
}
