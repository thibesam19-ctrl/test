package com.axiom.aicoach.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

// ── User ──────────────────────────────────────────────────────────────────────

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val sex: Sex,
    val dateOfBirth: LocalDate,
    val heightCm: Float,
    val weightKg: Float,
    val targetWeightKg: Float,
    val activityLevel: ActivityLevel,
    val experienceLevel: ExperienceLevel,
    val goal: FitnessGoal,
    val trainingDaysPerWeek: Int,
    val equipment: List<Equipment>,
    val injuries: List<String>,
    val dietaryPreferences: List<DietaryPreference>,
    val allergies: List<String>,
    val medicalConditions: List<String>,
    val isPremium: Boolean,
    val createdAt: LocalDateTime,
)

enum class Sex { MALE, FEMALE, PREFER_NOT_TO_SAY }
enum class ActivityLevel(val multiplier: Float, val label: String) {
    SEDENTARY(1.2f, "Sedentary (desk job, no exercise)"),
    LIGHTLY_ACTIVE(1.375f, "Light (1-3 days/week)"),
    MODERATELY_ACTIVE(1.55f, "Moderate (3-5 days/week)"),
    VERY_ACTIVE(1.725f, "Active (6-7 days/week)"),
    EXTRA_ACTIVE(1.9f, "Very Active (physical job + training)"),
}
enum class ExperienceLevel { BEGINNER, INTERMEDIATE, ADVANCED }
enum class FitnessGoal {
    LOSE_FAT, BUILD_MUSCLE, GET_STRONGER, MAINTAIN, IMPROVE_HEALTH, SPORT_SPECIFIC
}
enum class Equipment { NONE, DUMBBELLS, BARBELL, KETTLEBELL, RESISTANCE_BANDS, FULL_GYM, CABLES, MACHINES }
enum class DietaryPreference {
    OMNIVORE, VEGETARIAN, VEGAN, PESCATARIAN,
    EGG_FREE, DAIRY_FREE, GLUTEN_FREE,
    HALAL, KOSHER, SRI_LANKAN, INDIAN
}

// ── Nutrition ─────────────────────────────────────────────────────────────────

data class FoodItem(
    val id: String,
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
    val allergens: List<String>,
    val barcode: String?,
    val imageUrl: String?,
    val cuisine: String?,
    val isVerified: Boolean,
)

data class FoodLog(
    val id: String,
    val userId: String,
    val foodItemId: String,
    val foodItem: FoodItem?,
    val mealType: MealType,
    val servings: Float,
    val loggedAt: LocalDateTime,
)

data class NutritionSummary(
    val date: LocalDate,
    val totalCalories: Float,
    val totalProteinG: Float,
    val totalCarbsG: Float,
    val totalFatG: Float,
    val goalCalories: Float,
    val goalProteinG: Float,
    val goalCarbsG: Float,
    val goalFatG: Float,
)

enum class MealType { BREAKFAST, LUNCH, DINNER, SNACK, PRE_WORKOUT, POST_WORKOUT }

// ── Workout ───────────────────────────────────────────────────────────────────

data class Exercise(
    val id: String,
    val name: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup>,
    val equipment: Equipment,
    val difficulty: Int,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val formCues: List<String>,
    val commonMistakes: List<String>,
    val substitutions: List<String>,
    val contraindications: List<String>,
    val instructions: String,
)

data class WorkoutPlan(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val goal: FitnessGoal,
    val durationWeeks: Int,
    val daysPerWeek: Int,
    val experienceLevel: ExperienceLevel,
    val workouts: List<Workout>,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
)

data class Workout(
    val id: String,
    val planId: String,
    val name: String,
    val dayOfWeek: Int,
    val exercises: List<WorkoutExercise>,
    val estimatedDurationMin: Int,
)

data class WorkoutExercise(
    val id: String,
    val workoutId: String,
    val exercise: Exercise,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int,
    val restSeconds: Int,
    val notes: String?,
    val order: Int,
)

data class WorkoutSession(
    val id: String,
    val userId: String,
    val workoutId: String,
    val workoutName: String,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val exerciseLogs: List<ExerciseLog>,
    val durationMinutes: Int?,
    val rating: Int?,
    val rpe: Int?,
    val notes: String?,
)

data class ExerciseLog(
    val id: String,
    val sessionId: String,
    val exerciseId: String,
    val exerciseName: String,
    val sets: List<SetLog>,
)

data class SetLog(
    val id: String,
    val exerciseLogId: String,
    val setNumber: Int,
    val weightKg: Float?,
    val reps: Int?,
    val durationSeconds: Int?,
    val rpe: Int?,
    val isCompleted: Boolean,
)

data class PersonalRecord(
    val id: String,
    val userId: String,
    val exerciseId: String,
    val exerciseName: String,
    val weightKg: Float,
    val reps: Int,
    val oneRepMaxKg: Float,
    val achievedAt: LocalDateTime,
)

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, FOREARMS,
    ABS, OBLIQUES, LOWER_BACK,
    QUADS, HAMSTRINGS, GLUTES, CALVES, HIP_FLEXORS,
    FULL_BODY, CARDIO
}

// ── Progress ──────────────────────────────────────────────────────────────────

