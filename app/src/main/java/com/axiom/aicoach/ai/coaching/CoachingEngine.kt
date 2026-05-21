package com.axiom.aicoach.ai.coaching

import android.util.Log
import com.axiom.aicoach.ai.consent.AiConsentManager
import com.axiom.aicoach.ai.provider.AiChatRequest
import com.axiom.aicoach.ai.provider.AiMessage
import com.axiom.aicoach.ai.provider.AiProviderFactory
import com.axiom.aicoach.ai.provider.AiProviderType
import com.axiom.aicoach.ai.provider.AiRole
import com.axiom.aicoach.ai.provider.AiStreamChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachingEngine @Inject constructor(
    private val providerFactory: AiProviderFactory,
    private val contextBuilder: CoachContextBuilder,
    private val safetyFilter: SafetyFilter,
    private val rateLimiter: AiRateLimiter,
    private val consentManager: AiConsentManager,
) {

    var preferredProvider: AiProviderType = AiProviderType.LOCAL

    fun chatStream(
        userMessage: String,
        history: List<AiMessage>,
    ): Flow<AiStreamChunk> = flow {
        // 1. Safety check — always run, before any other processing
        val safetyResult = safetyFilter.check(userMessage)
        if (!safetyResult.isSafe) {
            emitString(safetyResult.safeResponse ?: "")
            return@flow
        }

        // 2. Rate limiting
        val rateResult = rateLimiter.checkAndRecord()
        if (!rateResult.allowed) {
            emitString(rateResult.reason ?: "Too many requests. Please slow down.")
            return@flow
        }

        // 3. Consent gate — only use cloud provider when user has consented
        val hasConsent = consentManager.hasConsentedToAiFeatures.first()
        val effectiveProvider = if (hasConsent) preferredProvider else AiProviderType.LOCAL

        // 4. Sanitize input before sending to provider
        val sanitizedMessage = safetyFilter.sanitize(userMessage)

        val systemPrompt = buildSystemPrompt()
        val request = AiChatRequest(
            messages = history + AiMessage(role = AiRole.USER, content = sanitizedMessage),
            systemPrompt = systemPrompt,
            maxTokens = 1024,
            temperature = 0.7f,
        )

        val provider = providerFactory.getProvider(effectiveProvider)
        try {
            provider.chatStream(request).collect { chunk -> emit(chunk) }
        } catch (e: Exception) {
            Log.w("CoachingEngine", "Provider ${effectiveProvider.name} failed, falling back to LOCAL", e)
            val localProvider = providerFactory.getProvider(AiProviderType.LOCAL)
            emitString("(Using local AI — cloud provider unavailable)\n\n")
            localProvider.chatStream(request).collect { chunk -> emit(chunk) }
        }
    }

    suspend fun chat(userMessage: String, history: List<AiMessage>): String {
        val safetyResult = safetyFilter.check(userMessage)
        if (!safetyResult.isSafe) return safetyResult.safeResponse ?: ""

        val rateResult = rateLimiter.checkAndRecord()
        if (!rateResult.allowed) return rateResult.reason ?: "Too many requests."

        val hasConsent = consentManager.hasConsentedToAiFeatures.first()
        val effectiveProvider = if (hasConsent) preferredProvider else AiProviderType.LOCAL

        val sanitizedMessage = safetyFilter.sanitize(userMessage)
        val systemPrompt = buildSystemPrompt()
        val request = AiChatRequest(
            messages = history + AiMessage(role = AiRole.USER, content = sanitizedMessage),
            systemPrompt = systemPrompt,
            maxTokens = 1024,
            temperature = 0.7f,
        )

        val provider = providerFactory.getProvider(effectiveProvider)
        return provider.chat(request).getOrElse {
            Log.w("CoachingEngine", "Provider ${effectiveProvider.name} failed, falling back to LOCAL", it)
            val localProvider = providerFactory.getProvider(AiProviderType.LOCAL)
            localProvider.chat(request).getOrElse { e -> e.message ?: "An error occurred." }
        }.content
    }

    fun getRemainingRequestsToday(): Int = rateLimiter.getRemainingToday()

    private suspend fun buildSystemPrompt(): String {
        val ctx = runCatching { contextBuilder.build() }.getOrDefault(UserCoachContext())
        val weightStr = ctx.weightKg?.let { "%.1f".format(it) } ?: "unknown"
        val goalWeightStr = ctx.goalWeightKg?.let { "%.1f".format(it) } ?: "unknown"
        val recentWorkoutsStr = if (ctx.recentWorkoutNames.isEmpty()) "none recorded"
        else ctx.recentWorkoutNames.joinToString(", ")

        return """
You are Axiom Coach, an elite AI fitness coach.
User: ${ctx.userName}, Goal: ${ctx.fitnessGoal}, Weight: ${weightStr}kg, Goal Weight: ${goalWeightStr}kg
Today: ${ctx.todayCaloriesKcal}/${ctx.calorieGoalKcal}kcal, ${ctx.todayProteinG}g protein, ${ctx.todayWaterMl}ml water
Recent workouts: $recentWorkoutsStr. Streak: ${ctx.streakDays} days. Weekly workouts: ${ctx.weekWorkoutCount}. Weight trend: ${ctx.weightTrend}.
Be concise, motivating, and science-based. Max 3 sentences unless the user asks for more detail.
When you change the plan or make a recommendation, briefly explain WHY (the evidence or reason).
SAFETY: Never recommend less than 1200 kcal/day for women or 1500 kcal/day for men.
Never recommend rapid weight loss greater than 0.5–1kg/week.
If the user shows signs of disordered eating or distress, respond with compassion and refer to professionals.
        """.trimIndent()
    }
}

private suspend fun kotlinx.coroutines.flow.FlowCollector<AiStreamChunk>.emitString(text: String) {
    for (char in text) emit(AiStreamChunk(delta = char.toString(), isComplete = false))
    emit(AiStreamChunk(delta = "", isComplete = true))
}
