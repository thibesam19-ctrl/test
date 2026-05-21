package com.axiom.aicoach.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.axiom.aicoach.data.local.dao.*
import com.axiom.aicoach.data.local.entities.*

@Database(
    entities = [
        UserProfileEntity::class,
        FoodItemEntity::class,
        FoodLogEntity::class,
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSessionEntity::class,
        ExerciseLogEntity::class,
        SetLogEntity::class,
        PersonalRecordEntity::class,
        WeightLogEntity::class,
        BodyMeasurementEntity::class,
        ProgressPhotoEntity::class,
        WaterLogEntity::class,
        SleepLogEntity::class,
        MoodLogEntity::class,
        StreakEntity::class,
        CoachMessageEntity::class,
        NotificationPreferenceEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(JsonConverters::class)
abstract class AxiomDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun setLogDao(): SetLogDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun sleepLogDao(): SleepLogDao
    abstract fun moodLogDao(): MoodLogDao
    abstract fun streakDao(): StreakDao
    abstract fun coachMessageDao(): CoachMessageDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao

    companion object {
        /**
         * v1 → v2: No structural change. Added JSON TypeConverters for list fields;
         * existing CSV-encoded values are migrated lazily on first read via the converter.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No DDL change needed — the converter handles the format upgrade at read time.
            }
        }
    }
}
