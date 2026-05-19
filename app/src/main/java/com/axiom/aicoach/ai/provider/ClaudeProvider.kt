package com.axiom.aicoach.ai.provider

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Serializable
private data class ClaudeRequestMessage(val role: String, val content: String)

@Serializable
private data class ClaudeChatRequest(
    val model: String,
    val messages: List<ClaudeRequestMessage>,
    @SerialName("max_tokens") val maxTokens: Int,
    val system: String? = null,
    val stream: Boolean,
)

@Serializable
private data class ClaudeContent(val type: String, val text: String? = null)

@Serializable
private data class ClaudeResponse(
    val content: List<ClaudeContent> = emptyList(),
    val usage: ClaudeUsage? = null,
)

@Serializable
private data class ClaudeUsage(
    @SerialName("input_tokens") val inputTokens: Int = 0,
    @SerialName("output_tokens") val outputTokens: Int = 0,
)

@Serializable
private data class ClaudeDelta(val type: String, val text: String? = null)

@Serializable
private data class ClaudeStreamEvent(
    val type: String,
    val delta: ClaudeDelta? = null,
    val index: Int? = null,
)

@Singleton
class ClaudeProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @Named("claude_api_key") private val apiKey: String,
) : AiProvider {

    override val type: AiProviderType = AiProviderType.CLAUDE

    override fun isConfigured(): Boolean = apiKey.isNotBlank()

    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json".toMediaType()
    private const val BASE_URL = "https://api.anthropic.com/v1/messages"
    private const val MODEL = "claude-haiku-4-5-20251001"
    private const val ANTHROPIC_VERSION = "2023-06-01"

    private fun buildMessages(request: AiChatRequest): List<ClaudeRequestMessage> {
        return request.messages
            .filter { it.role != AiRole.SYSTEM }
            .map { msg ->
                val role = when (msg.role) {
                    AiRole.USER -> "user"
                    AiRole.ASSISTANT -> "assistant"
                    AiRole.SYSTEM -> "user" // filtered above but need exhaustive when
                }
                ClaudeRequestMessage(role = role, content = msg.content)
            }
    }

    private fun buildHttpRequest(requestBody: String): Request =
        Request.Builder()
            .url(BASE_URL)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", ANTHROPIC_VERSION)
            .addHeader("content-type", "application/json")
            .post(requestBody.toRequestBody(mediaType))
            .build()

    override suspend fun chat(request: AiChatRequest): Result<AiChatResponse> {
        if (!isConfigured()) return Result.failure(IllegalStateException("Claude API key not configured"))

        val body = ClaudeChatRequest(
            model = MODEL,
            messages = buildMessages(request),
            maxTokens = request.maxTokens,
            system = request.systemPrompt.ifBlank { null },
            stream = false,
        )
        val httpRequest = buildHttpRequest(json.encodeToString(body))

        return suspendCancellableCoroutine { cont ->
            val call = okHttpClient.newCall(httpRequest)
            cont.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    cont.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (!resp.isSuccessful) {
                            cont.resumeWithException(IOException("Claude HTTP ${resp.code}: ${resp.body?.string()}"))
                            return
                        }
                        val responseText = resp.body?.string() ?: ""
                        try {
                            val parsed = json.decodeFromString<ClaudeResponse>(responseText)
                            val content = parsed.content.firstOrNull { it.type == "text" }?.text ?: ""
                            val tokens = (parsed.usage?.inputTokens ?: 0) + (parsed.usage?.outputTokens ?: 0)
                            cont.resume(
                                Result.success(
                                    AiChatResponse(
                                        content = content,
                                        providerType = AiProviderType.CLAUDE,
                                        tokensUsed = tokens,
                                    )
                                )
                            )
                        } catch (e: Exception) {
                            cont.resumeWithException(e)
                        }
                    }
                }
            })
        }
    }

    override fun chatStream(request: AiChatRequest): Flow<AiStreamChunk> = callbackFlow {
        if (!isConfigured()) {
            close(IllegalStateException("Claude API key not configured"))
            return@callbackFlow
        }

        val body = ClaudeChatRequest(
            model = MODEL,
            messages = buildMessages(request),
            maxTokens = request.maxTokens,
            system = request.systemPrompt.ifBlank { null },
            stream = true,
        )
        val httpRequest = buildHttpRequest(json.encodeToString(body))

        val factory = EventSources.createFactory(okHttpClient)
        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (data.isBlank()) return
                try {
                    val event = json.decodeFromString<ClaudeStreamEvent>(data)
                    when (event.type) {
                        "content_block_delta" -> {
                            val text = event.delta?.text
                            if (!text.isNullOrEmpty()) {
                                trySend(AiStreamChunk(delta = text, isComplete = false))
                            }
                        }
                        "message_stop" -> {
                            trySend(AiStreamChunk(delta = "", isComplete = true))
                            close()
                        }
                    }
                } catch (_: Exception) {
                    // ignore malformed SSE data lines
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                close(t ?: IOException("Claude SSE stream failed: HTTP ${response?.code}"))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        }

        val eventSource = factory.newEventSource(request = httpRequest, listener = listener)
        awaitClose { eventSource.cancel() }
    }
}
