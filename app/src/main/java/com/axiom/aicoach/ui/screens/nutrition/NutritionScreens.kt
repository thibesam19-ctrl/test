package com.axiom.aicoach.ui.screens.nutrition

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiom.aicoach.domain.model.MealType
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

// ── Nutrition Dashboard ───────────────────────────────────────────────────────

@Composable
fun NutritionDashboardScreen(
    onLogFood: () -> Unit,
    onBarcodeScan: () -> Unit,
    onWater: () -> Unit,
    onMealLog: () -> Unit,
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AxiomTopBar("Nutrition", onBack = onBack) },
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onLogFood,
                containerColor = colors.primary,
                contentColor = colors.textOnPrimary,
                shape = Radius.xl,
            ) {
                Icon(Icons.Default.Add, "Log food")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.xl,
                end = Spacing.xl,
                top = padding.calculateTopPadding() + Spacing.md,
                bottom = padding.calculateBottomPadding() + 80.dp,
            ),
        ) {
            item {
                MacroSummaryCard(
                    calories = uiState.caloriesConsumed,
                    caloriesGoal = uiState.caloriesGoal,
                    protein = uiState.proteinG,
                    carbs = uiState.carbsG,
                    fat = uiState.fatG,
                )
                Spacer(Modifier.height(Spacing.xl))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AxiomSecondaryButton(
                        text = "📷 Scan Food",
                        onClick = onBarcodeScan,
                        modifier = Modifier.weight(1f),
                    )
                    AxiomSecondaryButton(
                        text = "🔍 Search Food",
                        onClick = onLogFood,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(Spacing.xl))
            }
            if (uiState.mealSections.isEmpty()) {
                item {
                    EmptyState(
                        title = "No food logged today",
                        message = "Tap + to log your first meal",
                        actionLabel = "Log Food",
                        onAction = onLogFood,
                    )
                }
            } else {
                uiState.mealSections.forEach { section ->
                    item {
                        MealSectionHeader(section.mealType, onAdd = onLogFood)
                    }
                    items(section.entries) { entry ->
                        FoodLogEntryRow(
                            entry = entry,
                            onDelete = { viewModel.deleteLog(entry.logId) },
                        )
                    }
                    item { Spacer(Modifier.height(Spacing.lg)) }
                }
            }
        }
    }
}

@Composable
private fun MacroSummaryCard(
    calories: Int,
    caloriesGoal: Int,
    protein: Int,
    carbs: Int,
    fat: Int,
) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    "$calories kcal",
                    style = MaterialTheme.typography.displaySmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(Spacing.md))
                Text(
                    "/ $caloriesGoal goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            AxiomProgressBar(
                progress = (calories.toFloat() / caloriesGoal.toFloat()).coerceIn(0f, 1f),
                color = colors.primary,
            )
            Spacer(Modifier.height(Spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroChip("Protein", "${protein}g", colors.primary)
                MacroChip("Carbs", "${carbs}g", colors.secondary)
                MacroChip("Fat", "${fat}g", colors.accent)
            }
        }
    }
}

@Composable
private fun MealSectionHeader(mealType: MealType, onAdd: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            mealType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, null, tint = colors.primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun FoodLogEntryRow(entry: FoodLogUi, onDelete: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.name,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            Text(
                "P: ${entry.proteinG}g  C: ${entry.carbsG}g  F: ${entry.fatG}g",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
        Text(
            "${entry.calories} kcal",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(Spacing.md))
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, null, tint = colors.textMuted, modifier = Modifier.size(16.dp))
        }
    }
}

// ── Food Search ───────────────────────────────────────────────────────────────

@Composable
fun FoodSearchScreen(
    onFoodSelected: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AxiomTopBar("Search Food", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.md))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.searchFood(it) },
                placeholder = { Text("Search 1M+ foods...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.textMuted) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchFood("") }) {
                            Icon(Icons.Default.Close, null, tint = colors.textMuted)
                        }
                    } else {
                        IconButton(onClick = onBarcodeClick) {
                            Icon(Icons.Default.QrCodeScanner, null, tint = colors.primary)
                        }
                    }
                },
                shape = Radius.md,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.border,
                ),
            )
            Spacer(Modifier.height(Spacing.xl))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                items(listOf("Recent", "Favorites", "Breakfast", "Lunch", "Dinner")) { filter ->
                    FilterChip(
                        selected = filter == "Recent",
                        onClick = {},
                        label = { Text(filter) },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.lg))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                items(uiState.searchResults) { food ->
                    SearchFoodRow(food, onClick = { onFoodSelected(food.id) })
                    HorizontalDivider(color = colors.borderSubtle)
                }
            }
        }
    }
}

