package com.axiom.aicoach.ai.vision.food

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Food recognition engine backed by ML Kit Image Labeling.
 *
 * The on-device labeler produces general-purpose labels (e.g. "Food",
 * "Rice", "Chicken").  We filter to labels with confidence > 0.6, then
 * pass each label string through [FoodNutritionDatabase.findByLabel] to
 * obtain structured nutrition data.
 *
 * If the labeler produces no food-related hits the result is returned with
 * an empty [detectedFoods] list and a low [confidence] value so the caller
 * can surface a graceful "couldn't identify food" message.
 */
@Singleton
class MLKitFoodRecognitionEngine @Inject constructor() : FoodRecognitionEngine {

    override val source: RecognitionSource = RecognitionSource.MLKIT_IMAGE_LABELING

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)   // pre-filter; we re-filter at 0.6 below
            .build()
    )

    override suspend fun recognizeFromBitmap(bitmap: Bitmap): FoodRecognitionResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        return processImage(image)
    }

    override suspend fun recognizeFromUri(uri: Uri, context: Context): FoodRecognitionResult {
        val image = InputImage.fromFilePath(context, uri)
        return processImage(image)
    }

    // ── private ───────────────────────────────────────────────────────────────

    private suspend fun processImage(image: InputImage): FoodRecognitionResult {
        return runCatching {
            val labels = Tasks.await(labeler.process(image))

            // Keep only labels with confidence >= 0.6
            val highConfidenceLabels = labels.filter { it.confidence >= 0.6f }
            val rawLabelStrings = highConfidenceLabels.map { it.text }

            // Map ML Kit label strings → DetectedFood entries, deduplicating by name
            val detectedFoods = highConfidenceLabels
                .mapNotNull { label ->
                    FoodNutritionDatabase.findByLabel(label.text)?.copy(
                        confidence = label.confidence,
                    )
                }
                .distinctBy { it.name }

            val overallConfidence = if (detectedFoods.isNotEmpty()) {
                detectedFoods.maxOf { it.confidence }
            } else {
                0.1f
            }

            FoodRecognitionResult(
                detectedFoods = detectedFoods,
                confidence = overallConfidence,
                source = source,
                rawLabels = rawLabelStrings,
            )
        }.getOrElse { throwable ->
            // Return a safe empty result on any error so the UI can recover
            FoodRecognitionResult(
                detectedFoods = emptyList(),
                confidence = 0f,
                source = source,
                rawLabels = emptyList(),
            )
        }
    }
}
