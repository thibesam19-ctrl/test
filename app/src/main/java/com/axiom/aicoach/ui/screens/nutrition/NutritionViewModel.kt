package com.axiom.aicoach.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.data.local.entities.FoodItemEntity
import com.axiom.aicoach.data.local.entities.FoodLogEntity
import com.axiom.aicoach.data.local.entities.WaterLogEntity
import com.axiom.aicoach.domain.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

data class NutritionUiState(
    val caloriesConsumed: Int = 0,
    val caloriesGoal: Int = 2100,
    val proteinG: Int = 0,
    val proteinGoal: Int = 158,
    val carbsG: Int = 0,
    val carbsGoal: Int = 220,
    val fatG: Int = 0,
    val fatGoal: Int = 70,
    val mealSections: List<MealSectionUi> = emptyList(),
    val waterMl: Int = 0,
    val waterGoalMl: Int = 2400,
    val searchResults: List<FoodItemUi> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
)

data class MealSectionUi(
    val mealType: MealType,
    val entries: List<FoodLogUi>,
    val totalCalories: Int,
)

data class FoodLogUi(
    val logId: String,
    val name: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val servingQty: Float,
    val servingUnit: String,
)

data class FoodItemUi(
    val id: String,
    val name: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val servingSize: Float,
    val servingUnit: String,
)

