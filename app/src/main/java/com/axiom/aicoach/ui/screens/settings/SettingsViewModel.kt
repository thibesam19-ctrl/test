package com.axiom.aicoach.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.NotificationPreferenceDao
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.entities.NotificationPreferenceEntity
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val avatarInitial: String = "A",
    val notifyWorkout: Boolean = true,
    val notifyWater: Boolean = true,
    val notifyMeals: Boolean = false,
    val notifyWeeklyReport: Boolean = true,
    val isLoading: Boolean = true,
)

// Hardcoded until real auth session is wired in.
private const val CURRENT_USER_ID = "local_user"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val notificationPreferenceDao: NotificationPreferenceDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userProfileDao.observeProfile(CURRENT_USER_ID),
                notificationPreferenceDao.observe(CURRENT_USER_ID),
            ) { profile, prefs ->
                SettingsUiState(
                    userName = profile?.displayName ?: "",
                    userEmail = profile?.email ?: "",
                    avatarInitial = profile?.displayName
                        ?.firstOrNull()
                        ?.uppercaseChar()
                        ?.toString() ?: "A",
                    notifyWorkout = prefs?.workoutReminder ?: true,
                    notifyWater = prefs?.waterReminder ?: true,
                    notifyMeals = prefs?.mealReminder ?: false,
                    notifyWeeklyReport = prefs?.weeklyProgress ?: true,
                    isLoading = false,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(userName = name) }
        viewModelScope.launch {
            val existing = userProfileDao.getProfile(CURRENT_USER_ID)
            if (existing != null) {
                userProfileDao.upsert(existing.copy(displayName = name))
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(userEmail = email) }
        viewModelScope.launch {
            val existing = userProfileDao.getProfile(CURRENT_USER_ID)
            if (existing != null) {
                userProfileDao.upsert(existing.copy(email = email))
            }
        }
    }

    fun saveProfile(name: String, email: String) {
        _uiState.update { it.copy(userName = name, userEmail = email) }
        viewModelScope.launch {
            val existing = userProfileDao.getProfile(CURRENT_USER_ID)
            if (existing != null) {
                userProfileDao.upsert(existing.copy(displayName = name, email = email))
            } else {
                userProfileDao.upsert(
                    UserProfileEntity(
                        id = CURRENT_USER_ID,
                        email = email,
                        displayName = name,
                        photoUrl = null,
                        sex = "PREFER_NOT_TO_SAY",
                        dateOfBirth = "2000-01-01",
                        heightCm = 170f,
                        weightKg = 70f,
                        targetWeightKg = 70f,
                        activityLevel = "MODERATELY_ACTIVE",
                        experienceLevel = "BEGINNER",
                        goal = "MAINTAIN",
                        trainingDaysPerWeek = 3,
                        equipmentJson = "[]",
                        injuriesJson = "[]",
                        dietaryPreferencesJson = "[]",
                        allergiesJson = "[]",
                        medicalConditionsJson = "[]",
                        isPremium = false,
                        createdAt = java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    )
                )
            }
        }
    }

    fun toggleNotification(type: String, enabled: Boolean) {
        viewModelScope.launch {
            val existing = notificationPreferenceDao.observe(CURRENT_USER_ID).let {
                // Get a snapshot via a one-shot query isn't directly exposed; reconstruct from state.
                null
            }
            val current = _uiState.value
            val updated = when (type) {
                "workout" -> current.copy(notifyWorkout = enabled)
                "water" -> current.copy(notifyWater = enabled)
                "meals" -> current.copy(notifyMeals = enabled)
                "weekly_report" -> current.copy(notifyWeeklyReport = enabled)
                else -> current
            }
            _uiState.value = updated

            notificationPreferenceDao.upsert(
                NotificationPreferenceEntity(
                    userId = CURRENT_USER_ID,
                    workoutReminder = updated.notifyWorkout,
                    workoutReminderTime = "08:00",
                    mealReminder = updated.notifyMeals,
                    waterReminder = updated.notifyWater,
                    sleepReminder = false,
                    weeklyProgress = updated.notifyWeeklyReport,
                    streakProtection = true,
                    achievements = true,
                    tips = false,
                    quietHoursStart = 22,
                    quietHoursEnd = 7,
                )
            )
        }
    }
}
