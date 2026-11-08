package com.example.breakapp.data.ai

import com.example.breakapp.main.data.FoodCategory
import com.example.breakapp.main.data.Product
import com.example.breakapp.main.data.menuRepository
import com.example.breakapp.model.AiComboDto
import com.example.breakapp.model.AiRecommendationDto
import com.example.breakapp.model.IaResponse
import java.util.Calendar

/**
 * Motor local inteligente de recomendaciones basado en los principios
 * del agente hbrido de BreakApp-AI (reglas de utilidad, franjas horarias y maridajes).
 *
 * Permite que el Agente Break IA funcione de forma completamente autnoma
 * y sin cadas aun si el servidor externo de Python no est activo.
 */
object BreakAiLocalEngine {

    enum class TimeSlot(val displayName: String) {
        DESAYUNO("Mañana / Desayuno"),
        ALMUERZO("Mediodía / Almuerzo"),
        TARDE_SNACK("Tarde / Receso"),
        CENA_NOCHE("Cena / Noche")
    }

    private data class ParsedIntent(
        val categoryFilter: FoodCategory?,
        val timeSlot: TimeSlot,
        val budgetLimit: Int?,
        val wantsQuick: Boolean,
        val wantsHealthy: Boolean,
        val specificKeyword: String?
    )

    fun decide(prompt: String, userId: String = "usr_estudiante_01"): IaResponse {
        val intent = parsePrompt(prompt)
        val allProducts = getAllAvailableProducts()

        // Filtrar y puntuar productos
        val scoredProducts = allProducts.map { product ->
            val score = calculateUtilityScore(product, intent)
            val explanation = generateExplanation(product, intent)
            ScoredProduct(product, score, explanation)
        }.sortedByDescending { it.score }

        val topProducts = scoredProducts.take(4)
        if (topProducts.isEmpty()) {
            return IaResponse(
                response = "¡Hola! No encontré opciones que coincidan exactamente con tu búsqueda en este momento, pero puedes explorar nuestro menú general.",
                banner = "Explora el menú general del campus"
            )
        }

        // Seleccionar combo / maridaje sugerido para el primer producto
        val mainProduct = topProducts.first().product
        val pairingProduct = findBestPairing(mainProduct, allProducts)

        val comboDto = pairingProduct?.let {
            AiComboDto(
                mainName = mainProduct.name,
                pairingName = it.name,
                totalPrice = mainProduct.price + it.price
            )
        }

        // Construir DTOs estructurados
        val recDtos = topProducts.map { scored ->
            val pairing = if (scored.product.id == mainProduct.id) pairingProduct else null
            AiRecommendationDto(
                productId = scored.product.id,
                name = scored.product.name,
                price = scored.product.price,
                restaurantId = scored.product.restaurantId,
                restaurantName = getRestaurantName(scored.product.restaurantId),
                category = scored.product.category.name,
                prepTime = parsePrepTime(scored.product.prepTime),
                utilityScore = scored.score,
                explanations = listOf(scored.explanation),
                pairingId = pairing?.id,
                pairingName = pairing?.name,
                pairingPrice = pairing?.price
            )
        }

        // Generar texto conversacional natural
        val textBuilder = StringBuilder()
        val saludo = when (intent.timeSlot) {
            TimeSlot.DESAYUNO -> "¡Hola! Para empezar bien tu mañana en la Tadeo, te recomiendo estas opciones frescas y calientes:"
            TimeSlot.ALMUERZO -> "¡Hola! Para este almuerzo en el campus, el agente seleccionó estas opciones contundentes y deliciosas:"
            TimeSlot.TARDE_SNACK -> "¡Hola! Para tu receso o descanso entre clases, te recomiendo estos snacks rápidos y bebidas:"
            TimeSlot.CENA_NOCHE -> "¡Buenas noches! Para recargar energías al terminar clases, estas son las mejores opciones:"
        }

        textBuilder.append(saludo).append("\n\n")

        topProducts.forEachIndexed { index, item ->
            val p = item.product
            val rest = getRestaurantName(p.restaurantId)
            val precioFmt = formatCop(p.price)
            textBuilder.append("${index + 1}. **${p.name}** — $precioFmt ($rest)\n")
            textBuilder.append("   ⏱️ ${p.prepTime} • 💡 ${item.explanation}\n")
            if (p.id == mainProduct.id && pairingProduct != null) {
                textBuilder.append("   🥤 *Maridaje recomendado:* ${pairingProduct.name} (${formatCop(pairingProduct.price)})\n")
            }
            textBuilder.append("\n")
        }

        if (comboDto != null) {
            textBuilder.append("⭐ **Combo Sugerido:** ${comboDto.mainName} + ${comboDto.pairingName} por **${formatCop(comboDto.totalPrice)}**.\n\n")
        }

        textBuilder.append("¿Deseas agregar alguno directamente al carrito?")

        val banner = when (intent.timeSlot) {
            TimeSlot.ALMUERZO -> "Opciones ideales para el almuerzo universitario"
            TimeSlot.DESAYUNO -> "Desayunos frescos y energéticos"
            else -> "Recomendaciones personalizadas según tus hábitos"
        }

        return IaResponse(
            response = textBuilder.toString(),
            banner = banner,
            clusterLabel = "Estudiante Universitario Activo",
            recommendations = recDtos,
            suggestedCombo = comboDto
        )
    }

