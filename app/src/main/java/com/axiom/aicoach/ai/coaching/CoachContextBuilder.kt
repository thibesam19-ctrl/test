package com.axiom.aicoach.ai.coaching

import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.data.local.dao.WeightLogDao
import com.axiom.aicoach.data.local.dao.WorkoutSessionDao
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class UserCoachContext(
    val userName: String = "Athlete",
    val weightKg: Float? = null,
    val goalWeightKg: Float? = null,
    val todayCaloriesKcal: Int = 0,
    val calorieGoalKcal: Int = 2100,
    val todayProteinG: Int = 0,
    val proteinGoalG: Int = 158,
    val todayWaterMl: Int = 0,
    val waterGoalMl: Int = 2400,
    val weekWorkoutCount: Int = 0,
    val streakDays: Int = 0,
    val fitnessGoal: String = "get fit",
    val recentWorkoutNames: List<String> = emptyList(),
    val weightTrend: String = "stable", // "losing", "gaining", "stable"
)

// Hardcoded user id — mirrors CoachViewModel; replace when real auth is in place.
private const val CURRENT_USER_ID = "local_user"

@Singleton
class CoachContextBuilder @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val foodLogDao: FoodLogDao,
    private val waterLogDao: WaterLogDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val weightLogDao: WeightLogDao,
    private val streakDao: StreakDao,
) {

    suspend fun build(): UserCoachContext {
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // User profile
        val profile = userProfileDao.getProfile(CURRENT_USER_ID)
        val userName = profile?.displayName?.ifBlank { "Athlete" } ?: "Athlete"
        val weightKg = profile?.weightKg
        val goalWeightKg = profile?.targetWeightKg
        val fitnessGoal = profile?.goal?.lowercase()?.replace('_', ' ') ?: "get fit"

        // Today's food logs — FoodLogEntity only has foodItemId and servings; we use
        // a conservative estimate: we don't have inline macros per log entry, so we
        // report 0 until a JOIN query is available.  This context field is informational.
        val todayCalories = 0
        val todayProtein = 0

        // Today's water (ml)
        val todayWaterMl = waterLogDao
            .observeTotalForDate(CURRENT_USER_ID, todayStr)
            .first()
            .toInt()

        // This week's workouts
        val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val weekStartStr = weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val recentSessions = workoutSessionDao.getRecentSessions(CURRENT_USER_ID, 10)
        val weekSessions = recentSessions.filter { session ->
            session.startedAt >= "${weekStartStr}T00:00:00"
        }
        val weekWorkoutCount = weekSessions.size
        val recentWorkoutNames = recentSessions.take(3).map { it.workoutName }

        // Streak
        val streakEntity = streakDao.get(CURRENT_USER_ID)
        val streakDays = streakEntity?.currentStreak ?: 0

        // Weight trend (compare latest vs oldest in the last ~7-8 entries)
        val recentWeights = weightLogDao.getRecent(CURRENT_USER_ID, 8)
        val weightTrend = when {
            recentWeights.size < 2 -> "stable"
            else -> {
                val latest = recentWeights.first().weightKg
                val older = recentWeights.last().weightKg
                val diff = latest - older
                when {
                    diff < -0.3f -> "losing"
                    diff > 0.3f -> "gaining"
                    else -> "stable"
                }
            }
        }

        return UserCoachContext(
            userName = userName,
            weightKg = weightKg,
            goalWeightKg = goalWeightKg,
            todayCaloriesKcal = todayCalories,
            calorieGoalKcal = 2100,
            todayProteinG = todayProtein,
            proteinGoalG = 158,
            todayWaterMl = todayWaterMl,
            waterGoalMl = 2400,
            weekWorkoutCount = weekWorkoutCount,
            streakDays = streakDays,
            fitnessGoal = fitnessGoal,
            recentWorkoutNames = recentWorkoutNames,
            weightTrend = weightTrend,
        )
    }
}
