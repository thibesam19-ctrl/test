package com.axiom.aicoach.ui.screens.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.CoachMessageDao
import com.axiom.aicoach.data.local.entities.CoachMessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class CoachUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
    val isTyping: Boolean = false,
    val suggestedPrompts: List<String> = listOf(
        "Create a workout plan for me",
        "How many calories should I eat?",
        "Tips for better sleep",
        "I missed my workout today",
    ),
)

data class ChatMessageUi(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

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

private fun getRuleBasedResponse(message: String): String {
    val lower = message.lowercase()
    return when {
        "weight" in lower || "scale" in lower || "fat" in lower -> coachResponses["weight"]!!
        "workout" in lower || "exercise" in lower || "train" in lower -> coachResponses["workout"]!!
        "eat" in lower || "food" in lower || "meal" in lower || "hungry" in lower -> coachResponses["eat"]!!
        "sore" in lower || "ache" in lower || "pain" in lower -> coachResponses["sore"]!!
        "tired" in lower || "fatigue" in lower || "exhausted" in lower -> coachResponses["tired"]!!
        "plan" in lower || "adjust" in lower || "change" in lower -> coachResponses["plan"]!!
        "protein" in lower || "macro" in lower -> coachResponses["protein"]!!
        "skip" in lower || "miss" in lower || "lazy" in lower -> coachResponses["skip"]!!
        "skinny" in lower || "hate my body" in lower || "not eaten" in lower ->
            "I hear that you're struggling — those feelings are valid. Your worth has nothing to do with your body size. I'd encourage you to speak with a professional if these thoughts are weighing on you. You can reach a counselor at any time. 💙\n\nIn the meantime, let's focus on *feeling strong and healthy*, not a number. What would feel good to work on today?"
        else -> coachResponses["default"]!!
    }
}

// Hardcoded for now; replace with real session/auth source when available.
private const val CURRENT_USER_ID = "local_user"
private const val CONVERSATION_ID = "default_conversation"

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val coachMessageDao: CoachMessageDao,
) : ViewModel() {

    private val introMessage = ChatMessageUi(
        id = "intro",
        text = "Hey! I'm Coach Axiom — your personal AI fitness coach. I'm here to help you with workouts, nutrition, recovery, and motivation.\n\nWhat's on your mind today? 💪",
        isUser = false,
    )

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            coachMessageDao.observeMessages(CURRENT_USER_ID).collect { entities ->
                val mapped = entities
                    .sortedBy { it.timestamp }
                    .map { entity ->
                        ChatMessageUi(
                            id = entity.id,
                            text = entity.content,
                            isUser = entity.role.equals("USER", ignoreCase = true),
                            timestamp = runCatching {
                                LocalDateTime.parse(entity.timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    .let { ldt ->
                                        ldt.toEpochSecond(java.time.ZoneOffset.UTC) * 1000L
                                    }
                            }.getOrDefault(System.currentTimeMillis()),
                        )
                    }
                val messages = if (mapped.isEmpty()) listOf(introMessage) else mapped
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return
        _uiState.update { it.copy(inputText = "") }
        dispatchMessage(text)
    }

    fun sendSuggestedPrompt(text: String) {
        dispatchMessage(text)
    }

    private fun dispatchMessage(text: String) {
        viewModelScope.launch {
            val userEntity = CoachMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = CONVERSATION_ID,
                userId = CURRENT_USER_ID,
                role = "USER",
                content = text,
                intent = null,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
            coachMessageDao.insert(userEntity)

            _uiState.update { it.copy(isTyping = true) }
            delay(800L)

            val response = getRuleBasedResponse(text)
            val coachEntity = CoachMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = CONVERSATION_ID,
                userId = CURRENT_USER_ID,
                role = "ASSISTANT",
                content = response,
                intent = null,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
            coachMessageDao.insert(coachEntity)
            _uiState.update { it.copy(isTyping = false) }
        }
    }
}