    private fun parsePrompt(prompt: String): ParsedIntent {
        val p = prompt.lowercase()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val timeSlot = when {
            p.contains("desayuno") || p.contains("mañana") || p.contains("tinto") || p.contains("café") || p.contains("cafe") -> TimeSlot.DESAYUNO
            p.contains("almuerzo") || p.contains("almorzar") || p.contains("hambre") || p.contains("plato fuerte") -> TimeSlot.ALMUERZO
            p.contains("snack") || p.contains("onces") || p.contains("tarde") || p.contains("receso") -> TimeSlot.TARDE_SNACK
            p.contains("cena") || p.contains("noche") -> TimeSlot.CENA_NOCHE
            hour in 7..11 -> TimeSlot.DESAYUNO
            hour in 12..15 -> TimeSlot.ALMUERZO
            hour in 16..18 -> TimeSlot.TARDE_SNACK
            else -> TimeSlot.CENA_NOCHE
        }

        val category = when {
            p.contains("almuerzo") || p.contains("bandeja") || p.contains("ajiaco") -> FoodCategory.ALMUERZOS
            p.contains("bebida") || p.contains("jugo") || p.contains("gaseosa") || p.contains("café") || p.contains("cafe") -> FoodCategory.BEBIDAS
            p.contains("dulce") || p.contains("postre") || p.contains("helado") || p.contains("torta") -> FoodCategory.POSTRES
            p.contains("snack") || p.contains("empanada") || p.contains("pastel") || p.contains("salchipapa") -> FoodCategory.SNACKS
            else -> null
        }

        val wantsQuick = p.contains("rápido") || p.contains("rapido") || p.contains("receso") || p.contains("prisa") || p.contains("afán")
        val wantsHealthy = p.contains("saludable") || p.contains("fitness") || p.contains("ligero") || p.contains("ensalada") || p.contains("sin azúcar")

        var budget: Int? = null
        val matchNumbers = Regex("""(\d+)[.\s]*(k|mil|000)?""").find(p)
        if (p.contains("menos de") || p.contains("presupuesto") || p.contains("económico") || p.contains("barato") || p.contains("$")) {
            if (matchNumbers != null) {
                var value = matchNumbers.groupValues[1].toIntOrNull() ?: 10000
                if (value < 100) value *= 1000
                budget = value
            } else {
                budget = 10000
            }
        }

        val specificKeywords = listOf("pizza", "hamburguesa", "salchipapa", "perro", "ajiaco", "bandeja", "ensalada", "croissant", "dona", "brownie")
        val foundKeyword = specificKeywords.firstOrNull { p.contains(it) }

        return ParsedIntent(
            categoryFilter = category,
            timeSlot = timeSlot,
            budgetLimit = budget,
            wantsQuick = wantsQuick,
            wantsHealthy = wantsHealthy,
            specificKeyword = foundKeyword
        )
    }

