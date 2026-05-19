package com.axiom.aicoach.ai.vision.food

import android.graphics.Bitmap
import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.entities.FoodItemEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Orchestrates food recognition and persistence.
 *
 * After the [MLKitFoodRecognitionEngine] returns a [FoodRecognitionResult],
 * each newly detected food item is written to [FoodItemDao] (using
 * [OnConflictStrategy.IGNORE] so existing records are never clobbered).
 * This ensures the items appear in the food search / log flow immediately
 * after scanning.
 */
@Singleton
class FoodRecognitionRepository @Inject constructor(
    private val engine: MLKitFoodRecognitionEngine,
    private val foodItemDao: FoodItemDao,
) {

    /**
     * Runs food recognition on [bitmap] and persists any detected foods that
     * are not already in the local database.
     *
     * @return The raw [FoodRecognitionResult] from the engine.
     */
    suspend fun recognizeAndLog(bitmap: Bitmap): FoodRecognitionResult {
        val result = engine.recognizeFromBitmap(bitmap)

        val entities = result.detectedFoods.map { food ->
            food.toFoodItemEntity()
        }
        if (entities.isNotEmpty()) {
            foodItemDao.insertAll(entities)   // IGNORE conflict — safe to call multiple times
        }

        return result
    }

    // ── mapping ───────────────────────────────────────────────────────────────

    private fun DetectedFood.toFoodItemEntity(): FoodItemEntity {
        // Scale macros from per-100g to the food's estimated portion size
        val factor = estimatedGrams / 100f
        return FoodItemEntity(
            id = "ai_${name}",
            name = displayName,
            brand = null,
            servingSize = estimatedGrams.toFloat(),
            servingUnit = "g",
            calories = caloriesPer100g * factor,
            proteinG = proteinPer100g * factor,
            carbsG = carbsPer100g * factor,
            fatG = fatPer100g * factor,
            fiberG = null,
            sugarG = null,
            sodiumMg = null,
            glycemicIndex = null,
            allergensJson = "[]",
            barcode = null,
            imageUrl = null,
            cuisine = cuisineType.name,
            isVerified = false,
        )
    }
}
