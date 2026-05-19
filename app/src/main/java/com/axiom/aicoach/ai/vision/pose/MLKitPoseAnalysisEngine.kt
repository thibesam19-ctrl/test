package com.axiom.aicoach.ai.vision.pose

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MLKitPoseAnalysisEngine @Inject constructor() : PoseAnalysisEngine {

    private val poseDetector = PoseDetection.getClient(
        AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
    )

    @Volatile private var exerciseType = ExerciseType.SQUAT
    @Volatile private var repCounter   = RepCounter(ExerciseType.SQUAT)
    private val formAnalyzer           = FormAnalyzer()

    private val _repCount  = MutableStateFlow(0)
    private val _formScore = MutableStateFlow(100)
    private val _feedback  = MutableStateFlow<List<FormFeedback>>(emptyList())

    override val repCount:  StateFlow<Int>              = _repCount.asStateFlow()
    override val formScore: StateFlow<Int>              = _formScore.asStateFlow()
    override val feedback:  StateFlow<List<FormFeedback>> = _feedback.asStateFlow()

    // ── PoseAnalysisEngine ────────────────────────────────────────────────────

    override fun processFrame(imageProxy: ImageProxy): Flow<PoseAnalysisResult> = flow {
        try {
            val mediaImage = imageProxy.image ?: return@flow
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees,
            )

            val pose = poseDetector.process(inputImage).await()
            val landmarksMap: Map<Int, PoseLandmark> =
                pose.allPoseLandmarks.associateBy { it.landmarkType }

            if (landmarksMap.isEmpty()) return@flow

            // ── Calculate key angles ──────────────────────────────────────

            val angles = mutableMapOf<String, Float>()

            when (exerciseType) {
                ExerciseType.SQUAT -> {
                    val hip      = landmarksMap[PoseLandmark.LEFT_HIP]
                    val knee     = landmarksMap[PoseLandmark.LEFT_KNEE]
                    val shoulder = landmarksMap[PoseLandmark.LEFT_SHOULDER]
                    if (hip != null && knee != null && shoulder != null) {
                        angles["hipAngle"] = formAnalyzer.calculateAngle(
                            knee.position.toPointF(),
                            hip.position.toPointF(),
                            shoulder.position.toPointF(),
                        )
                    }
                }
                ExerciseType.PUSH_UP -> {
                    val wrist    = landmarksMap[PoseLandmark.LEFT_WRIST]
                    val elbow    = landmarksMap[PoseLandmark.LEFT_ELBOW]
                    val shoulder = landmarksMap[PoseLandmark.LEFT_SHOULDER]
                    if (wrist != null && elbow != null && shoulder != null) {
                        angles["elbowAngle"] = formAnalyzer.calculateAngle(
                            wrist.position.toPointF(),
                            elbow.position.toPointF(),
                            shoulder.position.toPointF(),
                        )
                    }
                }
                ExerciseType.BICEP_CURL -> {
                    val shoulder = landmarksMap[PoseLandmark.LEFT_SHOULDER]
                    val elbow    = landmarksMap[PoseLandmark.LEFT_ELBOW]
                    val wrist    = landmarksMap[PoseLandmark.LEFT_WRIST]
                    if (shoulder != null && elbow != null && wrist != null) {
                        angles["elbowAngle"] = formAnalyzer.calculateAngle(
                            shoulder.position.toPointF(),
                            elbow.position.toPointF(),
                            wrist.position.toPointF(),
                        )
                    }
                }
                ExerciseType.UNKNOWN -> Unit
            }

            // ── Update rep counter ────────────────────────────────────────

            val phase = repCounter.update(angles)
            _repCount.value = repCounter.count

            // ── Run form analysis ─────────────────────────────────────────

            val formFeedback = when (exerciseType) {
                ExerciseType.SQUAT      -> formAnalyzer.analyzeSquat(landmarksMap)
                ExerciseType.PUSH_UP    -> formAnalyzer.analyzePushUp(landmarksMap)
                ExerciseType.BICEP_CURL -> formAnalyzer.analyzeBicepCurl(landmarksMap)
                ExerciseType.UNKNOWN    -> emptyList()
            }
            _feedback.value = formFeedback

            // ── Calculate form score ──────────────────────────────────────

            val errorCount   = formFeedback.count { it.severity == FeedbackSeverity.ERROR }
            val warningCount = formFeedback.count { it.severity == FeedbackSeverity.WARNING }
            val score = (100 - errorCount * 20 - warningCount * 5).coerceIn(0, 100)
            _formScore.value = score

            // ── Build JointPair angle map for display ─────────────────────

            val jointAngles = angles.entries.associate { (key, value) ->
                JointPair(key, key, key) to value
            }

            emit(
                PoseAnalysisResult(
                    exerciseType = exerciseType,
                    repCount     = repCounter.count,
                    formScore    = score,
                    feedback     = formFeedback,
                    jointAngles  = jointAngles,
                    repPhase     = phase,
                )
            )
        } finally {
            imageProxy.close()
        }
    }

    override fun setExerciseType(type: ExerciseType) {
        exerciseType = type
        repCounter   = RepCounter(type)
        repCounter.reset()
        _repCount.value  = 0
        _formScore.value = 100
        _feedback.value  = emptyList()
    }

    override fun reset() {
        repCounter.reset()
        _repCount.value  = 0
        _formScore.value = 100
        _feedback.value  = emptyList()
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun android.graphics.PointF.toPointF(): android.graphics.PointF = this
}

// Extension: convert ML Kit PointF3D → android.graphics.PointF
private fun com.google.mlkit.vision.common.PointF3D.toPointF(): android.graphics.PointF =
    android.graphics.PointF(x, y)