    private fun calculateUtilityScore(product: Product, intent: ParsedIntent): Double {
        var score = 0.50

        // Coincidencia con palabra clave específica (ej. "pizza", "salchipapa")
        if (intent.specificKeyword != null) {
            val nameDesc = "${product.name} ${product.description}".lowercase()
            if (nameDesc.contains(intent.specificKeyword)) {
                score += 0.40
            }
        }

        // Afinidad por franja horaria y categoría
        when (intent.timeSlot) {
            TimeSlot.DESAYUNO -> {
                if (product.category == FoodCategory.BEBIDAS || product.category == FoodCategory.SNACKS) score += 0.25
                if (product.name.contains("Café", true) || product.name.contains("Capuchino", true) || product.name.contains("Pastel", true) || product.name.contains("Almojábana", true)) score += 0.20
            }
            TimeSlot.ALMUERZO -> {
                if (product.category == FoodCategory.ALMUERZOS) score += 0.35
                if (product.name.contains("Bandeja", true) || product.name.contains("Ajiaco", true) || product.name.contains("Pizza", true) || product.name.contains("Salchipapa", true)) score += 0.15
            }
            TimeSlot.TARDE_SNACK -> {
                if (product.category == FoodCategory.SNACKS || product.category == FoodCategory.POSTRES || product.category == FoodCategory.BEBIDAS) score += 0.25
                if (product.name.contains("Smoothie", true) || product.name.contains("Dona", true) || product.name.contains("Empanada", true)) score += 0.20
            }
            TimeSlot.CENA_NOCHE -> {
                if (product.name.contains("Pizza", true) || product.name.contains("Hamburguesa", true) || product.name.contains("Salchipapa", true)) score += 0.30
            }
        }

        // Filtro de categoría explícita
        if (intent.categoryFilter != null && product.category == intent.categoryFilter) {
            score += 0.25
        }

        // Preferencia de rapidez (prep time < 10 min)
        if (intent.wantsQuick) {
            val mins = parsePrepTime(product.prepTime)
            if (mins <= 10) score += 0.20 else score -= 0.10
        }

        // Preferencia saludable
        if (intent.wantsHealthy) {
            val healthyTokens = listOf("ensalada", "natural", "fruta", "agua", "aromática", "hierbas")
            if (healthyTokens.any { product.name.contains(it, true) || product.description.contains(it, true) }) {
                score += 0.30
            } else {
                score -= 0.15
            }
        }

        // Presupuesto
        if (intent.budgetLimit != null) {
            if (product.price <= intent.budgetLimit) {
                score += 0.25
            } else {
                score -= 0.40 // penalizar si supera el presupuesto
            }
        }

        // Popularidad base
        if (product.featured) {
            score += 0.05
        }

        return score.coerceIn(0.0, 1.0)
    }

    private fun generateExplanation(product: Product, intent: ParsedIntent): String {
        return when {
            intent.specificKeyword != null && product.name.contains(intent.specificKeyword, true) ->
                "Coincide directamente con tu antojo de ${intent.specificKeyword}"
            intent.budgetLimit != null && product.price <= intent.budgetLimit ->
                "Opción económica que se ajusta a tu presupuesto de ${formatCop(intent.budgetLimit)}"
            intent.wantsQuick && parsePrepTime(product.prepTime) <= 10 ->
                "Preparación súper rápida (${product.prepTime}) para tu receso"
            intent.wantsHealthy ->
                "Opción ligera y balanceada recomendada para tu bienestar"
            intent.timeSlot == TimeSlot.ALMUERZO && product.category == FoodCategory.ALMUERZOS ->
                "El plato fuerte más solicitado y mejor calificado para este mediodía"
            intent.timeSlot == TimeSlot.DESAYUNO ->
                "Excelente combinación de energía y sabor para iniciar la jornada"
            else ->
                "Estudiantes con gustos similares también eligen con frecuencia este producto"
        }
    }

    private fun findBestPairing(mainProduct: Product, allProducts: List<Product>): Product? {
        return when (mainProduct.category) {
            FoodCategory.ALMUERZOS, FoodCategory.SNACKS -> {
                // Sugerir una bebida
                allProducts.firstOrNull { it.category == FoodCategory.BEBIDAS && it.restaurantId == mainProduct.restaurantId }
                    ?: allProducts.firstOrNull { it.category == FoodCategory.BEBIDAS }
            }
            FoodCategory.BEBIDAS -> {
                // Sugerir un snack o postre
                allProducts.firstOrNull { (it.category == FoodCategory.SNACKS || it.category == FoodCategory.POSTRES) && it.restaurantId == mainProduct.restaurantId }
                    ?: allProducts.firstOrNull { it.category == FoodCategory.SNACKS }
            }
            FoodCategory.POSTRES -> {
                allProducts.firstOrNull { it.category == FoodCategory.BEBIDAS && (it.name.contains("Café", true) || it.name.contains("Tinto", true)) }
            }
        }
    }

    private fun getAllAvailableProducts(): List<Product> {
        val restaurants = menuRepository.restaurants()
        return restaurants.flatMap { menuRepository.productsForRestaurant(it.id) }.distinctBy { it.id }
    }

    private fun getRestaurantName(id: String): String {
        return menuRepository.restaurantById(id)?.name ?: id.replace("_", " ").capitalizeWords()
    }

    private fun parsePrepTime(prepStr: String): Int {
        val numbers = Regex("""\d+""").findAll(prepStr).map { it.value.toIntOrNull() ?: 10 }.toList()
        return if (numbers.isNotEmpty()) numbers.average().toInt() else 10
    }

    private fun formatCop(price: Int): String {
        return String.format(java.util.Locale.forLanguageTag("es-CO"), "$%,d", price).replace(',', '.')
    }

    private data class ScoredProduct(
        val product: Product,
        val score: Double,
        val explanation: String
    )
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() } }
