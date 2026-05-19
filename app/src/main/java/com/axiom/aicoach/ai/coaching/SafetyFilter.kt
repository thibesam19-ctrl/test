package com.axiom.aicoach.ai.coaching

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafetyFilter @Inject constructor() {

    data class SafetyResult(val isSafe: Boolean, val safeResponse: String? = null)

    private val triggers = listOf(
        "not eaten",
        "hate my body",
        "starving myself",
        "starving",
        "too fat",
        "skinny",
        "skip meals",
        "stop eating",
        "purge",
        "laxative",
        "self harm",
        "self-harm",
        "hurt myself",
        "don't want to eat",
        "don't want to eat",
        "extreme diet",
        "crash diet",
    )

    private val safeResponse =
        "I hear that you're struggling — those feelings are valid. Your worth has nothing to do " +
            "with your body size. I'd encourage you to speak with a professional if these thoughts " +
            "are weighing on you. You can reach a counselor at any time. 💙\n\n" +
            "In the meantime, let's focus on *feeling strong and healthy*, not a number. " +
            "What would feel good to work on today?"

    fun check(userMessage: String): SafetyResult {
        val lower = userMessage.lowercase()
        val triggered = triggers.any { trigger -> trigger in lower }
        return if (triggered) {
            SafetyResult(isSafe = false, safeResponse = safeResponse)
        } else {
            SafetyResult(isSafe = true, safeResponse = null)
        }
    }
}
