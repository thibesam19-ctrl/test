package com.axiom.aicoach.ai.vision.pose

import androidx.camera.core.ImageProxy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PoseAnalysisEngine {
    fun processFrame(imageProxy: ImageProxy): Flow<PoseAnalysisResult>
    fun setExerciseType(type: ExerciseType)
    fun reset()
    val repCount: StateFlow<Int>
    val formScore: StateFlow<Int>
    val feedback: StateFlow<List<FormFeedback>>
}
