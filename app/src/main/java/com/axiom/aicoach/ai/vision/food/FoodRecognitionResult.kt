package com.axiom.aicoach.ai.vision.food

data class FoodRecognitionResult(
    val detectedFoods: List<DetectedFood>,
    val confidence: Float,          // 0..1
    val source: RecognitionSource,
    val rawLabels: List<String>,
)

data class DetectedFood(
    val name: String,
    val displayName: String,        // localized/formatted
    val caloriesPer100g: Int,
    val proteinPer100g: Float,
    val carbsPer100g: Float,
    val fatPer100g: Float,
    val estimatedGrams: Int = 150,  // portion estimate
    val confidence: Float,
    val cuisineType: CuisineType = CuisineType.INTERNATIONAL,
)

enum class CuisineType { SRI_LANKAN, INDIAN, WESTERN, INTERNATIONAL }
enum class RecognitionSource { MLKIT_IMAGE_LABELING, TFLITE_CUSTOM, MOCK }
