package com.axiom.aicoach.ai.vision.food

import android.content.Context
import android.graphics.Bitmap

interface FoodRecognitionEngine {
    suspend fun recognizeFromBitmap(bitmap: Bitmap): FoodRecognitionResult
    suspend fun recognizeFromUri(uri: android.net.Uri, context: Context): FoodRecognitionResult
    val source: RecognitionSource
}
