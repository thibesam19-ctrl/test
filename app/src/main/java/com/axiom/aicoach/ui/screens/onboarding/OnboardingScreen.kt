package com.axiom.aicoach.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.domain.model.*
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.components.AxiomProgressBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val colors = AxiomTheme.colors
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 8

    val onNext = { if (step < totalSteps - 1) step++ else onComplete() }
    val onBack = { if (step > 0) step-- }

    // Onboarding state
    var selectedGoal by remember { mutableStateOf<FitnessGoal?>(null) }
    var selectedSex by remember { mutableStateOf<Sex?>(null) }
    var selectedActivity by remember { mutableStateOf<ActivityLevel?>(null) }
    var selectedExperience by remember { mutableStateOf<ExperienceLevel?>(null) }
    var selectedEquipment by remember { mutableStateOf(setOf<Equipment>()) }
    var selectedDiet by remember { mutableStateOf(setOf<DietaryPreference>()) }
    var heightCm by remember { mutableStateOf("170") }
    var weightKg by remember { mutableStateOf("70") }

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .systemBarsPadding(),
        ) {
            // Progress
            LinearProgressIndicator(
                progress = { (step + 1f) / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = colors.primary,
                trackColor = colors.borderSubtle,
            )

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    if (targetState > initialState)
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    else
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                },
                label = "onboarding_step",
                modifier = Modifier.weight(1f),
            ) { currentStep ->
                when (currentStep) {
                    0 -> GoalStep(selectedGoal) { selectedGoal = it; onNext() }
                    1 -> SexStep(selectedSex) { selectedSex = it; onNext() }
                    2 -> BodyStep(heightCm, weightKg, onHeightChange = { heightCm = it }, onWeightChange = { weightKg = it }, onNext = onNext)
                    3 -> ActivityStep(selectedActivity) { selectedActivity = it; onNext() }
                    4 -> ExperienceStep(selectedExperience) { selectedExperience = it; onNext() }
                    5 -> EquipmentStep(selectedEquipment, onToggle = { eq ->
                        selectedEquipment = if (eq in selectedEquipment) selectedEquipment - eq else selectedEquipment + eq
                    }, onNext = onNext)
                    6 -> DietStep(selectedDiet, onToggle = { d ->
                        selectedDiet = if (d in selectedDiet) selectedDiet - d else selectedDiet + d
                    }, onNext = onNext)
                    7 -> PARQStep(onNext)
                }
            }

            // Back button row
            if (step > 0) {
                TextButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .padding(horizontal = Spacing.xl, vertical = Spacing.md)
                        .align(Alignment.Start),
                ) {
                    Text("← Back", color = colors.textMuted)
                }
            } else {
                Spacer(Modifier.height(52.dp))
            }
        }
    }
}

@Composable
private fun GoalStep(selected: FitnessGoal?, onSelect: (FitnessGoal) -> Unit) {
    val goals = listOf(
        FitnessGoal.LOSE_FAT to ("🔥" to "Lose Fat"),
        FitnessGoal.BUILD_MUSCLE to ("💪" to "Build Muscle"),
        FitnessGoal.GET_STRONGER to ("🏋️" to "Get Stronger"),
        FitnessGoal.MAINTAIN to ("⚖️" to "Maintain"),
        FitnessGoal.IMPROVE_HEALTH to ("❤️" to "Improve Health"),
        FitnessGoal.SPORT_SPECIFIC to ("🏃" to "Sport-Specific"),
    )
    StepContainer(title = "What's your main goal?", subtitle = "We'll personalize everything around this.") {
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(goals) { (goal, pair) ->
                val (emoji, label) = pair
                SelectionCard(emoji = emoji, label = label, selected = selected == goal, onClick = { onSelect(goal) })
            }
        }
    }
}

@Composable
private fun SexStep(selected: Sex?, onSelect: (Sex) -> Unit) {
    val options = listOf(Sex.MALE to ("♂️" to "Male"), Sex.FEMALE to ("♀️" to "Female"), Sex.PREFER_NOT_TO_SAY to ("⚧" to "Prefer not to say"))
    StepContainer(title = "Biological sex", subtitle = "Used only for BMR calculation. Never shared.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { (sex, pair) ->
                val (emoji, label) = pair
                SelectionCard(emoji = emoji, label = label, selected = selected == sex, onClick = { onSelect(sex) }, wide = true)
            }
        }
    }
}

@Composable
private fun BodyStep(heightCm: String, weightKg: String, onHeightChange: (String) -> Unit, onWeightChange: (String) -> Unit, onNext: () -> Unit) {
    val colors = AxiomTheme.colors
    StepContainer(title = "Your body stats", subtitle = "Used to calculate your calorie and macro targets.") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = heightCm,
                onValueChange = onHeightChange,
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                shape = Radius.md,
                suffix = { Text("cm") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.border,
                ),
            )
            OutlinedTextField(
                value = weightKg,
                onValueChange = onWeightChange,
                label = { Text("Current Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                shape = Radius.md,
                suffix = { Text("kg") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.border,
                ),
            )
            Spacer(Modifier.height(8.dp))
            AxiomPrimaryButton("Continue", onNext, Modifier.fillMaxWidth(), enabled = heightCm.isNotBlank() && weightKg.isNotBlank())
        }
    }
}

