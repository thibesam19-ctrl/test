package com.axiom.aicoach.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomTopBar
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Spacing

// ── Settings Main ─────────────────────────────────────────────────────────────

data class SettingsItem(val icon: ImageVector, val title: String, val subtitle: String = "", val onClick: () -> Unit)

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
            SettingsItem(Icons.Default.Person, "Profile", "Update your personal details", onProfile),
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
            SettingsItem(Icons.Default.Info, "About Axiom"),
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
            sections.forEach { (sectionTitle, items) ->
                item {
                    Text(
                        sectionTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textMuted,
                        modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                    )
                }
                items(items) { item ->
                    SettingsRow(item)
                    Divider(color = colors.borderSubtle, modifier = Modifier.padding(start = 68.dp))
                }
                item { Spacer(Modifier.height(Spacing.lg)) }
            }
            item {
                // Danger zone
                Text(
                    "Danger Zone",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.error,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                )
                SettingsRow(SettingsItem(Icons.Default.Logout, "Sign Out", "", onClick = {}), isDestructive = true)
                Divider(color = colors.borderSubtle, modifier = Modifier.padding(start = 68.dp))
                SettingsRow(SettingsItem(Icons.Default.DeleteForever, "Delete Account", "Permanently delete all data", onClick = {}), isDestructive = true)
                Spacer(Modifier.height(80.dp))
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
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            item.icon,
            null,
            tint = if (isDestructive) colors.error else colors.textSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(Spacing.xl))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) colors.error else colors.textPrimary,
            )
            if (item.subtitle.isNotEmpty()) {
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .size(80.dp)
                            .let {
                                it.clip(androidx.compose.foundation.shape.CircleShape)
                                    .fillMaxSize()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("👤", style = MaterialTheme.typography.displayMedium)
                    }
                    Spacer(Modifier.height(Spacing.xl))
                    Text(name, style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                    Text(email, style = MaterialTheme.typography.bodyMedium, color = colors.textMuted)
                }
                Spacer(Modifier.height(Spacing.s40))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), shape = com.axiom.aicoach.ui.theme.Radius.md)
                Spacer(Modifier.height(Spacing.xl))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = com.axiom.aicoach.ui.theme.Radius.md)
                Spacer(Modifier.height(Spacing.s40))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = com.axiom.aicoach.ui.theme.Radius.md) {
                    Text("Save Changes")
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// ── Subscription ──────────────────────────────────────────────────────────────

@Composable
fun SubscriptionScreen(onBack: () -> Unit) {
    val colors = AxiomTheme.colors
    Scaffold(
        topBar = { AxiomTopBar("Subscription", onBack = onBack) },
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
            Text("✨", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(Spacing.xl))
            Text("Axiom Premium", style = MaterialTheme.typography.headlineLarge, color = colors.textPrimary, fontWeight = FontWeight.Bold)
            Text("Free Plan", style = MaterialTheme.typography.bodyLarge, color = colors.textMuted)
            Spacer(Modifier.height(Spacing.s40))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = com.axiom.aicoach.ui.theme.Radius.md) {
                Text("Upgrade to Premium — $9.99/mo")
            }
            Spacer(Modifier.height(Spacing.xl))
            TextButton(onClick = {}) { Text("Restore Purchases", color = colors.textMuted) }
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
                    "All notifications respect your quiet hours setting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.lg),
                )
            }
            items(items) { (triple, action) ->
                val (title, subtitle, enabled) = triple
                NotificationToggleRow(title, subtitle, enabled, onToggle = action)
                Divider(color = colors.borderSubtle, modifier = Modifier.padding(start = Spacing.xl))
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(title: String, subtitle: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = colors.textOnPrimary, checkedTrackColor = colors.primary),
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
            item {
                Spacer(Modifier.height(Spacing.md))
                Text("Your Privacy", style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    "Axiom is built privacy-first:\n\n" +
                    "• All body scans and health data are encrypted on your device\n" +
                    "• Cloud sync is optional and opt-in\n" +
                    "• We never sell your data\n" +
                    "• We never show ads\n" +
                    "• You can export or delete all your data at any time\n\n" +
                    "This app is NOT a medical device and does not provide medical advice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(Spacing.s32))
            }
            item {
                PrivacyActionRow("Export My Data", "Download ZIP of all your data", Icons.Default.Download, onClick = {})
                Divider(color = colors.borderSubtle)
                PrivacyActionRow("View Privacy Policy", "Last updated Jan 2025", Icons.Default.OpenInNew, onClick = {})
                Divider(color = colors.borderSubtle)
                PrivacyActionRow("View Terms of Service", "", Icons.Default.OpenInNew, onClick = {})
                Divider(color = colors.borderSubtle)
                PrivacyActionRow("Delete My Account", "Permanently delete all data within 30 days", Icons.Default.DeleteForever, onClick = {}, isDestructive = true)
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun PrivacyActionRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit, isDestructive: Boolean = false) {
    val colors = AxiomTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (isDestructive) colors.error else colors.textSecondary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(Spacing.xl))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = if (isDestructive) colors.error else colors.textPrimary)
            if (subtitle.isNotEmpty()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        }
        Icon(Icons.Default.ChevronRight, null, tint = colors.textMuted, modifier = Modifier.size(18.dp))
    }
}
