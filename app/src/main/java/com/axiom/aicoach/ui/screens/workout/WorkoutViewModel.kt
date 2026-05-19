package com.axiom.aicoach.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiom.aicoach.data.local.dao.ExerciseDao
import com.axiom.aicoach.data.local.dao.ExerciseLogDao
import com.axiom.aicoach.data.local.dao.SetLogDao
import com.axiom.aicoach.data.local.dao.WorkoutDao
import com.axiom.aicoach.data.local.dao.WorkoutExerciseDao
import com.axiom.aicoach.data.local.dao.WorkoutPlanDao
import com.axiom.aicoach.data.local.dao.WorkoutSessionDao
import com.axiom.aicoach.data.local.entities.ExerciseEntity
import com.axiom.aicoach.data.local.entities.ExerciseLogEntity
import com.axiom.aicoach.data.local.entities.SetLogEntity
import com.axiom.aicoach.data.local.entities.WorkoutEntity
import com.axiom.aicoach.data.local.entities.WorkoutExerciseEntity
import com.axiom.aicoach.data.local.entities.WorkoutPlanEntity
import com.axiom.aicoach.data.local.entities.WorkoutSessionEntity
import com.axiom.aicoach.util.newId
import com.axiom.aicoach.util.toDbString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────────

data class WorkoutPlanUiState(
    val workouts: List<WorkoutWithDetails> = emptyList(),
    val planName: String = "My Workout Plan",
    val isLoading: Boolean = true,
)

data class WorkoutWithDetails(
    val id: String,
    val name: String,
    val dayOfWeek: String,
    val exerciseCount: Int,
    val estimatedMin: Int,
    val muscleGroups: List<String>,
    val isRestDay: Boolean = false,
)

data class WorkoutSessionUiState(
    val workoutName: String = "Workout",
    val currentExerciseIndex: Int = 0,
    val exercises: List<SessionExercise> = emptyList(),
    val restTimerSec: Int = 0,
    val isRestTimerActive: Boolean = false,
    val isComplete: Boolean = false,
    val totalSets: Int = 0,
    val completedSets: Int = 0,
)

