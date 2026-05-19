package com.axiom.aicoach.ai.vision.body

data class BodyAnalysisResult(
    val transformationScore: Int,           // 0–100
    val consistencyScore: Int,              // 0–100
    val estimatedProgressPercent: Float,    // toward goal
    val insights: List<BodyInsight>,
    val disclaimer: String = "Visual estimation only. Not medical advice.",
    val analysisDate: String,
)

data class BodyInsight(
    val category: InsightCategory,
    val message: String,
    val sentiment: InsightSentiment,
)

enum class InsightCategory { POSTURE, SYMMETRY, OVERALL_PROGRESS, CONSISTENCY, RECOMMENDATION }
enum class InsightSentiment { POSITIVE, NEUTRAL, ENCOURAGING }
