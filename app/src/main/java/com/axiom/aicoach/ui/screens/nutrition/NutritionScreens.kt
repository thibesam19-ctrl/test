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

data class DemoMealSection(val type: MealType, val items: List<DemoFoodEntry>)
data class DemoFoodEntry(val name: String, val calories: Int, val protein: Int, val carbs: Int, val fat: Int)

val demoMeals = listOf(
    DemoMealSection(MealType.BREAKFAST, listOf(
        DemoFoodEntry("Oatmeal with banana", 380, 12, 68, 7),
        DemoFoodEntry("Black coffee", 5, 0, 1, 0),
    )),
    DemoMealSection(MealType.LUNCH, listOf(
        DemoFoodEntry("Grilled chicken breast", 280, 52, 0, 6),
        DemoFoodEntry("Brown rice (1 cup)", 215, 5, 45, 2),
        DemoFoodEntry("Steamed broccoli", 55, 4, 11, 1),
    )),
    DemoMealSection(MealType.SNACK, listOf(
        DemoFoodEntry("Greek yogurt", 100, 17, 6, 0),
        DemoFoodEntry("Almonds (20g)", 120, 4, 5, 10),
    )),
)

@Composable
fun NutritionDashboardScreen(
    onLogFood: () -> Unit,
    onBarcodeScan: () -> Unit,
    onWater: () -> Unit,
    onMealLog: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val totalCalories = demoMeals.flatMap { it.items }.sumOf { it.calories }
    val totalProtein = demoMeals.flatMap { it.items }.sumOf { it.protein }
    val totalCarbs = demoMeals.flatMap { it.items }.sumOf { it.carbs }
    val totalFat = demoMeals.flatMap { it.items }.sumOf { it.fat }

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
                MacroSummaryCard(totalCalories, totalProtein, totalCarbs, totalFat)
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
            demoMeals.forEach { section ->
                item {
                    MealSectionHeader(section.type, onAdd = onLogFood)
                }
                items(section.items) { item ->
                    FoodEntryRow(item, onDelete = {})
                }
                item { Spacer(Modifier.height(Spacing.lg)) }
            }
            item {
                MealSectionHeader(MealType.DINNER, onAdd = onLogFood)
                EmptyState("No dinner logged", "Tap + to add your dinner", actionLabel = "Add Dinner", onAction = onLogFood)
            }
        }
    }
}

@Composable
private fun MacroSummaryCard(calories: Int, protein: Int, carbs: Int, fat: Int) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    "1,420 kcal",
                    style = MaterialTheme.typography.displaySmall,
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(Spacing.md))
                Text(
                    "/ 2,100 goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            AxiomProgressBar(calories / 2100f, color = colors.primary)
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
private fun FoodEntryRow(entry: DemoFoodEntry, onDelete: () -> Unit) {
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
                "P: ${entry.protein}g  C: ${entry.carbs}g  F: ${entry.fat}g",
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

val demoFoodDatabase = listOf(
    DemoFoodEntry("Chicken Breast (100g)", 165, 31, 0, 4),
    DemoFoodEntry("Brown Rice (100g)", 216, 5, 45, 2),
    DemoFoodEntry("Whole Egg", 70, 6, 0, 5),
    DemoFoodEntry("Oats (100g)", 389, 17, 66, 7),
    DemoFoodEntry("Greek Yogurt (100g)", 59, 10, 3, 0),
    DemoFoodEntry("Banana", 89, 1, 23, 0),
    DemoFoodEntry("Almonds (30g)", 174, 6, 6, 15),
    DemoFoodEntry("Salmon (100g)", 208, 20, 0, 13),
    DemoFoodEntry("Sweet Potato (100g)", 86, 2, 20, 0),
    DemoFoodEntry("Cottage Cheese (100g)", 98, 11, 3, 4),
    DemoFoodEntry("Lentils (100g cooked)", 116, 9, 20, 0),
    DemoFoodEntry("Dhal (100g)", 140, 8, 23, 3),
    DemoFoodEntry("Rice & Curry (Sri Lankan, 1 plate)", 520, 18, 82, 10),
    DemoFoodEntry("Roti (1 piece)", 95, 3, 17, 2),
)

@Composable
fun FoodSearchScreen(
    onFoodSelected: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isEmpty()) demoFoodDatabase
        else demoFoodDatabase.filter { it.name.contains(query, ignoreCase = true) }
    }

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
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search 1M+ foods...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.textMuted) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
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
                items(results) { food ->
                    SearchFoodRow(food, onClick = { onFoodSelected(food.name) })
                    HorizontalDivider(color = colors.borderSubtle)
                }
            }
        }
    }
}

@Composable
private fun SearchFoodRow(food: DemoFoodEntry, onClick: () -> Unit) {
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
                "P: ${food.protein}g · C: ${food.carbs}g · F: ${food.fat}g",
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
fun FoodDetailScreen(foodId: String, onLogFood: () -> Unit, onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    val food = demoFoodDatabase.firstOrNull { it.name == foodId } ?: demoFoodDatabase.first()
    var servings by remember { mutableStateOf(1f) }
    var selectedMeal by remember { mutableStateOf(MealType.LUNCH) }

    Scaffold(
        topBar = { AxiomTopBar(food.name, onBack = onBack) },
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
                            "${(food.calories * servings).toInt()}",
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
                            MacroChip("Protein", "${(food.protein * servings).toInt()}g", colors.primary)
                            MacroChip("Carbs", "${(food.carbs * servings).toInt()}g", colors.secondary)
                            MacroChip("Fat", "${(food.fat * servings).toInt()}g", colors.accent)
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
                AxiomPrimaryButton("Log Food", onLogFood, Modifier.fillMaxWidth())
            }
        }
    }
}

// ── Meal Log ──────────────────────────────────────────────────────────────────

@Composable
fun MealLogScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
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
            demoMeals.forEach { section ->
                item {
                    AxiomCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.xl)) {
                            Text(
                                section.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(Modifier.height(Spacing.lg))
                            section.items.forEachIndexed { index, item ->
                                FoodEntryRow(item, onDelete = {})
                                if (index < section.items.size - 1) {
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

// ── Water Tracking ────────────────────────────────────────────────────────────

@Composable
fun WaterTrackingScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var waterMl by remember { mutableIntStateOf(1500) }
    val goalMl = 2400
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
                progress = waterMl.toFloat() / goalMl,
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
            Text(
                "of ${goalMl}ml · ${(waterMl.toFloat() / goalMl * 100).toInt()}% complete",
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
                        onClick = { waterMl = (waterMl + ml).coerceAtMost(goalMl * 2) },
                        modifier = Modifier.weight(1f),
                        shape = Radius.md,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.info),
                        border = BorderStroke(1.dp, colors.info.copy(alpha = 0.5f)),
                    ) {
                        Text("+${ml}ml", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(Spacing.xxxl))
            if (waterMl > 0) {
                TextButton(onClick = { waterMl = (waterMl - 250).coerceAtLeast(0) }) {
                    Text("Undo last entry", color = colors.textMuted)
                }
            }
        }
    }
}
