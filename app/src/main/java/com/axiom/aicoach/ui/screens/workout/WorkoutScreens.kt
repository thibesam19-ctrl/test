package com.axiom.aicoach.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Spacer(Modifier.height(Spacing.md))
                PlanHeaderCard()
                Spacer(Modifier.height(Spacing.xl))
                Text("This Week", style = MaterialTheme.typography.titleLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(Spacing.md))
            }
            items(demoWorkouts) { workout ->
                WorkoutDayCard(workout, onStartWorkout = { onStartWorkout(workout.id) })
            }
            item { Spacer(Modifier.height(Spacing.s64)) }
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
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(colors.primary, colors.secondary)
                    )
                )
                .padding(Spacing.xl)
        ) {
            Column {
                Text("Upper/Lower Split", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Week 3 of 8 · Intermediate · 4 days/week", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(Spacing.xl))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.s32)) {
                    StatBadge("Week", "3/8")
                    StatBadge("Sessions", "2/4")
                    StatBadge("Volume", "↑12%")
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
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
                    Text(workout.day, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                    Text(workout.name, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("${workout.exerciseCount} exercises · ~${workout.estimatedMin} min", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
                Button(
                    onClick = onStartWorkout,
                    shape = Radius.md,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Start")
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                workout.muscleGroups.forEach { muscle ->
                    Box(
                        Modifier
                            .clip(Radius.pill)
                            .background(colors.primaryLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(muscle, style = MaterialTheme.typography.labelSmall, color = colors.primary)
                    }
                }
            }
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
        topBar = {
            SessionTopBar(
                workoutName = "Upper Body Power",
                elapsedSeconds = elapsedSeconds,
                progress = totalSetsCompleted.toFloat() / totalSets,
                onFinish = { sessionFinished = true },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (restTimerActive) {
                RestTimerBanner(restSeconds) { restTimerActive = false }
            }

            // Exercise list
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
private fun SessionTopBar(workoutName: String, elapsedSeconds: Int, progress: Float, onFinish: () -> Unit) {
    val colors = AxiomTheme.colors
    val min = elapsedSeconds / 60
    val sec = elapsedSeconds % 60
    Column {
        TopAppBar(
            title = { Text(workoutName, style = MaterialTheme.typography.titleLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold) },
            actions = {
                Text(String.format("%02d:%02d", min, sec), style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onFinish) { Text("Finish", color = colors.success) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(3.dp),
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
            .background(colors.info.copy(alpha = 0.15f))
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timer, null, tint = colors.info)
            Spacer(Modifier.width(8.dp))
            Text("Rest: ${secondsRemaining}s", style = MaterialTheme.typography.titleMedium, color = colors.info, fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onSkip) { Text("Skip Rest", color = colors.info) }
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
            containerColor = if (isActive) colors.primaryLight.copy(alpha = 0.4f) else colors.card
        ),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, colors.primary) else null,
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.name, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    Text("${exercise.sets} sets × ${exercise.repsMin}–${exercise.repsMax} reps · ${exercise.muscleGroup}", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
                IconButton(onClick = onInfo, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Info, null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
            }

            if (isActive) {
                Spacer(Modifier.height(Spacing.lg))
                // Set indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(exercise.sets) { i ->
                        val done = i < setsCompleted
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (done) colors.success else colors.borderSubtle),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (done) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            else Text("${i + 1}", style = MaterialTheme.typography.labelSmall, color = if (done) Color.White else colors.textMuted)
                        }
                    }
                }
                if (setsCompleted < exercise.sets) {
                    Spacer(Modifier.height(Spacing.lg))
                    Button(
                        onClick = onSetComplete,
                        modifier = Modifier.fillMaxWidth(),
                        shape = Radius.md,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Log Set ${setsCompleted + 1}")
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(Spacing.xl)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🎉", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(Spacing.xxxl))
        Text("Workout Complete!", style = MaterialTheme.typography.headlineLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.xl))
        Text("Incredible work. Your consistency is building results.", style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.s40))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl)) {
            StatCard("Sets Done", totalSets.toString())
            StatCard("Duration", "${elapsedMin}m")
        }
        Spacer(Modifier.height(Spacing.s48))
        AxiomPrimaryButton("Back to Dashboard", onFinish, Modifier.fillMaxWidth())
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            item {
                Spacer(Modifier.height(Spacing.md))
                // Thumbnail placeholder
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(Radius.lg)
                        .background(colors.primaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PlayCircle, null, tint = colors.primary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Exercise Video", style = MaterialTheme.typography.bodyMedium, color = colors.primary)
                    }
                }
                Spacer(Modifier.height(Spacing.xl))
                Text("Bench Press", style = MaterialTheme.typography.headlineLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Text("Chest · Compound · Intermediate", style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                Spacer(Modifier.height(Spacing.xxxl))
            }
            item {
                DetailSection("Form Cues", listOf(
                    "Set up with a natural arch in your lower back",
                    "Retract and depress your shoulder blades",
                    "Grip slightly wider than shoulder-width",
                    "Touch bar to lower chest, elbows at 45–75°",
                    "Drive feet into floor, press bar up and back",
                ))
            }
            item {
                DetailSection("Common Mistakes", listOf(
                    "Flaring elbows too wide (shoulder risk)",
                    "Bouncing bar off chest (loss of control)",
                    "Lifting glutes off bench",
                    "Incomplete range of motion",
                ))
            }
            item {
                DetailSection("Substitutions", listOf(
                    "Dumbbell Press (any equipment level)",
                    "Push-Up (bodyweight alternative)",
                    "Machine Chest Press (beginner-friendly)",
                ))
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun DetailSection(title: String, items: List<String>) {
    val colors = AxiomTheme.colors
    Column(modifier = Modifier.padding(bottom = Spacing.xxxl)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(Spacing.lg))
        items.forEachIndexed { i, item ->
            Row(modifier = Modifier.padding(bottom = Spacing.md)) {
                Text("${i + 1}.", style = MaterialTheme.typography.bodyMedium, color = colors.primary, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                Text(item, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }
        }
    }
}
