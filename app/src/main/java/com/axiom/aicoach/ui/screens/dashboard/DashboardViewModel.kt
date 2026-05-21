package com.axiom.aicoach.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.ai.analytics.InsightsEngine
import com.axiom.aicoach.ai.analytics.Recommendation
import com.axiom.aicoach.ai.analytics.RecommendationEngine
import com.axiom.aicoach.ai.analytics.WeeklyInsight
import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.analytics.AnalyticsEvent
import com.axiom.aicoach.analytics.AxiomAnalytics
import com.axiom.aicoach.security.UserSession
import com.axiom.aicoach.util.toDbString
import dagger.hilt.android.lifecycle.HiltViewModel
import com.axiom.aicoach.data.local.entities.FoodLogEntity
import com.axiom.aicoach.data.local.entities.StreakEntity
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "Athlete",
    val caloriesConsumed: Int = 0,
    val caloriesGoal: Int = 2100,
    val proteinG: Int = 0,
    val proteinGoal: Int = 158,
    val carbsG: Int = 0,
    val carbsGoal: Int = 220,
    val fatG: Int = 0,
    val fatGoal: Int = 70,
    val waterMl: Int = 0,
    val waterGoalMl: Int = 2400,
    val streakDays: Int = 0,
    val isLoading: Boolean = true,
    val insights: List<WeeklyInsight> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val foodLogDao: FoodLogDao,
    private val foodItemDao: FoodItemDao,
    private val waterLogDao: WaterLogDao,
    private val streakDao: StreakDao,
    private val insightsEngine: InsightsEngine,
    private val recommendationEngine: RecommendationEngine,
    private val analytics: AxiomAnalytics,
    private val userSession: UserSession,
) : ViewModel() {

    private val userId: String get() = userSession.userId
    private val today: String = LocalDate.now().toDbString()

    private val _insights = MutableStateFlow<List<WeeklyInsight>>(emptyList())
    val insights: StateFlow<List<WeeklyInsight>> = _insights.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Recommendation>>(emptyList())
    val recommendations: StateFlow<List<Recommendation>> = _recommendations.asStateFlow()

    init {
        analytics.track(AnalyticsEvent.ScreenViewed("dashboard"))
        viewModelScope.launch {
            _insights.value = insightsEngine.generateInsights(userId)
            _recommendations.value = recommendationEngine.getRecommendations(userId)
        }
    }

    // Observe the user profile (nullable — may not exist yet).
    private val profileFlow: Flow<UserProfileEntity?> =
        userProfileDao.observeProfile(userId)

    // Observe today's food logs.
    private val foodLogsFlow: Flow<List<FoodLogEntity>> =
        foodLogDao.observeLogsForDate(userId, today)

    // Observe today's water total (ml) — the DAO returns a pre-aggregated Float.
    private val waterTotalFlow: Flow<Float> =
        waterLogDao.observeTotalForDate(userId, today)

    // Observe the streak record.
    private val streakFlow: Flow<StreakEntity?> =
        streakDao.observe(userId)

    // Combine the 4 nutrition/activity flows into a base state, then merge with
    // the AI insight/recommendation flows using a second combine.
    private val baseStateFlow = combine(
        profileFlow,
        foodLogsFlow,
        waterTotalFlow,
        streakFlow,
    ) { profile, foodLogs, waterTotal, streak ->
        // Sum macros from food logs by loading each food item synchronously from the
        // already-cached entities. Because FoodLogEntity stores only the foodItemId we
        // need to look up calorie/macro data. We fetch them in a batch here; this is
        // intentionally simple — a full repository layer would handle this join.
        val foodItems = if (foodLogs.isNotEmpty()) {
            foodItemDao.getAll()
                .filter { item -> foodLogs.any { log -> log.foodItemId == item.id } }
                .associateBy { it.id }
        } else {
            emptyMap()
        }

        var totalCalories = 0f
        var totalProtein = 0f
        var totalCarbs = 0f
        var totalFat = 0f

        for (log in foodLogs) {
            val item = foodItems[log.foodItemId] ?: continue
            val servings = log.servings
            totalCalories += item.calories * servings
            totalProtein += item.proteinG * servings
            totalCarbs += item.carbsG * servings
            totalFat += item.fatG * servings
        }

        // Compute goals from profile when available; fall back to defaults.
        val caloriesGoal: Int
        val proteinGoal: Int
        val carbsGoal: Int
        val fatGoal: Int
        val waterGoalMl: Int

        if (profile != null) {
            // Simple heuristic: 35 kcal/kg, 2g protein/kg, etc.
            val weight = profile.weightKg
            caloriesGoal = (weight * 35).toInt().coerceAtLeast(1500)
            proteinGoal = (weight * 2f).toInt()
            carbsGoal = ((caloriesGoal * 0.40f) / 4f).toInt()
            fatGoal = ((caloriesGoal * 0.25f) / 9f).toInt()
            waterGoalMl = (weight * 35).toInt()
        } else {
            caloriesGoal = 2100
            proteinGoal = 158
            carbsGoal = 220
            fatGoal = 70
            waterGoalMl = 2400
        }

        DashboardUiState(
            userName = profile?.displayName ?: "Athlete",
            caloriesConsumed = totalCalories.toInt(),
            caloriesGoal = caloriesGoal,
            proteinG = totalProtein.toInt(),
            proteinGoal = proteinGoal,
            carbsG = totalCarbs.toInt(),
            carbsGoal = carbsGoal,
            fatG = totalFat.toInt(),
            fatGoal = fatGoal,
            waterMl = waterTotal.toInt(),
            waterGoalMl = waterGoalMl,
            streakDays = streak?.currentStreak ?: 0,
            isLoading = false,
        )
    }

    val uiState = combine(
        baseStateFlow,
        _insights,
        _recommendations,
    ) { base, insightsList, recommendationsList ->
        base.copy(
            insights = insightsList,
            recommendations = recommendationsList,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(isLoading = true),
    )
}