private val DEMO_FOOD_ITEMS = listOf(
    FoodItemEntity(
        id = "demo_chicken_breast", name = "Chicken Breast", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 165f, proteinG = 31f,
        carbsG = 0f, fatG = 4f, fiberG = 0f, sugarG = 0f, sodiumMg = 74f,
        glycemicIndex = null, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_brown_rice", name = "Brown Rice", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 216f, proteinG = 5f,
        carbsG = 45f, fatG = 2f, fiberG = 2f, sugarG = 0f, sodiumMg = 5f,
        glycemicIndex = 50, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_whole_egg", name = "Whole Egg", brand = null,
        servingSize = 1f, servingUnit = "egg", calories = 70f, proteinG = 6f,
        carbsG = 0f, fatG = 5f, fiberG = 0f, sugarG = 0f, sodiumMg = 70f,
        glycemicIndex = null, allergensJson = "[\"eggs\"]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_oats", name = "Oats", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 389f, proteinG = 17f,
        carbsG = 66f, fatG = 7f, fiberG = 11f, sugarG = 1f, sodiumMg = 2f,
        glycemicIndex = 55, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_greek_yogurt", name = "Greek Yogurt", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 59f, proteinG = 10f,
        carbsG = 3f, fatG = 0f, fiberG = 0f, sugarG = 3f, sodiumMg = 36f,
        glycemicIndex = null, allergensJson = "[\"dairy\"]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_banana", name = "Banana", brand = null,
        servingSize = 1f, servingUnit = "medium", calories = 89f, proteinG = 1f,
        carbsG = 23f, fatG = 0f, fiberG = 3f, sugarG = 12f, sodiumMg = 1f,
        glycemicIndex = 51, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_almonds", name = "Almonds", brand = null,
        servingSize = 30f, servingUnit = "g", calories = 174f, proteinG = 6f,
        carbsG = 6f, fatG = 15f, fiberG = 4f, sugarG = 1f, sodiumMg = 0f,
        glycemicIndex = null, allergensJson = "[\"nuts\"]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_salmon", name = "Salmon", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 208f, proteinG = 20f,
        carbsG = 0f, fatG = 13f, fiberG = 0f, sugarG = 0f, sodiumMg = 59f,
        glycemicIndex = null, allergensJson = "[\"fish\"]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_sweet_potato", name = "Sweet Potato", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 86f, proteinG = 2f,
        carbsG = 20f, fatG = 0f, fiberG = 3f, sugarG = 4f, sodiumMg = 55f,
        glycemicIndex = 63, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
    FoodItemEntity(
        id = "demo_lentils", name = "Lentils (cooked)", brand = null,
        servingSize = 100f, servingUnit = "g", calories = 116f, proteinG = 9f,
        carbsG = 20f, fatG = 0f, fiberG = 8f, sugarG = 2f, sodiumMg = 2f,
        glycemicIndex = 29, allergensJson = "[]", barcode = null,
        imageUrl = null, cuisine = null, isVerified = true,
    ),
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodLogDao: FoodLogDao,
    private val foodItemDao: FoodItemDao,
    private val waterLogDao: WaterLogDao,
    private val userProfileDao: UserProfileDao,
) : ViewModel() {

    // Hard-coded demo user id; swap for real auth when ready
    private val userId = "demo_user"
    private val todayDate: String get() = LocalDate.now().toString()

    private val _searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<FoodItemUi>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NutritionUiState> = combine(
        foodLogDao.observeLogsForDate(userId, todayDate),
        waterLogDao.observeTotalForDate(userId, todayDate),
        _searchQuery,
        _searchResults,
        _isLoading,
    ) { logs, waterTotal, query, searchResults, loading ->
        // Build a lookup map of food items for today's logs
        val foodIds = logs.map { it.foodItemId }.distinct()
        val foodMap = foodIds.associateWith { id ->
            foodItemDao.findById(id)
        }

        // Group logs by meal type
        val sections = MealType.values().mapNotNull { mealType ->
            val mealLogs = logs.filter { it.mealType == mealType.name }
            if (mealLogs.isEmpty()) return@mapNotNull null
            val entries = mealLogs.mapNotNull { log ->
                val item = foodMap[log.foodItemId] ?: return@mapNotNull null
                FoodLogUi(
                    logId = log.id,
                    name = item.name,
                    calories = (item.calories * log.servings).toInt(),
                    proteinG = (item.proteinG * log.servings).toInt(),
                    carbsG = (item.carbsG * log.servings).toInt(),
                    fatG = (item.fatG * log.servings).toInt(),
                    servingQty = log.servings,
                    servingUnit = item.servingUnit,
                )
            }
            MealSectionUi(
                mealType = mealType,
                entries = entries,
                totalCalories = entries.sumOf { it.calories },
            )
        }

        val totalCalories = sections.sumOf { it.totalCalories }
        val totalProtein = sections.flatMap { it.entries }.sumOf { it.proteinG }
        val totalCarbs = sections.flatMap { it.entries }.sumOf { it.carbsG }
        val totalFat = sections.flatMap { it.entries }.sumOf { it.fatG }

        NutritionUiState(
            caloriesConsumed = totalCalories,
            proteinG = totalProtein,
            carbsG = totalCarbs,
            fatG = totalFat,
            mealSections = sections,
            waterMl = waterTotal.toInt(),
            searchQuery = query,
            searchResults = searchResults,
            isLoading = loading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NutritionUiState(isLoading = true),
    )

    init {
        viewModelScope.launch {
            seedFoodItemsIfEmpty()
        }
    }

    private suspend fun seedFoodItemsIfEmpty() {
        val existing = foodItemDao.getAll()
        if (existing.isEmpty()) {
            foodItemDao.insertAll(DEMO_FOOD_ITEMS)
        }
    }

    fun searchFood(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            val dbResults = foodItemDao.search(query)
            val results = if (dbResults.isEmpty()) {
                // Fall back to seeded demo items filtered in-memory
                DEMO_FOOD_ITEMS
                    .filter { it.name.contains(query, ignoreCase = true) || query.isBlank() }
                    .map { it.toUi() }
            } else {
                dbResults.map { it.toUi() }
            }
            _searchResults.value = results
            _isLoading.value = false
        }
    }

    fun logFood(foodItemId: String, mealType: MealType, servingQty: Float) {
        viewModelScope.launch {
            val entity = FoodLogEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                foodItemId = foodItemId,
                mealType = mealType.name,
                servings = servingQty,
                loggedAt = LocalDateTime.now().toString(),
            )
            foodLogDao.insert(entity)
        }
    }

    fun deleteLog(logId: String) {
        viewModelScope.launch {
            foodLogDao.delete(logId)
        }
    }

    fun logWater(ml: Int) {
        viewModelScope.launch {
            val entity = WaterLogEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amountMl = ml.toFloat(),
                loggedAt = LocalDateTime.now().toString(),
            )
            waterLogDao.insert(entity)
        }
    }

    private fun FoodItemEntity.toUi() = FoodItemUi(
        id = id,
        name = name,
        calories = calories.toInt(),
        proteinG = proteinG.toInt(),
        carbsG = carbsG.toInt(),
        fatG = fatG.toInt(),
        servingSize = servingSize,
        servingUnit = servingUnit,
    )
}
