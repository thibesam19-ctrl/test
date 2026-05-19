package com.axiom.aicoach.ai.analytics

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationEngine @Inject constructor(
    private val insightsEngine: InsightsEngine,
) {
    suspend fun getRecommendations(userId: String): List<Recommendation> {
        val insights = insightsEngine.generateInsights(userId)
        val recommendations = mutableListOf<Recommendation>()

        for (insight in insights) {
            when (insight.type) {
                InsightType.PLATEAU_DETECTED -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.WORKOUT,
                            title = "Add a Cardio Session",
                            description = "Break your plateau by adding one extra cardio session this week to increase your energy expenditure.",
                            actionType = ActionType.ADD_WORKOUT,
                            actionValue = "30min cardio",
                        )
                    )
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.NUTRITION,
                            title = "Slight Calorie Adjustment",
                            description = "Try reducing your daily intake by 100–200kcal to break through the plateau without sacrificing muscle.",
                            actionType = ActionType.ADJUST_CALORIES,
                            actionValue = "-150kcal",
                        )
                    )
                }

                InsightType.LOW_PROTEIN -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.NUTRITION,
                            title = "Add a High-Protein Meal",
                            description = "Boost your protein by adding a chicken breast, Greek yogurt, or a protein shake to your daily routine.",
                            actionType = ActionType.ADD_MEAL,
                            actionValue = "add chicken / yogurt",
                        )
                    )
                }

                InsightType.HYDRATION_LOW -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.HYDRATION,
                            title = "Drink More Water",
                            description = "Set a reminder every hour to drink a glass of water. Aim for at least 2L today.",
                            actionType = ActionType.DRINK_WATER,
                            actionValue = "+500ml now",
                        )
                    )
                }

                InsightType.MISSED_WORKOUTS -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.WORKOUT,
                            title = "Try a Short Session",
                            description = "Can't fit a full workout? A 20-minute bodyweight circuit or brisk walk still counts and keeps momentum.",
                            actionType = ActionType.ADD_WORKOUT,
                            actionValue = "20min bodyweight",
                        )
                    )
                }

                InsightType.RECOVERY_NEEDED -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.RECOVERY,
                            title = "Active Recovery Day",
                            description = "Take a rest day with light stretching or a gentle walk to let your muscles repair and grow.",
                            actionType = ActionType.REST,
                            actionValue = "stretch / walk",
                        )
                    )
                }

                InsightType.GREAT_CONSISTENCY -> {
                    recommendations.add(
                        Recommendation(
                            id = UUID.randomUUID().toString(),
                            category = RecommendationCategory.MINDSET,
                            title = "Increase the Challenge",
                            description = "You're consistent — try adding 5% more weight or one extra set to keep making gains.",
                            actionType = ActionType.NONE,
                            actionValue = "+5% load",
                        )
                    )
                }

                else -> {
                    // No specific recommendation for other insight types
                }
            }
        }

        return recommendations
    }
}
