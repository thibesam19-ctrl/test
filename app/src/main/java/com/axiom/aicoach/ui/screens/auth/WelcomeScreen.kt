package com.axiom.aicoach.ui.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiom.aicoach.ui.components.AxiomOutlinedButton
import com.axiom.aicoach.ui.components.AxiomPrimaryButton
import com.axiom.aicoach.ui.theme.AxiomTheme
import com.axiom.aicoach.ui.theme.Radius
import com.axiom.aicoach.ui.theme.Spacing

data class WelcomeSlide(val emoji: String, val title: String, val subtitle: String)

private val slides = listOf(
    WelcomeSlide("🧠", "Your AI Personal Trainer", "Get expert-level workout plans tailored to your body, goals, and schedule — no gym required."),
    WelcomeSlide("🥗", "Smart Nutrition Coaching", "Log meals with your camera, track macros effortlessly, and get personalized meal plans."),
    WelcomeSlide("📈", "See Real Progress", "Track your transformation with body scans, progress charts, and weekly AI insights."),
)

@Composable
fun WelcomeScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onContinueWithGoogle: () -> Unit,
) {
    val colors = AxiomTheme.colors
    val pagerState = rememberPagerState { slides.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(colors.primaryLight.copy(alpha = 0.3f), colors.background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.s48))

            // Logo
            Text(
                text = "AXIOM",
                style = MaterialTheme.typography.displayMedium,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Text(
                text = "AI COACH",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textMuted,
                letterSpacing = 6.sp,
            )

            Spacer(Modifier.height(Spacing.s64))

            // Carousel
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(slide.emoji, style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(Spacing.xxxl))
                    Text(
                        text = slide.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(Spacing.xl))
                    Text(
                        text = slide.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Page indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = Spacing.xxxl),
            ) {
                repeat(slides.size) { i ->
                    val width by animateFloatAsState(
                        targetValue = if (i == pagerState.currentPage) 24f else 8f,
                        animationSpec = tween(180),
                        label = "dot_width"
                    )
                    Box(
                        Modifier
                            .width(width.dp)
                            .height(8.dp)
                            .background(
                                if (i == pagerState.currentPage) colors.primary else colors.border,
                                Radius.pill,
                            )
                    )
                }
            }

            // CTA buttons
            AxiomPrimaryButton(
                text = "Get Started Free",
                onClick = onSignUp,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.lg))
            AxiomOutlinedButton(
                text = "Sign In",
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xl))

            Text(
                text = "By continuing you agree to our Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.xxxl),
            )
        }
    }
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
private val Float.sp get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
