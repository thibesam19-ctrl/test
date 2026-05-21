package com.axiom.aicoach.ui.screens.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.ai.coaching.CoachingEngine
import com.axiom.aicoach.ai.provider.AiMessage
import com.axiom.aicoach.ai.provider.AiRole
import com.axiom.aicoach.analytics.AnalyticsEvent
import com.axiom.aicoach.analytics.AxiomAnalytics
import com.axiom.aicoach.data.local.dao.CoachMessageDao
import com.axiom.aicoach.data.local.entities.CoachMessageEntity
import com.axiom.aicoach.security.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
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

private const val CONVERSATION_ID = "default_conversation"

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val coachMessageDao: CoachMessageDao,
    private val coachingEngine: CoachingEngine,
    private val analytics: AxiomAnalytics,
    private val userSession: UserSession,
) : ViewModel() {

    private val currentUserId: String get() = userSession.userId

    private val introMessage = ChatMessageUi(
        id = "intro",
        text = "Hey! I'm Coach Axiom — your personal AI fitness coach. I'm here to help you with workouts, nutrition, recovery, and motivation.\n\nWhat's on your mind today? 💪",
        isUser = false,
    )

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    init {
        analytics.track(AnalyticsEvent.ScreenViewed("coach_chat"))
        viewModelScope.launch {
            coachMessageDao.observeMessages(currentUserId).collect { entities ->
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
        analytics.track(AnalyticsEvent.AiCoachMessageSent)
        dispatchMessage(text)
    }

    fun sendSuggestedPrompt(text: String) {
        dispatchMessage(text)
    }

    private fun dispatchMessage(text: String) {
        viewModelScope.launch {
            // Persist the user message to Room
            val userEntity = CoachMessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = CONVERSATION_ID,
                userId = currentUserId,
                role = "USER",
                content = text,
                intent = null,
                timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            )
            coachMessageDao.insert(userEntity)

            // Show typing indicator and prepare a streaming response message in the UI
            _uiState.update { it.copy(isTyping = true) }

            val streamingMessageId = UUID.randomUUID().toString()
            val streamingMessage = ChatMessageUi(
                id = streamingMessageId,
                text = "",
                isUser = false,
            )
            _uiState.update { state ->
                state.copy(messages = state.messages + streamingMessage)
            }

            // Build history from the last 10 persisted messages (before the new user turn)
            val history = buildHistory()

            var fullResponse = ""

            // Collect streaming chunks and update the in-progress message
            coachingEngine.chatStream(text, history).collect { chunk ->
                if (!chunk.isComplete) {
                    fullResponse += chunk.delta
                    val updatedMessage = streamingMessage.copy(text = fullResponse)
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages.map { msg ->
                                if (msg.id == streamingMessageId) updatedMessage else msg
                            },
                            isTyping = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isTyping = false) }
                }
            }

            analytics.track(AnalyticsEvent.AiCoachResponseReceived(coachingEngine.preferredProvider.name))
            // Persist the completed response to Room
            if (fullResponse.isNotBlank()) {
                val coachEntity = CoachMessageEntity(
                    id = streamingMessageId,
                    conversationId = CONVERSATION_ID,
                    userId = currentUserId,
                    role = "ASSISTANT",
                    content = fullResponse,
                    intent = null,
                    timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                )
                coachMessageDao.insert(coachEntity)
            }
        }
    }

    /**
     * Converts the most recent [ChatMessageUi] entries (up to 10) into [AiMessage] objects
     * for use as conversation history in the AI provider request.
     */
    private fun buildHistory(): List<AiMessage> {
        return _uiState.value.messages
            .filter { it.id != "intro" }
            .takeLast(10)
            .map { msg ->
                AiMessage(
                    role = if (msg.isUser) AiRole.USER else AiRole.ASSISTANT,
                    content = msg.text,
                )
            }
    }
}
