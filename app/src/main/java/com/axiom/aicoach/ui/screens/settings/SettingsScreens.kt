package com.axiom.aicoach.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.*
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

// ── Data ──────────────────────────────────────────────────────────────────────

data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String = "",
    val onClick: () -> Unit = {},
)

// ── Settings Main ─────────────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    onProfile: () -> Unit,
    onSubscription: () -> Unit,
    onNotifications: () -> Unit,
    onPrivacy: () -> Unit,
    onBack: () -> Unit,
) {
    val colors = AxiomTheme.colors

    val sections = listOf(
        "Account" to listOf(
            SettingsItem(Icons.Default.Star, "Subscription", "Manage your plan", onSubscription),
        ),
        "Preferences" to listOf(
            SettingsItem(Icons.Default.Notifications, "Notifications", "Reminders & alerts", onNotifications),
            SettingsItem(Icons.Default.Lock, "Privacy & Data", "Data, export, deletion", onPrivacy),
            SettingsItem(Icons.Default.FitnessCenter, "Units", "kg / lbs, cm / ft"),
            SettingsItem(Icons.Default.Palette, "Appearance", "Theme & display"),
        ),
        "Integrations" to listOf(
            SettingsItem(Icons.Default.Watch, "Google Fit", "Steps, heart rate, calories"),
            SettingsItem(Icons.Default.Bluetooth, "Health Apps", "Connect wearables"),
        ),
        "Support" to listOf(
            SettingsItem(Icons.Default.Help, "Help Center", "FAQs & guides"),
            SettingsItem(Icons.Default.Feedback, "Send Feedback", "Report a bug or request"),
            SettingsItem(Icons.Default.Info, "About Axiom", ""),
        ),
    )

    Scaffold(
        topBar = { AxiomTopBar("Settings", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // ── Profile card ──────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.lg))
                AxiomCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl),
                    onClick = onProfile,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colors.primaryLight),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("👤", style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.width(Spacing.lg))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Alex Johnson",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                            )
                            Text(
                                text = "alex@example.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textMuted,
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = colors.textMuted,
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.xl))
            }

            // ── Settings sections ─────────────────────────────────────────────
            sections.forEach { (sectionTitle, sectionItems) ->
                item {
                    Text(
                        text = sectionTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textMuted,
                        modifier = Modifier.padding(horizontal = Spacing.xxl, vertical = Spacing.md),
                    )
                }
                items(sectionItems) { settingsItem ->
                    SettingsRow(item = settingsItem)
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(start = 68.dp),
                    )
                }
                item { Spacer(Modifier.height(Spacing.lg)) }
            }

            // ── Danger zone ───────────────────────────────────────────────────
            item {
                Text(
                    text = "Danger Zone",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.error,
                    modifier = Modifier.padding(horizontal = Spacing.xxl, vertical = Spacing.md),
                )
            }
            item {
                SettingsRow(
                    item = SettingsItem(Icons.Default.Logout, "Sign Out", onClick = {}),
                    isDestructive = true,
                )
                HorizontalDivider(
                    color = colors.borderSubtle,
                    modifier = Modifier.padding(start = 68.dp),
                )
                SettingsRow(
                    item = SettingsItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete Account",
                        subtitle = "Permanently delete all data",
                        onClick = {},
                    ),
                    isDestructive = true,
                )
            }

            // ── App version footer ────────────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.s32))
                Text(
                    text = "Axiom v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.s80),
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(item: SettingsItem, isDestructive: Boolean = false) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = Spacing.xxl, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (isDestructive) colors.error else colors.textSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(Spacing.xl))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) colors.error else colors.textPrimary,
            )
            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}

