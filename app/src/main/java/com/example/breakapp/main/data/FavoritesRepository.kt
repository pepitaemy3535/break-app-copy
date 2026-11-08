package com.example.breakapp.main.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositorio en memoria para gestionar los productos y restaurantes favoritos del usuario.
 */
object FavoritesRepository {

    private val _favoriteProductIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteProductIds: StateFlow<Set<String>> = _favoriteProductIds.asStateFlow()

    private val _favoriteRestaurantIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteRestaurantIds: StateFlow<Set<String>> = _favoriteRestaurantIds.asStateFlow()

    fun toggleProductFavorite(productId: String) {
        _favoriteProductIds.update { current ->
            if (productId in current) current - productId else current + productId
        }
    }

    fun isProductFavorite(productId: String): Boolean =
        productId in _favoriteProductIds.value

    fun toggleRestaurantFavorite(restaurantId: String) {
        _favoriteRestaurantIds.update { current ->
            if (restaurantId in current) current - restaurantId else current + restaurantId
        }
    }

    fun isRestaurantFavorite(restaurantId: String): Boolean =
        restaurantId in _favoriteRestaurantIds.value
}
