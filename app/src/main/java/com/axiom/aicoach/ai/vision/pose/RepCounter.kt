package com.axiom.aicoach.ai.vision.pose

class RepCounter(private val exerciseType: ExerciseType) {

    private var currentPhase = RepPhase.UP
    private var _count = 0
    val count: Int get() = _count

    /**
     * Updates the rep counter based on the current joint angles.
     *
     * Exercise phase logic:
     *  SQUAT      – key angle: "hipAngle" (knee-hip-shoulder)
     *               DOWN when < 90°, UP when > 150°
     *  PUSH_UP    – key angle: "elbowAngle" (wrist-elbow-shoulder)
     *               DOWN when < 90°, UP when > 160°
     *  BICEP_CURL – key angle: "elbowAngle" (shoulder-elbow-wrist)
     *               UP when < 50°, DOWN when > 150°
     *
     * @return the new [RepPhase] after evaluating the angles
     */
    fun update(angles: Map<String, Float>): RepPhase {
        val newPhase: RepPhase = when (exerciseType) {
            ExerciseType.SQUAT -> {
                val hipAngle = angles["hipAngle"] ?: return currentPhase
                when {
                    hipAngle < 90f  -> RepPhase.DOWN
                    hipAngle > 150f -> RepPhase.UP
                    else            -> RepPhase.TRANSITION
                }
            }
            ExerciseType.PUSH_UP -> {
                val elbowAngle = angles["elbowAngle"] ?: return currentPhase
                when {
                    elbowAngle < 90f  -> RepPhase.DOWN
                    elbowAngle > 160f -> RepPhase.UP
                    else              -> RepPhase.TRANSITION
                }
            }
            ExerciseType.BICEP_CURL -> {
                val elbowAngle = angles["elbowAngle"] ?: return currentPhase
                when {
                    elbowAngle < 50f  -> RepPhase.UP
                    elbowAngle > 150f -> RepPhase.DOWN
                    else              -> RepPhase.TRANSITION
                }
            }
            ExerciseType.UNKNOWN -> return currentPhase
        }

        // Count a rep on transition from DOWN → UP (for squat/push-up)
        // or from UP → DOWN (for bicep curl, i.e., returning to extended)
        if (exerciseType == ExerciseType.BICEP_CURL) {
            if (currentPhase == RepPhase.UP && newPhase == RepPhase.DOWN) {
                _count++
            }
        } else {
            if (currentPhase == RepPhase.DOWN && newPhase == RepPhase.UP) {
                _count++
            }
        }

        if (newPhase != RepPhase.TRANSITION) {
            currentPhase = newPhase
        }
        return newPhase
    }

    fun reset() {
        _count = 0
        currentPhase = RepPhase.UP
    }
}
