package com.axiom.aicoach.data.repository

import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.data.local.entities.FoodItemEntity
import com.axiom.aicoach.data.local.entities.FoodLogEntity
import com.axiom.aicoach.data.local.entities.WaterLogEntity
import com.axiom.aicoach.domain.model.*
import com.axiom.aicoach.security.UserSession
import com.axiom.aicoach.util.newId
import com.axiom.aicoach.util.toDbString
import com.axiom.aicoach.util.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// ── Mapping ───────────────────────────────────────────────────────────────────

private fun FoodItemEntity.toDomain() = FoodItem(
    id = id,
    name = name,
    brand = brand,
    servingSize = servingSize,
    servingUnit = servingUnit,
    calories = calories,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
    fiberG = fiberG,
    sugarG = sugarG,
    sodiumMg = sodiumMg,
    glycemicIndex = glycemicIndex,
    allergens = allergensJson.split(",").filter { it.isNotBlank() }.map { it.trim() },
    barcode = barcode,
    imageUrl = imageUrl,
    cuisine = cuisine,
    isVerified = isVerified,
)

private fun FoodItem.toEntity() = FoodItemEntity(
    id = id,
    name = name,
    brand = brand,
    servingSize = servingSize,
    servingUnit = servingUnit,
    calories = calories,
    proteinG = proteinG,
    carbsG = carbsG,
    fatG = fatG,
    fiberG = fiberG,
    sugarG = sugarG,
    sodiumMg = sodiumMg,
    glycemicIndex = glycemicIndex,
    allergensJson = allergens.joinToString(","),
    barcode = barcode,
    imageUrl = imageUrl,
    cuisine = cuisine,
    isVerified = isVerified,
)

private fun FoodLogEntity.toDomain(foodItem: FoodItem?) = FoodLog(
    id = id,
    userId = userId,
    foodItemId = foodItemId,
    foodItem = foodItem,
    mealType = MealType.valueOf(mealType),
    servings = servings,
    loggedAt = loggedAt.toLocalDateTime(),
)

private fun FoodLog.toEntity() = FoodLogEntity(
    id = id,
    userId = userId,
    foodItemId = foodItemId,
    mealType = mealType.name,
    servings = servings,
    loggedAt = loggedAt.toDbString(),
)

private fun WaterLogEntity.toDomain() = WaterLog(
    id = id,
    userId = userId,
    amountMl = amountMl,
    loggedAt = loggedAt.toLocalDateTime(),
)

// ── Interface ─────────────────────────────────────────────────────────────────

interface NutritionRepository {
    fun getTodayFoodLogs(): Flow<List<FoodLog>>
    fun getFoodLogsForDate(date: LocalDate): Flow<List<FoodLog>>
    suspend fun logFood(foodLog: FoodLog)
    suspend fun deleteLog(logId: String)
    fun searchFood(query: String): Flow<List<FoodItem>>
    fun getFoodItem(id: String): Flow<FoodItem?>
    suspend fun saveCustomFood(item: FoodItem)
    fun getTodayNutrition(): Flow<NutritionSummary>
    fun getTodayWaterLogs(): Flow<List<WaterLog>>
    suspend fun logWater(ml: Int)
    fun getTodayWaterTotal(): Flow<Int>
}

// ── Implementation ────────────────────────────────────────────────────────────

@Singleton
class NutritionRepositoryImpl @Inject constructor(
    private val foodLogDao: FoodLogDao,
    private val foodItemDao: FoodItemDao,
    private val waterLogDao: WaterLogDao,
    private val userSession: UserSession,
) : NutritionRepository {

    private val userId: String get() = userSession.userId

    private suspend fun enrichLogs(entities: List<FoodLogEntity>): List<FoodLog> =
        entities.map { entity ->
            val item = foodItemDao.findById(entity.foodItemId)?.toDomain()
            entity.toDomain(item)
        }

    override fun getTodayFoodLogs(): Flow<List<FoodLog>> {
        val today = LocalDate.now().toDbString()
        return foodLogDao.observeLogsForDate(userId, today)
            .distinctUntilChanged()
            .map { enrichLogs(it) }
    }

    override fun getFoodLogsForDate(date: LocalDate): Flow<List<FoodLog>> {
        val dateStr = date.toDbString()
        return foodLogDao.observeLogsForDate(userId, dateStr)
            .distinctUntilChanged()
            .map { enrichLogs(it) }
    }

    override suspend fun logFood(foodLog: FoodLog) {
        foodLogDao.insert(foodLog.toEntity())
    }

    override suspend fun deleteLog(logId: String) {
        foodLogDao.delete(logId)
    }

    override fun searchFood(query: String): Flow<List<FoodItem>> = flow {
        emit(foodItemDao.search(query).map { it.toDomain() })
    }

    override fun getFoodItem(id: String): Flow<FoodItem?> = flow {
        emit(foodItemDao.findById(id)?.toDomain())
    }

    override suspend fun saveCustomFood(item: FoodItem) {
        foodItemDao.upsert(item.toEntity())
    }

    override fun getTodayNutrition(): Flow<NutritionSummary> {
        val today = LocalDate.now()
        return getTodayFoodLogs().map { logs ->
            var calories = 0f
            var protein = 0f
            var carbs = 0f
            var fat = 0f
            logs.forEach { log ->
                val item = log.foodItem ?: return@forEach
                val factor = log.servings
                calories += item.calories * factor
                protein += item.proteinG * factor
                carbs += item.carbsG * factor
                fat += item.fatG * factor
            }
            NutritionSummary(
                date = today,
                totalCalories = calories,
                totalProteinG = protein,
                totalCarbsG = carbs,
                totalFatG = fat,
                goalCalories = 2000f,
                goalProteinG = 150f,
                goalCarbsG = 200f,
                goalFatG = 65f,
            )
        }
    }

    override fun getTodayWaterLogs(): Flow<List<WaterLog>> {
        val today = LocalDate.now().toDbString()
        return waterLogDao.observeForDate(userId, today)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun logWater(ml: Int) {
        waterLogDao.insert(
            WaterLogEntity(
                id = newId(),
                userId = userId,
                amountMl = ml.toFloat(),
                loggedAt = LocalDateTime.now().toDbString(),
            )
        )
    }

    override fun getTodayWaterTotal(): Flow<Int> {
        val today = LocalDate.now().toDbString()
        return waterLogDao.observeTotalForDate(userId, today)
            .distinctUntilChanged()
            .map { it.toInt() }
    }
}
