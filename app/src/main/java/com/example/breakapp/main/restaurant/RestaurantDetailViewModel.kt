package com.example.breakapp.main.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breakapp.R
import com.example.breakapp.main.data.CartRepository
import com.example.breakapp.main.data.CartState
import com.example.breakapp.main.data.FoodCategory
import com.example.breakapp.main.data.MenuRepository
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.Restaurant
import com.example.breakapp.main.data.StockRepository
import com.example.breakapp.main.data.menuRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                    "Cafés" -> list.filter { it.category == FoodCategory.BEBIDAS && (it.name.contains("tinto", true) || it.name.contains("capuchino", true) || it.name.contains("café", true) || it.name.contains("frappé", true) || it.name.contains("aromática", true)) }
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
    private val firestore = FirebaseFirestore.getInstance()

    fun loadRestaurant(restaurantId: String, initialSearch: String? = null) {
        _uiState.update {
            it.copy(
                isLoading = true,
                searchQuery = initialSearch ?: "",
                selectedCategory = "Todos"
            )
        }
        val rest = repository.restaurantById(restaurantId) ?: repository.restaurants().first()

        // Mapeo del ID recibido al documento exacto en Firestore
        val firestoreDocId = when {
            restaurantId.contains("fruit", ignoreCase = true) || rest.name.contains("fruit", ignoreCase = true) -> "Fruit&coffee"
            restaurantId.contains("people", ignoreCase = true) || rest.name.contains("people", ignoreCase = true) -> "People’s_pizza"
            restaurantId.contains("world", ignoreCase = true) || rest.name.contains("world", ignoreCase = true) -> "World_Gourmet"
            restaurantId.contains("catering", ignoreCase = true) || rest.name.contains("catering", ignoreCase = true) -> "catering_gourmet"
            else -> restaurantId
        }

        cartRepo.setRestaurant(
            id = rest.id,
            name = rest.name,
            location = rest.location,
            estimatedTime = rest.timeRange
        )

        viewModelScope.launch {
            val productsList = mutableListOf<Product>()
            try {
                val snapshot = firestore.collection("Tiendas")
                    .document(firestoreDocId)
                    .collection("Productos")
                    .get()
                    .await()

                for (document in snapshot.documents) {
                    val id = document.id
                    val nombre = document.getString("nombre") ?: ""

                    // Convertimos el precio a Int para coincidir con Models.kt
                    val precioLong = document.getLong("precio") ?: 0L
                    val precio = precioLong.toInt()

                    val categoriaStr = document.getString("categoría") ?: "Snacks"
                    val calorias = document.getString("calorías") ?: document.getString("calorias") ?: ""
                    val tiempoPrep = document.getString("tiempoPreparacion") ?: "3-5 min"
                    val stock = document.getLong("stock")?.toInt() ?: 0

                    // Mapeo exacto de los 27 productos a sus imágenes locales
                    val localImage = when (nombre.trim()) {
                        "Bandeja paisa", "Bandeja Paisa Break" -> R.drawable.main_img_food_bandeja_paisa
                        "Ajiaco Santafereño" -> R.drawable.main_img_food_ajiaco
                        "Tinto" -> R.drawable.main_img_food_capuchino
                        "Jugos Hit" -> R.drawable.main_img_food_jugo_hit
                        "Chocolatina Jet" -> R.drawable.main_img_food_chocolatina_jet
                        "Chocorramo" -> R.drawable.main_img_food_chocoramo
                        "Pastel de pollo" -> R.drawable.main_img_food_pastel_pollo
                        "Almojábana" -> R.drawable.main_img_food_almojabana
                        "Mote de Queso" -> R.drawable.main_img_food_mote_queso
                        "Empanada de carne" -> R.drawable.main_img_food_empanada
                        "Coca-cola personal" -> R.drawable.main_img_food_cocacola
                        "Pandebono" -> R.drawable.main_img_food_pandebono
                        "Capuchino" -> R.drawable.main_img_food_capuchino
                        "Smoothie de Mango y Passion" -> R.drawable.main_img_food_smoothie
                        "Muffin de arándanos" -> R.drawable.main_img_food_muffin
                        "Copa de Helado con Frutas" -> R.drawable.main_img_food_paleta // O puedes cambiarlo si tienes otro recurso
                        "Banana Split Especial" -> R.drawable.main_img_food_smoothie // Usamos otra alternativa visual
                        "Paleta Artesanal de Frutas" -> R.drawable.main_img_food_paleta
                        "Ensalada de Frutas con Helado" -> R.drawable.main_img_food_muffin // Alternativa para variar
                        "Frappé de Café y Caramelo" -> R.drawable.main_img_food_smoothie
                        "Aromática de Frutas" -> R.drawable.main_img_food_smoothie
                        "Pizza Hawaiana" -> R.drawable.img_pizza_hawaiana
                        "Pizza Pepperoni" -> R.drawable.img_pizza_pepperoni
                        "Pizza de Pollo con Champiñones" -> R.drawable.img_pizza_pollo_champinones
                        "Salchipapa Tradicional" -> R.drawable.img_salchipapa_tradicional
                        "Salchipapa Especial (Queso y Tocineta)",
                        "Salchipapa Especial (Pollo y Queso)" -> R.drawable.img_salchipapa_especial
                        else -> R.drawable.main_img_food_chocoramo
                    }

                    val foodCategory = when (categoriaStr.lowercase()) {
                        "cafés", "bebidas" -> FoodCategory.BEBIDAS
                        "repostería", "postres" -> FoodCategory.POSTRES
                        else -> FoodCategory.SNACKS
                    }

                    val availableStock = StockRepository.getStock(id)
                    // Construcción exacta acorde a data class Product en Models.kt
                    val product = Product(
                        id = id,
                        name = nombre,
                        description = "",
                        price = precio,
                        prepTime = tiempoPrep,
                        available = availableStock > 0,
                        imageRes = localImage,
                        category = foodCategory,
                        restaurantId = restaurantId,
                        featured = true,
                        calories = calorias
                    )
                    productsList.add(product)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val finalProducts = if (productsList.isNotEmpty()) {
                productsList
            } else {
                repository.productsForRestaurant(rest.id)
            }

            _uiState.update {
                it.copy(
                    restaurant = rest,
                    allProducts = finalProducts,
                    searchQuery = initialSearch ?: it.searchQuery,
                    selectedCategory = "Todos",
                    isLoading = false
                )
            }
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
        val available = getProductStock(product.id)
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