// ── Profile ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var name by remember { mutableStateOf("Alex Johnson") }
    var email by remember { mutableStateOf("alex@example.com") }

    Scaffold(
        topBar = { AxiomTopBar("Profile", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            item {
                Spacer(Modifier.height(Spacing.xl))

                // ── Avatar section ────────────────────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(colors.primaryLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "👤",
                            style = MaterialTheme.typography.displayMedium,
                        )
                    }
                    Spacer(Modifier.height(Spacing.lg))
                    TextButton(onClick = {}) {
                        Text(
                            text = "Change Photo",
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.primary,
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.s40))

                // ── Form fields ───────────────────────────────────────────────
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Radius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                    ),
                    singleLine = true,
                )
                Spacer(Modifier.height(Spacing.xl))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Radius.md,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.border,
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                    ),
                    singleLine = true,
                )
                Spacer(Modifier.height(Spacing.s40))

                AxiomPrimaryButton(
                    text = "Save Changes",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

// ── Subscription ──────────────────────────────────────────────────────────────

private data class FeatureRow(val name: String, val available: Boolean)

@Composable
fun SubscriptionScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors

    val features = listOf(
        FeatureRow("AI Coach Chat", true),
        FeatureRow("Custom Workout Plans", true),
        FeatureRow("Nutrition Tracking", true),
        FeatureRow("Progress Analytics", false),
        FeatureRow("Priority Support", false),
        FeatureRow("Export Data", false),
    )

    Scaffold(
        topBar = { AxiomTopBar("Subscription", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(Modifier.height(Spacing.s40))

                // ── Hero gradient box ─────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(Radius.lg)
                        .background(
                            Brush.linearGradient(
                                listOf(colors.primaryDark, colors.primary)
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✨ Axiom Premium",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                Spacer(Modifier.height(Spacing.xl))

                // ── Current plan chip ─────────────────────────────────────────
                PillBadge(
                    text = "Free Plan",
                    color = colors.textMuted,
                )

                Spacer(Modifier.height(Spacing.s32))
            }

            // ── Feature comparison ────────────────────────────────────────────
            item {
                AxiomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Spacing.xl)) {
                        Text(
                            text = "Features",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                        )
                        Spacer(Modifier.height(Spacing.lg))
                        features.forEachIndexed { index, feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = feature.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = if (feature.available) "✓" else "✗",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (feature.available) colors.success else colors.borderSubtle,
                                )
                            }
                            if (index < features.lastIndex) {
                                HorizontalDivider(color = colors.borderSubtle)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.s32))
            }

            item {
                AxiomPrimaryButton(
                    text = "Upgrade to Premium — $9.99/mo",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.lg))
                AxiomGhostButton(
                    text = "Restore Purchases",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

// ── Notification Settings ─────────────────────────────────────────────────────

@Composable
fun NotificationSettingsScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    var workoutReminder by remember { mutableStateOf(true) }
    var mealReminder by remember { mutableStateOf(true) }
    var waterReminder by remember { mutableStateOf(true) }
    var sleepReminder by remember { mutableStateOf(false) }
    var weeklyProgress by remember { mutableStateOf(true) }
    var streakProtection by remember { mutableStateOf(true) }
    var tips by remember { mutableStateOf(false) }

    val items = listOf(
        Triple("Workout Reminder", "Daily workout nudge", workoutReminder) to { v: Boolean -> workoutReminder = v },
        Triple("Meal Logging", "Remember to log meals", mealReminder) to { v: Boolean -> mealReminder = v },
        Triple("Water Reminder", "Every 2h during waking hours", waterReminder) to { v: Boolean -> waterReminder = v },
        Triple("Sleep Wind-Down", "Evening wind-down reminder", sleepReminder) to { v: Boolean -> sleepReminder = v },
        Triple("Weekly Progress", "Sunday evening summary", weeklyProgress) to { v: Boolean -> weeklyProgress = v },
        Triple("Streak Protection", "Don't break your streak!", streakProtection) to { v: Boolean -> streakProtection = v },
        Triple("Daily Tips", "Educational content", tips) to { v: Boolean -> tips = v },
    )

    Scaffold(
        topBar = { AxiomTopBar("Notifications", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item {
                Text(
                    text = "All notifications respect your quiet hours setting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                )
            }

            items(items) { (triple, action) ->
                val (title, subtitle, enabled) = triple
                NotificationToggleRow(
                    title = title,
                    subtitle = subtitle,
                    enabled = enabled,
                    onToggle = action,
                )
                HorizontalDivider(
                    color = colors.borderSubtle,
                    modifier = Modifier.padding(start = Spacing.xl),
                )
            }

            // ── Quiet Hours section ───────────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.xl))
                Text(
                    text = "Quiet Hours",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.md),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(Spacing.xl))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quiet Hours Active",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = "No notifications between 10pm–7am",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                        )
                    }
                }
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.textOnPrimary,
                checkedTrackColor = colors.primary,
            ),
        )
    }
}

// ── Privacy Screen ────────────────────────────────────────────────────────────

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors

    Scaffold(
        topBar = { AxiomTopBar("Privacy & Data", onBack = onBack) },
        containerColor = colors.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.xl),
        ) {
            // ── Privacy summary info box ───────────────────────────────────────
            item {
                Spacer(Modifier.height(Spacing.lg))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Radius.lg)
                        .border(
                            width = 4.dp,
                            color = colors.success,
                            shape = Radius.lg,
                        )
                        .background(colors.card)
                        .padding(Spacing.xl),
                ) {
                    Column {
                        Text(
                            text = "Your Privacy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                        )
                        Spacer(Modifier.height(Spacing.md))
                        val bullets = listOf(
                            "All health data is encrypted on your device",
                            "Cloud sync is optional and opt-in",
                            "We never sell your data",
                            "We never show ads",
                            "Export or delete your data at any time",
                        )
                        bullets.forEach { bullet ->
                            Text(
                                text = "• $bullet",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(vertical = Spacing.xs),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.s32))
            }

            // ── Action rows ───────────────────────────────────────────────────
            item {
                PrivacyActionRow(
                    title = "Export My Data",
                    subtitle = "Download ZIP of all your data",
                    icon = Icons.Default.Download,
                    onClick = {},
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyActionRow(
                    title = "View Privacy Policy",
                    subtitle = "Last updated Jan 2025",
                    icon = Icons.Default.OpenInNew,
                    onClick = {},
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyActionRow(
                    title = "View Terms of Service",
                    subtitle = "",
                    icon = Icons.Default.OpenInNew,
                    onClick = {},
                )
                HorizontalDivider(color = colors.borderSubtle)
                PrivacyActionRow(
                    title = "Delete My Account",
                    subtitle = "Permanently delete all data within 30 days",
                    icon = Icons.Default.DeleteForever,
                    onClick = {},
                    isDestructive = true,
                )
                Spacer(Modifier.height(Spacing.s80))
            }
        }
    }
}

@Composable
private fun PrivacyActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) colors.error else colors.textSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(Spacing.xl))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) colors.error else colors.textPrimary,
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(18.dp),
        )
    }
}
