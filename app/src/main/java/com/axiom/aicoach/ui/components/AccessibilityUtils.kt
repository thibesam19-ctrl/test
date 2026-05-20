package com.axiom.aicoach.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings

// True when user has enabled "Remove animations" in accessibility settings
@Composable
fun isReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    val scale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return scale == 0f
}

// Wraps animation duration — returns 0 if reduced motion is on
@Composable
fun adaptiveDuration(normal: Int): Int = if (isReducedMotionEnabled()) 0 else normal
