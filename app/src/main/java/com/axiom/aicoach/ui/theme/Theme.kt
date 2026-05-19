package com.axiom.aicoach.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class AxiomColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val card: Color,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textOnPrimary: Color,
    val border: Color,
    val borderSubtle: Color,
    val inputBackground: Color,
    val disabledBg: Color,
    val disabledText: Color,
    val success: Color,
    val successBg: Color,
    val warning: Color,
    val warningBg: Color,
    val error: Color,
    val errorBg: Color,
    val info: Color,
    val isDark: Boolean,
)

val LightAxiomColors = AxiomColors(
    background = LightBackground,
    surface = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    card = LightCard,
    primary = LightPrimary,
    primaryDark = LightPrimaryDark,
    primaryLight = LightPrimaryLight,
    secondary = LightSecondary,
    accent = LightAccent,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    textOnPrimary = LightTextOnPrimary,
    border = LightBorder,
    borderSubtle = LightBorderSubtle,
    inputBackground = LightInputBackground,
    disabledBg = LightDisabledBg,
    disabledText = LightDisabledText,
    success = LightSuccess,
    successBg = LightSuccessBg,
    warning = LightWarning,
    warningBg = LightWarningBg,
    error = LightError,
    errorBg = LightErrorBg,
    info = LightInfo,
    isDark = false,
)

val DarkAxiomColors = AxiomColors(
    background = DarkBackground,
    surface = DarkSurface,
    surfaceElevated = DarkSurfaceElevated,
    card = DarkCard,
    primary = DarkPrimary,
    primaryDark = DarkPrimaryDark,
    primaryLight = DarkPrimaryLight,
    secondary = DarkSecondary,
    accent = DarkAccent,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    textOnPrimary = DarkTextOnPrimary,
    border = DarkBorder,
    borderSubtle = DarkBorderSubtle,
    inputBackground = DarkInputBackground,
    disabledBg = DarkDisabledBg,
    disabledText = DarkDisabledText,
    success = DarkSuccess,
    successBg = DarkSuccessBg,
    warning = DarkWarning,
    warningBg = DarkWarningBg,
    error = DarkError,
    errorBg = DarkErrorBg,
    info = DarkInfo,
    isDark = true,
)

val LocalAxiomColors = compositionLocalOf { LightAxiomColors }

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightTextOnPrimary,
    primaryContainer = LightPrimaryLight,
    onPrimaryContainer = LightPrimaryDark,
    secondary = LightSecondary,
    onSecondary = LightTextOnPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    error = LightError,
    onError = Color.White,
    errorContainer = LightErrorBg,
    outline = LightBorder,
    outlineVariant = LightBorderSubtle,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkTextOnPrimary,
    primaryContainer = DarkPrimaryLight,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkTextOnPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    error = DarkError,
    onError = Color.White,
    errorContainer = DarkErrorBg,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle,
)

@Composable
fun AxiomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val axiomColors = if (darkTheme) DarkAxiomColors else LightAxiomColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAxiomColors provides axiomColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AxiomTypography,
            content = content
        )
    }
}

object AxiomTheme {
    val colors: AxiomColors
        @Composable get() = LocalAxiomColors.current
}

// Spacing
object Spacing {
    val xs = androidx.compose.ui.unit.Dp(2f)
    val sm = androidx.compose.ui.unit.Dp(4f)
    val md = androidx.compose.ui.unit.Dp(8f)
    val lg = androidx.compose.ui.unit.Dp(12f)
    val xl = androidx.compose.ui.unit.Dp(16f)
    val xxl = androidx.compose.ui.unit.Dp(20f)
    val xxxl = androidx.compose.ui.unit.Dp(24f)
    val s32 = androidx.compose.ui.unit.Dp(32f)
    val s40 = androidx.compose.ui.unit.Dp(40f)
    val s48 = androidx.compose.ui.unit.Dp(48f)
    val s64 = androidx.compose.ui.unit.Dp(64f)
    val s80 = androidx.compose.ui.unit.Dp(80f)
    val s96 = androidx.compose.ui.unit.Dp(96f)
}

// Radius
object Radius {
    val xs = androidx.compose.foundation.shape.RoundedCornerShape(4)
    val sm = androidx.compose.foundation.shape.RoundedCornerShape(8)
    val md = androidx.compose.foundation.shape.RoundedCornerShape(12)
    val lg = androidx.compose.foundation.shape.RoundedCornerShape(16)
    val xl = androidx.compose.foundation.shape.RoundedCornerShape(24)
    val pill = androidx.compose.foundation.shape.RoundedCornerShape(9999)
}
