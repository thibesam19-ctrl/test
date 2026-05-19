package com.axiom.aicoach.ai.provider

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

private val coachResponses = mapOf(
    "weight" to "Based on your 14-day trend, your weight is moving in the right direction — down about 0.4 kg/week. That's a healthy, sustainable rate. Keep it consistent! 💪",
    "workout" to "You've completed 3 workouts this week — great job! I'd recommend not skipping your planned upper body session tomorrow. Consistency over perfection.",
    "eat" to "Looking at your remaining macros for today, I'd suggest a meal with about 40g protein and 50g carbs. Something like grilled chicken with rice or a protein shake with banana would work perfectly.",
    "sore" to "Muscle soreness means your body is adapting — that's a good sign! Make sure to get 7-8 hours of sleep tonight, stay hydrated, and consider a light walk or foam rolling for recovery.",
    "tired" to "Fatigue can signal that your body needs rest. Take a full rest day today. Tomorrow, focus on quality sleep and nutrition. Overtraining is counterproductive.",
    "plan" to "I can adjust your plan! Tell me what you'd like to change — more or fewer days, different split (PPL vs Upper/Lower), or different focus areas and I'll update it for you.",
    "protein" to "For your goal, aim for 1.6–2.2g of protein per kg of bodyweight. At your current weight, that's roughly 128–175g per day. Your current intake is tracking well!",
    "skip" to "Missing one workout isn't the end of the world — but consistency is where results come from. If you're not injured or sick, I'd encourage you to do at least a 20-minute version. Even half is better than none.",
    "default" to "That's a great question! I'm here to help with your fitness journey. Whether it's nutrition, workouts, recovery, or motivation — just ask. I'm powered by Axiom's coaching engine. 🤖\n\n*Note: I'm an AI assistant, not a doctor or licensed trainer. Always consult a professional for medical advice.*",
)

private const val ED_SAFETY_RESPONSE =
    "I hear that you're struggling — those feelings are valid. Your worth has nothing to do with your body size. I'd encourage you to speak with a professional if these thoughts are weighing on you. You can reach a counselor at any time. 💙\n\nIn the meantime, let's focus on *feeling strong and healthy*, not a number. What would feel good to work on today?"

@Singleton
class LocalAiProvider @Inject constructor() : AiProvider {

    override val type: AiProviderType = AiProviderType.LOCAL

    override fun isConfigured(): Boolean = true

    override suspend fun chat(request: AiChatRequest): Result<AiChatResponse> {
        val userMessage = request.messages.lastOrNull { it.role == AiRole.USER }?.content ?: ""
        val response = getRuleBasedResponse(userMessage)
        return Result.success(
            AiChatResponse(
                content = response,
                providerType = AiProviderType.LOCAL,
                tokensUsed = 0,
            )
        )
    }

    override fun chatStream(request: AiChatRequest): Flow<AiStreamChunk> = flow {
        val userMessage = request.messages.lastOrNull { it.role == AiRole.USER }?.content ?: ""
        val response = getRuleBasedResponse(userMessage)
        for (char in response) {
            emit(AiStreamChunk(delta = char.toString(), isComplete = false))
            delay(12L)
        }
        emit(AiStreamChunk(delta = "", isComplete = true))
    }

    fun getRuleBasedResponse(message: String): String {
        val lower = message.lowercase()
        return when {
            "not eaten" in lower || "hate my body" in lower || "starving" in lower ||
                "skinny" in lower || "skip meals" in lower || "stop eating" in lower ||
                "purge" in lower || "laxative" in lower || "starving myself" in lower ||
                "too fat" in lower -> ED_SAFETY_RESPONSE

            "weight" in lower || "scale" in lower || "fat" in lower -> coachResponses["weight"]!!
            "workout" in lower || "exercise" in lower || "train" in lower -> coachResponses["workout"]!!
            "eat" in lower || "food" in lower || "meal" in lower || "hungry" in lower -> coachResponses["eat"]!!
            "sore" in lower || "ache" in lower || "pain" in lower -> coachResponses["sore"]!!
            "tired" in lower || "fatigue" in lower || "exhausted" in lower -> coachResponses["tired"]!!
            "plan" in lower || "adjust" in lower || "change" in lower -> coachResponses["plan"]!!
            "protein" in lower || "macro" in lower -> coachResponses["protein"]!!
            "skip" in lower || "miss" in lower || "lazy" in lower -> coachResponses["skip"]!!
            else -> coachResponses["default"]!!
        }
    }
}
