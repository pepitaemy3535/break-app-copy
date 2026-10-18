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
    fun restaurantById(id: String): Restaurant?
    fun productsForRestaurant(restaurantId: String): List<Product>
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
        Restaurant(CATERING, "Catering Gourmet", 4.8, "15-20 min", R.drawable.main_img_rest_catering_gourmet, schedule = "Lun - Vie • 7:00 AM - 8:00 PM", location = "Módulo 2 y 7 en Planta Baja Ext. 1933"),
        Restaurant(WORLD, "World Gourmet", 4.5, "20-25 min", R.drawable.main_img_rest_world_gourmet, schedule = "Lun - Vie • 7:00 AM - 8:00 PM", location = "Módulo 2 y 7 en Planta Baja"),
        Restaurant(PEOPLES, "People’s pizza", 4.5, "10-15 min", R.drawable.main_img_rest_peoples_pizza, schedule = "Lun - Vie • 7:00 AM - 8:00 PM", location = "Módulo 2, Planta Baja"),
        Restaurant(FRUIT, "Fruit&coffee", 4.8, "5-10 min", R.drawable.main_img_rest_fruit_coffee, schedule = "Lun - Vie • 7:00 AM - 8:00 PM", location = "Módulo 7, Planta Baja")
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
        // ---------- Productos de Catering Gourmet (según diseño Figma) ----------
        Product(
            "tinto_catering", "Tinto",
            "Café tradicional colombiano recién colado",
            2500, "3-5 min", true, R.drawable.main_img_food_capuchino, FoodCategory.BEBIDAS, CATERING,
            calories = "2 - 5 kcal"
        ),
        Product(
            "jugos_hit_catering", "Jugos Hit",
            "Refrescante bebida de fruta en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit, FoodCategory.BEBIDAS, CATERING,
            calories = "180 - 210 kcal"
        ),
        Product(
            "chocolatina_jet_catering", "Chocolatina Jet",
            "El chocolate más tradicional de Colombia en su tamaño clásico.",
            2500, "1 min", true, R.drawable.main_img_food_chocolatina_jet, FoodCategory.POSTRES, CATERING,
            calories = "65 kcal"
        ),
        Product(
            "chocoramo_catering", "Chocorramo",
            "Bizcocho tradicional colombiano cubierto de chocolate crocante.",
            5000, "1 min", true, R.drawable.main_img_food_chocoramo, FoodCategory.SNACKS, CATERING,
            calories = "240 kcal"
        ),
        Product(
            "pastel_pollo_catering", "Pastel de pollo",
            "Hojaldre crujiente relleno de pollo sazonado.",
            4000, "3-5 min", true, R.drawable.main_img_food_pastel_pollo, FoodCategory.SNACKS, CATERING,
            calories = "380 - 420 kcal"
        ),
        Product(
            "almojabana_catering", "Almojábana",
            "Suave panecillo horneado con auténtico sabor a queso.",
            3000, "3-5 min", true, R.drawable.main_img_food_almojabana, FoodCategory.SNACKS, CATERING,
            calories = "220 kcal"
        ),

        // ---------- Menú World Gourmet (Figma) ----------
        Product(
            "capuchino_world", "Capuchino",
            "Espresso intenso con leche cremosa y abundante espuma.",
            4500, "3-5 min", true, R.drawable.main_img_food_capuchino, FoodCategory.BEBIDAS, WORLD,
            calories = "110 - 140 kcal"
        ),
        Product(
            "aromatica_world", "Aromática de Frutas",
            "Infusión caliente de frutas naturales y especias aromáticas.",
            2000, "2 min", true, R.drawable.main_img_food_smoothie, FoodCategory.BEBIDAS, WORLD,
            calories = "15 - 30 kcal"
        ),
        Product(
            "empanada_world", "Empanada de Carne",
            "Crujiente masa de maíz rellena de carne sazonada.",
            3500, "2-4 min", true, R.drawable.main_img_food_empanada, FoodCategory.SNACKS, WORLD,
            calories = "210 - 250 kcal"
        ),
        Product(
            "pandebono_world", "Pandebono",
            "Tradicional panecillo horneado de maíz y abundante queso.",
            3000, "2-4 min", true, R.drawable.main_img_food_pandebono, FoodCategory.SNACKS, WORLD,
            calories = "180 - 210 kcal"
        ),
        Product(
            "muffin_world", "Muffin de Arándanos",
            "Suave bizcocho horneado con arándanos silvestres.",
            4000, "1 min", true, R.drawable.main_img_food_muffin, FoodCategory.POSTRES, WORLD,
            calories = "260 - 300 kcal"
        ),
        Product(
            "cocacola_world", "Coca-Cola Personal",
            "La clásica y refrescante bebida de siempre en su tamaño individual.",
            4500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, WORLD,
            calories = "170 kcal"
        ),

        // ---------- Menú People's Pizza (Figma) ----------
        Product(
            "pizza_hawaiana", "Pizza Hawaiana",
            "Queso mozzarella fundido, jamón seleccionado y trozos de piña.",
            7500, "3-5 min", true, R.drawable.img_pizza, FoodCategory.SNACKS, PEOPLES,
            calories = "280 - 320 kcal"
        ),
        Product(
            "pizza_pepperoni", "Pizza Pepperoni",
            "Crujiente masa artesanal con generoso pepperoni y queso mozzarella.",
            7500, "3-5 min", true, R.drawable.img_pizza, FoodCategory.SNACKS, PEOPLES,
            calories = "310 - 350 kcal"
        ),
        Product(
            "pizza_pollo_champ", "Pizza de Pollo con Champiñones",
            "Pollo desmechado sazonado, champiñones frescos y queso derretido.",
            8000, "3-5 min", true, R.drawable.img_pizza, FoodCategory.SNACKS, PEOPLES,
            calories = "300 - 340 kcal"
        ),
        Product(
            "salchipapa_tradicional", "Salchipapa Tradicional",
            "Papas a la francesa crujientes con salchicha dorada y salsas de la casa.",
            10000, "5-8 min", true, R.drawable.main_img_search_bowl, FoodCategory.ALMUERZOS, PEOPLES,
            calories = "520 - 600 kcal"
        ),
        Product(
            "salchipapa_especial", "Salchipapa Especial (Pollo y Queso)",
            "Papas doradas con salchicha especial, pollo desmechado y queso fundido.",
            14000, "6-10 min", true, R.drawable.main_img_search_bowl, FoodCategory.ALMUERZOS, PEOPLES,
            calories = "680 - 780 kcal"
        ),
        Product(
            "cocacola_peoples", "Coca-Cola Personal",
            "La clásica y refrescante bebida de siempre en su tamaño individual.",
            4500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, PEOPLES,
            calories = "170 kcal"
        ),

        // ---------- Menú Fruit&coffee (Figma) ----------
        Product(
            "copa_helado_fruit", "Copa de Helado con Frutas",
            "Bolas de helado artesanal acompañadas de fruta fresca picada.",
            9000, "3-5 min", true, R.drawable.main_img_search_bowl, FoodCategory.POSTRES, FRUIT,
            calories = "290 - 350 kcal"
        ),
        Product(
            "banana_split_fruit", "Banana Split Especial",
            "Plátano fresco con tres bolas de helado, barquillo, crema y cereza.",
            12000, "4-6 min", true, R.drawable.main_img_search_bowl, FoodCategory.POSTRES, FRUIT,
            calories = "450 - 520 kcal"
        ),
        Product(
            "paleta_fruit", "Paleta Artesanal de Frutos Rojos",
            "Paleta artesanal y refrescante con una deliciosa mezcla de bayas naturales.",
            6000, "1 min", true, R.drawable.main_img_food_paleta, FoodCategory.POSTRES, FRUIT,
            calories = "120 - 150 kcal"
        ),
        Product(
            "ensalada_frutas_fruit", "Ensalada de Frutas con Queso",
            "Variedad de frutas frescas con queso campesino rallado y crema de leche.",
            10000, "4-6 min", true, R.drawable.main_img_search_bowl, FoodCategory.POSTRES, FRUIT,
            calories = "250 - 310 kcal"
        ),
        Product(
            "smoothie_fruit", "Smoothie de Mango y Maracuyá",
            "Bebida fría y cremosa de mango y maracuyá llena de sabor tropical.",
            7500, "3-5 min", true, R.drawable.main_img_food_smoothie, FoodCategory.BEBIDAS, FRUIT,
            calories = "180 - 220 kcal"
        ),
        Product(
            "frappe_fruit", "Frappé de Café y Caramelo",
            "Café granizado con leche, salsa de caramelo y crema chantilly.",
            8500, "3-5 min", true, R.drawable.main_img_food_capuchino, FoodCategory.BEBIDAS, FRUIT,
            calories = "320 - 380 kcal"
        )
    )

    override fun restaurants(): List<Restaurant> = restaurantList

    override fun restaurantById(id: String): Restaurant? =
        restaurantsById[id] ?: restaurantList.find { it.name.contains(id, ignoreCase = true) } ?: restaurantList.firstOrNull()

    override fun productsForRestaurant(restaurantId: String): List<Product> {
        val list = catalog.filter { it.restaurantId == restaurantId }
        return if (list.isNotEmpty()) list else catalog.filter { it.restaurantId == CATERING }
    }

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
