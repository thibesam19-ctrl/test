package com.axiom.aicoach.ai.provider

import kotlinx.coroutines.flow.Flow

enum class AiProviderType { OPENAI, CLAUDE, GEMINI, LOCAL }

data class AiMessage(val role: AiRole, val content: String)
enum class AiRole { SYSTEM, USER, ASSISTANT }

data class AiChatRequest(
    val messages: List<AiMessage>,
    val systemPrompt: String = "",
    val maxTokens: Int = 1024,
    val temperature: Float = 0.7f,
)

data class AiChatResponse(
    val content: String,
    val providerType: AiProviderType,
    val tokensUsed: Int = 0,
)

data class AiStreamChunk(val delta: String, val isComplete: Boolean = false)

interface AiProvider {
    val type: AiProviderType
    suspend fun chat(request: AiChatRequest): Result<AiChatResponse>
    fun chatStream(request: AiChatRequest): Flow<AiStreamChunk>
    fun isConfigured(): Boolean
}