@Composable
private fun ActivityStep(selected: ActivityLevel?, onSelect: (ActivityLevel) -> Unit) {
    StepContainer(title = "Activity level", subtitle = "How active are you outside the gym?") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ActivityLevel.values().forEach { level ->
                SelectionCard(label = level.label, selected = selected == level, onClick = { onSelect(level) }, wide = true)
            }
        }
    }
}

@Composable
private fun ExperienceStep(selected: ExperienceLevel?, onSelect: (ExperienceLevel) -> Unit) {
    val options = listOf(
        ExperienceLevel.BEGINNER to ("🌱" to "Beginner\n< 1 year training"),
        ExperienceLevel.INTERMEDIATE to ("🌿" to "Intermediate\n1-3 years training"),
        ExperienceLevel.ADVANCED to ("🌳" to "Advanced\n3+ years training"),
    )
    StepContainer(title = "Training experience", subtitle = "This shapes the complexity of your workout plan.") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { (level, pair) ->
                val (emoji, label) = pair
                SelectionCard(emoji = emoji, label = label, selected = selected == level, onClick = { onSelect(level) }, wide = true)
            }
        }
    }
}

@Composable
private fun EquipmentStep(selected: Set<Equipment>, onToggle: (Equipment) -> Unit, onNext: () -> Unit) {
    val options = listOf(
        Equipment.NONE to ("🤸" to "No Equipment"),
        Equipment.DUMBBELLS to ("🏋️" to "Dumbbells"),
        Equipment.BARBELL to ("⚡" to "Barbell"),
        Equipment.KETTLEBELL to ("🔔" to "Kettlebell"),
        Equipment.RESISTANCE_BANDS to ("🎀" to "Resistance Bands"),
        Equipment.FULL_GYM to ("🏟️" to "Full Gym"),
    )
    StepContainer(title = "Available equipment", subtitle = "Select all that apply.") {
        Column {
            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(options) { (eq, pair) ->
                    val (emoji, label) = pair
                    SelectionCard(emoji = emoji, label = label, selected = eq in selected, onClick = { onToggle(eq) })
                }
            }
            Spacer(Modifier.height(16.dp))
            AxiomPrimaryButton("Continue", onNext, Modifier.fillMaxWidth(), enabled = selected.isNotEmpty())
        }
    }
}

@Composable
private fun DietStep(selected: Set<DietaryPreference>, onToggle: (DietaryPreference) -> Unit, onNext: () -> Unit) {
    val options = listOf(
        DietaryPreference.OMNIVORE to "Omnivore",
        DietaryPreference.VEGETARIAN to "Vegetarian",
        DietaryPreference.VEGAN to "Vegan",
        DietaryPreference.PESCATARIAN to "Pescatarian",
        DietaryPreference.DAIRY_FREE to "Dairy-Free",
        DietaryPreference.GLUTEN_FREE to "Gluten-Free",
        DietaryPreference.HALAL to "Halal",
        DietaryPreference.SRI_LANKAN to "Sri Lankan",
        DietaryPreference.INDIAN to "Indian",
    )
    StepContainer(title = "Dietary preferences", subtitle = "We'll filter recipes and meal plans for you.") {
        Column {
            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(options) { (pref, label) ->
                    SelectionCard(label = label, selected = pref in selected, onClick = { onToggle(pref) })
                }
            }
            Spacer(Modifier.height(16.dp))
            AxiomPrimaryButton("Continue", onNext, Modifier.fillMaxWidth(), enabled = selected.isNotEmpty())
        }
    }
}

@Composable
private fun PARQStep(onNext: () -> Unit) {
    val colors = AxiomTheme.colors
    var accepted by remember { mutableStateOf(false) }
    StepContainer(title = "Health Screening", subtitle = "PAR-Q+ Safety Assessment") {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                "Before starting any exercise program, please confirm:\n\n" +
                "• You have no known heart condition\n" +
                "• You don't experience chest pain during activity\n" +
                "• You haven't had chest pain at rest in the past month\n" +
                "• You don't lose balance or become dizzy\n" +
                "• You have no bone or joint problems that exercise could worsen\n" +
                "• You are not pregnant or recently postpartum\n\n" +
                "Axiom AI Coach is NOT a medical device and does NOT replace professional medical advice. Always consult your physician before starting an exercise program.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = accepted, onCheckedChange = { accepted = it }, colors = CheckboxDefaults.colors(checkedColor = colors.primary))
                Spacer(Modifier.width(8.dp))
                Text("I confirm all of the above apply to me and I am medically cleared to exercise.", style = MaterialTheme.typography.bodySmall, color = colors.textPrimary)
            }
            Spacer(Modifier.height(24.dp))
            AxiomPrimaryButton("Continue", onNext, Modifier.fillMaxWidth(), enabled = accepted)
        }
    }
}

@Composable
private fun StepContainer(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = AxiomTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.s40),
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(Spacing.md))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        Spacer(Modifier.height(Spacing.xxxl))
        content()
    }
}

@Composable
private fun SelectionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String = "",
    wide: Boolean = false,
) {
    val colors = AxiomTheme.colors
    Box(
        modifier = Modifier
            .then(if (wide) Modifier.fillMaxWidth() else Modifier)
            .clip(Radius.lg)
            .background(if (selected) colors.primaryLight else colors.card)
            .border(1.5.dp, if (selected) colors.primary else colors.borderSubtle, Radius.lg)
            .clickable(onClick = onClick)
            .padding(vertical = if (wide) 16.dp else 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (emoji.isNotEmpty()) {
                Text(emoji, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) colors.primary else colors.textPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
            if (selected && wide) {
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Check, null, tint = colors.primary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
