package com.axiom.aicoach.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import kotlinx.coroutines.delay

// ── Workout Plan Screen ───────────────────────────────────────────────────────

data class DemoWorkout(
    val id: String,
    val name: String,
    val day: String,
    val exerciseCount: Int,
    val estimatedMin: Int,
    val muscleGroups: List<String>,
)

val demoWorkouts = listOf(
    DemoWorkout("w1", "Upper Body Power", "Monday", 6, 50, listOf("Chest", "Back", "Shoulders")),
    DemoWorkout("w2", "Lower Body Strength", "Wednesday", 5, 45, listOf("Quads", "Hamstrings", "Glutes")),
    DemoWorkout("w3", "Push Day", "Friday", 7, 55, listOf("Chest", "Shoulders", "Triceps")),
    DemoWorkout("w4", "Pull Day", "Saturday", 6, 50, listOf("Back", "Biceps", "Rear Delts")),
)

@Composable
fun WorkoutPlanScreen(
    onStartWorkout: (String) -> Unit,
    onExerciseDetail: (String) -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    Scaffold(
        topBar = { AxiomTopBar("My Workout Plan", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.xl,
                end = Spacing.xl,
                top = padding.calculateTopPadding() + Spacing.md,
                bottom = padding.calculateBottomPadding() + Spacing.s64,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                PlanHeaderCard()
                Spacer(Modifier.height(Spacing.xl))
                Text(
                    "This Week",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.md))
            }
            items(demoWorkouts) { workout ->
                WorkoutDayCard(workout, onStartWorkout = { onStartWorkout(workout.id) })
            }
            item {
                RestDayCard()
            }
        }
    }
}

@Composable
private fun PlanHeaderCard() {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.primaryDark, colors.primary)
                    )
                )
                .padding(Spacing.xl),
        ) {
            Column {
                Text(
                    "Upper/Lower Split · Week 3 of 8",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.s32))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s32)) {
                    PlanStatItem("Week", "3/8")
                    PlanStatItem("Sessions", "2/4")
                    PlanStatItem("Volume", "↑12%")
                }
            }
        }
    }
}

@Composable
private fun PlanStatItem(label: String, value: String) {
    Column {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun WorkoutDayCard(workout: DemoWorkout, onStartWorkout: () -> Unit) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        workout.day,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                    Text(
                        workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${workout.exerciseCount} exercises · ~${workout.estimatedMin} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
                Button(
                    onClick = onStartWorkout,
                    shape = Radius.md,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    contentPadding = PaddingValues(horizontal = Spacing.xl, vertical = Spacing.md),
                ) {
                    Text("Start →", style = MaterialTheme.typography.labelMedium, color = colors.textOnPrimary)
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                workout.muscleGroups.forEach { muscle ->
                    PillBadge(text = muscle, color = colors.primary)
                }
            }
        }
    }
}

@Composable
private fun RestDayCard() {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Sunday", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
            Spacer(Modifier.width(Spacing.lg))
            Text(
                "🛌 Rest Day — Recovery is gains too",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
        }
    }
}

// ── Active Workout Session ────────────────────────────────────────────────────

data class ActiveExercise(
    val id: String,
    val name: String,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int,
    val restSeconds: Int,
    val muscleGroup: String,
)

val demoExercises = listOf(
    ActiveExercise("e1", "Bench Press", 4, 6, 10, 90, "Chest"),
    ActiveExercise("e2", "Incline Dumbbell Press", 3, 8, 12, 75, "Chest"),
    ActiveExercise("e3", "Overhead Press", 3, 8, 10, 90, "Shoulders"),
    ActiveExercise("e4", "Lateral Raise", 3, 12, 15, 60, "Shoulders"),
    ActiveExercise("e5", "Tricep Pushdown", 3, 10, 15, 60, "Triceps"),
    ActiveExercise("e6", "Face Pull", 3, 15, 20, 60, "Rear Delts"),
)

