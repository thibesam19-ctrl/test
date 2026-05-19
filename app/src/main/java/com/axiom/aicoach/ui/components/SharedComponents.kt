package com.axiom.aicoach.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.theme.AxiomMotion
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

// ---------------------------------------------------------------------------
// AxiomTopBar
// ---------------------------------------------------------------------------

/**
 * App-wide top bar.
 *
 * Renders on a transparent background that matches the screen background,
 * avoiding the default M3 scroll-tint behaviour. Pass [onBack] to show a
 * leading arrow button. The [actions] slot accepts arbitrary composable
 * content on the trailing side.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AxiomTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    val colors = AxiomTheme.colors
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary,
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary,
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.background,
            scrolledContainerColor = colors.background,
            titleContentColor = colors.textPrimary,
            navigationIconContentColor = colors.textPrimary,
            actionIconContentColor = colors.textPrimary,
        ),
    )
}

// ---------------------------------------------------------------------------
// AxiomPrimaryButton
// ---------------------------------------------------------------------------

/**
 * Primary CTA button.
 *
 * - Fixed 52 dp height for comfortable tap targets.
 * - Springs down to 97 % scale on press for tactile feedback.
 * - Shows a 20 dp [CircularProgressIndicator] when [loading] is true.
 * - Fully respects disabled state via distinct background and text colours.
 */
@Composable
fun AxiomPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = AxiomTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = AxiomMotion.tweenFast,
        label = "primary_button_scale",
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .scale(scale),
        enabled = enabled && !loading,
        shape = Radius.md,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.textOnPrimary,
            disabledContainerColor = colors.disabledBg,
            disabledContentColor = colors.disabledText,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.textOnPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// AxiomSecondaryButton
// ---------------------------------------------------------------------------

/**
 * Secondary / outlined button — same dimensions and press animation as
 * [AxiomPrimaryButton], but with a transparent background and border stroke.
 */
@Composable
fun AxiomSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AxiomTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = AxiomMotion.tweenFast,
        label = "secondary_button_scale",
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .scale(scale),
        enabled = enabled,
        shape = Radius.md,
        interactionSource = interactionSource,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.primary,
            disabledContentColor = colors.disabledText,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) colors.border else colors.disabledBg,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** Backwards-compatible alias. */
@Composable
fun AxiomOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = AxiomSecondaryButton(text = text, onClick = onClick, modifier = modifier, enabled = enabled)

// ---------------------------------------------------------------------------
// AxiomGhostButton
// ---------------------------------------------------------------------------

/**
 * Ghost / text-only button — no background, no border.
 *
 * Ideal for low-emphasis actions such as "Skip" or "Cancel". Text is
 * rendered in [com.axiom.aicoach.ui.theme.AxiomColors.textSecondary] to
 * signal reduced visual weight without being invisible.
 */
@Composable
fun AxiomGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = AxiomTheme.colors
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = Radius.md,
        colors = ButtonDefaults.textButtonColors(
            contentColor = colors.textSecondary,
            disabledContentColor = colors.disabledText,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ---------------------------------------------------------------------------
// AxiomCard
// ---------------------------------------------------------------------------

/**
 * Standard surface card.
 *
 * Uses a soft custom shadow ([shadow] with tinted ambient + spot colours)
 * instead of M3's default elevation tint so the card reads cleanly on both
 * light and dark backgrounds. Pass [onClick] to make the card interactive;
 * the ripple is provided by [clickable].
 */
@Composable
fun AxiomCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = AxiomTheme.colors
    val shadowColor = Color(0x0F172A).copy(alpha = 0.08f)
    val cardShape = RoundedCornerShape(16.dp)

    val baseModifier = modifier.shadow(
        elevation = 2.dp,
        shape = cardShape,
        clip = false,
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )

    Card(
        modifier = if (onClick != null) {
            baseModifier.clickable(onClick = onClick)
        } else {
            baseModifier
        },
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        content()
    }
}

// ---------------------------------------------------------------------------
// AxiomProgressRing
// ---------------------------------------------------------------------------

/**
 * Circular progress ring, inspired by Apple Fitness activity rings.
 *
 * Draws two concentric arcs on a [Canvas]:
 *  1. A full 360° track arc in [trackColor].
 *  2. A progress arc whose sweep angle is derived from [progress] (0–1).
 *
 * Both arcs use [StrokeCap.Round] for a premium pill-cap appearance.
 * Progress change is animated via a medium-bouncy spring.
 *
 * @param content  Optional composable rendered in the centre of the ring
 *                 (e.g., a percentage label).
 */
@Composable
fun AxiomProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    color: Color = AxiomTheme.colors.primary,
    trackColor: Color = AxiomTheme.colors.borderSubtle,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = AxiomMotion.springDefault,
        label = "ring_progress",
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(
                width = this.size.width - strokePx,
                height = this.size.height - strokePx,
            )

            // Track arc — always full circle
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

// ---------------------------------------------------------------------------
// AxiomLinearProgress
// ---------------------------------------------------------------------------

