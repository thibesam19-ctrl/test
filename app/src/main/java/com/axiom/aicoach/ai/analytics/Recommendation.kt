package com.axiom.aicoach.ai.analytics

data class Recommendation(
    val id: String,
    val category: RecommendationCategory,
    val title: String,
    val description: String,
    val actionType: ActionType,
    val actionValue: String = "",
)

enum class RecommendationCategory { NUTRITION, WORKOUT, RECOVERY, HYDRATION, MINDSET }
enum class ActionType { ADJUST_CALORIES, ADD_MEAL, ADD_WORKOUT, REST, DRINK_WATER, NONE }
