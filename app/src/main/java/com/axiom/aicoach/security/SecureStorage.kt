package com.axiom.aicoach.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences? by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "axiom_secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            logError("Failed to initialize EncryptedSharedPreferences", e)
            null
        }
    }

    fun putString(key: String, value: String) {
        try {
            prefs?.edit()?.putString(key, value)?.apply()
        } catch (e: Exception) {
            logError("Failed to write string for key: $key", e)
        }
    }

    fun getString(key: String, default: String = ""): String {
        return try {
            prefs?.getString(key, default) ?: default
        } catch (e: Exception) {
            logError("Failed to read string for key: $key", e)
            default
        }
    }

    fun putBoolean(key: String, value: Boolean) {
        try {
            prefs?.edit()?.putBoolean(key, value)?.apply()
        } catch (e: Exception) {
            logError("Failed to write boolean for key: $key", e)
        }
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return try {
            prefs?.getBoolean(key, default) ?: default
        } catch (e: Exception) {
            logError("Failed to read boolean for key: $key", e)
            default
        }
    }

    fun remove(key: String) {
        try {
            prefs?.edit()?.remove(key)?.apply()
        } catch (e: Exception) {
            logError("Failed to remove key: $key", e)
        }
    }

    fun clear() {
        try {
            prefs?.edit()?.clear()?.apply()
        } catch (e: Exception) {
            logError("Failed to clear secure storage", e)
        }
    }

    private fun logError(message: String, throwable: Exception) {
        Log.e(TAG, message, throwable)
        try {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()
                .recordException(throwable)
        } catch (ignored: Exception) {
            // Crashlytics may not be initialized; log only
        }
    }

    companion object {
        private const val TAG = "SecureStorage"
    }
}
