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
private data class OpenAiRequestMessage(val role: String, val content: String)

@Serializable
private data class OpenAiChatRequest(
    val model: String,
    val messages: List<OpenAiRequestMessage>,
    @SerialName("max_tokens") val maxTokens: Int,
    val temperature: Float,
    val stream: Boolean,
)

@Serializable
private data class OpenAiChatResponse(
    val choices: List<OpenAiChoice>,
    val usage: OpenAiUsage? = null,
)

@Serializable
private data class OpenAiChoice(
    val message: OpenAiResponseMessage? = null,
    val delta: OpenAiDelta? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)

@Serializable
private data class OpenAiResponseMessage(val role: String, val content: String)

@Serializable
private data class OpenAiDelta(val content: String? = null)

@Serializable
private data class OpenAiUsage(
    @SerialName("total_tokens") val totalTokens: Int = 0,
)

@Singleton
class OpenAiProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @Named("openai_api_key") private val apiKey: String,
) : AiProvider {

    override val type: AiProviderType = AiProviderType.OPENAI

    override fun isConfigured(): Boolean = apiKey.isNotBlank()

    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json".toMediaType()
    private const val BASE_URL = "https://api.openai.com/v1/chat/completions"
    private const val MODEL = "gpt-4o-mini"

    private fun buildMessages(request: AiChatRequest): List<OpenAiRequestMessage> {
        val messages = mutableListOf<OpenAiRequestMessage>()
        if (request.systemPrompt.isNotBlank()) {
            messages.add(OpenAiRequestMessage(role = "system", content = request.systemPrompt))
        }
        request.messages.forEach { msg ->
            val role = when (msg.role) {
                AiRole.SYSTEM -> "system"
                AiRole.USER -> "user"
                AiRole.ASSISTANT -> "assistant"
            }
            messages.add(OpenAiRequestMessage(role = role, content = msg.content))
        }
        return messages
    }

    override suspend fun chat(request: AiChatRequest): Result<AiChatResponse> {
        if (!isConfigured()) return Result.failure(IllegalStateException("OpenAI API key not configured"))

        val body = OpenAiChatRequest(
            model = MODEL,
            messages = buildMessages(request),
            maxTokens = request.maxTokens,
            temperature = request.temperature,
            stream = false,
        )
        val requestBody = json.encodeToString(body).toRequestBody(mediaType)
        val httpRequest = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

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
                            cont.resumeWithException(IOException("OpenAI HTTP ${resp.code}: ${resp.body?.string()}"))
                            return
                        }
                        val responseText = resp.body?.string() ?: ""
                        try {
                            val parsed = json.decodeFromString<OpenAiChatResponse>(responseText)
                            val content = parsed.choices.firstOrNull()?.message?.content ?: ""
                            val tokens = parsed.usage?.totalTokens ?: 0
                            cont.resume(
                                Result.success(
                                    AiChatResponse(
                                        content = content,
                                        providerType = AiProviderType.OPENAI,
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
            close(IllegalStateException("OpenAI API key not configured"))
            return@callbackFlow
        }

        val body = OpenAiChatRequest(
            model = MODEL,
            messages = buildMessages(request),
            maxTokens = request.maxTokens,
            temperature = request.temperature,
            stream = true,
        )
        val requestBody = json.encodeToString(body).toRequestBody(mediaType)
        val httpRequest = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                if (data == "[DONE]") {
                    trySend(AiStreamChunk(delta = "", isComplete = true))
                    close()
                    return
                }
                try {
                    val parsed = json.decodeFromString<OpenAiChatResponse>(data)
                    val delta = parsed.choices.firstOrNull()?.delta?.content ?: ""
                    if (delta.isNotEmpty()) {
                        trySend(AiStreamChunk(delta = delta, isComplete = false))
                    }
                } catch (_: Exception) {
                    // ignore malformed SSE data lines
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                close(t ?: IOException("OpenAI SSE stream failed: HTTP ${response?.code}"))
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }
        }

        val eventSource = factory.newEventSource(request = httpRequest, listener = listener)
        awaitClose { eventSource.cancel() }
    }
}
