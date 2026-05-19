package com.axiom.aicoach.data.repository

import com.axiom.aicoach.data.local.dao.*
import com.axiom.aicoach.data.local.entities.*
import com.axiom.aicoach.domain.model.*
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

private fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    primaryMuscle = MuscleGroup.valueOf(primaryMuscle),
    secondaryMuscles = secondaryMusclesJson.split(",").filter { it.isNotBlank() }.map { MuscleGroup.valueOf(it.trim()) },
    equipment = Equipment.valueOf(equipment),
    difficulty = difficulty,
    videoUrl = videoUrl,
    thumbnailUrl = thumbnailUrl,
    formCues = formCuesJson.split("|").filter { it.isNotBlank() },
    commonMistakes = commonMistakesJson.split("|").filter { it.isNotBlank() },
    substitutions = substitutionsJson.split("|").filter { it.isNotBlank() },
    contraindications = contraindicationsJson.split("|").filter { it.isNotBlank() },
    instructions = instructions,
)

private fun WorkoutPlanEntity.toDomain(workouts: List<Workout>) = WorkoutPlan(
    id = id,
    userId = userId,
    name = name,
    description = description,
    goal = FitnessGoal.valueOf(goal),
    durationWeeks = durationWeeks,
    daysPerWeek = daysPerWeek,
    experienceLevel = ExperienceLevel.valueOf(experienceLevel),
    workouts = workouts,
    isActive = isActive,
    createdAt = createdAt.toLocalDateTime(),
)

private fun WorkoutEntity.toDomain(exercises: List<WorkoutExercise>) = Workout(
    id = id,
    planId = planId,
    name = name,
    dayOfWeek = dayOfWeek,
    exercises = exercises,
    estimatedDurationMin = estimatedDurationMin,
)

private fun WorkoutExerciseEntity.toDomain(exercise: Exercise) = WorkoutExercise(
    id = id,
    workoutId = workoutId,
    exercise = exercise,
    sets = sets,
    repsMin = repsMin,
    repsMax = repsMax,
    restSeconds = restSeconds,
    notes = notes,
    order = order,
)

private fun WorkoutSessionEntity.toDomain(exerciseLogs: List<ExerciseLog>) = WorkoutSession(
    id = id,
    userId = userId,
    workoutId = workoutId,
    workoutName = workoutName,
    startedAt = startedAt.toLocalDateTime(),
    completedAt = completedAt?.toLocalDateTime(),
    exerciseLogs = exerciseLogs,
    durationMinutes = durationMinutes,
    rating = rating,
    rpe = rpe,
    notes = notes,
)

private fun ExerciseLogEntity.toDomain(sets: List<SetLog>) = ExerciseLog(
    id = id,
    sessionId = sessionId,
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    sets = sets,
)

private fun SetLogEntity.toDomain() = SetLog(
    id = id,
    exerciseLogId = exerciseLogId,
    setNumber = setNumber,
    weightKg = weightKg,
    reps = reps,
    durationSeconds = durationSeconds,
    rpe = rpe,
    isCompleted = isCompleted,
)

private fun PersonalRecordEntity.toDomain() = PersonalRecord(
    id = id,
    userId = userId,
    exerciseId = exerciseId,
    exerciseName = exerciseName,
    weightKg = weightKg,
    reps = reps,
    oneRepMaxKg = oneRepMaxKg,
    achievedAt = achievedAt.toLocalDateTime(),
)

// ── Interface ─────────────────────────────────────────────────────────────────

interface WorkoutRepository {
    fun getWorkoutPlans(): Flow<List<WorkoutPlan>>
    fun getWorkoutPlan(planId: String): Flow<WorkoutPlan?>
    fun getWorkoutsForPlan(planId: String): Flow<List<Workout>>
    suspend fun startSession(planId: String, workoutId: String): String
    fun getSession(sessionId: String): Flow<WorkoutSession?>
    suspend fun logSet(sessionId: String, exerciseId: String, setNum: Int, repsActual: Int, weightKg: Float)
    suspend fun finishSession(sessionId: String)
    fun getRecentSessions(limit: Int = 10): Flow<List<WorkoutSession>>
    fun getPersonalRecords(): Flow<List<PersonalRecord>>
}

