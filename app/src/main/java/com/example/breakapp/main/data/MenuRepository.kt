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
        Product(
            "almuerzo_ensalada_catering", "Almuerzo de Ensalada",
            "Ensalada fresca de lechuga, tomate y mazorca.",
            12000, "5-8 min", true, R.drawable.main_img_food_almuerzo_ensalada, FoodCategory.ALMUERZOS, CATERING,
            featured = false,
            calories = "250 - 320 kcal"
        ),
        Product(
            "arepa_queso_catering", "Arepa de Queso",
            "Arepa de maíz asada y rellena de queso.",
            4000, "3-5 min", true, R.drawable.main_img_food_arepa_queso, FoodCategory.SNACKS, CATERING,
            featured = false,
            calories = "230 - 280 kcal"
        ),
        Product(
            "palitos_queso_catering", "Palitos de Queso",
            "Palitos de masa rellenos de queso, horneados y dorados.",
            3500, "3-5 min", true, R.drawable.main_img_food_pandebono, FoodCategory.SNACKS, CATERING,
            featured = false,
            calories = "200 - 240 kcal"
        ),
        Product(
            "croissant_chocolate_catering", "Croissant de Chocolate",
            "Croissant hojaldrado relleno de chocolate.",
            4500, "1 min", true, R.drawable.main_img_food_croissant_chocolate, FoodCategory.POSTRES, CATERING,
            featured = false,
            calories = "300 - 340 kcal"
        ),
        Product(
            "arroz_leche_catering", "Arroz con Leche",
            "Postre tradicional cremoso con canela.",
            3500, "1 min", true, R.drawable.main_img_food_arroz_leche, FoodCategory.POSTRES, CATERING,
            featured = false,
            calories = "230 - 270 kcal"
        ),
        Product(
            "capuchino_catering", "Capuchino",
            "Espresso intenso con leche cremosa y abundante espuma.",
            4500, "3-5 min", true, R.drawable.main_img_food_capuchino, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "110 - 140 kcal"
        ),
        Product(
            "chocolate_leche_catering", "Chocolate de Leche",
            "Chocolate caliente preparado con leche.",
            4000, "3-5 min", true, R.drawable.main_img_food_chocolate_leche, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "190 - 230 kcal"
        ),
        Product(
            "chocolate_agua_catering", "Chocolate en Agua",
            "Chocolate caliente tradicional preparado con agua.",
            3500, "3-5 min", true, R.drawable.main_img_food_chocolate_agua, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "120 - 150 kcal"
        ),
        Product(
            "aromatica_panela_catering", "Aromática de Panela",
            "Infusión caliente endulzada con panela.",
            2000, "2 min", true, R.drawable.main_img_food_aromatica_panela, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "40 - 60 kcal"
        ),
        Product(
            "aromatica_hierbas_catering", "Aromática de Hierbas",
            "Infusión caliente de hierbas aromáticas.",
            2000, "2 min", true, R.drawable.main_img_food_aromatica_hierbas, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "0 - 10 kcal"
        ),
        Product(
            "agua_catering", "Agua",
            "Agua natural en botella individual.",
            2500, "1 min", true, R.drawable.main_img_food_agua, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "agua_gas_catering", "Agua con Gas",
            "Agua mineral con gas, fría y refrescante.",
            3000, "1 min", true, R.drawable.main_img_food_agua_gas, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "jugo_hit_mora_catering", "Jugos Hit de Mora",
            "Refrescante bebida de mora en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit_mora, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "180 - 210 kcal"
        ),
        Product(
            "jugo_hit_mango_catering", "Jugos Hit de Mango",
            "Refrescante bebida de mango en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit_mango, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "180 - 210 kcal"
        ),
        Product(
            "jugo_hit_lulo_catering", "Jugos Hit de Lulo",
            "Refrescante bebida de lulo en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit_lulo, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "180 - 210 kcal"
        ),
        Product(
            "pepsi_catering", "Pepsi Personal",
            "Gaseosa de cola en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "160 kcal"
        ),
        Product(
            "colombiana_catering", "Colombiana",
            "La gaseosa colombiana de sabor inconfundible, en su tamaño individual.",
            3500, "1 min", true, R.drawable.main_img_food_colombiana, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "150 kcal"
        ),
        Product(
            "sprite_catering", "Sprite",
            "Gaseosa de limón fría y refrescante.",
            4000, "1 min", true, R.drawable.main_img_food_sprite, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "uva_postobon_catering", "Uva Postobón",
            "Gaseosa de uva con el sabor de siempre.",
            3500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, CATERING,
            featured = false,
            calories = "140 kcal"
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
            2000, "2 min", true, R.drawable.main_img_food_aromatica_frutas, FoodCategory.BEBIDAS, WORLD,
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
        // ---------- Más productos World Gourmet ----------
        Product(
            "perro_caliente_world", "Perro Caliente Sencillo",
            "Pan suave con salchicha, salsas y papa ripio.",
            5000, "4-6 min", true, R.drawable.main_img_food_perro_caliente, FoodCategory.ALMUERZOS, WORLD,
            featured = false,
            calories = "300 - 380 kcal"
        ),
        Product(
            "hamburguesa_world", "Hamburguesa Sencilla",
            "Pan, carne de res, lechuga, tomate y salsas.",
            7000, "6-8 min", true, R.drawable.main_img_food_hamburguesa, FoodCategory.ALMUERZOS, WORLD,
            featured = false,
            calories = "400 - 500 kcal"
        ),
        Product(
            "sandwich_sencillo_world", "Sándwich Sencillo",
            "Pan tostado con jamón y queso.",
            6000, "4-6 min", true, R.drawable.main_img_food_sandwich_sencillo, FoodCategory.ALMUERZOS, WORLD,
            featured = false,
            calories = "280 - 340 kcal"
        ),
        Product(
            "sandwich_especial_world", "Sándwich Especial",
            "Pan tostado con jamón, queso, pollo, lechuga y tomate.",
            8500, "5-7 min", true, R.drawable.main_img_food_sandwich_especial, FoodCategory.ALMUERZOS, WORLD,
            featured = false,
            calories = "380 - 450 kcal"
        ),
        Product(
            "empanada_queso_world", "Empanada de Queso",
            "Crujiente masa de maíz rellena de queso.",
            3500, "2-4 min", true, R.drawable.main_img_food_empanada_queso, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "200 - 240 kcal"
        ),
        Product(
            "empanada_criolla_world", "Empanada Criolla",
            "Crujiente masa de maíz rellena de carne criolla.",
            3500, "2-4 min", true, R.drawable.main_img_food_empanada_criolla, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "210 - 250 kcal"
        ),
        Product(
            "empanada_ranchera_world", "Empanada Ranchera",
            "Crujiente masa de maíz rellena al estilo ranchero.",
            3500, "2-4 min", true, R.drawable.main_img_food_empanada_ranchera, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "210 - 250 kcal"
        ),
        Product(
            "arepa_queso_world", "Arepa de Queso",
            "Arepa de maíz asada y rellena de queso.",
            4000, "3-5 min", true, R.drawable.main_img_food_arepa_queso, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "230 - 280 kcal"
        ),
        Product(
            "arepa_choclo_world", "Arepa de Choclo con Queso",
            "Arepa dulce de choclo con queso fundido.",
            4500, "3-5 min", true, R.drawable.main_img_food_arepa_choclo_queso, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "250 - 300 kcal"
        ),
        Product(
            "papas_tomate_world", "Papas Margarita de Tomate",
            "Papas fritas en paquete individual, sabor tomate.",
            3000, "1 min", true, R.drawable.main_img_food_papas_margarita_tomate, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "150 - 170 kcal"
        ),
        Product(
            "papas_pollo_world", "Papas Margarita de Pollo",
            "Papas fritas en paquete individual, sabor pollo.",
            3000, "1 min", true, R.drawable.main_img_food_papas_margarita_pollo, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "150 - 170 kcal"
        ),
        Product(
            "papas_limon_world", "Papas Margarita de Limón",
            "Papas fritas en paquete individual, sabor limón.",
            3000, "1 min", true, R.drawable.main_img_food_papas_margarita_limon, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "150 - 170 kcal"
        ),
        Product(
            "todorico_natural_world", "Todorico Natural",
            "Snack de maíz crocante, sabor natural.",
            3000, "1 min", true, R.drawable.main_img_food_todorico_natural, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "140 - 160 kcal"
        ),
        Product(
            "todorico_picante_world", "Todorico Picante",
            "Snack de maíz crocante, con un toque picante.",
            3000, "1 min", true, R.drawable.main_img_food_todorico_picante, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "140 - 160 kcal"
        ),
        Product(
            "platanitos_dulces_world", "Platanitos Dulces",
            "Chips de plátano maduro, dulces y crocantes.",
            2500, "1 min", true, R.drawable.main_img_food_platanitos_dulces, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "130 - 150 kcal"
        ),
        Product(
            "platanitos_salados_world", "Platanitos Salados",
            "Chips de plátano verde, salados y crocantes.",
            2500, "1 min", true, R.drawable.main_img_food_platanitos_salados, FoodCategory.SNACKS, WORLD,
            featured = false,
            calories = "130 - 150 kcal"
        ),
        Product(
            "donas_world", "Donas",
            "Dona suave cubierta de azúcar o glaseado.",
            3000, "1 min", true, R.drawable.main_img_food_donas, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "250 - 300 kcal"
        ),
        Product(
            "gansito_world", "Gansito",
            "Bizcocho relleno de crema y mermelada, cubierto de chocolate.",
            3000, "1 min", true, R.drawable.main_img_food_gansito, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "230 - 260 kcal"
        ),
        Product(
            "chocolatina_jumbo_world", "Chocolatina Jumbo",
            "La chocolatina clásica en tamaño grande.",
            4500, "1 min", true, R.drawable.main_img_food_chocolatina_jumbo, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "200 - 240 kcal"
        ),
        Product(
            "galletas_festival_4_world", "Galletas Festival x4",
            "Paquete de 4 galletas Festival.",
            2500, "1 min", true, R.drawable.main_img_food_galletas_festival_4, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "180 - 220 kcal"
        ),
        Product(
            "galletas_festival_6_world", "Galletas Festival x6",
            "Paquete de 6 galletas Festival.",
            3500, "1 min", true, R.drawable.main_img_food_galletas_festival_6, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "270 - 330 kcal"
        ),
        Product(
            "galletas_oreo_world", "Galletas Oreo",
            "Paquete de galletas Oreo.",
            2000, "1 min", true, R.drawable.main_img_food_galletas_oreo, FoodCategory.POSTRES, WORLD,
            featured = false,
            calories = "160 - 190 kcal"
        ),
        Product(
            "sprite_world", "Sprite",
            "Gaseosa de limón fría y refrescante.",
            4000, "1 min", true, R.drawable.main_img_food_sprite, FoodCategory.BEBIDAS, WORLD,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "speed_max_world", "Speed Max",
            "Bebida energizante en lata.",
            4500, "1 min", true, R.drawable.main_img_food_speed_max, FoodCategory.BEBIDAS, WORLD,
            featured = false,
            calories = "110 - 140 kcal"
        ),
        Product(
            "agua_world", "Agua",
            "Agua natural en botella individual.",
            2500, "1 min", true, R.drawable.main_img_food_agua, FoodCategory.BEBIDAS, WORLD,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "agua_gas_world", "Agua con Gas",
            "Agua mineral con gas, fría y refrescante.",
            3000, "1 min", true, R.drawable.main_img_food_agua_gas, FoodCategory.BEBIDAS, WORLD,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "jugo_valle_world", "Jugo del Valle",
            "Jugo de fruta en caja individual.",
            3500, "1 min", true, R.drawable.main_img_food_jugo_del_valle, FoodCategory.BEBIDAS, WORLD,
            featured = false,
            calories = "100 - 140 kcal"
        ),

        // ---------- Menú People's Pizza (Figma) ----------
        Product(
            "pizza_hawaiana", "Pizza Hawaiana",
            "Queso mozzarella fundido, jamón seleccionado y trozos de piña.",
            7500, "3-5 min", true, R.drawable.img_pizza_hawaiana, FoodCategory.SNACKS, PEOPLES,
            calories = "280 - 320 kcal"
        ),
        Product(
            "pizza_pepperoni", "Pizza Pepperoni",
            "Crujiente masa artesanal con generoso pepperoni y queso mozzarella.",
            7500, "3-5 min", true, R.drawable.img_pizza_pepperoni, FoodCategory.SNACKS, PEOPLES,
            calories = "310 - 350 kcal"
        ),
        Product(
            "pizza_pollo_champ", "Pizza de Pollo con Champiñones",
            "Pollo desmechado sazonado, champiñones frescos y queso derretido.",
            8000, "3-5 min", true, R.drawable.img_pizza_pollo_champinones, FoodCategory.SNACKS, PEOPLES,
            calories = "300 - 340 kcal"
        ),
        Product(
            "pizza_margarita", "Pizza Margarita",
            "Salsa de tomate natural, queso mozzarella y albahaca fresca.",
            7000, "3-5 min", true, R.drawable.img_pizza_margarita, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "250 - 290 kcal"
        ),
        Product(
            "pizza_jamon_queso", "Pizza de Jamón y Queso",
            "Clásica pizza con jamón de cerdo y abundante queso mozzarella.",
            7000, "3-5 min", true, R.drawable.img_pizza_jamon_queso, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "270 - 310 kcal"
        ),
        Product(
            "pizza_vegetariana", "Pizza Vegetariana",
            "Pimentón, champiñones, cebolla, maíz tierno y queso mozzarella.",
            7500, "3-5 min", true, R.drawable.img_pizza_vegetariana, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "240 - 280 kcal"
        ),
        Product(
            "pizza_mexicana", "Pizza Mexicana",
            "Carne molida sazonada, jalapeños, maíz y queso mozzarella.",
            8500, "3-5 min", true, R.drawable.img_pizza_mexicana, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "320 - 360 kcal"
        ),
        Product(
            "pizza_carnes", "Pizza de Carnes",
            "Pepperoni, jamón, tocineta y carne molida con queso mozzarella.",
            9000, "4-6 min", true, R.drawable.img_pizza_pepperoni, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "350 - 400 kcal"
        ),
        Product(
            "pizza_cuatro_quesos", "Pizza Cuatro Quesos",
            "Mozzarella, queso azul, parmesano y queso campesino.",
            8500, "3-5 min", false, R.drawable.img_pizza_hawaiana, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "330 - 370 kcal"
        ),
        Product(
            "palitos_queso_peoples", "Palitos de Queso",
            "Palitos de masa rellenos de queso, horneados y dorados.",
            5000, "4-6 min", true, R.drawable.main_img_food_pandebono, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "200 - 240 kcal"
        ),
        Product(
            "pan_ajo_peoples", "Pan de Ajo con Queso",
            "Pan artesanal con mantequilla de ajo y queso gratinado.",
            4500, "3-5 min", true, R.drawable.main_img_food_almojabana, FoodCategory.SNACKS, PEOPLES,
            featured = false,
            calories = "190 - 230 kcal"
        ),
        Product(
            "salchipapa_tradicional", "Salchipapa Tradicional",
            "Papas a la francesa crujientes con salchicha dorada y salsas de la casa.",
            10000, "5-8 min", true, R.drawable.img_salchipapa_tradicional, FoodCategory.ALMUERZOS, PEOPLES,
            calories = "520 - 600 kcal"
        ),
        Product(
            "salchipapa_especial", "Salchipapa Especial (Pollo y Queso)",
            "Papas doradas con salchicha especial, pollo desmechado y queso fundido.",
            14000, "6-10 min", true, R.drawable.img_salchipapa_especial, FoodCategory.ALMUERZOS, PEOPLES,
            calories = "680 - 780 kcal"
        ),
        // ---------- Bebidas People's Pizza ----------
        Product(
            "cocacola_peoples", "Coca-Cola Personal",
            "La clásica y refrescante bebida de siempre en su tamaño individual.",
            4500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, PEOPLES,
            calories = "170 kcal"
        ),
        Product(
            "colombiana_peoples", "Colombiana",
            "La gaseosa colombiana de sabor inconfundible, en su tamaño individual.",
            3500, "1 min", true, R.drawable.main_img_food_colombiana, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "150 kcal"
        ),
        Product(
            "manzana_postobon_peoples", "Manzana Postobón",
            "La clásica gaseosa de manzana, fría y refrescante.",
            3500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "uva_postobon_peoples", "Uva Postobón",
            "Gaseosa de uva con el sabor de siempre.",
            3500, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "pepsi_peoples", "Pepsi Personal",
            "Gaseosa de cola en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "160 kcal"
        ),
        Product(
            "agua_peoples", "Agua",
            "Agua natural en botella individual.",
            2500, "1 min", true, R.drawable.main_img_food_agua, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "agua_gas_peoples", "Agua con Gas",
            "Agua mineral con gas, fría y refrescante.",
            3000, "1 min", true, R.drawable.main_img_food_agua_gas, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "jugos_hit_peoples", "Jugos Hit",
            "Refrescante bebida de fruta en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_jugo_hit, FoodCategory.BEBIDAS, PEOPLES,
            featured = false,
            calories = "180 - 210 kcal"
        ),
        // ---------- Postre People's Pizza ----------
        Product(
            "chocolatina_jet_peoples", "Chocolatina Jet",
            "El chocolate más tradicional de Colombia en su tamaño clásico.",
            2500, "1 min", true, R.drawable.main_img_food_chocolatina_jet, FoodCategory.POSTRES, PEOPLES,
            featured = false,
            calories = "65 kcal"
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
        ),
        // ---------- Más productos Fruit&coffee ----------
        Product(
            "brownie_helado_fruit", "Brownie con Helado",
            "Brownie de chocolate tibio acompañado de una bola de helado.",
            9000, "4-6 min", true, R.drawable.main_img_food_brownie_helado, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "380 - 450 kcal"
        ),
        Product(
            "parfait_fruit", "Parfait de Yogurt",
            "Capas de yogurt, granola crocante y fruta fresca.",
            8000, "3-5 min", true, R.drawable.main_img_food_parfait_yogurt, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "220 - 280 kcal"
        ),
        Product(
            "fresas_crema_fruit", "Fresas con Crema",
            "Fresas frescas con crema de leche.",
            8500, "3-5 min", true, R.drawable.main_img_food_fresas_crema, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "200 - 260 kcal"
        ),
        Product(
            "fresas_chocolate_fruit", "Fresas con Chocolate",
            "Fresas frescas bañadas en chocolate.",
            9000, "3-5 min", true, R.drawable.main_img_food_fresas_chocolate, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "230 - 290 kcal"
        ),
        Product(
            "ensalada_premium_fruit", "Ensalada de Frutas Premium",
            "Selección de frutas frescas con yogurt, granola y miel.",
            11000, "4-6 min", true, R.drawable.main_img_food_ensalada_frutas_premium, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "260 - 330 kcal"
        ),
        Product(
            "arroz_leche_fruit", "Arroz con Leche",
            "Postre tradicional cremoso con canela.",
            3500, "1 min", true, R.drawable.main_img_food_arroz_leche, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "230 - 270 kcal"
        ),
        Product(
            "postre_maracuya_fruit", "Postre de Maracuyá",
            "Postre frío y cremoso con pulpa de maracuyá.",
            5000, "1 min", true, R.drawable.main_img_food_postre_maracuya, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "250 - 300 kcal"
        ),
        Product(
            "tres_leches_fruit", "Postre Tres Leches",
            "Suave bizcocho bañado en tres leches con crema.",
            5500, "1 min", true, R.drawable.main_img_food_postre_tres_leches, FoodCategory.POSTRES, FRUIT,
            featured = false,
            calories = "300 - 360 kcal"
        ),
        Product(
            "queso_aguapanela_fruit", "Queso con Aguapanela",
            "Aguapanela caliente acompañada de queso campesino.",
            6000, "3-5 min", true, R.drawable.main_img_food_queso_aguapanela, FoodCategory.SNACKS, FRUIT,
            featured = false,
            calories = "250 - 300 kcal"
        ),
        Product(
            "cafe_fruit", "Café",
            "Café colombiano recién preparado.",
            3000, "3-5 min", true, R.drawable.main_img_food_cafe, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "2 - 5 kcal"
        ),
        Product(
            "jugo_natural_leche_fruit", "Jugo Natural en Leche",
            "Jugo de fruta natural preparado con leche.",
            6500, "3-5 min", true, R.drawable.main_img_food_jugo_natural_leche, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "180 - 240 kcal"
        ),
        Product(
            "jugo_natural_agua_fruit", "Jugo Natural en Agua",
            "Jugo de fruta natural preparado con agua.",
            5500, "3-5 min", true, R.drawable.main_img_food_jugo_natural_agua, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "90 - 130 kcal"
        ),
        Product(
            "aromatica_hierbas_fruit", "Aromática de Hierbas",
            "Infusión caliente de hierbas aromáticas.",
            2000, "2 min", true, R.drawable.main_img_food_aromatica_hierbas, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "0 - 10 kcal"
        ),
        Product(
            "aromatica_frutas_fruit", "Aromática de Frutas",
            "Infusión caliente de frutas naturales y especias aromáticas.",
            2500, "2 min", true, R.drawable.main_img_food_aromatica_frutas, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "15 - 30 kcal"
        ),
        Product(
            "agua_fruit", "Agua",
            "Agua natural en botella individual.",
            2500, "1 min", true, R.drawable.main_img_food_agua, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "agua_gas_fruit", "Agua con Gas",
            "Agua mineral con gas, fría y refrescante.",
            3000, "1 min", true, R.drawable.main_img_food_agua_gas, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "0 kcal"
        ),
        Product(
            "pepsi_fruit", "Pepsi Personal",
            "Gaseosa de cola en su tamaño individual.",
            4000, "1 min", true, R.drawable.main_img_food_cocacola, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "160 kcal"
        ),
        Product(
            "sprite_fruit", "Sprite",
            "Gaseosa de limón fría y refrescante.",
            4000, "1 min", true, R.drawable.main_img_food_sprite, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "quatro_fruit", "Quatro",
            "Gaseosa de toronja fría y refrescante.",
            3500, "1 min", true, R.drawable.main_img_food_quatro, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "140 kcal"
        ),
        Product(
            "speed_max_fruit", "Speed Max",
            "Bebida energizante en lata.",
            4500, "1 min", true, R.drawable.main_img_food_speed_max, FoodCategory.BEBIDAS, FRUIT,
            featured = false,
            calories = "110 - 140 kcal"
        )
    )

    override fun restaurants(): List<Restaurant> = restaurantList

    override fun restaurantById(id: String): Restaurant? {
        val clean = id.trim().lowercase()
        if (clean.isEmpty()) return restaurantList.firstOrNull()

        return restaurantsById[clean]
            ?: restaurantList.find { it.id.lowercase() == clean }
            ?: when {
                clean.contains("pizza") || clean.contains("people") -> restaurantsById[PEOPLES]
                clean.contains("world") -> restaurantsById[WORLD]
                clean.contains("fruit") || clean.contains("coffee") -> restaurantsById[FRUIT]
                clean.contains("cater") || clean.contains("catherine") -> restaurantsById[CATERING]
                else -> restaurantList.find { it.name.lowercase().contains(clean) }
                    ?: restaurantList.find { clean.contains(it.id.lowercase()) }
                    ?: restaurantList.firstOrNull()
            }
    }

    override fun productsForRestaurant(restaurantId: String): List<Product> {
        val resolvedRest = restaurantById(restaurantId)
        val targetId = resolvedRest?.id ?: restaurantId
        val list = catalog.filter { it.restaurantId == targetId }
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