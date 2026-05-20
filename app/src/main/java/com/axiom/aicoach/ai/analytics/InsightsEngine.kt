package com.axiom.aicoach.ai.analytics

import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.data.local.dao.WeightLogDao
import com.axiom.aicoach.data.local.dao.WorkoutSessionDao
import com.axiom.aicoach.domain.model.FitnessGoal
import com.axiom.aicoach.util.toDbString
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsightsEngine @Inject constructor(
    private val weightLogDao: WeightLogDao,
    private val foodLogDao: FoodLogDao,
    private val waterLogDao: WaterLogDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val userProfileDao: UserProfileDao,
    private val streakDao: StreakDao,
    private val foodItemDao: FoodItemDao,
) {
    suspend fun generateInsights(userId: String): List<WeeklyInsight> {
        val insights = mutableListOf<WeeklyInsight>()

        val today = LocalDate.now()
        val profile = userProfileDao.getProfile(userId)

        // ── 1. Plateau Detection ──────────────────────────────────────────────
        val isWeightLossGoal = profile != null &&
            (profile.goal == FitnessGoal.LOSE_FAT.name || profile.goal == FitnessGoal.MAINTAIN.name)

        if (isWeightLossGoal) {
            // Use getRecent with a large enough limit to cover 14 days. We filter by date below.
            val fourteenDaysAgo = today.minusDays(14)
            val start = fourteenDaysAgo.toDbString()
            val end = today.toDbString() + "T23:59:59"
            // WeightLogDao doesn't have a range query — use getRecent and filter in memory.
            // Fetching last 30 is more than enough to cover 14 days.
            val recentWeightLogs = weightLogDao.getRecent(userId, 30)
            val logsIn14Days = recentWeightLogs.filter { log ->
                val logDate = log.loggedAt.substring(0, 10)
                logDate >= fourteenDaysAgo.toDbString()
            }
            if (logsIn14Days.size >= 3) {
                val weights = logsIn14Days.map { it.weightKg }
                val range = weights.max() - weights.min()
                if (range < 0.5f) {
                    insights.add(
                        WeeklyInsight(
                            id = UUID.randomUUID().toString(),
                            type = InsightType.PLATEAU_DETECTED,
                            title = "Weight Plateau Detected",
                            body = "Your weight hasn't changed in 2 weeks. Consider adjusting your calorie deficit by 100–200kcal or adding a cardio session.",
                            priority = InsightPriority.HIGH,
                        )
                    )
                }
            }
        }

        // ── 2. Low Protein ────────────────────────────────────────────────────
        val bodyWeightKg = profile?.weightKg ?: 70f
        val sevenDaysAgo = today.minusDays(7)
        val foodLogsLast7 = foodLogDao.getLogsInRange(
            userId = userId,
            start = sevenDaysAgo.toDbString(),
            end = today.toDbString() + "T23:59:59",
        )
        if (foodLogsLast7.isNotEmpty()) {
            val allFoodItemIds = foodLogsLast7.map { it.foodItemId }.distinct()
            val foodItemMap = allFoodItemIds.mapNotNull { id -> foodItemDao.findById(id) }
                .associateBy { it.id }

            var totalProtein = 0f
            for (log in foodLogsLast7) {
                val item = foodItemMap[log.foodItemId] ?: continue
                totalProtein += item.proteinG * log.servings
            }
            // Calculate days with any food data (up to 7)
            val daysWithData = foodLogsLast7
                .map { it.loggedAt.substring(0, 10) }
                .distinct()
                .size
                .coerceAtLeast(1)
            val avgDailyProtein = totalProtein / daysWithData
            val minProteinTarget = 0.8f * bodyWeightKg

            if (avgDailyProtein < minProteinTarget) {
                insights.add(
                    WeeklyInsight(
                        id = UUID.randomUUID().toString(),
                        type = InsightType.LOW_PROTEIN,
                        title = "Protein Intake Below Goal",
                        body = "You're averaging ${"%.0f".format(avgDailyProtein)}g protein/day. Aim for at least ${"%.0f".format(minProteinTarget)}g to support muscle retention.",
                        priority = InsightPriority.MEDIUM,
                    )
                )
            }
        }

        // ── 3 & 4. Missed Workouts / Great Consistency ────────────────────────
        // WorkoutSessionDao only has getRecentSessions(limit). Fetch last 30 and filter by date.
        val recentSessions = workoutSessionDao.getRecentSessions(userId, 30)
        val sessionsLast7 = recentSessions.filter { session ->
            val sessionDate = session.startedAt.substring(0, 10)
            sessionDate >= sevenDaysAgo.toDbString()
        }
        val workoutCountLast7 = sessionsLast7.size

        if (workoutCountLast7 >= 4) {
            insights.add(
                WeeklyInsight(
                    id = UUID.randomUUID().toString(),
                    type = InsightType.GREAT_CONSISTENCY,
                    title = "Crushing It This Week!",
                    body = "4+ workouts this week. Consistency is the #1 predictor of results.",
                    priority = InsightPriority.LOW,
                )
            )
        } else if (workoutCountLast7 < 2) {
            insights.add(
                WeeklyInsight(
                    id = UUID.randomUUID().toString(),
                    type = InsightType.MISSED_WORKOUTS,
                    title = "Workouts This Week: $workoutCountLast7/3",
                    body = "You've had fewer workouts than planned. Even a 20-minute session keeps momentum going.",
                    priority = InsightPriority.MEDIUM,
                )
            )
        }

        // ── 5. Calorie Deficit Too High ────────────────────────────────────────
        val calorieGoal = if (profile != null) {
            (profile.weightKg * 35).toInt().coerceAtLeast(1500).toFloat()
        } else {
            2100f
        }
        val threeDaysAgo = today.minusDays(3)
        val foodLogsLast3 = foodLogDao.getLogsInRange(
            userId = userId,
            start = threeDaysAgo.toDbString(),
            end = today.toDbString() + "T23:59:59",
        )
        if (foodLogsLast3.isNotEmpty()) {
            val allFoodItemIds3 = foodLogsLast3.map { it.foodItemId }.distinct()
            val foodItemMap3 = allFoodItemIds3.mapNotNull { id -> foodItemDao.findById(id) }
                .associateBy { it.id }

            var totalCalories3 = 0f
            for (log in foodLogsLast3) {
                val item = foodItemMap3[log.foodItemId] ?: continue
                totalCalories3 += item.calories * log.servings
            }
            val daysLast3 = foodLogsLast3
                .map { it.loggedAt.substring(0, 10) }
                .distinct()
                .size
                .coerceAtLeast(1)
            val avgCalories3 = totalCalories3 / daysLast3

            if (avgCalories3 < calorieGoal * 0.7f) {
                insights.add(
                    WeeklyInsight(
                        id = UUID.randomUUID().toString(),
                        type = InsightType.CALORIE_DEFICIT_TOO_HIGH,
                        title = "Very Low Calorie Intake",
                        body = "Eating under 70% of your goal can slow metabolism and cause muscle loss. Try to stay within 200–500kcal below your goal.",
                        priority = InsightPriority.HIGH,
                    )
                )
            }
        }

        // ── 6. Hydration Low ─────────────────────────────────────────────────
        val todayStr = today.toDbString()
        val todayWaterTotal = try {
            waterLogDao.observeTotalForDate(userId, todayStr).first()
        } catch (e: Exception) {
            0f
        }

        if (todayWaterTotal < 1500f) {
            insights.add(
                WeeklyInsight(
                    id = UUID.randomUUID().toString(),
                    type = InsightType.HYDRATION_LOW,
                    title = "Low Hydration Today",
                    body = "You've only logged ${"%.0f".format(todayWaterTotal)}ml water today. Proper hydration improves performance and recovery.",
                    priority = InsightPriority.LOW,
                )
            )
        }

        // ── 7. Streak Milestone ───────────────────────────────────────────────
        val streak = streakDao.get(userId)
        val streakMilestones = setOf(7, 14, 21, 30, 60, 90)
        if (streak != null && streak.currentStreak in streakMilestones) {
            val n = streak.currentStreak
            insights.add(
                WeeklyInsight(
                    id = UUID.randomUUID().toString(),
                    type = InsightType.STREAK_MILESTONE,
                    title = "$n-Day Streak! 🔥",
                    body = "You've been consistent for $n days. Keep going!",
                    priority = InsightPriority.LOW,
                )
            )
        }

        // ── 8. Weight Loss Progress ───────────────────────────────────────────
        val allWeightLogs = weightLogDao.getRecent(userId, 200)
        if (allWeightLogs.size >= 2) {
            // getRecent returns DESC order — latest first, oldest last
            val latestWeight = allWeightLogs.first().weightKg
            val firstWeight = allWeightLogs.last().weightKg
            val lost = firstWeight - latestWeight
            if (lost >= 1f) {
                insights.add(
                    WeeklyInsight(
                        id = UUID.randomUUID().toString(),
                        type = InsightType.WEIGHT_LOSS_PROGRESS,
                        title = "${"%.1f".format(lost)}kg Lost Since You Started",
                        body = "You've lost ${"%.1f".format(lost)}kg. That's real progress — keep trusting the process.",
                        priority = InsightPriority.LOW,
                    )
                )
            }
        }

        // ── 9. Recovery Needed ────────────────────────────────────────────────
        val fiveDaysAgo = today.minusDays(5)
        val sessionsLast5 = recentSessions.filter { session ->
            val sessionDate = session.startedAt.substring(0, 10)
            sessionDate >= fiveDaysAgo.toDbString()
        }
        // Count distinct training days in last 5 days
        val trainingDaysLast5 = sessionsLast5
            .map { it.startedAt.substring(0, 10) }
            .distinct()
            .size
        if (trainingDaysLast5 >= 5) {
            insights.add(
                WeeklyInsight(
                    id = UUID.randomUUID().toString(),
                    type = InsightType.RECOVERY_NEEDED,
                    title = "Rest Day Recommended",
                    body = "You've trained 5 days in a row. A rest day improves muscle recovery and prevents injury.",
                    priority = InsightPriority.MEDIUM,
                )
            )
        }

        // Sort by priority (HIGH first) and return top 3
        val priorityOrder = mapOf(
            InsightPriority.HIGH to 0,
            InsightPriority.MEDIUM to 1,
            InsightPriority.LOW to 2,
        )
        return insights
            .sortedBy { priorityOrder[it.priority] ?: Int.MAX_VALUE }
            .take(3)
    }
}
