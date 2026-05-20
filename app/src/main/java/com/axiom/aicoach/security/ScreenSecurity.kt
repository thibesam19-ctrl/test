package com.axiom.aicoach.security

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

/**
 * A wrapper composable that sets FLAG_SECURE on the host window when [enabled] is true,
 * preventing the screen content from appearing in screenshots or the recent-apps thumbnail.
 * The flag is cleared when [enabled] is false.
 */
@Composable
fun SecureScreen(enabled: Boolean = true, content: @Composable () -> Unit) {
    val view = LocalView.current

    SideEffect {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    content()
}
