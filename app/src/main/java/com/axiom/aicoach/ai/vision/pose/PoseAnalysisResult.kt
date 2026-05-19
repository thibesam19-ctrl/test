package com.axiom.aicoach.ai.vision.pose

data class PoseAnalysisResult(
    val exerciseType: ExerciseType,
    val repCount: Int,
    val formScore: Int,             // 0–100
    val feedback: List<FormFeedback>,
    val jointAngles: Map<JointPair, Float>,  // for debugging/display
    val repPhase: RepPhase,
)

data class FormFeedback(
    val message: String,
    val severity: FeedbackSeverity,
    val bodyPart: String,
)

enum class ExerciseType { SQUAT, PUSH_UP, BICEP_CURL, UNKNOWN }
enum class RepPhase { UP, DOWN, TRANSITION }
enum class FeedbackSeverity { GOOD, WARNING, ERROR }

data class JointPair(val joint1: String, val joint2: String, val joint3: String)
