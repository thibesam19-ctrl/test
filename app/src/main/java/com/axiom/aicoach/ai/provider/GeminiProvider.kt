package com.axiom.aicoach.ai.provider

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiProvider @Inject constructor(
    @Named("gemini_api_key") private val apiKey: String,
) : AiProvider {

    override val type: AiProviderType = AiProviderType.GEMINI

    override fun isConfigured(): Boolean = apiKey.isNotBlank()

    private fun buildModel(): GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
    )

    /**
     * Converts the request history + system prompt into a Gemini-compatible prompt string.
     * Gemini 1.5 Flash accepts a system instruction as part of the model config, but since
     * [GenerativeModel] in the SDK version used here doesn't expose systemInstruction at
     * construction time via the simple constructor, we prepend it to the conversation text
     * so the model still receives the coaching context.
     */
    private fun buildPrompt(request: AiChatRequest): String {
        val sb = StringBuilder()
        if (request.systemPrompt.isNotBlank()) {
            sb.appendLine("[System]: ${request.systemPrompt}")
            sb.appendLine()
        }
        request.messages.forEach { msg ->
            val prefix = when (msg.role) {
                AiRole.SYSTEM -> "[System]"
                AiRole.USER -> "[User]"
                AiRole.ASSISTANT -> "[Assistant]"
            }
            sb.appendLine("$prefix: ${msg.content}")
        }
        return sb.toString().trimEnd()
    }

    override suspend fun chat(request: AiChatRequest): Result<AiChatResponse> {
        if (!isConfigured()) return Result.failure(IllegalStateException("Gemini API key not configured"))
        return try {
            val model = buildModel()
            val prompt = buildPrompt(request)
            val promptContent = content { text(prompt) }
            val response = model.generateContent(promptContent)
            val text = response.text ?: ""
            Result.success(
                AiChatResponse(
                    content = text,
                    providerType = AiProviderType.GEMINI,
                    tokensUsed = 0,
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun chatStream(request: AiChatRequest): Flow<AiStreamChunk> = flow {
        if (!isConfigured()) {
            throw IllegalStateException("Gemini API key not configured")
        }
        val model = buildModel()
        val prompt = buildPrompt(request)
        val promptContent = content { text(prompt) }
        model.generateContentStream(promptContent).collect { response ->
            val text = response.text
            if (!text.isNullOrEmpty()) {
                emit(AiStreamChunk(delta = text, isComplete = false))
            }
        }
        emit(AiStreamChunk(delta = "", isComplete = true))
    }
}
