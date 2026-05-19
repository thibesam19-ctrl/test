package com.axiom.aicoach.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── 1. Scrollable header (greeting + date + streak + settings) ──────
        item {
            DashboardHeader(
                streakDays = streakDays,
                onSettings = onNavigateToSettings,
            )
        }

        // ── 2. Calorie Hero Card ────────────────────────────────────────────
        item {
            CalorieHeroCard(
                consumed = caloriesConsumed,
                goal = caloriesGoal,
                proteinG = proteinG, proteinGoal = proteinGoal,
                carbsG = carbsG, carbsGoal = carbsGoal,
                fatG = fatG, fatGoal = fatGoal,
                onClick = onNavigateToNutrition,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── 3. Quick Actions row ────────────────────────────────────────────
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val actions = quickActions(
                    onFood = onNavigateToNutrition,
                    onWorkout = onNavigateToWorkout,
                    onWater = onNavigateToWater,
                    onCoach = onNavigateToCoach,
                )
                actions.forEach { action ->
                    item { QuickActionChip(action) }
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── 4. Water Card ───────────────────────────────────────────────────
        item {
            WaterCard(
                currentMl = waterMl,
                goalMl = waterGoalMl,
                onClick = onNavigateToWater,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── 5. Today's Workout ──────────────────────────────────────────────
        item {
            SectionHeader("Today's Workout", "View Plan", onNavigateToWorkout)
            TodayWorkoutCard(
                workoutName = "Upper Body Power",
                exerciseCount = 6,
                estimatedMin = 45,
                onClick = onNavigateToWorkout,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── 6. This Week stats ──────────────────────────────────────────────
        item {
            SectionHeader("This Week", "Details", onNavigateToProgress)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard("Workouts", "4", modifier = Modifier.weight(1f))
                StatCard("Avg Cal", "1,847", modifier = Modifier.weight(1f))
                StatCard("Steps", "62K", modifier = Modifier.weight(1f))
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // ── 7. Coach Nudge card ─────────────────────────────────────────────
        item {
            CoachNudgeCard(
                message = "You're 94% to your protein goal today. Add a Greek yogurt or a scoop of protein to finish strong!",
                onClick = onNavigateToCoach,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(Spacing.s80))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scrollable header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader(
    streakDays: Int,
    onSettings: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val greeting = buildGreeting()
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = Spacing.xl, bottom = Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Greeting + date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$greeting, Alex",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted,
                )
            }

            // Streak badge + settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PillBadge(
                    text = "🔥 $streakDays",
                    color = colors.warning,
                )
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Calorie Hero Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CalorieHeroCard(
    consumed: Int,
    goal: Int,
    proteinG: Int, proteinGoal: Int,
    carbsG: Int, carbsGoal: Int,
    fatG: Int, fatGoal: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    val calorieProgress = (consumed.toFloat() / goal).coerceIn(0f, 1f)
    val remaining = (goal - consumed).coerceAtLeast(0)

    val cardGradient = Brush.verticalGradient(
        listOf(colors.card, colors.primaryLight.copy(alpha = 0.2f))
    )

    AxiomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                // Ring + Macros row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left: progress ring
                    AxiomProgressRing(
                        progress = calorieProgress,
                        size = 96.dp,
                        strokeWidth = 9.dp,
                        color = colors.primary,
                        trackColor = colors.borderSubtle,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "$consumed",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                            )
                            Text(
                                text = "kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted,
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Right: macro rows
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MacroRow(
                            label = "Protein",
                            value = "${proteinG}g",
                            progress = proteinG.toFloat() / proteinGoal,
                            barColor = colors.primary,
                        )
                        MacroRow(
                            label = "Carbs",
                            value = "${carbsG}g",
                            progress = carbsG.toFloat() / carbsGoal,
                            barColor = colors.secondary,
                        )
                        MacroRow(
                            label = "Fat",
                            value = "${fatG}g",
                            progress = fatG.toFloat() / fatGoal,
                            barColor = colors.accent,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Bottom summary text
                Text(
                    text = "Consumed: ${"%,d".format(consumed)} · Remaining: ${"%,d".format(remaining)} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun MacroRow(
    label: String,
    value: String,
    progress: Float,
    barColor: Color,
) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            modifier = Modifier.width(46.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.width(36.dp),
        )
        AxiomLinearProgress(
            progress = progress,
            color = barColor,
            modifier = Modifier.weight(1f),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Actions row
// ─────────────────────────────────────────────────────────────────────────────

private data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

private fun quickActions(
    onFood: () -> Unit,
    onWorkout: () -> Unit,
    onWater: () -> Unit,
    onCoach: () -> Unit,
) = listOf(
    QuickAction(Icons.Default.RestaurantMenu, "Log Meal", onFood),
    QuickAction(Icons.Default.FitnessCenter, "Log Workout", onWorkout),
    QuickAction(Icons.Default.WaterDrop, "Add Water", onWater),
    QuickAction(Icons.Default.SmartToy, "Ask Coach", onCoach),
)

@Composable
private fun QuickActionChip(action: QuickAction) {
    val colors = AxiomTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(Radius.lg)
            .clickable(onClick = action.onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(Radius.md)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = colors.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = action.label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Water Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WaterCard(
    currentMl: Int,
    goalMl: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    val waterProgress = (currentMl.toFloat() / goalMl).coerceIn(0f, 1f)
    val pct = (waterProgress * 100).toInt()

    AxiomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left: emoji + label + amount
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💧",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Water",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Text(
                        text = "${"%,d".format(currentMl)} / ${"%,d".format(goalMl)} ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                }
            }

            // Right: ring with percentage
            AxiomProgressRing(
                progress = waterProgress,
                size = 52.dp,
                strokeWidth = 6.dp,
                color = colors.info,
                trackColor = colors.borderSubtle,
            ) {
                Text(
                    text = "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.info,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Today's Workout Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodayWorkoutCard(
    workoutName: String,
    exerciseCount: Int,
    estimatedMin: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors

    AxiomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Icon box + workout info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(Radius.md)
                        .background(colors.primaryLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = workoutName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "$exerciseCount exercises · ~$estimatedMin min",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
            }

            // Start button
            Button(
                onClick = onClick,
                shape = Radius.md,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.textOnPrimary,
                ),
            ) {
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Coach Nudge Card (gradient background)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CoachNudgeCard(
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    val gradient = Brush.linearGradient(
        listOf(colors.primaryDark, colors.primary)
    )

    AxiomCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Robot avatar circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "🤖",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Coach name + message
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Coach Axiom",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                    }
                }

                // Ask Coach CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onClick) {
                        Text(
                            text = "Ask Coach →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun buildGreeting(): String {
    return when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}
