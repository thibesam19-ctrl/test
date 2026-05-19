package com.axiom.aicoach.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userProfileDao: UserProfileDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password are required.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // Stub: no real Firebase auth yet. Succeeds immediately for non-empty credentials.
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }

    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "All fields are required.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val userId = "local_user"
            val entity = UserProfileEntity(
                id = userId,
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
                createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
            userProfileDao.upsert(entity)
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            delay(500L)
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
