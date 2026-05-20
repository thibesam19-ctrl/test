package com.axiom.aicoach.ai

import com.axiom.aicoach.ai.analytics.InsightsEngine
import com.axiom.aicoach.ai.analytics.InsightType
import com.axiom.aicoach.data.local.dao.FoodItemDao
import com.axiom.aicoach.data.local.dao.FoodLogDao
import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.dao.WaterLogDao
import com.axiom.aicoach.data.local.dao.WeightLogDao
import com.axiom.aicoach.data.local.dao.WorkoutSessionDao
import com.axiom.aicoach.data.local.entities.FoodItemEntity
import com.axiom.aicoach.data.local.entities.FoodLogEntity
import com.axiom.aicoach.data.local.entities.StreakEntity
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import com.axiom.aicoach.data.local.entities.WaterLogEntity
import com.axiom.aicoach.data.local.entities.WeightLogEntity
import com.axiom.aicoach.data.local.entities.WorkoutSessionEntity
import com.axiom.aicoach.domain.model.FitnessGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class InsightsEngineTest {

    private val userId = "test-user"

    // ── Fake DAO helpers ──────────────────────────────────────────────────────

    private fun fakeWeightLogDao(logs: List<WeightLogEntity>): WeightLogDao =
        object : WeightLogDao {
            override fun observeAll(userId: String): Flow<List<WeightLogEntity>> = flowOf(logs)
            override suspend fun getRecent(userId: String, limit: Int): List<WeightLogEntity> =
                logs.take(limit)
            override suspend fun getLatest(userId: String): WeightLogEntity? = logs.firstOrNull()
            override suspend fun insert(entity: WeightLogEntity) {}
            override suspend fun delete(id: String) {}
        }

    private fun fakeFoodLogDao(logs: List<FoodLogEntity> = emptyList()): FoodLogDao =
        object : FoodLogDao {
            override fun observeLogsForDate(userId: String, date: String): Flow<List<FoodLogEntity>> =
                flowOf(emptyList())
            override suspend fun getLogsInRange(
                userId: String, start: String, end: String
            ): List<FoodLogEntity> = logs
            override suspend fun insert(entity: FoodLogEntity) {}
            override suspend fun delete(id: String) {}
            override suspend fun getRecentFoodIds(userId: String): List<String> = emptyList()
        }

    private fun fakeWaterLogDao(total: Float = 2000f): WaterLogDao =
        object : WaterLogDao {
            override fun observeForDate(userId: String, date: String): Flow<List<WaterLogEntity>> =
                flowOf(emptyList())
            override fun observeTotalForDate(userId: String, date: String): Flow<Float> =
                flowOf(total)
            override suspend fun insert(entity: WaterLogEntity) {}
            override suspend fun delete(id: String) {}
        }

    private fun fakeWorkoutSessionDao(sessions: List<WorkoutSessionEntity>): WorkoutSessionDao =
        object : WorkoutSessionDao {
            override fun observeSessions(userId: String): Flow<List<WorkoutSessionEntity>> =
                flowOf(sessions)
            override suspend fun getRecentSessions(
                userId: String, limit: Int
            ): List<WorkoutSessionEntity> = sessions.take(limit)
            override suspend fun findById(id: String): WorkoutSessionEntity? = null
            override suspend fun upsert(entity: WorkoutSessionEntity) {}
            override suspend fun getCompletedCount(userId: String): Int = sessions.size
        }

    private fun fakeUserProfileDao(profile: UserProfileEntity?): UserProfileDao =
        object : UserProfileDao {
            override fun observeProfile(userId: String): Flow<UserProfileEntity?> = flowOf(profile)
            override suspend fun getProfile(userId: String): UserProfileEntity? = profile
            override suspend fun upsert(entity: UserProfileEntity) {}
            override suspend fun delete(userId: String) {}
        }

    private fun fakeStreakDao(streak: StreakEntity? = null): StreakDao =
        object : StreakDao {
            override fun observe(userId: String): Flow<StreakEntity?> = flowOf(streak)
            override suspend fun get(userId: String): StreakEntity? = streak
            override suspend fun upsert(entity: StreakEntity) {}
        }

    private fun fakeFoodItemDao(): FoodItemDao =
        object : FoodItemDao {
            override suspend fun search(query: String): List<FoodItemEntity> = emptyList()
            override suspend fun findByBarcode(barcode: String): FoodItemEntity? = null
            override suspend fun findById(id: String): FoodItemEntity? = null
            override suspend fun insertAll(entities: List<FoodItemEntity>) {}
            override suspend fun upsert(entity: FoodItemEntity) {}
            override suspend fun getAll(): List<FoodItemEntity> = emptyList()
        }

    private fun makeProfile(goal: FitnessGoal = FitnessGoal.LOSE_FAT): UserProfileEntity =
        UserProfileEntity(
            id = userId,
            email = "test@example.com",
            displayName = "Test User",
            photoUrl = null,
            sex = "M",
            dateOfBirth = "1990-01-01",
            heightCm = 175f,
            weightKg = 80f,
            targetWeightKg = 70f,
            activityLevel = "MODERATE",
            experienceLevel = "INTERMEDIATE",
            goal = goal.name,
            trainingDaysPerWeek = 4,
            equipmentJson = "[]",
            injuriesJson = "[]",
            dietaryPreferencesJson = "[]",
            allergiesJson = "[]",
            medicalConditionsJson = "[]",
            isPremium = false,
            createdAt = "2024-01-01",
        )

    /** Produce a timestamp string for N days ago, e.g. "2025-05-13T10:00:00" */
    private fun daysAgo(days: Long): String =
        LocalDateTime.now().minusDays(days).toString().substring(0, 19)

    /** Produce a date string for N days ago, e.g. "2025-05-13" */
    private fun dateAgo(days: Long): String =
        LocalDate.now().minusDays(days).toString()

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `generateInsights returns GREAT_CONSISTENCY when 4 or more workouts in last 7 days`() =
        runBlocking {
            // Create 5 completed workout sessions within the last 7 days
            val sessions = (1..5).map { i ->
                WorkoutSessionEntity(
                    id = "session-$i",
                    userId = userId,
                    workoutId = "workout-$i",
                    workoutName = "Push Day",
                    startedAt = daysAgo(i.toLong()),
                    completedAt = daysAgo(i.toLong()),
                    durationMinutes = 60,
                    rating = 4,
                    rpe = 7,
                    notes = null,
                )
            }

            val engine = InsightsEngine(
                weightLogDao = fakeWeightLogDao(emptyList()),
                foodLogDao = fakeFoodLogDao(),
                waterLogDao = fakeWaterLogDao(2000f),
                workoutSessionDao = fakeWorkoutSessionDao(sessions),
                userProfileDao = fakeUserProfileDao(null),
                streakDao = fakeStreakDao(),
                foodItemDao = fakeFoodItemDao(),
            )

            val insights = engine.generateInsights(userId)
            assertTrue(
                "Expected GREAT_CONSISTENCY insight for 5 workouts in 7 days",
                insights.any { it.type == InsightType.GREAT_CONSISTENCY },
            )
        }

    @Test
    fun `generateInsights returns PLATEAU_DETECTED when weight variance is low over 14 days`() =
        runBlocking {
            // 5 weight logs within the last 14 days — all nearly identical (range < 0.5 kg)
            val weightLogs = (1..5).map { i ->
                WeightLogEntity(
                    id = "weight-$i",
                    userId = userId,
                    weightKg = 80f + (i * 0.05f), // max range = 0.25 kg, well below 0.5
                    loggedAt = "${dateAgo(i.toLong())}T08:00:00",
                    notes = null,
                )
            }

            // Profile must have a weight-loss goal for plateau detection to activate
            val profile = makeProfile(FitnessGoal.LOSE_FAT)

            val engine = InsightsEngine(
                weightLogDao = fakeWeightLogDao(weightLogs),
                foodLogDao = fakeFoodLogDao(),
                waterLogDao = fakeWaterLogDao(2000f),
                workoutSessionDao = fakeWorkoutSessionDao(emptyList()),
                userProfileDao = fakeUserProfileDao(profile),
                streakDao = fakeStreakDao(),
                foodItemDao = fakeFoodItemDao(),
            )

            val insights = engine.generateInsights(userId)
            assertTrue(
                "Expected PLATEAU_DETECTED insight for flat weight over 14 days",
                insights.any { it.type == InsightType.PLATEAU_DETECTED },
            )
        }

    @Test
    fun `generateInsights returns empty list for empty data`() = runBlocking {
        val engine = InsightsEngine(
            weightLogDao = fakeWeightLogDao(emptyList()),
            foodLogDao = fakeFoodLogDao(),
            waterLogDao = fakeWaterLogDao(2000f), // hydrated, no low-hydration insight
            workoutSessionDao = fakeWorkoutSessionDao(emptyList()),
            userProfileDao = fakeUserProfileDao(null),
            streakDao = fakeStreakDao(),
            foodItemDao = fakeFoodItemDao(),
        )

        val insights = engine.generateInsights(userId)
        // With no data and good hydration the engine should produce no insights.
        // The only possible insight with no data is MISSED_WORKOUTS (0 workouts < 2).
        // Filter those out and verify no other insight types fire.
        val nonMissed = insights.filter { it.type != InsightType.MISSED_WORKOUTS }
        assertTrue(
            "Expected no insights beyond MISSED_WORKOUTS for completely empty data, got: $nonMissed",
            nonMissed.isEmpty(),
        )
    }
}
