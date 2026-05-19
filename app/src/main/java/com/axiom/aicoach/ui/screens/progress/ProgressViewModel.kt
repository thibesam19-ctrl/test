package com.axiom.aicoach.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.BodyMeasurementDao
import com.axiom.aicoach.data.local.dao.ProgressPhotoDao
import com.axiom.aicoach.data.local.dao.WeightLogDao
import com.axiom.aicoach.data.local.entities.BodyMeasurementEntity
import com.axiom.aicoach.data.local.entities.WeightLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class ProgressUiState(
    val currentWeightKg: Float? = null,
    val startWeightKg: Float? = null,
    val weightLogs: List<WeightLogUi> = emptyList(),
    val bodyMeasurements: List<BodyMeasurementUi> = emptyList(),
    val progressPhotos: List<ProgressPhotoUi> = emptyList(),
    val totalWeightLost: Float = 0f,
    val weeklyChange: Float = 0f,
    val isLoading: Boolean = true,
)

data class WeightLogUi(val id: String, val date: String, val weightKg: Float, val note: String)
data class BodyMeasurementUi(
    val id: String,
    val date: String,
    val waistCm: Float?,
    val hipCm: Float?,
    val chestCm: Float?,
    val armCm: Float?,
    val thighCm: Float?,
)
data class ProgressPhotoUi(val id: String, val date: String, val angle: String, val uriPath: String)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val weightLogDao: WeightLogDao,
    private val bodyMeasurementDao: BodyMeasurementDao,
    private val progressPhotoDao: ProgressPhotoDao,
) : ViewModel() {

    private val userId = "demo_user"
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM d")

    val uiState: StateFlow<ProgressUiState> = combine(
        weightLogDao.observeAll(userId),
        bodyMeasurementDao.observeAll(userId),
        progressPhotoDao.observeAll(userId),
    ) { weightLogs, measurements, photos ->

        val weightLogUis = weightLogs.map { entity ->
            WeightLogUi(
                id = entity.id,
                date = formatDate(entity.loggedAt),
                weightKg = entity.weightKg,
                note = entity.notes.orEmpty(),
            )
        }

        val currentWeight = weightLogs.firstOrNull()?.weightKg
        val startWeight = weightLogs.lastOrNull()?.weightKg
        val totalLost = if (startWeight != null && currentWeight != null) {
            startWeight - currentWeight
        } else 0f

        // Weekly change: compare most recent entry vs entry from ~7 days ago
        val now = LocalDateTime.now()
        val weekAgoThreshold = now.minusDays(10).toString()
        val recentEntry = weightLogs.firstOrNull()?.weightKg
        val weekAgoEntry = weightLogs.firstOrNull { it.loggedAt <= weekAgoThreshold }?.weightKg
        val weeklyChange = if (recentEntry != null && weekAgoEntry != null) {
            recentEntry - weekAgoEntry
        } else 0f

        val measurementUis = measurements.map { m ->
            BodyMeasurementUi(
                id = m.id,
                date = formatDate(m.measuredAt),
                waistCm = m.waistCm,
                hipCm = m.hipsCm,
                chestCm = m.chestCm,
                armCm = m.leftArmCm,
                thighCm = m.leftThighCm,
            )
        }

        val photoUis = photos.map { p ->
            ProgressPhotoUi(
                id = p.id,
                date = formatDate(p.takenAt),
                angle = p.angle,
                uriPath = p.localPath,
            )
        }

        ProgressUiState(
            currentWeightKg = currentWeight,
            startWeightKg = startWeight,
            weightLogs = weightLogUis,
            bodyMeasurements = measurementUis,
            progressPhotos = photoUis,
            totalWeightLost = totalLost,
            weeklyChange = weeklyChange,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState(isLoading = true),
    )

    init {
        viewModelScope.launch {
            seedWeightHistoryIfEmpty()
        }
    }

    private suspend fun seedWeightHistoryIfEmpty() {
        val recent = weightLogDao.getRecent(userId, 1)
        if (recent.isEmpty()) {
            // Seed 30 days of demo weight data (gentle downward trend)
            val startWeight = 82.4f
            val seedEntries = (30 downTo 0).map { daysAgo ->
                val noise = (-0.3f..0.3f).random()
                val trend = daysAgo * (4.2f / 30f)   // 4.2 kg total over 30 days
                val kg = (startWeight - (4.2f - trend) + noise)
                    .coerceIn(76f, 84f)
                    .let { Math.round(it * 10) / 10f }
                WeightLogEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    weightKg = kg,
                    loggedAt = LocalDateTime.now().minusDays(daysAgo.toLong()).toString(),
                    notes = null,
                )
            }
            seedEntries.forEach { weightLogDao.insert(it) }
        }
    }

    fun logWeight(kg: Float, note: String = "") {
        viewModelScope.launch {
            val entity = WeightLogEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                weightKg = kg,
                loggedAt = LocalDateTime.now().toString(),
                notes = note.ifBlank { null },
            )
            weightLogDao.insert(entity)
        }
    }

    fun deleteWeight(id: String) {
        viewModelScope.launch {
            weightLogDao.delete(id)
        }
    }

    fun saveMeasurement(
        waist: Float?,
        hip: Float?,
        chest: Float?,
        arm: Float?,
        thigh: Float?,
    ) {
        viewModelScope.launch {
            val entity = BodyMeasurementEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                chestCm = chest,
                waistCm = waist,
                hipsCm = hip,
                leftArmCm = arm,
                rightArmCm = arm,
                leftThighCm = thigh,
                rightThighCm = thigh,
                neckCm = null,
                measuredAt = LocalDateTime.now().toString(),
            )
            bodyMeasurementDao.insert(entity)
        }
    }

    private fun formatDate(isoDateTime: String): String {
        return runCatching {
            val dt = LocalDateTime.parse(isoDateTime)
            dt.toLocalDate().format(displayFormatter)
        }.getOrElse { isoDateTime.take(10) }
    }

    private fun ClosedFloatingPointRange<Float>.random(): Float {
        val range = endInclusive - start
        return start + (Math.random() * range).toFloat()
    }
}