data class SessionExercise(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float,
    val completedSets: List<Boolean> = emptyList(),
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val setLogDao: SetLogDao,
    private val exerciseDao: ExerciseDao,
) : ViewModel() {

    private val userId = "local_user"

    // ── Plan state ────────────────────────────────────────────────────────────

    private val _planUiState = MutableStateFlow(WorkoutPlanUiState(isLoading = true))
    val planUiState: StateFlow<WorkoutPlanUiState> = _planUiState.asStateFlow()

    // ── Session state ─────────────────────────────────────────────────────────

    private val _sessionUiState = MutableStateFlow(WorkoutSessionUiState())
    val sessionUiState: StateFlow<WorkoutSessionUiState> = _sessionUiState.asStateFlow()

    // In-progress session entity (kept so we can persist on finish).
    private var activeSessionId: String? = null
    // ExerciseLog ids keyed by SessionExercise index.
    private val exerciseLogIds = mutableMapOf<Int, String>()

    private var restTimerJob: Job? = null

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        viewModelScope.launch {
            ensureDemoDataAndLoad()
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun loadWorkoutsForPlan(planId: String) {
        viewModelScope.launch {
            _planUiState.update { it.copy(isLoading = true) }
            loadPlan(planId)
        }
    }

    fun startSession(planId: String, workoutId: String) {
        viewModelScope.launch {
            val workout = workoutDao.findById(workoutId) ?: return@launch
            val exerciseEntities = workoutExerciseDao.getForWorkout(workoutId)
            val exerciseIds = exerciseEntities.map { it.exerciseId }
            val allExercises = exerciseDao.getAll().associateBy { it.id }

            val sessionId = newId()
            activeSessionId = sessionId
            exerciseLogIds.clear()

            val sessionEntity = WorkoutSessionEntity(
                id = sessionId,
                userId = userId,
                workoutId = workoutId,
                workoutName = workout.name,
                startedAt = LocalDateTime.now().toDbString(),
                completedAt = null,
                durationMinutes = null,
                rating = null,
                rpe = null,
                notes = null,
            )
            workoutSessionDao.upsert(sessionEntity)

            // Create an ExerciseLog placeholder for each exercise.
            exerciseEntities.forEachIndexed { index, we ->
                val exercise = allExercises[we.exerciseId]
                val logId = newId()
                exerciseLogIds[index] = logId
                val logEntity = ExerciseLogEntity(
                    id = logId,
                    sessionId = sessionId,
                    exerciseId = we.exerciseId,
                    exerciseName = exercise?.name ?: "Exercise",
                )
                exerciseLogDao.upsert(logEntity)
            }

            val sessionExercises = exerciseEntities.mapIndexed { index, we ->
                val exercise = allExercises[we.exerciseId]
                SessionExercise(
                    id = we.id,
                    name = exercise?.name ?: "Exercise",
                    sets = we.sets,
                    reps = we.repsMax,
                    weightKg = 0f,
                    completedSets = List(we.sets) { false },
                )
            }

            val totalSets = sessionExercises.sumOf { it.sets }

            _sessionUiState.update {
                WorkoutSessionUiState(
                    workoutName = workout.name,
                    currentExerciseIndex = 0,
                    exercises = sessionExercises,
                    totalSets = totalSets,
                    completedSets = 0,
                    isComplete = false,
                )
            }
        }
    }

    fun logSet(exerciseIndex: Int, setIndex: Int, reps: Int, weightKg: Float) {
        viewModelScope.launch {
            val logId = exerciseLogIds[exerciseIndex] ?: return@launch
            val setEntity = SetLogEntity(
                id = newId(),
                exerciseLogId = logId,
                setNumber = setIndex + 1,
                weightKg = weightKg,
                reps = reps,
                durationSeconds = null,
                rpe = null,
                isCompleted = true,
            )
            setLogDao.upsert(setEntity)
        }
    }

    fun completeSet(exerciseIndex: Int, setIndex: Int) {
        val state = _sessionUiState.value
        val exercises = state.exercises.toMutableList()
        val exercise = exercises.getOrNull(exerciseIndex) ?: return

        val updatedCompleted = exercise.completedSets.toMutableList()
        if (setIndex < updatedCompleted.size) {
            updatedCompleted[setIndex] = true
        }
        exercises[exerciseIndex] = exercise.copy(completedSets = updatedCompleted)

        // Derive default weight from previous sets in this exercise (last used weight).
        val usedWeight = exercise.weightKg

        // Persist to DB
        logSet(exerciseIndex, setIndex, exercise.reps, usedWeight)

        val newCompletedCount = exercises.sumOf { ex -> ex.completedSets.count { it } }

        _sessionUiState.update {
            it.copy(
                exercises = exercises,
                completedSets = newCompletedCount,
            )
        }

        // Start rest timer after completing a set, using the workout's rest seconds.
        // Since SessionExercise doesn't carry restSeconds we default to 90s.
        startRestTimer(90)
    }

    fun nextExercise() {
        _sessionUiState.update { state ->
            val next = (state.currentExerciseIndex + 1).coerceAtMost(state.exercises.size - 1)
            state.copy(currentExerciseIndex = next)
        }
    }

    fun startRestTimer(seconds: Int = 90) {
        restTimerJob?.cancel()
        _sessionUiState.update { it.copy(restTimerSec = seconds, isRestTimerActive = true) }
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1_000)
                remaining--
                _sessionUiState.update { it.copy(restTimerSec = remaining) }
            }
            _sessionUiState.update { it.copy(isRestTimerActive = false, restTimerSec = 0) }
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        restTimerJob = null
        _sessionUiState.update { it.copy(isRestTimerActive = false, restTimerSec = 0) }
    }

    fun finishSession() {
        restTimerJob?.cancel()
        viewModelScope.launch {
            activeSessionId?.let { sessionId ->
                val existing = workoutSessionDao.findById(sessionId) ?: return@let
                val completed = existing.copy(
                    completedAt = LocalDateTime.now().toDbString(),
                )
                workoutSessionDao.upsert(completed)
            }
            _sessionUiState.update { it.copy(isComplete = true, isRestTimerActive = false) }
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private suspend fun ensureDemoDataAndLoad() {
        val existingPlans = workoutPlanDao.findById("demo_plan_1")
        if (existingPlans == null) {
            seedDemoData()
        }
        loadPlan("demo_plan_1")
    }

    private suspend fun loadPlan(planId: String) {
        val plan = workoutPlanDao.findById(planId)
        if (plan == null) {
            _planUiState.update {
                WorkoutPlanUiState(
                    workouts = emptyList(),
                    planName = "No Plan",
                    isLoading = false,
                )
            }
            return
        }

        val workouts = workoutDao.getForPlan(planId)
        val allExercises = exerciseDao.getAll().associateBy { it.id }

        val workoutDetails = workouts.map { workout ->
            val exercises = workoutExerciseDao.getForWorkout(workout.id)
            val muscleGroups = exercises.mapNotNull { we ->
                allExercises[we.exerciseId]?.primaryMuscle
                    ?.replaceFirstChar { it.uppercase() }
            }.distinct().take(3)

            WorkoutWithDetails(
                id = workout.id,
                name = workout.name,
                dayOfWeek = dayOfWeekName(workout.dayOfWeek),
                exerciseCount = exercises.size,
                estimatedMin = workout.estimatedDurationMin,
                muscleGroups = muscleGroups,
                isRestDay = false,
            )
        }

        _planUiState.update {
            WorkoutPlanUiState(
                workouts = workoutDetails,
                planName = plan.name,
                isLoading = false,
            )
        }
    }

    private suspend fun seedDemoData() {
        // Seed exercise catalogue entries needed by the plan.
        val exercises = listOf(
            ExerciseEntity(
                id = "ex_bench_press",
                name = "Bench Press",
                primaryMuscle = "CHEST",
                secondaryMusclesJson = "[\"TRICEPS\",\"SHOULDERS\"]",
                equipment = "BARBELL",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Lie on bench, lower bar to chest, press up.",
            ),
            ExerciseEntity(
                id = "ex_incline_db_press",
                name = "Incline Dumbbell Press",
                primaryMuscle = "CHEST",
                secondaryMusclesJson = "[\"TRICEPS\"]",
                equipment = "DUMBBELLS",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Set bench to 30-45°, press dumbbells overhead.",
            ),
            ExerciseEntity(
                id = "ex_overhead_press",
                name = "Overhead Press",
                primaryMuscle = "SHOULDERS",
                secondaryMusclesJson = "[\"TRICEPS\"]",
                equipment = "BARBELL",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Press bar from shoulders to overhead.",
            ),
            ExerciseEntity(
                id = "ex_lateral_raise",
                name = "Lateral Raise",
                primaryMuscle = "SHOULDERS",
                secondaryMusclesJson = "[]",
                equipment = "DUMBBELLS",
                difficulty = 1,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Raise dumbbells to shoulder height with straight arms.",
            ),
            ExerciseEntity(
                id = "ex_squat",
                name = "Barbell Squat",
                primaryMuscle = "QUADS",
                secondaryMusclesJson = "[\"GLUTES\",\"HAMSTRINGS\"]",
                equipment = "BARBELL",
                difficulty = 3,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Bar on traps, squat to parallel, drive up.",
            ),
            ExerciseEntity(
                id = "ex_romanian_dl",
                name = "Romanian Deadlift",
                primaryMuscle = "HAMSTRINGS",
                secondaryMusclesJson = "[\"GLUTES\",\"LOWER_BACK\"]",
                equipment = "BARBELL",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Hinge at hip, lower bar along legs, return.",
            ),
            ExerciseEntity(
                id = "ex_pull_up",
                name = "Pull-Up",
                primaryMuscle = "BACK",
                secondaryMusclesJson = "[\"BICEPS\"]",
                equipment = "NONE",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Dead hang, pull chest to bar.",
            ),
            ExerciseEntity(
                id = "ex_barbell_row",
                name = "Barbell Row",
                primaryMuscle = "BACK",
                secondaryMusclesJson = "[\"BICEPS\",\"REAR_DELTS\"]",
                equipment = "BARBELL",
                difficulty = 2,
                videoUrl = null,
                thumbnailUrl = null,
                formCuesJson = "[]",
                commonMistakesJson = "[]",
                substitutionsJson = "[]",
                contraindicationsJson = "[]",
                instructions = "Hinge over bar, row to lower chest.",
            ),
        )
        exerciseDao.insertAll(exercises)

        // Seed plan.
        val plan = WorkoutPlanEntity(
            id = "demo_plan_1",
            userId = userId,
            name = "Upper/Lower Split",
            description = "4-day upper/lower split for intermediate lifters.",
            goal = "BUILD_MUSCLE",
            durationWeeks = 8,
            daysPerWeek = 4,
            experienceLevel = "INTERMEDIATE",
            isActive = true,
            createdAt = LocalDateTime.now().toDbString(),
        )
        workoutPlanDao.upsert(plan)

        // Seed workouts.
        val workoutEntities = listOf(
            WorkoutEntity("w_upper_a", "demo_plan_1", "Upper Body Power", 1, 50),
            WorkoutEntity("w_lower_a", "demo_plan_1", "Lower Body Strength", 3, 45),
            WorkoutEntity("w_upper_b", "demo_plan_1", "Push Day", 5, 55),
            WorkoutEntity("w_lower_b", "demo_plan_1", "Pull Day", 6, 50),
        )
        workoutDao.insertAll(workoutEntities)

        // Seed exercises per workout.
        val workoutExercises = listOf(
            // Upper A
            WorkoutExerciseEntity("we1", "w_upper_a", "ex_bench_press", 4, 6, 10, 90, null, 1),
            WorkoutExerciseEntity("we2", "w_upper_a", "ex_incline_db_press", 3, 8, 12, 75, null, 2),
            WorkoutExerciseEntity("we3", "w_upper_a", "ex_overhead_press", 3, 8, 10, 90, null, 3),
            WorkoutExerciseEntity("we4", "w_upper_a", "ex_lateral_raise", 3, 12, 15, 60, null, 4),
            // Lower A
            WorkoutExerciseEntity("we5", "w_lower_a", "ex_squat", 4, 5, 8, 120, null, 1),
            WorkoutExerciseEntity("we6", "w_lower_a", "ex_romanian_dl", 3, 8, 10, 90, null, 2),
            WorkoutExerciseEntity("we7", "w_lower_a", "ex_lateral_raise", 3, 12, 15, 60, null, 3),
            // Upper B
            WorkoutExerciseEntity("we8", "w_upper_b", "ex_bench_press", 4, 8, 12, 75, null, 1),
            WorkoutExerciseEntity("we9", "w_upper_b", "ex_overhead_press", 3, 8, 12, 75, null, 2),
            WorkoutExerciseEntity("we10", "w_upper_b", "ex_lateral_raise", 4, 12, 15, 60, null, 3),
            // Lower B
            WorkoutExerciseEntity("we11", "w_lower_b", "ex_pull_up", 4, 6, 10, 90, null, 1),
            WorkoutExerciseEntity("we12", "w_lower_b", "ex_barbell_row", 4, 6, 10, 90, null, 2),
            WorkoutExerciseEntity("we13", "w_lower_b", "ex_romanian_dl", 3, 8, 12, 75, null, 3),
        )
        workoutExerciseDao.insertAll(workoutExercises)
    }

    private fun dayOfWeekName(dayOfWeek: Int): String = when (dayOfWeek) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        6 -> "Saturday"
        7 -> "Sunday"
        else -> "Day $dayOfWeek"
    }
}
