package com.axiom.aicoach.data.repository

import com.axiom.aicoach.data.local.dao.StreakDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.entities.StreakEntity
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import com.axiom.aicoach.domain.model.*
import com.axiom.aicoach.util.newId
import com.axiom.aicoach.util.toDbString
import com.axiom.aicoach.util.toLocalDate
import com.axiom.aicoach.util.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

// ── Mapping ───────────────────────────────────────────────────────────────────

private fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    sex = Sex.valueOf(sex),
    dateOfBirth = dateOfBirth.toLocalDate(),
    heightCm = heightCm,
    weightKg = weightKg,
    targetWeightKg = targetWeightKg,
    activityLevel = ActivityLevel.valueOf(activityLevel),
    experienceLevel = ExperienceLevel.valueOf(experienceLevel),
    goal = FitnessGoal.valueOf(goal),
    trainingDaysPerWeek = trainingDaysPerWeek,
    equipment = equipmentJson.split(",").filter { it.isNotBlank() }.map { Equipment.valueOf(it.trim()) },
    injuries = injuriesJson.split(",").filter { it.isNotBlank() }.map { it.trim() },
    dietaryPreferences = dietaryPreferencesJson.split(",").filter { it.isNotBlank() }.map { DietaryPreference.valueOf(it.trim()) },
    allergies = allergiesJson.split(",").filter { it.isNotBlank() }.map { it.trim() },
    medicalConditions = medicalConditionsJson.split(",").filter { it.isNotBlank() }.map { it.trim() },
    isPremium = isPremium,
    createdAt = createdAt.toLocalDateTime(),
)

private fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    sex = sex.name,
    dateOfBirth = dateOfBirth.toDbString(),
    heightCm = heightCm,
    weightKg = weightKg,
    targetWeightKg = targetWeightKg,
    activityLevel = activityLevel.name,
    experienceLevel = experienceLevel.name,
    goal = goal.name,
    trainingDaysPerWeek = trainingDaysPerWeek,
    equipmentJson = equipment.joinToString(",") { it.name },
    injuriesJson = injuries.joinToString(","),
    dietaryPreferencesJson = dietaryPreferences.joinToString(",") { it.name },
    allergiesJson = allergies.joinToString(","),
    medicalConditionsJson = medicalConditions.joinToString(","),
    isPremium = isPremium,
    createdAt = createdAt.toDbString(),
)

// ── Interface ─────────────────────────────────────────────────────────────────

interface UserRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
    suspend fun updateProfile(profile: UserProfile)
    fun getStreak(): Flow<StreakEntity?>
    suspend fun incrementStreak()
}

// ── Implementation ────────────────────────────────────────────────────────────

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao,
    private val streakDao: StreakDao,
) : UserRepository {

    // Uses a fixed single-user ID; swap for real auth when needed.
    private val userId = "local_user"

    override fun getProfile(): Flow<UserProfile?> =
        dao.observeProfile(userId)
            .distinctUntilChanged()
            .map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    override suspend fun updateProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    override fun getStreak(): Flow<StreakEntity?> =
        streakDao.observe(userId).distinctUntilChanged()

    override suspend fun incrementStreak() {
        val today = LocalDate.now().toDbString()
        val existing = streakDao.get(userId)
        if (existing == null) {
            streakDao.upsert(
                StreakEntity(
                    userId = userId,
                    currentStreak = 1,
                    longestStreak = 1,
                    lastActivityDate = today,
                    freezesRemaining = 3,
                )
            )
        } else {
            val newCurrent = existing.currentStreak + 1
            streakDao.upsert(
                existing.copy(
                    currentStreak = newCurrent,
                    longestStreak = maxOf(existing.longestStreak, newCurrent),
                    lastActivityDate = today,
                )
            )
        }
    }
}
