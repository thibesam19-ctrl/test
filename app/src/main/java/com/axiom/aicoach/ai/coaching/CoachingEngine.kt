package com.axiom.aicoach.ai.coaching

import com.axiom.aicoach.ai.provider.AiChatRequest
import com.axiom.aicoach.ai.provider.AiMessage
import com.axiom.aicoach.ai.provider.AiProviderFactory
import com.axiom.aicoach.ai.provider.AiProviderType
import com.axiom.aicoach.ai.provider.AiRole
import com.axiom.aicoach.ai.provider.AiStreamChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoachingEngine @Inject constructor(
    private val providerFactory: AiProviderFactory,
    private val contextBuilder: CoachContextBuilder,
    private val safetyFilter: SafetyFilter,
) {

    /**
     * Preferred provider type. Defaults to LOCAL (rule-based). Upgrade to a cloud provider
     * at runtime once an API key has been supplied and the user has granted AI consent.
     */
    var preferredProvider: AiProviderType = AiProviderType.LOCAL

    fun chatStream(
        userMessage: String,
        history: List<AiMessage>,
    ): Flow<AiStreamChunk> = flow {
        // Safety check first — always run regardless of provider
        val safetyResult = safetyFilter.check(userMessage)
        if (!safetyResult.isSafe) {
            val safeText = safetyResult.safeResponse ?: ""
            for (char in safeText) {
                emit(AiStreamChunk(delta = char.toString(), isComplete = false))
            }
            emit(AiStreamChunk(delta = "", isComplete = true))
            return@flow
        }

        val systemPrompt = buildSystemPrompt()
        val request = AiChatRequest(
            messages = history + AiMessage(role = AiRole.USER, content = userMessage),
            systemPrompt = systemPrompt,
            maxTokens = 1024,
            temperature = 0.7f,
        )
        val provider = providerFactory.getProvider(preferredProvider)
        provider.chatStream(request).collect { chunk ->
            emit(chunk)
        }
    }

    suspend fun chat(userMessage: String, history: List<AiMessage>): String {
        val safetyResult = safetyFilter.check(userMessage)
        if (!safetyResult.isSafe) {
            return safetyResult.safeResponse ?: ""
        }

        val systemPrompt = buildSystemPrompt()
        val request = AiChatRequest(
            messages = history + AiMessage(role = AiRole.USER, content = userMessage),
            systemPrompt = systemPrompt,
            maxTokens = 1024,
            temperature = 0.7f,
        )
        val provider = providerFactory.getProvider(preferredProvider)
        return provider.chat(request).getOrElse { throwable ->
            // On any provider failure, fall back to the local rule-based response
            val localProvider = providerFactory.getProvider(AiProviderType.LOCAL)
            localProvider.chat(request).getOrElse { throwable.message ?: "An error occurred." }
        }.content
    }

    private suspend fun buildSystemPrompt(): String {
        val ctx = runCatching { contextBuilder.build() }.getOrDefault(UserCoachContext())
        val weightStr = ctx.weightKg?.let { "%.1f".format(it) } ?: "unknown"
        val goalWeightStr = ctx.goalWeightKg?.let { "%.1f".format(it) } ?: "unknown"
        val recentWorkoutsStr = if (ctx.recentWorkoutNames.isEmpty()) {
            "none recorded"
        } else {
            ctx.recentWorkoutNames.joinToString(", ")
        }

        return """
You are Axiom Coach, an elite AI fitness coach.
User: ${ctx.userName}, Goal: ${ctx.fitnessGoal}, Weight: ${weightStr}kg, Goal Weight: ${goalWeightStr}kg
Today: ${ctx.todayCaloriesKcal}/${ctx.calorieGoalKcal}kcal, ${ctx.todayProteinG}g protein, ${ctx.todayWaterMl}ml water
Recent workouts: $recentWorkoutsStr. Streak: ${ctx.streakDays} days. Weekly workouts: ${ctx.weekWorkoutCount}. Weight trend: ${ctx.weightTrend}.
Be concise, motivating, and science-based. Max 3 sentences unless the user asks for more detail.
SAFETY: Never recommend less than 1200 kcal/day for women or less than 1500 kcal/day for men.
Never recommend rapid weight loss greater than 1kg/week.
        """.trimIndent()
    }
}
