package com.axiom.aicoach.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val sex: String,
    val dateOfBirth: String,
    val heightCm: Float,
    val weightKg: Float,
    val targetWeightKg: Float,
    val activityLevel: String,
    val experienceLevel: String,
    val goal: String,
    val trainingDaysPerWeek: Int,
    val equipmentJson: String,
    val injuriesJson: String,
    val dietaryPreferencesJson: String,
    val allergiesJson: String,
    val medicalConditionsJson: String,
    val isPremium: Boolean,
    val createdAt: String,
)

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String?,
    val servingSize: Float,
    val servingUnit: String,
    val calories: Float,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val fiberG: Float?,
    val sugarG: Float?,
    val sodiumMg: Float?,
    val glycemicIndex: Int?,
    val allergensJson: String,
    val barcode: String?,
    val imageUrl: String?,
    val cuisine: String?,
    val isVerified: Boolean,
)

@Entity(
    tableName = "food_logs",
    indices = [Index("userId"), Index("loggedAt")]
)
data class FoodLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val foodItemId: String,
    val mealType: String,
    val servings: Float,
    val loggedAt: String,
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val primaryMuscle: String,
    val secondaryMusclesJson: String,
    val equipment: String,
    val difficulty: Int,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val formCuesJson: String,
    val commonMistakesJson: String,
    val substitutionsJson: String,
    val contraindicationsJson: String,
    val instructions: String,
)

@Entity(tableName = "workout_plans", indices = [Index("userId")])
data class WorkoutPlanEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val goal: String,
    val durationWeeks: Int,
    val daysPerWeek: Int,
    val experienceLevel: String,
    val isActive: Boolean,
    val createdAt: String,
)

@Entity(tableName = "workouts", indices = [Index("planId")])
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val planId: String,
    val name: String,
    val dayOfWeek: Int,
    val estimatedDurationMin: Int,
)

@Entity(tableName = "workout_exercises", indices = [Index("workoutId"), Index("exerciseId")])
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val exerciseId: String,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int,
    val restSeconds: Int,
    val notes: String?,
    val order: Int,
)

@Entity(tableName = "workout_sessions", indices = [Index("userId"), Index("workoutId")])
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val workoutId: String,
    val workoutName: String,
    val startedAt: String,
    val completedAt: String?,
    val durationMinutes: Int?,
    val rating: Int?,
    val rpe: Int?,
    val notes: String?,
)

@Entity(tableName = "exercise_logs", indices = [Index("sessionId")])
data class ExerciseLogEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseId: String,
    val exerciseName: String,
)

@Entity(tableName = "set_logs", indices = [Index("exerciseLogId")])
data class SetLogEntity(
    @PrimaryKey val id: String,
    val exerciseLogId: String,
    val setNumber: Int,
    val weightKg: Float?,
    val reps: Int?,
    val durationSeconds: Int?,
    val rpe: Int?,
    val isCompleted: Boolean,
)

@Entity(tableName = "personal_records", indices = [Index("userId"), Index("exerciseId")])
data class PersonalRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val exerciseId: String,
    val exerciseName: String,
    val weightKg: Float,
    val reps: Int,
    val oneRepMaxKg: Float,
    val achievedAt: String,
)

@Entity(tableName = "weight_logs", indices = [Index("userId"), Index("loggedAt")])
data class WeightLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val weightKg: Float,
    val loggedAt: String,
    val notes: String?,
)

@Entity(tableName = "body_measurements", indices = [Index("userId")])
data class BodyMeasurementEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val chestCm: Float?,
    val waistCm: Float?,
    val hipsCm: Float?,
    val leftArmCm: Float?,
    val rightArmCm: Float?,
    val leftThighCm: Float?,
    val rightThighCm: Float?,
    val neckCm: Float?,
    val measuredAt: String,
)

@Entity(tableName = "progress_photos", indices = [Index("userId")])
data class ProgressPhotoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val localPath: String,
    val angle: String,
    val takenAt: String,
    val notes: String?,
)

@Entity(tableName = "water_logs", indices = [Index("userId"), Index("loggedAt")])
data class WaterLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amountMl: Float,
    val loggedAt: String,
)

@Entity(tableName = "sleep_logs", indices = [Index("userId"), Index("loggedAt")])
data class SleepLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val durationHours: Float,
    val quality: Int,
    val bedTime: String?,
    val wakeTime: String?,
    val loggedAt: String,
)

@Entity(tableName = "mood_logs", indices = [Index("userId"), Index("loggedAt")])
data class MoodLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val mood: Int,
    val energy: Int,
    val stress: Int,
    val notes: String?,
    val loggedAt: String,
)

@Entity(tableName = "streaks", indices = [Index("userId")])
data class StreakEntity(
    @PrimaryKey val userId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String?,
    val freezesRemaining: Int,
)

@Entity(tableName = "coach_messages", indices = [Index("conversationId")])
data class CoachMessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val userId: String,
    val role: String,
    val content: String,
    val intent: String?,
    val timestamp: String,
)

@Entity(tableName = "notification_preferences")
data class NotificationPreferenceEntity(
    @PrimaryKey val userId: String,
    val workoutReminder: Boolean,
    val workoutReminderTime: String,
    val mealReminder: Boolean,
    val waterReminder: Boolean,
    val sleepReminder: Boolean,
    val weeklyProgress: Boolean,
    val streakProtection: Boolean,
    val achievements: Boolean,
    val tips: Boolean,
    val quietHoursStart: Int,
    val quietHoursEnd: Int,
)
