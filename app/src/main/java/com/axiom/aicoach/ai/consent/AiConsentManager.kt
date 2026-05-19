package com.axiom.aicoach.ai.consent

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiConsentManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val hasConsentedToAiFeatures: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AI_CONSENT_KEY] ?: false
    }

    val hasConsentedToCameraAi: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[CAMERA_AI_CONSENT_KEY] ?: false
    }

    suspend fun grantAiConsent() {
        dataStore.edit { prefs -> prefs[AI_CONSENT_KEY] = true }
    }

    suspend fun grantCameraAiConsent() {
        dataStore.edit { prefs -> prefs[CAMERA_AI_CONSENT_KEY] = true }
    }

    suspend fun revokeAllAiConsent() {
        dataStore.edit { prefs ->
            prefs[AI_CONSENT_KEY] = false
            prefs[CAMERA_AI_CONSENT_KEY] = false
        }
    }

    companion object {
        val AI_CONSENT_KEY = booleanPreferencesKey("ai_consent")
        val CAMERA_AI_CONSENT_KEY = booleanPreferencesKey("camera_ai_consent")
    }
}
