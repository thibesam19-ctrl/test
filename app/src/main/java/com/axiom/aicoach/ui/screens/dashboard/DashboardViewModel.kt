package com.axiom.aicoach.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.util.toDbString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val foodLogDao: FoodLogDao,
    private val foodItemDao: FoodItemDao,
    private val waterLogDao: WaterLogDao,
    private val streakDao: StreakDao,
) : ViewModel() {

    // The local user ID used until proper auth is wired up.
    private val userId = "local_user"
    private val today: String = LocalDate.now().toDbString()

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

    val uiState = combine(
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(isLoading = true),
    )
}
