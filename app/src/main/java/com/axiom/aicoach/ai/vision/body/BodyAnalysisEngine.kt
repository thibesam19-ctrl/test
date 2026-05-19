package com.axiom.aicoach.ai.vision.body

import com.axiom.aicoach.data.local.entities.WeightLogEntity
import com.axiom.aicoach.data.local.entities.WorkoutSessionEntity

interface BodyAnalysisEngine {
    suspend fun analyze(
        recentWeightLogs: List<WeightLogEntity>,
        recentSessions: List<WorkoutSessionEntity>,
        photoCount: Int,
        daysSinceStart: Int,
    ): BodyAnalysisResult
}
