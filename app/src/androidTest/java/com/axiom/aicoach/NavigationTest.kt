package com.axiom.aicoach

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.axiom.aicoach.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke test that verifies the app launches without crashing and the root
 * composable is displayed.
 *
 * Prerequisites to run:
 *  - The instrumentation runner in build.gradle must be set to
 *    "com.axiom.aicoach.HiltTestRunner" (a custom runner that extends
 *    AndroidJUnitRunner and uses HiltTestApplication).
 *  - hilt-android-testing must be added to androidTestImplementation.
 *  - kspAndroidTest(libs.hilt.compiler) must be configured.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appLaunchesWithoutCrashing() {
        // Assert that the root composable tree is present and visible
        composeRule.onRoot().assertIsDisplayed()
    }
}
