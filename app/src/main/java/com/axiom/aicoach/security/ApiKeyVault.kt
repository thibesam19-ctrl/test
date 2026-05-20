package com.axiom.aicoach.security

import android.util.Log
import com.axiom.aicoach.BuildConfig

/**
 * Centralised access point for API keys baked into the build via BuildConfig fields.
 * In release builds each getter validates that the key is non-empty and logs a warning
 * when a key is missing so the issue surfaces at runtime rather than silently failing.
 */
object ApiKeyVault {

    private const val TAG = "ApiKeyVault"

    fun getOpenAiKey(): String {
        val key = BuildConfig.OPENAI_API_KEY
        if (!BuildConfig.DEBUG && key.isBlank()) {
            Log.w(TAG, "OPENAI_API_KEY is not configured for this build")
        }
        return key
    }

    fun getClaudeKey(): String {
        val key = BuildConfig.CLAUDE_API_KEY
        if (!BuildConfig.DEBUG && key.isBlank()) {
            Log.w(TAG, "CLAUDE_API_KEY is not configured for this build")
        }
        return key
    }

    fun getGeminiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        if (!BuildConfig.DEBUG && key.isBlank()) {
            Log.w(TAG, "GEMINI_API_KEY is not configured for this build")
        }
        return key
    }

    /**
     * Returns true if at least one cloud AI provider key is present and non-blank.
     */
    fun isAnyCloudProviderConfigured(): Boolean =
        getOpenAiKey().isNotBlank() ||
            getClaudeKey().isNotBlank() ||
            getGeminiKey().isNotBlank()
}
