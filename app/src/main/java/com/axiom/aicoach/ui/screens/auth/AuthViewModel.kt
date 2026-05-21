package com.axiom.aicoach.ui.screens.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.UserProfileDao
import com.axiom.aicoach.data.local.entities.UserProfileEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private val auth: FirebaseAuth,
    private val userProfileDao: UserProfileDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (!validateEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Password is required.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.toFriendlyMessage()) }
            }
        }
    }

    fun signUp(name: String, email: String, password: String, onSuccess: () -> Unit) {
        val nameError = validateName(name)
        if (nameError != null) { _uiState.update { it.copy(error = nameError) }; return }
        if (!validateEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }
        val pwError = validatePassword(password)
        if (pwError != null) { _uiState.update { it.copy(error = pwError) }; return }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching {
                val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val uid = result.user?.uid ?: error("Missing UID after sign-up")
                val entity = UserProfileEntity(
                    id = uid,
                    email = email.trim(),
                    displayName = name.trim(),
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
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.toFriendlyMessage()) }
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        if (!validateEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching {
                auth.sendPasswordResetEmail(email.trim()).await()
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.toFriendlyMessage()) }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { AuthUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun validateName(name: String): String? {
        if (name.isBlank()) return "Name is required."
        if (name.trim().length < 2) return "Name must be at least 2 characters."
        return null
    }

    private fun validateEmail(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "Password must be at least 8 characters."
        if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter."
        if (!password.any { it.isDigit() }) return "Password must contain at least one number."
        if (!password.any { !it.isLetterOrDigit() }) return "Password must contain at least one special character."
        return null
    }

    private fun Throwable.toFriendlyMessage(): String = when (this) {
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Choose a stronger password."
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
        is FirebaseAuthUserCollisionException -> "An account with this email already exists."
        else -> message?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
    }
}