@Composable
private fun SearchFoodRow(food: FoodItemUi, onClick: () -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                food.name,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            Text(
                "P: ${food.proteinG}g · C: ${food.carbsG}g · F: ${food.fatG}g",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
        Text(
            "${food.calories} kcal",
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(Spacing.md))
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, null, tint = colors.primary, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Barcode Scan ──────────────────────────────────────────────────────────────

@Composable
fun BarcodeScanScreen(onBarcodeDetected: (String) -> Unit, onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AxiomTopBar("Scan Barcode", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = Executors.newSingleThreadExecutor()
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
                        val scanner = BarcodeScanning.getClient(options)
                        val analysis = ImageAnalysis.Builder().build()
                        analysis.setAnalyzer(executor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null && !detected) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        barcodes.firstOrNull()?.rawValue?.let { barcode ->
                                            detected = true
                                            onBarcodeDetected(barcode)
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }
                        runCatching {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize(),
            )

            // Scan guide overlay
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.Center)
                    .clip(Radius.lg)
                    .background(colors.textOnPrimary.copy(alpha = 0.1f)),
            ) {
                Text(
                    "Align barcode within frame",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textOnPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }
}

// ── Food Detail ───────────────────────────────────────────────────────────────

@Composable
fun FoodDetailScreen(
    foodId: String,
    onLogFood: () -> Unit,
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Find the food item from search results, falling back to a placeholder
    val food = uiState.searchResults.firstOrNull { it.id == foodId }

    var servings by remember { mutableStateOf(1f) }
    var selectedMeal by remember { mutableStateOf(MealType.LUNCH) }

    val displayName = food?.name ?: foodId
    val displayCalories = food?.let { (it.calories * servings).toInt() } ?: 0
    val displayProtein = food?.let { (it.proteinG * servings).toInt() } ?: 0
    val displayCarbs = food?.let { (it.carbsG * servings).toInt() } ?: 0
    val displayFat = food?.let { (it.fatG * servings).toInt() } ?: 0

    Scaffold(
        topBar = { AxiomTopBar(displayName, onBack = onBack) },
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
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            item {
                // Calories big display
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "$displayCalories",
                            style = MaterialTheme.typography.displaySmall,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "calories",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                        )
                        Spacer(Modifier.height(Spacing.xl))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            MacroChip("Protein", "${displayProtein}g", colors.primary)
                            MacroChip("Carbs", "${displayCarbs}g", colors.secondary)
                            MacroChip("Fat", "${displayFat}g", colors.accent)
                        }
                    }
                }
            }
            item {
                // Serving size adjuster
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.xl)) {
                        Text(
                            "Servings",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(Spacing.lg))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = { if (servings > 0.5f) servings -= 0.5f }) {
                                Icon(Icons.Default.Remove, null, tint = colors.primary)
                            }
                            Text(
                                String.format("%.1f", servings),
                                style = MaterialTheme.typography.headlineMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = Spacing.xl),
                            )
                            IconButton(onClick = { servings += 0.5f }) {
                                Icon(Icons.Default.Add, null, tint = colors.primary)
                            }
                            Spacer(Modifier.width(Spacing.md))
                            Text(
                                "serving(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textMuted,
                            )
                        }
                        Slider(
                            value = servings,
                            onValueChange = { servings = it },
                            valueRange = 0.5f..5f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = colors.primary,
                                activeTrackColor = colors.primary,
                            ),
                        )
                    }
                }
            }
            item {
                // Meal picker
                Column {
                    Text(
                        "Add to Meal",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(Spacing.lg))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        items(MealType.values().toList()) { meal ->
                            FilterChip(
                                selected = selectedMeal == meal,
                                onClick = { selectedMeal = meal },
                                label = {
                                    Text(
                                        meal.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                                    )
                                },
                            )
                        }
                    }
                }
            }
            item {
                AxiomPrimaryButton(
                    text = "Log Food",
                    onClick = {
                        if (food != null) {
                            viewModel.logFood(food.id, selectedMeal, servings)
                        }
                        onLogFood()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ── Meal Log ──────────────────────────────────────────────────────────────────

@Composable
fun MealLogScreen(
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AxiomTopBar("Meal Log", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.xl,
                end = Spacing.xl,
                top = padding.calculateTopPadding() + Spacing.md,
                bottom = padding.calculateBottomPadding() + Spacing.xl,
            ),
        ) {
            if (uiState.mealSections.isEmpty()) {
                item {
                    EmptyState(
                        title = "Nothing logged today",
                        message = "Use the Nutrition screen to log meals",
                    )
                }
            } else {
                uiState.mealSections.forEach { section ->
                    item {
                        AxiomCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(Spacing.xl)) {
                                Text(
                                    section.mealType.name.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(Modifier.height(Spacing.lg))
                                section.entries.forEachIndexed { index, entry ->
                                    FoodLogEntryRow(
                                        entry = entry,
                                        onDelete = { viewModel.deleteLog(entry.logId) },
                                    )
                                    if (index < section.entries.size - 1) {
                                        HorizontalDivider(
                                            color = colors.borderSubtle,
                                            modifier = Modifier.padding(vertical = Spacing.sm),
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(Spacing.xl))
                    }
                }
            }
        }
    }
}

// ── Water Tracking ────────────────────────────────────────────────────────────

@Composable
fun WaterTrackingScreen(
    onBack: () -> Unit,
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val colors = AxiomTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val waterMl = uiState.waterMl
    val goalMl = uiState.waterGoalMl
    val quickAddOptions = listOf(150, 250, 350, 500)

    Scaffold(
        topBar = { AxiomTopBar("Water Tracker", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.s40))

            // Progress ring with centered content
            AxiomProgressRing(
                progress = if (goalMl > 0) (waterMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f,
                size = 160.dp,
                strokeWidth = 12.dp,
                color = colors.info,
                trackColor = colors.borderSubtle,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💧", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "${waterMl}ml",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.info,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xl))
            val pct = if (goalMl > 0) (waterMl.toFloat() / goalMl * 100).toInt() else 0
            Text(
                "of ${goalMl}ml · ${pct}% complete",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(Spacing.s40))
            Text(
                "Quick Add",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start),
            )
            Spacer(Modifier.height(Spacing.xl))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                modifier = Modifier.fillMaxWidth(),
            ) {
                quickAddOptions.forEach { ml ->
                    OutlinedButton(
                        onClick = { viewModel.logWater(ml) },
                        modifier = Modifier.weight(1f),
                        shape = Radius.md,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.info),
                        border = BorderStroke(1.dp, colors.info.copy(alpha = 0.5f)),
                    ) {
                        Text("+${ml}ml", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
