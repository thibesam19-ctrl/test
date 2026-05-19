package com.axiom.aicoach.ai.analytics

data class WeeklyInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val body: String,
    val actionLabel: String? = null,
    val priority: InsightPriority,
    val generatedAt: Long = System.currentTimeMillis(),
)

enum class InsightType {
    PLATEAU_DETECTED,
    LOW_PROTEIN,
    MISSED_WORKOUTS,
    GREAT_CONSISTENCY,
    CALORIE_SURPLUS,
    CALORIE_DEFICIT_TOO_HIGH,
    HYDRATION_LOW,
    STREAK_MILESTONE,
    WEIGHT_LOSS_PROGRESS,
    RECOVERY_NEEDED,
}

enum class InsightPriority { HIGH, MEDIUM, LOW }
