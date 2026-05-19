package com.axiom.aicoach.data.local.dao

import androidx.room.*
import com.axiom.aicoach.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun observeProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getProfile(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserProfileEntity)

    @Query("DELETE FROM user_profiles WHERE id = :userId")
    suspend fun delete(userId: String)
}

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' LIMIT 50")
    suspend fun search(query: String): List<FoodItemEntity>

    @Query("SELECT * FROM food_items WHERE barcode = :barcode LIMIT 1")
    suspend fun findByBarcode(barcode: String): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun findById(id: String): FoodItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<FoodItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FoodItemEntity)

    @Query("SELECT * FROM food_items ORDER BY name LIMIT 200")
    suspend fun getAll(): List<FoodItemEntity>
}

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_logs WHERE userId = :userId AND loggedAt LIKE :date || '%' ORDER BY loggedAt DESC")
    fun observeLogsForDate(userId: String, date: String): Flow<List<FoodLogEntity>>

    @Query("SELECT * FROM food_logs WHERE userId = :userId AND loggedAt BETWEEN :start AND :end ORDER BY loggedAt DESC")
    suspend fun getLogsInRange(userId: String, start: String, end: String): List<FoodLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FoodLogEntity)

    @Query("DELETE FROM food_logs WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT DISTINCT foodItemId FROM food_logs WHERE userId = :userId ORDER BY loggedAt DESC LIMIT 20")
    suspend fun getRecentFoodIds(userId: String): List<String>
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' LIMIT 50")
    suspend fun search(query: String): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun findById(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE primaryMuscle = :muscle")
    suspend fun findByMuscle(muscle: String): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<ExerciseEntity>)

    @Query("SELECT * FROM exercises ORDER BY name LIMIT 300")
    suspend fun getAll(): List<ExerciseEntity>
}

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans WHERE userId = :userId ORDER BY createdAt DESC")
    fun observePlans(userId: String): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plans WHERE userId = :userId AND isActive = 1 LIMIT 1")
    fun observeActivePlan(userId: String): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun findById(id: String): WorkoutPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkoutPlanEntity)

    @Query("UPDATE workout_plans SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateAll(userId: String)

    @Query("UPDATE workout_plans SET isActive = 1 WHERE id = :planId")
    suspend fun activate(planId: String)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts WHERE planId = :planId ORDER BY dayOfWeek")
    suspend fun getForPlan(planId: String): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun findById(id: String): WorkoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WorkoutEntity>)
}

@Dao
interface WorkoutExerciseDao {
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY `order`")
    suspend fun getForWorkout(workoutId: String): List<WorkoutExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WorkoutExerciseEntity>)
}

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun observeSessions(userId: String): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    suspend fun getRecentSessions(userId: String, limit: Int): List<WorkoutSessionEntity>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun findById(id: String): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkoutSessionEntity)

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE userId = :userId AND completedAt IS NOT NULL")
    suspend fun getCompletedCount(userId: String): Int
}

@Dao
interface ExerciseLogDao {
    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId")
    suspend fun getForSession(sessionId: String): List<ExerciseLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ExerciseLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExerciseLogEntity>)
}

@Dao
interface SetLogDao {
    @Query("SELECT * FROM set_logs WHERE exerciseLogId = :exerciseLogId ORDER BY setNumber")
    suspend fun getForExerciseLog(exerciseLogId: String): List<SetLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SetLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SetLogEntity>)
}

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records WHERE userId = :userId ORDER BY achievedAt DESC")
    fun observeAll(userId: String): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE userId = :userId AND exerciseId = :exerciseId ORDER BY oneRepMaxKg DESC LIMIT 1")
    suspend fun getBest(userId: String, exerciseId: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PersonalRecordEntity)
}

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs WHERE userId = :userId ORDER BY loggedAt DESC")
    fun observeAll(userId: String): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs WHERE userId = :userId ORDER BY loggedAt DESC LIMIT :limit")
    suspend fun getRecent(userId: String, limit: Int): List<WeightLogEntity>

    @Query("SELECT * FROM weight_logs WHERE userId = :userId ORDER BY loggedAt DESC LIMIT 1")
    suspend fun getLatest(userId: String): WeightLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WeightLogEntity)

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY measuredAt DESC")
    fun observeAll(userId: String): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY measuredAt DESC LIMIT 1")
    suspend fun getLatest(userId: String): BodyMeasurementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BodyMeasurementEntity)
}

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY takenAt DESC")
    fun observeAll(userId: String): Flow<List<ProgressPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ProgressPhotoEntity)

    @Query("DELETE FROM progress_photos WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE userId = :userId AND loggedAt LIKE :date || '%' ORDER BY loggedAt DESC")
    fun observeForDate(userId: String, date: String): Flow<List<WaterLogEntity>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_logs WHERE userId = :userId AND loggedAt LIKE :date || '%'")
    fun observeTotalForDate(userId: String, date: String): Flow<Float>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WaterLogEntity)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs WHERE userId = :userId ORDER BY loggedAt DESC LIMIT 30")
    fun observeRecent(userId: String): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs WHERE userId = :userId AND loggedAt = :date LIMIT 1")
    suspend fun getForDate(userId: String, date: String): SleepLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SleepLogEntity)
}

@Dao
interface MoodLogDao {
    @Query("SELECT * FROM mood_logs WHERE userId = :userId ORDER BY loggedAt DESC LIMIT 30")
    fun observeRecent(userId: String): Flow<List<MoodLogEntity>>

    @Query("SELECT * FROM mood_logs WHERE userId = :userId AND loggedAt = :date LIMIT 1")
    suspend fun getForDate(userId: String, date: String): MoodLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MoodLogEntity)
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE userId = :userId")
    fun observe(userId: String): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE userId = :userId")
    suspend fun get(userId: String): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: StreakEntity)
}

@Dao
interface CoachMessageDao {
    @Query("SELECT * FROM coach_messages WHERE userId = :userId ORDER BY timestamp DESC LIMIT 100")
    fun observeMessages(userId: String): Flow<List<CoachMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CoachMessageEntity)

    @Query("SELECT COUNT(*) FROM coach_messages WHERE userId = :userId AND timestamp >= :since AND role = 'USER'")
    suspend fun countUserMessagesSince(userId: String, since: String): Int

    @Query("DELETE FROM coach_messages WHERE userId = :userId")
    suspend fun deleteAll(userId: String)
}

@Dao
interface NotificationPreferenceDao {
    @Query("SELECT * FROM notification_preferences WHERE userId = :userId")
    fun observe(userId: String): Flow<NotificationPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NotificationPreferenceEntity)
}
