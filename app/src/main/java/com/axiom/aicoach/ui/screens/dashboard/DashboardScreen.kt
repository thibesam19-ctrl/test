package com.axiom.aicoach.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onNavigateToWorkout: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToCoach: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToWater: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

    // Demo state
    val caloriesConsumed = 1420
    val caloriesGoal = 2100
    val proteinG = 98
    val proteinGoal = 158
    val carbsG = 168
    val carbsGoal = 220
    val fatG = 52
    val fatGoal = 70
    val waterMl = 1500
    val waterGoalMl = 2400
    val streakDays = 7

    Scaffold(
        containerColor = colors.background,
        topBar = {
            DashboardTopBar(
                greeting = getGreeting(),
                streakDays = streakDays,
                onSettings = onNavigateToSettings,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // Date header
            item {
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.md),
                )
            }

            // Calorie ring card
            item {
                CalorieRingCard(
                    consumed = caloriesConsumed,
                    goal = caloriesGoal,
                    proteinG = proteinG, proteinGoal = proteinGoal,
                    carbsG = carbsG, carbsGoal = carbsGoal,
                    fatG = fatG, fatGoal = fatGoal,
                    onClick = onNavigateToNutrition,
                    modifier = Modifier.padding(horizontal = Spacing.xl),
                )
            }

            item { Spacer(Modifier.height(Spacing.xl)) }

            // Quick action row
            item {
                SectionHeader("Quick Log")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Spacing.xl),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(quickActions(onNavigateToNutrition, onNavigateToWorkout, onNavigateToWater, onNavigateToCoach)) { action ->
                        QuickActionChip(action)
                    }
                }
            }

            item { Spacer(Modifier.height(Spacing.xl)) }

            // Water progress
            item {
                WaterCard(
                    currentMl = waterMl,
                    goalMl = waterGoalMl,
                    onClick = onNavigateToWater,
                    modifier = Modifier.padding(horizontal = Spacing.xl),
                )
            }

            item { Spacer(Modifier.height(Spacing.xl)) }

            // Today's workout
            item {
                SectionHeader("Today's Workout", "View Plan", onNavigateToWorkout)
                TodayWorkoutCard(
                    workoutName = "Upper Body Power",
                    exerciseCount = 6,
                    estimatedMin = 45,
                    onClick = onNavigateToWorkout,
                    modifier = Modifier.padding(horizontal = Spacing.xl),
                )
            }

            item { Spacer(Modifier.height(Spacing.xl)) }

            // Stats row
            item {
                SectionHeader("This Week", "Details", onNavigateToProgress)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatCard("Workouts", "4", modifier = Modifier.weight(1f))
                    StatCard("Avg Cal", "1,847", modifier = Modifier.weight(1f))
                    StatCard("Steps", "62K", modifier = Modifier.weight(1f))
                }
            }

            item { Spacer(Modifier.height(Spacing.s40)) }

            // Coach nudge
            item {
                CoachNudgeCard(
                    message = "You're 94% to your protein goal today. Add a Greek yogurt or a scoop of protein to finish strong! 💪",
                    onClick = onNavigateToCoach,
                    modifier = Modifier.padding(horizontal = Spacing.xl),
                )
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(greeting: String, streakDays: Int, onSettings: () -> Unit) {
    val colors = AxiomTheme.colors
    TopAppBar(
        title = {
            Column {
                Text(greeting, style = MaterialTheme.typography.titleLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Text("Keep the momentum going", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
        },
        actions = {
            // Streak badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(Radius.pill)
                    .background(colors.warningBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("🔥", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(4.dp))
                Text("$streakDays", style = MaterialTheme.typography.labelLarge, color = colors.warning, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, "Settings", tint = colors.textSecondary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
    )
}

@Composable
private fun CalorieRingCard(
    consumed: Int, goal: Int,
    proteinG: Int, proteinGoal: Int,
    carbsG: Int, carbsGoal: Int,
    fatG: Int, fatGoal: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    val progress = (consumed.toFloat() / goal).coerceIn(0f, 1f)
    val remaining = (goal - consumed).coerceAtLeast(0)

    AxiomCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text("Calories", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$consumed", style = MaterialTheme.typography.displaySmall, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                        Text(" / $goal kcal", style = MaterialTheme.typography.bodySmall, color = colors.textMuted, modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Text("$remaining kcal remaining", style = MaterialTheme.typography.bodySmall, color = if (remaining > 0) colors.success else colors.warning)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp),
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = colors.borderSubtle,
                        strokeWidth = 6.dp,
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = colors.primary,
                        strokeWidth = 6.dp,
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.xl))
            // Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroProgressItem("Protein", proteinG, proteinGoal, "g", colors.primary)
                MacroProgressItem("Carbs", carbsG, carbsGoal, "g", colors.secondary)
                MacroProgressItem("Fat", fatG, fatGoal, "g", colors.accent)
            }
        }
    }
}

@Composable
private fun MacroProgressItem(label: String, current: Int, goal: Int, unit: String, color: androidx.compose.ui.graphics.Color) {
    val colors = AxiomTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Text("$current$unit", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.Bold)
        Text("/ $goal$unit", style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        Spacer(Modifier.height(6.dp))
        AxiomProgressBar(progress = current.toFloat() / goal, color = color, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
    }
}

@Composable
private fun WaterCard(currentMl: Int, goalMl: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WaterDrop, null, tint = colors.info, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Water", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                    Text("${currentMl}ml / ${goalMl}ml", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${((currentMl.toFloat() / goalMl) * 100).toInt()}%", style = MaterialTheme.typography.headlineSmall, color = colors.info, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                AxiomProgressBar(currentMl.toFloat() / goalMl, color = colors.info, modifier = Modifier.width(80.dp))
            }
        }
    }
}

@Composable
private fun TodayWorkoutCard(workoutName: String, exerciseCount: Int, estimatedMin: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(Radius.md)
                        .background(colors.primaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.FitnessCenter, null, tint = colors.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(workoutName, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                    Text("$exerciseCount exercises · ~$estimatedMin min", style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
                }
            }
            Button(
                onClick = onClick,
                shape = Radius.md,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text("Start", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun CoachNudgeCard(message: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.xl),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primaryLight),
                contentAlignment = Alignment.Center,
            ) {
                Text("🤖", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Coach Axiom", style = MaterialTheme.typography.labelMedium, color = colors.primary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(message, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
            }
        }
    }
}

data class QuickAction(val icon: ImageVector, val label: String, val onClick: () -> Unit)

private fun quickActions(onFood: () -> Unit, onWorkout: () -> Unit, onWater: () -> Unit, onCoach: () -> Unit) = listOf(
    QuickAction(Icons.Default.LocalFireDepartment, "Log Meal", onFood),
    QuickAction(Icons.Default.FitnessCenter, "Log Workout", onWorkout),
    QuickAction(Icons.Default.WaterDrop, "Add Water", onWater),
    QuickAction(Icons.Default.Bolt, "Ask Coach", onCoach),
)

@Composable
private fun QuickActionChip(action: QuickAction) {
    val colors = AxiomTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(Radius.lg)
            .clickable(onClick = action.onClick)
            .padding(Spacing.md),
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(Radius.lg)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(action.icon, null, tint = colors.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(action.label, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
    }
}

private fun getGreeting(): String {
    return when (java.time.LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}