// ── Implementation ────────────────────────────────────────────────────────────

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val sessionDao: WorkoutSessionDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val setLogDao: SetLogDao,
    private val personalRecordDao: PersonalRecordDao,
) : WorkoutRepository {

    private val userId = "local_user"

    private suspend fun buildWorkout(entity: WorkoutEntity): Workout {
        val weEntities = workoutExerciseDao.getForWorkout(entity.id)
        val exercises = weEntities.map { we ->
            val exEntity = exerciseDao.findById(we.exerciseId)
            we.toDomain(exEntity!!.toDomain())
        }
        return entity.toDomain(exercises)
    }

    private suspend fun buildSession(entity: WorkoutSessionEntity): WorkoutSession {
        val exLogs = exerciseLogDao.getForSession(entity.id).map { el ->
            val sets = setLogDao.getForExerciseLog(el.id).map { it.toDomain() }
            el.toDomain(sets)
        }
        return entity.toDomain(exLogs)
    }

    override fun getWorkoutPlans(): Flow<List<WorkoutPlan>> =
        workoutPlanDao.observePlans(userId)
            .distinctUntilChanged()
            .map { plans ->
                plans.map { planEntity ->
                    val workouts = workoutDao.getForPlan(planEntity.id).map { buildWorkout(it) }
                    planEntity.toDomain(workouts)
                }
            }

    override fun getWorkoutPlan(planId: String): Flow<WorkoutPlan?> = flow {
        val planEntity = workoutPlanDao.findById(planId) ?: run { emit(null); return@flow }
        val workouts = workoutDao.getForPlan(planId).map { buildWorkout(it) }
        emit(planEntity.toDomain(workouts))
    }

    override fun getWorkoutsForPlan(planId: String): Flow<List<Workout>> = flow {
        val workouts = workoutDao.getForPlan(planId).map { buildWorkout(it) }
        emit(workouts)
    }

    override suspend fun startSession(planId: String, workoutId: String): String {
        val workout = workoutDao.findById(workoutId)
        val sessionId = newId()
        val entity = WorkoutSessionEntity(
            id = sessionId,
            userId = userId,
            workoutId = workoutId,
            workoutName = workout?.name ?: "",
            startedAt = LocalDateTime.now().toDbString(),
            completedAt = null,
            durationMinutes = null,
            rating = null,
            rpe = null,
            notes = null,
        )
        sessionDao.upsert(entity)
        return sessionId
    }

    override fun getSession(sessionId: String): Flow<WorkoutSession?> = flow {
        val entity = sessionDao.findById(sessionId) ?: run { emit(null); return@flow }
        emit(buildSession(entity))
    }

    override suspend fun logSet(
        sessionId: String,
        exerciseId: String,
        setNum: Int,
        repsActual: Int,
        weightKg: Float,
    ) {
        // Find or create an ExerciseLog for this session+exercise
        val existingLogs = exerciseLogDao.getForSession(sessionId)
        val exerciseLog = existingLogs.firstOrNull { it.exerciseId == exerciseId }
            ?: run {
                val exEntity = exerciseDao.findById(exerciseId)
                val logEntity = ExerciseLogEntity(
                    id = newId(),
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    exerciseName = exEntity?.name ?: "",
                )
                exerciseLogDao.upsert(logEntity)
                logEntity
            }

        setLogDao.upsert(
            SetLogEntity(
                id = newId(),
                exerciseLogId = exerciseLog.id,
                setNumber = setNum,
                weightKg = weightKg,
                reps = repsActual,
                durationSeconds = null,
                rpe = null,
                isCompleted = true,
            )
        )
    }

    override suspend fun finishSession(sessionId: String) {
        val existing = sessionDao.findById(sessionId) ?: return
        val now = LocalDateTime.now()
        val startedAt = existing.startedAt.toLocalDateTime()
        val durationMinutes = java.time.Duration.between(startedAt, now).toMinutes().toInt()
        sessionDao.upsert(
            existing.copy(
                completedAt = now.toDbString(),
                durationMinutes = durationMinutes,
            )
        )
    }

    override fun getRecentSessions(limit: Int): Flow<List<WorkoutSession>> = flow {
        val sessions = sessionDao.getRecentSessions(userId, limit).map { buildSession(it) }
        emit(sessions)
    }

    override fun getPersonalRecords(): Flow<List<PersonalRecord>> =
        personalRecordDao.observeAll(userId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }
}