/**
 * Horizontal progress bar.
 *
 * 6 dp tall, pill-clipped, animated with [AxiomMotion.tweenStandard].
 * Thin and unobtrusive — suits inline display alongside stat labels.
 */
@Composable
fun AxiomLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = AxiomTheme.colors.primary,
    trackColor: Color = AxiomTheme.colors.borderSubtle,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = AxiomMotion.tweenStandard,
        label = "linear_progress",
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(Radius.pill),
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round,
    )
}

/** Backwards-compatible alias. */
@Composable
fun AxiomProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = AxiomTheme.colors.primary,
    trackColor: Color = AxiomTheme.colors.borderSubtle,
) = AxiomLinearProgress(progress = progress, modifier = modifier, color = color, trackColor = trackColor)

// ---------------------------------------------------------------------------
// SkeletonBox
// ---------------------------------------------------------------------------

/**
 * Shimmer placeholder for loading states.
 *
 * Animates a [Brush.linearGradient] that sweeps left-to-right over a
 * 1 400 ms cycle ([RepeatMode.Restart]) giving the classic skeleton-loading
 * feel. Drop this wherever content is pending and swap for real data once
 * loaded.
 *
 * @param height  Vertical extent of the skeleton slot.
 * @param shape   Corner shape — defaults to [Radius.sm] for text-line skeletons;
 *                use [Radius.lg] for card-shaped placeholders.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    shape: Shape = Radius.sm,
) {
    val colors = AxiomTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    )

    val shimmerColors = listOf(
        colors.borderSubtle,
        colors.disabledBg.copy(alpha = 0.8f),
        colors.borderSubtle,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    // Sweep a highlight band across a nominal 1 400 px canvas width
                    start = Offset(x = (shimmerProgress - 0.3f) * 1400f, y = 0f),
                    end   = Offset(x = (shimmerProgress + 0.3f) * 1400f, y = 0f),
                ),
            ),
    )
}

// ---------------------------------------------------------------------------
// SectionHeader
// ---------------------------------------------------------------------------

/**
 * Standard section divider row.
 *
 * Renders a bold [titleMedium] title on the left. When both [actionLabel]
 * and [onAction] are provided, an interactive [labelMedium] text link
 * (coloured [AxiomTheme.colors.primary]) appears on the right.
 */
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xxl, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AxiomTheme.colors.textPrimary,
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelMedium,
                color = AxiomTheme.colors.primary,
                modifier = Modifier
                    .clip(Radius.sm)
                    .clickable(onClick = onAction)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// EmptyState
// ---------------------------------------------------------------------------

/**
 * Full-width empty-state illustration block.
 *
 * Use when a list or feed has no data to show. Optionally supply an [icon]
 * (emoji string), a CTA button via [actionLabel] + [onAction], and any
 * descriptive [message] copy.
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: String = "",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = AxiomTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xl))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(Spacing.xxxl))
            AxiomPrimaryButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.widthIn(min = 180.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// StatCard
// ---------------------------------------------------------------------------

/**
 * Compact metric card.
 *
 * Renders a small [labelSmall] caption above a large [headlineMedium] value
 * in the supplied [color]. An optional [unit] string (e.g. "kg", "kcal") is
 * rendered inline in muted [bodySmall] at baseline alignment.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    unit: String = "",
    color: Color = AxiomTheme.colors.primary,
    modifier: Modifier = Modifier,
) {
    val colors = AxiomTheme.colors
    AxiomCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )
                if (unit.isNotEmpty()) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        modifier = Modifier.padding(bottom = 3.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// MacroChip
// ---------------------------------------------------------------------------

/**
 * Macro nutrient display column.
 *
 * Stacks a coloured 8 dp dot, a bold [value], and a muted [label] vertically
 * with 4 dp gaps. Use in a row of three for protein / carb / fat breakdowns.
 */
@Composable
fun MacroChip(
    label: String,
    value: String,
    color: Color,
) {
    val colors = AxiomTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}

// ---------------------------------------------------------------------------
// PillBadge
// ---------------------------------------------------------------------------

/**
 * Small pill-shaped status badge.
 *
 * Renders [text] on a tinted background ([color] at 15 % alpha) with the
 * same [color] at full opacity for the label. Suitable for workout types,
 * difficulty levels, or goal categories.
 */
@Composable
fun PillBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(Radius.pill)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

// ---------------------------------------------------------------------------
// ConfettiOverlay
// ---------------------------------------------------------------------------

/**
 * Simple celebratory overlay.
 *
 * Transitions in with [scaleIn] + [fadeIn] and out with [scaleOut] +
 * [fadeOut] using the app's standard motion specs. Renders a large 🎉 emoji
 * centred on screen — lightweight, no particle system required for the
 * majority of milestone moments.
 */
@Composable
fun ConfettiOverlay(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = AxiomMotion.springDefault) +
                fadeIn(animationSpec = tween(durationMillis = 200)),
        exit  = scaleOut(animationSpec = AxiomMotion.tweenFast) +
                fadeOut(animationSpec = tween(durationMillis = 160)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}
