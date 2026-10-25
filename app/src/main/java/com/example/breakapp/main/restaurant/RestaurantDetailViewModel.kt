package com.example.breakapp.main.restaurant

import android.util.Log
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
    // CAMBIO 3: People's pizza tiene sus propios chips de categorías
    val categories: List<String>
        get() = when (restaurant?.id) {
            "peoples_pizza" -> listOf("Todos", "Pizzas", "Salchipapas", "Bebidas", "Snacks")
            "catering_gourmet" -> listOf("Todos", "Almuerzos", "Cafés", "Bebidas", "Snacks", "Repostería")
            "fruit_coffee" -> listOf("Todos", "Cafés", "Jugos", "Bebidas", "Snacks", "Repostería")
            "world_gourmet" -> listOf("Todos", "Comidas", "Empanadas y arepas", "Snacks", "Bebidas", "Repostería")
            else -> listOf("Todos", "Cafés", "Repostería", "Bebidas", "Snacks")
        }

    val filteredProducts: List<Product>
        get() {
            var list = allProducts
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                list = list.filter { it.name.lowercase().contains(query) }
            }
            if (selectedCategory != "Todos") {
                list = when (selectedCategory) {
                    // CAMBIO 4: filtros de los chips nuevos
                    "Pizzas" -> list.filter { it.name.contains("pizza", true) }
                    "Salchipapas" -> list.filter { it.name.contains("salchipapa", true) }
                    "Almuerzos" -> list.filter { it.category == FoodCategory.ALMUERZOS }
                    "Jugos" -> list.filter { it.category == FoodCategory.BEBIDAS && it.name.contains("jugo", true) }
                    "Comidas" -> list.filter { it.category == FoodCategory.ALMUERZOS }
                    "Empanadas y arepas" -> list.filter { it.name.contains("empanada", true) || it.name.contains("arepa", true) }
                    "Cafés" -> list.filter { it.category == FoodCategory.BEBIDAS && (it.name.contains("chocolate", true) || it.name.contains("tinto", true) || it.name.contains("capuchino", true) || it.name.contains("café", true) || it.name.contains("frappé", true) || it.name.contains("aromática", true)) }
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

                    // Mapeo exacto de los productos a sus imágenes locales
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
                        // ---------- People's pizza: imágenes reales ----------
                        "Pizza Hawaiana" -> R.drawable.img_pizza_hawaiana
                        "Pizza Pepperoni" -> R.drawable.img_pizza_pepperoni
                        "Pizza de Pollo con Champiñones" -> R.drawable.img_pizza_pollo_champinones
                        "Pizza Margarita" -> R.drawable.img_pizza_margarita
                        "Pizza de Jamón y Queso" -> R.drawable.img_pizza_jamon_queso
                        "Pizza Vegetariana" -> R.drawable.img_pizza_vegetariana
                        "Pizza Mexicana" -> R.drawable.img_pizza_mexicana
                        "Salchipapa Tradicional" -> R.drawable.img_salchipapa_tradicional
                        "Salchipapa Especial (Queso y Tocineta)",
                        "Salchipapa Especial (Pollo y Queso)" -> R.drawable.img_salchipapa_especial
                        // Todavía sin foto propia (imágenes temporales)
                        "Pizza Cuatro Quesos" -> R.drawable.img_pizza_hawaiana
                        "Pizza de Carnes" -> R.drawable.img_pizza_pepperoni
                        "Palitos de Queso" -> R.drawable.main_img_food_pandebono
                        "Pan de Ajo con Queso" -> R.drawable.main_img_food_almojabana
                        // ---------- Bebidas con imagen real ----------
                        "Colombiana" -> R.drawable.main_img_food_colombiana
                        "Agua" -> R.drawable.main_img_food_agua
                        "Agua con Gas" -> R.drawable.main_img_food_agua_gas
                        // Bebidas todavía con imagen temporal
                        "Manzana Postobón", "Uva Postobón", "Pepsi Personal" -> R.drawable.main_img_food_cocacola
                        // ---------- Catering Gourmet: imágenes reales ----------
                        "Sprite" -> R.drawable.main_img_food_sprite
                        "Jugos Hit de Mora" -> R.drawable.main_img_food_jugo_hit_mora
                        "Jugos Hit de Mango" -> R.drawable.main_img_food_jugo_hit_mango
                        "Jugos Hit de Lulo" -> R.drawable.main_img_food_jugo_hit_lulo
                        "Chocolate de Leche" -> R.drawable.main_img_food_chocolate_leche
                        "Chocolate en Agua" -> R.drawable.main_img_food_chocolate_agua
                        "Aromática de Panela" -> R.drawable.main_img_food_aromatica_panela
                        "Aromática de Hierbas" -> R.drawable.main_img_food_aromatica_hierbas
                        "Croissant de Chocolate" -> R.drawable.main_img_food_croissant_chocolate
                        "Arepa de Queso" -> R.drawable.main_img_food_arepa_queso
                        "Almuerzo de Ensalada" -> R.drawable.main_img_food_almuerzo_ensalada
                        "Arroz con Leche" -> R.drawable.main_img_food_arroz_leche
                        // ---------- Fruit&coffee: imágenes reales ----------
                        "Brownie con Helado" -> R.drawable.main_img_food_brownie_helado
                        "Parfait de Yogurt" -> R.drawable.main_img_food_parfait_yogurt
                        "Fresas con Crema" -> R.drawable.main_img_food_fresas_crema
                        "Fresas con Chocolate" -> R.drawable.main_img_food_fresas_chocolate
                        "Ensalada de Frutas Premium" -> R.drawable.main_img_food_ensalada_frutas_premium
                        "Postre de Maracuyá" -> R.drawable.main_img_food_postre_maracuya
                        "Postre Tres Leches" -> R.drawable.main_img_food_postre_tres_leches
                        "Queso con Aguapanela" -> R.drawable.main_img_food_queso_aguapanela
                        "Café" -> R.drawable.main_img_food_cafe
                        "Jugo Natural en Leche" -> R.drawable.main_img_food_jugo_natural_leche
                        "Jugo Natural en Agua" -> R.drawable.main_img_food_jugo_natural_agua
                        "Aromática de Frutas" -> R.drawable.main_img_food_aromatica_frutas
                        "Quatro" -> R.drawable.main_img_food_quatro
                        "Speed Max" -> R.drawable.main_img_food_speed_max
                        // Productos nuevos de World Gourmet (imágenes temporales)
                        "Perro Caliente Sencillo", "Hamburguesa Sencilla",
                        "Sándwich Sencillo", "Sándwich Especial" -> R.drawable.main_img_food_pastel_pollo
                        "Empanada de Queso", "Empanada Criolla", "Empanada Ranchera" -> R.drawable.main_img_food_empanada
                        "Arepa de Choclo con Queso" -> R.drawable.main_img_food_almojabana
                        "Donas", "Galletas Festival x4", "Galletas Festival x6", "Galletas Oreo" -> R.drawable.main_img_food_muffin
                        "Papas Margarita de Tomate", "Papas Margarita de Pollo", "Papas Margarita de Limón",
                        "Todorico Natural", "Todorico Picante",
                        "Platanitos Dulces", "Platanitos Salados" -> R.drawable.main_img_food_pandebono
                        "Jugo del Valle" -> R.drawable.main_img_food_jugo_hit
                        "Gansito" -> R.drawable.main_img_food_chocoramo
                        "Chocolatina Jumbo" -> R.drawable.main_img_food_chocolatina_jet
                        else -> R.drawable.main_img_food_chocoramo
                    }

                    val foodCategory = when (categoriaStr.lowercase()) {
                        "cafés", "bebidas" -> FoodCategory.BEBIDAS
                        "repostería", "postres" -> FoodCategory.POSTRES
                        "almuerzos", "almuerzo" -> FoodCategory.ALMUERZOS
                        else -> FoodCategory.SNACKS
                    }

                    // CAMBIO 1: usa el campo "stock" de Firestore como stock inicial (solo si existe)
                    val stockFirestore = document.getLong("stock")?.toInt()
                    if (stockFirestore != null) StockRepository.seedStock(id, stockFirestore)

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
                Log.e("BreakMenu", "Error leyendo Firestore: ${e.message}", e)
            }

            Log.d("BreakMenu", "Firestore devolvió ${productsList.size} productos para $firestoreDocId")

            val localProducts = repository.productsForRestaurant(rest.id)
            val finalProducts = if (productsList.isNotEmpty()) {
                // Firestore manda; se agregan los del código que no estén ya en Firestore (por nombre)
                val namesInFirestore = productsList.map { it.name.trim().lowercase() }.toSet()
                productsList + localProducts.filter { it.name.trim().lowercase() !in namesInFirestore }
            } else {
                localProducts
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