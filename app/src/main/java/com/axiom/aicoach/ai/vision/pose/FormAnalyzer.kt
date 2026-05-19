package com.axiom.aicoach.ai.vision.pose

import android.graphics.PointF
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class FormAnalyzer {

    // ── Public analysis functions ─────────────────────────────────────────────

    fun analyzeSquat(landmarks: Map<Int, PoseLandmark>): List<FormFeedback> {
        val feedback = mutableListOf<FormFeedback>()

        val leftHip    = landmarks[PoseLandmark.LEFT_HIP]
        val rightHip   = landmarks[PoseLandmark.RIGHT_HIP]
        val leftKnee   = landmarks[PoseLandmark.LEFT_KNEE]
        val rightKnee  = landmarks[PoseLandmark.RIGHT_KNEE]
        val leftAnkle  = landmarks[PoseLandmark.LEFT_ANKLE]
        val rightAnkle = landmarks[PoseLandmark.RIGHT_ANKLE]
        val leftShoulder  = landmarks[PoseLandmark.LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmark.RIGHT_SHOULDER]
        val leftToe    = landmarks[PoseLandmark.LEFT_FOOT_INDEX]
        val rightToe   = landmarks[PoseLandmark.RIGHT_FOOT_INDEX]

        // 1. Knee tracking over toes
        if (leftKnee != null && leftToe != null) {
            val kneeX = leftKnee.position.x
            val toeX  = leftToe.position.x
            if (abs(kneeX - toeX) > 40f) {
                feedback += FormFeedback(
                    message  = "Left knee is caving in — drive it out over your toes",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Left Knee",
                )
            }
        }
        if (rightKnee != null && rightToe != null) {
            val kneeX = rightKnee.position.x
            val toeX  = rightToe.position.x
            if (abs(kneeX - toeX) > 40f) {
                feedback += FormFeedback(
                    message  = "Right knee is caving in — drive it out over your toes",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Right Knee",
                )
            }
        }

        // 2. Back straightness (shoulder-hip-knee angle should be close to 180° for upright torso)
        if (leftShoulder != null && leftHip != null && leftKnee != null) {
            val backAngle = calculateAngle(
                leftShoulder.position.toPointF(),
                leftHip.position.toPointF(),
                leftKnee.position.toPointF(),
            )
            if (backAngle < 150f) {
                feedback += FormFeedback(
                    message  = "Keep your chest up — your torso is leaning too far forward",
                    severity = if (backAngle < 120f) FeedbackSeverity.ERROR else FeedbackSeverity.WARNING,
                    bodyPart = "Back",
                )
            }
        }

        // 3. Depth (hip should reach at or below knee level for good depth)
        if (leftHip != null && leftKnee != null) {
            if (leftHip.position.y < leftKnee.position.y) {
                // In image coordinates y increases downward, so hip y < knee y = hip is ABOVE knee
                feedback += FormFeedback(
                    message  = "Go deeper — aim for thighs parallel to the floor",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Hips",
                )
            } else {
                feedback += FormFeedback(
                    message  = "Great squat depth!",
                    severity = FeedbackSeverity.GOOD,
                    bodyPart = "Hips",
                )
            }
        }

        // 4. Feet shoulder-width apart
        if (leftAnkle != null && rightAnkle != null &&
            leftShoulder != null && rightShoulder != null
        ) {
            val ankleWidth    = abs(leftAnkle.position.x - rightAnkle.position.x)
            val shoulderWidth = abs(leftShoulder.position.x - rightShoulder.position.x)
            if (ankleWidth < shoulderWidth * 0.7f) {
                feedback += FormFeedback(
                    message  = "Widen your stance to about shoulder-width",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Stance",
                )
            }
        }

        return feedback
    }

    fun analyzePushUp(landmarks: Map<Int, PoseLandmark>): List<FormFeedback> {
        val feedback = mutableListOf<FormFeedback>()

        val leftShoulder  = landmarks[PoseLandmark.LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmark.RIGHT_SHOULDER]
        val leftElbow     = landmarks[PoseLandmark.LEFT_ELBOW]
        val rightElbow    = landmarks[PoseLandmark.RIGHT_ELBOW]
        val leftWrist     = landmarks[PoseLandmark.LEFT_WRIST]
        val rightWrist    = landmarks[PoseLandmark.RIGHT_WRIST]
        val leftHip       = landmarks[PoseLandmark.LEFT_HIP]
        val rightHip      = landmarks[PoseLandmark.RIGHT_HIP]
        val leftAnkle     = landmarks[PoseLandmark.LEFT_ANKLE]
        val rightAnkle    = landmarks[PoseLandmark.RIGHT_ANKLE]
        val nose          = landmarks[PoseLandmark.NOSE]

        // 1. Body straight line — shoulder-hip-ankle angle should be ~180°
        if (leftShoulder != null && leftHip != null && leftAnkle != null) {
            val bodyAngle = calculateAngle(
                leftShoulder.position.toPointF(),
                leftHip.position.toPointF(),
                leftAnkle.position.toPointF(),
            )
            if (bodyAngle < 160f) {
                feedback += FormFeedback(
                    message  = if (bodyAngle < 140f)
                        "Your hips are dropping — engage your core and keep a straight line"
                    else
                        "Slight hip drop — brace your core",
                    severity = if (bodyAngle < 140f) FeedbackSeverity.ERROR else FeedbackSeverity.WARNING,
                    bodyPart = "Core / Hips",
                )
            } else {
                feedback += FormFeedback(
                    message  = "Great body alignment!",
                    severity = FeedbackSeverity.GOOD,
                    bodyPart = "Core",
                )
            }
        }

        // 2. Elbow flare — elbow should be roughly 45° from body
        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
            val elbowAngle = calculateAngle(
                leftWrist.position.toPointF(),
                leftElbow.position.toPointF(),
                leftShoulder.position.toPointF(),
            )
            if (elbowAngle > 80f) {
                feedback += FormFeedback(
                    message  = "Elbows are flaring too wide — tuck them to ~45° from your body",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Elbows",
                )
            }
        }

        // 3. Head / neck neutral — nose should be roughly in line with shoulders (not drooping forward)
        if (nose != null && leftShoulder != null && rightShoulder != null) {
            val shoulderMidX = (leftShoulder.position.x + rightShoulder.position.x) / 2f
            if (abs(nose.position.x - shoulderMidX) > 60f) {
                feedback += FormFeedback(
                    message  = "Keep your head neutral — look slightly ahead of your hands",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Neck",
                )
            }
        }

        // 4. Depth — chest should approach floor (elbow angle < 90° = good)
        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
            val elbowAngle = calculateAngle(
                leftShoulder.position.toPointF(),
                leftElbow.position.toPointF(),
                leftWrist.position.toPointF(),
            )
            if (elbowAngle > 110f) {
                feedback += FormFeedback(
                    message  = "Lower your chest closer to the floor for full range of motion",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Depth",
                )
            }
        }

        return feedback
    }

    fun analyzeBicepCurl(landmarks: Map<Int, PoseLandmark>): List<FormFeedback> {
        val feedback = mutableListOf<FormFeedback>()

        val leftShoulder  = landmarks[PoseLandmark.LEFT_SHOULDER]
        val rightShoulder = landmarks[PoseLandmark.RIGHT_SHOULDER]
        val leftElbow     = landmarks[PoseLandmark.LEFT_ELBOW]
        val rightElbow    = landmarks[PoseLandmark.RIGHT_ELBOW]
        val leftWrist     = landmarks[PoseLandmark.LEFT_WRIST]
        val rightWrist    = landmarks[PoseLandmark.RIGHT_WRIST]
        val leftHip       = landmarks[PoseLandmark.LEFT_HIP]
        val rightHip      = landmarks[PoseLandmark.RIGHT_HIP]

        // 1. Elbow staying at side — elbow x should be close to hip x
        if (leftElbow != null && leftHip != null) {
            val elbowDrift = abs(leftElbow.position.x - leftHip.position.x)
            if (elbowDrift > 50f) {
                feedback += FormFeedback(
                    message  = "Keep your left elbow pinned at your side — don't let it drift forward",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Left Elbow",
                )
            }
        }
        if (rightElbow != null && rightHip != null) {
            val elbowDrift = abs(rightElbow.position.x - rightHip.position.x)
            if (elbowDrift > 50f) {
                feedback += FormFeedback(
                    message  = "Keep your right elbow pinned at your side — don't let it drift forward",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Right Elbow",
                )
            }
        }

        // 2. Full range of motion — check elbow extension at the bottom (angle > 150°)
        if (leftShoulder != null && leftElbow != null && leftWrist != null) {
            val elbowAngle = calculateAngle(
                leftShoulder.position.toPointF(),
                leftElbow.position.toPointF(),
                leftWrist.position.toPointF(),
            )
            if (elbowAngle < 140f) {
                feedback += FormFeedback(
                    message  = "Fully extend your arm at the bottom for complete range of motion",
                    severity = FeedbackSeverity.WARNING,
                    bodyPart = "Left Elbow",
                )
            }
        }

        // 3. No swinging — hips should remain still (compare left/right hip Y positions)
        if (leftHip != null && rightHip != null) {
            val hipTilt = abs(leftHip.position.y - rightHip.position.y)
            if (hipTilt > 30f) {
                feedback += FormFeedback(
                    message  = "Avoid swinging — keep your hips level and stable",
                    severity = FeedbackSeverity.ERROR,
                    bodyPart = "Hips",
                )
            }
        }

        // 4. Good form indicator
        if (feedback.none { it.severity == FeedbackSeverity.ERROR || it.severity == FeedbackSeverity.WARNING }) {
            feedback += FormFeedback(
                message  = "Great curl form — keep it up!",
                severity = FeedbackSeverity.GOOD,
                bodyPart = "Overall",
            )
        }

        return feedback
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Calculates the angle (in degrees) at vertex [b] formed by points [a], [b], [c].
     */
    fun calculateAngle(a: PointF, b: PointF, c: PointF): Float {
        val radians = atan2(
            (c.y - b.y).toDouble(),
            (c.x - b.x).toDouble(),
        ) - atan2(
            (a.y - b.y).toDouble(),
            (a.x - b.x).toDouble(),
        )
        var degrees = Math.toDegrees(radians)
        if (degrees < 0) degrees += 360.0
        if (degrees > 180) degrees = 360.0 - degrees
        return degrees.toFloat()
    }

    // ── Extension ─────────────────────────────────────────────────────────────

    private fun android.graphics.PointF.toPointF(): PointF = PointF(x, y)
}

// Extension to convert ML Kit PointF3D to android.graphics.PointF
private fun com.google.mlkit.vision.common.PointF3D.toPointF(): android.graphics.PointF =
    android.graphics.PointF(x, y)
