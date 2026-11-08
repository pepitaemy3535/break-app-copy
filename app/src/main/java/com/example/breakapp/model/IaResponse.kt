package com.example.breakapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta que entrega el Agente de IA de BreakApp.
 * Es compatible tanto con respuestas de solo texto plano como con recomendaciones estructuradas y combos.
 */
data class IaResponse(
    @SerializedName("response")
    val response: String,

    @SerializedName("banner")
    val banner: String? = null,

    @SerializedName("cluster_label")
    val clusterLabel: String? = null,

    @SerializedName("recommendations")
    val recommendations: List<AiRecommendationDto>? = null,

    @SerializedName("suggested_combo")
    val suggestedCombo: AiComboDto? = null
)

data class AiRecommendationDto(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("restaurant_id")
    val restaurantId: String? = null,

    @SerializedName("restaurant_name")
    val restaurantName: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("prep_time")
    val prepTime: Int? = null,

    @SerializedName("utility_score")
    val utilityScore: Double? = null,

    @SerializedName("explanations")
    val explanations: List<String>? = null,

    @SerializedName("pairing_id")
    val pairingId: String? = null,

    @SerializedName("pairing_name")
    val pairingName: String? = null,

    @SerializedName("pairing_price")
    val pairingPrice: Int? = null
)

data class AiComboDto(
    @SerializedName("main_name")
    val mainName: String,

    @SerializedName("pairing_name")
    val pairingName: String,

    @SerializedName("total_price")
    val totalPrice: Int
)