@Composable
fun WorkoutSessionScreen(
    planId: String,
    onFinish: () -> Unit,
    onExerciseDetail: (String) -> Unit,
) {
    val colors = AxiomTheme.colors
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    var completedSets by remember { mutableStateOf(mutableMapOf<String, Int>()) }
    var restTimerActive by remember { mutableStateOf(false) }
    var restSeconds by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var sessionFinished by remember { mutableStateOf(false) }

    val currentExercise = demoExercises.getOrNull(currentExerciseIndex)
    val totalSetsCompleted = completedSets.values.sum()
    val totalSets = demoExercises.sumOf { it.sets }

    LaunchedEffect(Unit) {
        while (!sessionFinished) {
            delay(1000)
            elapsedSeconds++
        }
    }

    LaunchedEffect(restTimerActive, restSeconds) {
        if (restTimerActive && restSeconds > 0) {
            delay(1000)
            restSeconds--
            if (restSeconds == 0) restTimerActive = false
        }
    }

    if (sessionFinished) {
        WorkoutCompleteScreen(
            totalSets = totalSets,
            elapsedMin = elapsedSeconds / 60,
            onFinish = onFinish,
        )
        return
    }

    Scaffold(
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            SessionTopBar(
                workoutName = "Upper Body Power",
                elapsedSeconds = elapsedSeconds,
                progress = totalSetsCompleted.toFloat() / totalSets,
                onFinish = { sessionFinished = true },
            )

            if (restTimerActive) {
                RestTimerBanner(restSeconds) { restTimerActive = false }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                item { Spacer(Modifier.height(Spacing.md)) }
                itemsIndexed(demoExercises) { index, exercise ->
                    val isActive = index == currentExerciseIndex
                    val setsLogged = completedSets[exercise.id] ?: 0
                    ExerciseSetCard(
                        exercise = exercise,
                        setsCompleted = setsLogged,
                        isActive = isActive,
                        onSetComplete = {
                            val newCount = (completedSets[exercise.id] ?: 0) + 1
                            completedSets = (completedSets + (exercise.id to newCount)).toMutableMap()
                            if (newCount < exercise.sets) {
                                restSeconds = exercise.restSeconds
                                restTimerActive = true
                            } else if (index < demoExercises.size - 1) {
                                currentExerciseIndex = index + 1
                                restSeconds = exercise.restSeconds
                                restTimerActive = true
                            }
                        },
                        onInfo = { onExerciseDetail(exercise.id) },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopBar(
    workoutName: String,
    elapsedSeconds: Int,
    progress: Float,
    onFinish: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val min = elapsedSeconds / 60
    val sec = elapsedSeconds % 60
    Column {
        TopAppBar(
            title = {
                Text(
                    workoutName,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
                Text(
                    String.format("%02d:%02d", min, sec),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textMuted,
                )
                Spacer(Modifier.width(Spacing.md))
                TextButton(onClick = onFinish) {
                    Text("Finish", color = colors.success, fontWeight = FontWeight.SemiBold)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = colors.success,
            trackColor = colors.borderSubtle,
        )
    }
}

@Composable
private fun RestTimerBanner(secondsRemaining: Int, onSkip: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(colors.info.copy(alpha = 0.1f), colors.info.copy(alpha = 0.05f))
                )
            )
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "⏱ Rest ${secondsRemaining}s",
            style = MaterialTheme.typography.titleMedium,
            color = colors.info,
            fontWeight = FontWeight.Bold,
        )
        TextButton(onClick = onSkip) {
            Text("Skip", color = colors.info)
        }
    }
}

@Composable
private fun ExerciseSetCard(
    exercise: ActiveExercise,
    setsCompleted: Int,
    isActive: Boolean,
    onSetComplete: () -> Unit,
    onInfo: () -> Unit,
) {
    val colors = AxiomTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Radius.lg,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) colors.primaryLight.copy(alpha = 0.3f) else colors.card,
        ),
        border = if (isActive) BorderStroke(1.5.dp, colors.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "${exercise.sets} sets × ${exercise.repsMin}–${exercise.repsMax} reps · ${exercise.muscleGroup}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
                IconButton(onClick = onInfo, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Info,
                        null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            if (isActive) {
                Spacer(Modifier.height(Spacing.lg))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    repeat(exercise.sets) { i ->
                        val done = i < setsCompleted
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (done) colors.success else colors.borderSubtle),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (done) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp),
                                )
                            } else {
                                Text(
                                    "${i + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textMuted,
                                )
                            }
                        }
                    }
                }
                if (setsCompleted < exercise.sets) {
                    Spacer(Modifier.height(Spacing.lg))
                    AxiomPrimaryButton(
                        text = "Log Set ${setsCompleted + 1}",
                        onClick = onSetComplete,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                Spacer(Modifier.height(Spacing.md))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(exercise.sets) { i ->
                        val done = i < setsCompleted
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (done) colors.success else colors.borderSubtle)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutCompleteScreen(totalSets: Int, elapsedMin: Int, onFinish: () -> Unit) {
    val colors = AxiomTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ConfettiOverlay(visible = true)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("🎉", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(Spacing.xxxl))
            Text(
                "Workout Complete!",
                style = MaterialTheme.typography.displaySmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xl))
            Text(
                "Incredible work. Your consistency is building results.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.s40))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                StatCard("Sets Done", totalSets.toString())
                StatCard("Duration", "${elapsedMin}m")
            }
            Spacer(Modifier.height(Spacing.s48))
            AxiomPrimaryButton("Back to Dashboard", onFinish, Modifier.fillMaxWidth())
        }
    }
}

