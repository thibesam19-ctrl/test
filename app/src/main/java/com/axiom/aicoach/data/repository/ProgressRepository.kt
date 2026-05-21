package com.axiom.aicoach.data.repository

import com.axiom.aicoach.data.local.dao.BodyMeasurementDao
import com.axiom.aicoach.data.local.dao.ProgressPhotoDao
import com.axiom.aicoach.data.local.dao.WeightLogDao
import com.axiom.aicoach.data.local.entities.BodyMeasurementEntity
import com.axiom.aicoach.data.local.entities.ProgressPhotoEntity
import com.axiom.aicoach.data.local.entities.WeightLogEntity
import com.axiom.aicoach.domain.model.BodyMeasurement
import com.axiom.aicoach.domain.model.PhotoAngle
import com.axiom.aicoach.domain.model.ProgressPhoto
import com.axiom.aicoach.domain.model.WeightLog
import com.axiom.aicoach.security.UserSession
import com.axiom.aicoach.util.newId
import com.axiom.aicoach.util.toDbString
import com.axiom.aicoach.util.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

// ── Mapping ───────────────────────────────────────────────────────────────────

private fun WeightLogEntity.toDomain() = WeightLog(
    id = id,
    userId = userId,
    weightKg = weightKg,
    loggedAt = loggedAt.toLocalDateTime(),
    notes = notes,
)

private fun WeightLog.toEntity() = WeightLogEntity(
    id = id,
    userId = userId,
    weightKg = weightKg,
    loggedAt = loggedAt.toDbString(),
    notes = notes,
)

private fun BodyMeasurementEntity.toDomain() = BodyMeasurement(
    id = id,
    userId = userId,
    chestCm = chestCm,
    waistCm = waistCm,
    hipsCm = hipsCm,
    leftArmCm = leftArmCm,
    rightArmCm = rightArmCm,
    leftThighCm = leftThighCm,
    rightThighCm = rightThighCm,
    neckCm = neckCm,
    measuredAt = measuredAt.toLocalDateTime(),
)

private fun BodyMeasurement.toEntity() = BodyMeasurementEntity(
    id = id,
    userId = userId,
    chestCm = chestCm,
    waistCm = waistCm,
    hipsCm = hipsCm,
    leftArmCm = leftArmCm,
    rightArmCm = rightArmCm,
    leftThighCm = leftThighCm,
    rightThighCm = rightThighCm,
    neckCm = neckCm,
    measuredAt = measuredAt.toDbString(),
)

private fun ProgressPhotoEntity.toDomain() = ProgressPhoto(
    id = id,
    userId = userId,
    localPath = localPath,
    angle = PhotoAngle.valueOf(angle),
    takenAt = takenAt.toLocalDateTime(),
    notes = notes,
)

private fun ProgressPhoto.toEntity() = ProgressPhotoEntity(
    id = id,
    userId = userId,
    localPath = localPath,
    angle = angle.name,
    takenAt = takenAt.toDbString(),
    notes = notes,
)

// ── Interface ─────────────────────────────────────────────────────────────────

interface ProgressRepository {
    fun getWeightLogs(): Flow<List<WeightLog>>
    suspend fun logWeight(kg: Float, note: String = "")
    fun getBodyMeasurements(): Flow<List<BodyMeasurement>>
    suspend fun saveMeasurement(measurement: BodyMeasurement)
    fun getProgressPhotos(): Flow<List<ProgressPhoto>>
    suspend fun savePhoto(photo: ProgressPhoto)
    fun getLatestWeight(): Flow<WeightLog?>
}

// ── Implementation ────────────────────────────────────────────────────────────

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val weightLogDao: WeightLogDao,
    private val bodyMeasurementDao: BodyMeasurementDao,
    private val progressPhotoDao: ProgressPhotoDao,
    private val userSession: UserSession,
) : ProgressRepository {

    private val userId: String get() = userSession.userId

    override fun getWeightLogs(): Flow<List<WeightLog>> =
        weightLogDao.observeAll(userId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun logWeight(kg: Float, note: String) {
        weightLogDao.insert(
            WeightLogEntity(
                id = newId(),
                userId = userId,
                weightKg = kg,
                loggedAt = LocalDateTime.now().toDbString(),
                notes = note.ifBlank { null },
            )
        )
    }

    override fun getBodyMeasurements(): Flow<List<BodyMeasurement>> =
        bodyMeasurementDao.observeAll(userId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun saveMeasurement(measurement: BodyMeasurement) {
        bodyMeasurementDao.insert(measurement.toEntity())
    }

    override fun getProgressPhotos(): Flow<List<ProgressPhoto>> =
        progressPhotoDao.observeAll(userId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun savePhoto(photo: ProgressPhoto) {
        progressPhotoDao.insert(photo.toEntity())
    }

    override fun getLatestWeight(): Flow<WeightLog?> = flow {
        emit(weightLogDao.getLatest(userId)?.toDomain())
    }
}
