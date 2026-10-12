package com.example.breakapp.main.data

import com.example.breakapp.R
import java.text.Normalizer

/**
 * Fuente de datos de las pantallas principales.
 *
 * Hoy se usa [LocalMenuRepository] (lista dentro del código). Cuando conectes Firestore,
 * crea otra clase que implemente esta interfaz y cambia [menuRepository]; las pantallas
 * no hay que tocarlas.
 */
interface MenuRepository {
    fun restaurants(): List<Restaurant>
    fun featuredProducts(category: FoodCategory): List<Product>
    fun search(query: String): SearchResults
}

/** Cambia aquí la implementación cuando tengas Firestore. */
val menuRepository: MenuRepository = LocalMenuRepository

object LocalMenuRepository : MenuRepository {

    private const val CATERING = "catering_gourmet"
    private const val WORLD = "world_gourmet"
    private const val PEOPLES = "peoples_pizza"
    private const val FRUIT = "fruit_coffee"

    private val restaurantList = listOf(
        Restaurant(CATERING, "Catering Gourmet", 4.8, "15-20 min", R.drawable.main_img_rest_catering_gourmet),
        Restaurant(WORLD, "World Gourmet", 4.6, "20-25 min", R.drawable.main_img_rest_world_gourmet),
        Restaurant(PEOPLES, "People’s pizza", 4.5, "10-15 min", R.drawable.main_img_rest_peoples_pizza),
        Restaurant(FRUIT, "Fruit & Coffee", 4.9, "5-10 min", R.drawable.main_img_rest_fruit_coffee)
    )

    private val restaurantsById = restaurantList.associateBy { it.id }

    // OJO: los restaurantes asignados a cada producto son datos de ejemplo.
    private val catalog = listOf(
        // ---------- Solo búsqueda (ejemplo del diseño "Bandeja"); no salen en el Inicio ----------
        Product(
            "bandeja_paisa_catering", "Bandeja paisa",
            "Arroz, frijoles, carne molida, huevo, arepa y plátano",
            12000, "12 min", true, R.drawable.main_img_food_bandeja_paisa, FoodCategory.ALMUERZOS, CATERING,
            featured = false
        ),
        Product(
            "bandeja_paisa_world", "Bandeja paisa",
            "Arroz, frijoles, carne molida, huevo, arepa y plátano",
            9000, "12 min", true, R.drawable.main_img_food_bandeja_paisa, FoodCategory.ALMUERZOS, WORLD,
            featured = false
        ),
        // ---------- Almuerzos ----------
        Product(
            "bandeja_paisa_break", "Bandeja Paisa Break",
            "Arroz, frijoles, carne molida, huevo, arepa y plátano",
            18500, "12 min", true, R.drawable.main_img_food_bandeja_paisa, FoodCategory.ALMUERZOS, CATERING
        ),
        Product(
            "ajiaco", "Ajiaco Santafereño",
            "Sopa tradicional con pollo, tres tipos de papas, alcaparras y crema",
            14500, "12 min", true, R.drawable.main_img_food_ajiaco, FoodCategory.ALMUERZOS, CATERING
        ),
        Product(
            "mote_queso", "Mote de Queso",
            "Sopa de ñame y queso costeño con sofrito",
            12000, "12 min", false, R.drawable.main_img_food_mote_queso, FoodCategory.ALMUERZOS, WORLD
        ),
        // ---------- Snacks ----------
        Product(
            "pastel_pollo", "Pastel de pollo",
            "Hojaldre crujiente relleno de pollo sazonado.",
            4000, "3 - 5 min", true, R.drawable.main_img_food_pastel_pollo, FoodCategory.SNACKS, WORLD
        ),
        Product(
            "almojabana", "Almojábana",
            "Suave panecillo horneado con auténtico sabor a queso.",
            3000, "3 - 5 min", true, R.drawable.main_img_food_almojabana, FoodCategory.SNACKS, FRUIT
        ),
        Product(
            "chocoramo", "Chocoramo",
            "Bizcocho tradicional colombiano cubierto de chocolate crocante.",
            5000, "1 min", true, R.drawable.main_img_food_chocoramo, FoodCategory.SNACKS, WORLD
        ),
        Product(
            "empanada_carne", "Empanada de carne",
            "Crujiente masa de maíz rellena de carne sazonada.",
            3500, "2 - 4 min", true, R.drawable.main_img_food_empanada, FoodCategory.SNACKS, WORLD
        ),
        Product(
            "pandebono", "Pandebono",
            "Tradicional panecillo horneado de maíz y abundante queso, suave y esponjoso.",
            3000, "2 - 4 min", false, R.drawable.main_img_food_pandebono, FoodCategory.SNACKS, FRUIT
        ),
        // ---------- Bebidas ----------
        Product(
            "cocacola", "Coca-cola personal",
            "La clásica y refrescante bebida de siempre en su tamaño individual.",
            4500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, WORLD
        ),
        Product(
            "capuchino", "Capuchino",
            "Espresso intenso con leche cremosa y abundante espuma. ¡Pura energía!",
            4500, "3 - 5 min", true, R.drawable.main_img_food_capuchino, FoodCategory.BEBIDAS, FRUIT
        ),
        Product(
            "jugos_hit", "Jugos hit",
            "Refrescante bebida de fruta en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit, FoodCategory.BEBIDAS, FRUIT
        ),
        Product(
            "smoothie", "Smoothie de Mango y Maracuyá",
            "Bebida fría de mango y maracuyá, cremosa y llena de sabor tropical.",
            4500, "3 - 5 min", true, R.drawable.main_img_food_smoothie, FoodCategory.BEBIDAS, FRUIT
        ),
        // ---------- Postres ----------
        Product(
            "chocolatina_jet", "Chocolatina Jet",
            "El chocolate más tradicional de Colombia en su tamaño clásico.",
            2500, "1 min", true, R.drawable.main_img_food_chocolatina_jet, FoodCategory.POSTRES, WORLD
        ),
        Product(
            "muffin", "Muffin de arándanos",
            "Suave y esponjoso bizcocho horneado con arándanos silvestres.",
            4000, "1 min", true, R.drawable.main_img_food_muffin, FoodCategory.POSTRES, FRUIT
        ),
        Product(
            "paleta", "Paleta artesanal de frutos rojos",
            "Paleta artesanal y refrescante con una deliciosa mezcla de bayas naturales.",
            6000, "1 min", true, R.drawable.main_img_food_paleta, FoodCategory.POSTRES, FRUIT
        )
    )

    override fun restaurants(): List<Restaurant> = restaurantList

    override fun featuredProducts(category: FoodCategory): List<Product> =
        catalog.filter { it.featured && it.category == category }

    override fun search(query: String): SearchResults {
        val tokens = query.normalized().split(" ").filter { it.isNotBlank() }
        if (tokens.isEmpty()) return SearchResults(emptyList(), emptyList())

        val products = catalog
            .filter { p -> p.name.normalized().let { n -> tokens.all { it in n } } }
            .mapNotNull { p -> restaurantsById[p.restaurantId]?.let { ProductMatch(p, it) } }

        val restaurants = restaurantList
            .filter { r -> r.name.normalized().let { n -> tokens.all { it in n } } }

        return SearchResults(products, restaurants)
    }

    /** Minúsculas, sin tildes ni apóstrofes: "Almojábana" -> "almojabana". */
    private fun String.normalized(): String =
        Normalizer.normalize(lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace("'", "")
            .replace("’", "")
            .trim()
}
