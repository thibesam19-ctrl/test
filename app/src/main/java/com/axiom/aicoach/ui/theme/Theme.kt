package com.axiom.aicoach.ui.theme

import android.app.Activity
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ---------------------------------------------------------------------------
// Color token definitions
// ---------------------------------------------------------------------------

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

// ---------------------------------------------------------------------------
// Material3 color schemes
// ---------------------------------------------------------------------------

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

// ---------------------------------------------------------------------------
// Motion system
// ---------------------------------------------------------------------------

/**
 * Centralised animation specs. Prefer these over ad-hoc values so the app
 * moves as a single, coherent system.
 */
object AxiomMotion {
    /** General-purpose spring — slightly bouncy for interactive elements. */
    val springDefault = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** Crisp, no-overshoot spring for precise UI transitions (drawers, sheets). */
    val springSnappy = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    /** Gentle, playful spring for celebratory or large-scale motion. */
    val springGentle = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

    /** Standard cross-fade / slide duration (240 ms). */
    val tweenStandard = tween<Float>(durationMillis = 240, easing = FastOutSlowInEasing)

    /** Fast micro-interactions — button presses, badge updates (180 ms). */
    val tweenFast = tween<Float>(durationMillis = 180, easing = FastOutSlowInEasing)

    /** Micro-animations where speed is paramount — scale feedback (120 ms). */
    val tweenMicro = tween<Float>(durationMillis = 120, easing = FastOutLinearInEasing)
}

// ---------------------------------------------------------------------------
// Elevation tokens
// ---------------------------------------------------------------------------

/**
 * Named elevation ramp. Use these instead of raw Dp literals so every
 * shadow in the app responds to a single source of truth.
 */
object AxiomElevation {
    /** Flat — no shadow. */
    val e0: Dp = 0.dp

    /** Hair-line lift for focused / hovered surfaces. */
    val e1: Dp = 1.dp

    /** Default card shadow. */
    val e2: Dp = 2.dp

    /** Raised cards, bottom sheets at rest. */
    val e3: Dp = 4.dp

    /** Dialogs and menus. */
    val e4: Dp = 8.dp

    /** Navigation drawers, full-screen overlays. */
    val e5: Dp = 16.dp
}

// ---------------------------------------------------------------------------
// Spacing tokens
// ---------------------------------------------------------------------------

/**
 * 8-pt grid spacing scale. Prefer multiples of 4 for micro-adjustments
 * and multiples of 8 for layout spacing.
 */
object Spacing {
    val xs: Dp = 2.dp
    val sm: Dp = 4.dp
    val md: Dp = 8.dp
    val lg: Dp = 12.dp
    val xl: Dp = 16.dp
    val xxl: Dp = 20.dp
    val xxxl: Dp = 24.dp
    val s32: Dp = 32.dp
    val s40: Dp = 40.dp
    val s48: Dp = 48.dp
    val s64: Dp = 64.dp
    val s80: Dp = 80.dp
    val s96: Dp = 96.dp
}

// ---------------------------------------------------------------------------
// Radius tokens
// ---------------------------------------------------------------------------

/**
 * Corner-radius scale. Use `Radius.pill` for badge-style elements and
 * `Radius.lg` for cards and bottom-sheets.
 */
object Radius {
    val xs   = RoundedCornerShape(4.dp)
    val sm   = RoundedCornerShape(8.dp)
    val md   = RoundedCornerShape(12.dp)
    val lg   = RoundedCornerShape(16.dp)
    val xl   = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(9999.dp)
}

// ---------------------------------------------------------------------------
// Shadow helper
// ---------------------------------------------------------------------------

/**
 * Applies a soft, colour-tinted drop shadow without Material3's default
 * clip-to-shape behaviour, preserving the glow outside component bounds.
 *
 * @param elevation  Shadow depth — prefer [AxiomElevation] values.
 * @param color      Tint applied to both ambient and spot shadow layers.
 * @param shape      Contour used for the shadow geometry.
 */
fun Modifier.axiomShadow(
    elevation: Dp,
    color: Color,
    shape: Shape,
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = color,
    spotColor = color,
)

// ---------------------------------------------------------------------------
// Theme entry-point
// ---------------------------------------------------------------------------

@Composable
fun AxiomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
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
            content = content,
        )
    }
}

object AxiomTheme {
    val colors: AxiomColors
        @Composable get() = LocalAxiomColors.current
}
