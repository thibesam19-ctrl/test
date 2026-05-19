package com.axiom.aicoach.ai.provider

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiProviderFactory @Inject constructor(
    private val localProvider: LocalAiProvider,
    private val openAiProvider: OpenAiProvider,
    private val claudeProvider: ClaudeProvider,
    private val geminiProvider: GeminiProvider,
) {

    /**
     * Returns the requested provider if configured, otherwise falls back to [LocalAiProvider].
     */
    fun getProvider(preferred: AiProviderType = AiProviderType.LOCAL): AiProvider {
        return when {
            preferred == AiProviderType.OPENAI && openAiProvider.isConfigured() -> openAiProvider
            preferred == AiProviderType.CLAUDE && claudeProvider.isConfigured() -> claudeProvider
            preferred == AiProviderType.GEMINI && geminiProvider.isConfigured() -> geminiProvider
            else -> localProvider
        }
    }

    /**
     * Returns the first available cloud provider in priority order (OpenAI → Claude → Gemini),
     * falling back to the local rule-based provider when none are configured.
     */
    fun getBestAvailable(): AiProvider {
        return when {
            openAiProvider.isConfigured() -> openAiProvider
            claudeProvider.isConfigured() -> claudeProvider
            geminiProvider.isConfigured() -> geminiProvider
            else -> localProvider
        }
    }
}