// ── Exercise Detail ───────────────────────────────────────────────────────────

@Composable
fun ExerciseDetailScreen(exerciseId: String, onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    Scaffold(
        topBar = { AxiomTopBar("Exercise Details", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.xl,
                end = Spacing.xl,
                top = padding.calculateTopPadding() + Spacing.md,
                bottom = padding.calculateBottomPadding() + 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxxl),
        ) {
            item {
                // Video placeholder
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(Radius.lg)
                        .background(
                            Brush.verticalGradient(
                                listOf(colors.primaryDark, colors.primary)
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PlayCircle,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp),
                        )
                        Spacer(Modifier.height(Spacing.md))
                        Text(
                            "Watch Demo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            item {
                Column {
                    Text(
                        "Bench Press",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Spacing.md))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        PillBadge("Chest", colors.primary)
                        PillBadge("Compound", colors.secondary)
                        PillBadge("Intermediate", colors.accent)
                    }
                }
            }
            item {
                DetailSection(
                    "Form Cues",
                    listOf(
                        "Set up with a natural arch in your lower back",
                        "Retract and depress your shoulder blades",
                        "Grip slightly wider than shoulder-width",
                        "Touch bar to lower chest, elbows at 45–75°",
                        "Drive feet into floor, press bar up and back",
                    )
                )
            }
            item {
                DetailSection(
                    "Common Mistakes",
                    listOf(
                        "Flaring elbows too wide (shoulder risk)",
                        "Bouncing bar off chest (loss of control)",
                        "Lifting glutes off bench",
                        "Incomplete range of motion",
                    )
                )
            }
            item {
                DetailSection(
                    "Substitutions",
                    listOf(
                        "Dumbbell Press (any equipment level)",
                        "Push-Up (bodyweight alternative)",
                        "Machine Chest Press (beginner-friendly)",
                    )
                )
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, items: List<String>) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(Spacing.lg))
            items.forEachIndexed { i, item ->
                Row(
                    modifier = Modifier.padding(bottom = if (i < items.size - 1) Spacing.md else 0.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        "${i + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp),
                    )
                    Text(
                        item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
            }
        }
    }
}