data class WeightLog(
    val id: String,
    val userId: String,
    val weightKg: Float,
    val loggedAt: LocalDateTime,
    val notes: String?,
)

data class BodyMeasurement(
    val id: String,
    val userId: String,
    val chestCm: Float?,
    val waistCm: Float?,
    val hipsCm: Float?,
    val leftArmCm: Float?,
    val rightArmCm: Float?,
    val leftThighCm: Float?,
    val rightThighCm: Float?,
    val neckCm: Float?,
    val measuredAt: LocalDateTime,
)

data class ProgressPhoto(
    val id: String,
    val userId: String,
    val localPath: String,
    val angle: PhotoAngle,
    val takenAt: LocalDateTime,
    val notes: String?,
)

enum class PhotoAngle { FRONT, SIDE, BACK }

// ── Habits & Recovery ─────────────────────────────────────────────────────────

data class WaterLog(
    val id: String,
    val userId: String,
    val amountMl: Float,
    val loggedAt: LocalDateTime,
)

data class SleepLog(
    val id: String,
    val userId: String,
    val durationHours: Float,
    val quality: Int,
    val bedTime: LocalDateTime?,
    val wakeTime: LocalDateTime?,
    val loggedAt: LocalDate,
)

data class MoodLog(
    val id: String,
    val userId: String,
    val mood: Int,
    val energy: Int,
    val stress: Int,
    val notes: String?,
    val loggedAt: LocalDate,
)

data class DailyHabits(
    val date: LocalDate,
    val waterMl: Float,
    val waterGoalMl: Float,
    val steps: Int,
    val stepsGoal: Int,
    val sleepHours: Float?,
    val mood: Int?,
    val energy: Int?,
    val streakDays: Int,
)

// ── AI Coach ──────────────────────────────────────────────────────────────────

data class CoachMessage(
    val id: String,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val intent: CoachIntent?,
    val timestamp: LocalDateTime,
)

data class CoachConversation(
    val id: String,
    val userId: String,
    val messages: List<CoachMessage>,
    val startedAt: LocalDateTime,
    val lastMessageAt: LocalDateTime,
)

enum class MessageRole { USER, ASSISTANT }
enum class CoachIntent {
    WEIGHT_LOSS_QUESTION, PLAN_ADJUSTMENT, MEAL_SUGGESTION,
    RECOVERY_ADVICE, MOTIVATION, EXERCISE_SAFETY,
    GENERAL, SAFETY_REFERRAL
}

// ── Subscription ──────────────────────────────────────────────────────────────

data class SubscriptionInfo(
    val isActive: Boolean,
    val tier: SubscriptionTier,
    val expiresAt: LocalDateTime?,
    val isTrialActive: Boolean,
    val trialEndsAt: LocalDateTime?,
)

enum class SubscriptionTier { FREE, PREMIUM_MONTHLY, PREMIUM_ANNUAL, PREMIUM_LIFETIME }

// ── Calorie Math ──────────────────────────────────────────────────────────────

object CalorieMath {
    fun bmr(sex: Sex, weightKg: Float, heightCm: Float, ageYears: Int): Float {
        return when (sex) {
            Sex.MALE -> 10f * weightKg + 6.25f * heightCm - 5f * ageYears + 5f
            Sex.FEMALE -> 10f * weightKg + 6.25f * heightCm - 5f * ageYears - 161f
            Sex.PREFER_NOT_TO_SAY -> 10f * weightKg + 6.25f * heightCm - 5f * ageYears - 78f
        }
    }

    fun tdee(bmr: Float, activityLevel: ActivityLevel) = bmr * activityLevel.multiplier

    fun dailyCalorieTarget(tdee: Float, goal: FitnessGoal, sex: Sex): Float {
        val delta = when (goal) {
            FitnessGoal.LOSE_FAT -> -0.20f
            FitnessGoal.BUILD_MUSCLE -> 0.15f
            FitnessGoal.GET_STRONGER -> 0.10f
            FitnessGoal.MAINTAIN, FitnessGoal.IMPROVE_HEALTH, FitnessGoal.SPORT_SPECIFIC -> 0f
        }
        val floor = if (sex == Sex.MALE) 1500f else 1200f
        return maxOf(tdee * (1f + delta), floor)
    }

    fun macros(calories: Float, goal: FitnessGoal): Triple<Float, Float, Float> {
        val (pPct, cPct, fPct) = when (goal) {
            FitnessGoal.LOSE_FAT -> Triple(0.40f, 0.35f, 0.25f)
            FitnessGoal.BUILD_MUSCLE -> Triple(0.25f, 0.50f, 0.25f)
            FitnessGoal.GET_STRONGER -> Triple(0.30f, 0.45f, 0.25f)
            else -> Triple(0.30f, 0.40f, 0.30f)
        }
        return Triple(
            calories * pPct / 4f,
            calories * cPct / 4f,
            calories * fPct / 9f,
        )
    }

    fun oneRepMax(weightKg: Float, reps: Int): Float {
        val epley = weightKg * (1 + reps / 30f)
        val brzycki = weightKg * (36f / (37f - reps))
        return (epley + brzycki) / 2f
    }
}
