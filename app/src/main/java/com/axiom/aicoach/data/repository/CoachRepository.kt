package com.axiom.aicoach.data.repository

import com.axiom.aicoach.data.local.dao.CoachMessageDao
import com.axiom.aicoach.data.local.entities.CoachMessageEntity
import com.axiom.aicoach.domain.model.CoachIntent
import com.axiom.aicoach.domain.model.CoachMessage
import com.axiom.aicoach.domain.model.MessageRole
import com.axiom.aicoach.util.newId
import com.axiom.aicoach.util.toDbString
import com.axiom.aicoach.util.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// ── Mapping ───────────────────────────────────────────────────────────────────

private fun CoachMessageEntity.toDomain() = CoachMessage(
    id = id,
    conversationId = conversationId,
    role = MessageRole.valueOf(role),
    content = content,
    intent = intent?.let { CoachIntent.valueOf(it) },
    timestamp = timestamp.toLocalDateTime(),
)

// ── Interface ─────────────────────────────────────────────────────────────────

interface CoachRepository {
    fun getMessages(): Flow<List<CoachMessage>>
    suspend fun sendUserMessage(text: String)
    suspend fun saveCoachResponse(text: String)
    suspend fun clearHistory()
}

// ── Implementation ────────────────────────────────────────────────────────────

@Singleton
class CoachRepositoryImpl @Inject constructor(
    private val coachMessageDao: CoachMessageDao,
) : CoachRepository {

    private val userId = "local_user"
    private val conversationId = "default_conversation"

    override fun getMessages(): Flow<List<CoachMessage>> =
        coachMessageDao.observeMessages(userId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun sendUserMessage(text: String) {
        coachMessageDao.insert(
            CoachMessageEntity(
                id = newId(),
                conversationId = conversationId,
                userId = userId,
                role = MessageRole.USER.name,
                content = text,
                intent = null,
                timestamp = LocalDateTime.now().toDbString(),
            )
        )
    }

    override suspend fun saveCoachResponse(text: String) {
        coachMessageDao.insert(
            CoachMessageEntity(
                id = newId(),
                conversationId = conversationId,
                userId = userId,
                role = MessageRole.ASSISTANT.name,
                content = text,
                intent = null,
                timestamp = LocalDateTime.now().toDbString(),
            )
        )
    }

    override suspend fun clearHistory() {
        coachMessageDao.deleteAll(userId)
    }
}
