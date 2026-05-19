package com.axiom.aicoach.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.axiom.aicoach.ui.screens.auth.SignInScreen
import com.axiom.aicoach.ui.screens.auth.SignUpScreen
import com.axiom.aicoach.ui.screens.auth.PasswordResetScreen
import com.axiom.aicoach.ui.screens.coach.CoachChatScreen
import com.axiom.aicoach.ui.screens.dashboard.DashboardScreen
import com.axiom.aicoach.ui.screens.nutrition.BarcodeScanScreen
import com.axiom.aicoach.ui.screens.nutrition.FoodDetailScreen
import com.axiom.aicoach.ui.screens.nutrition.FoodSearchScreen
import com.axiom.aicoach.ui.screens.nutrition.MealLogScreen
import com.axiom.aicoach.ui.screens.nutrition.NutritionDashboardScreen
import com.axiom.aicoach.ui.screens.nutrition.WaterTrackingScreen
import com.axiom.aicoach.ui.screens.onboarding.OnboardingScreen
import com.axiom.aicoach.ui.screens.onboarding.PaywallScreen
import com.axiom.aicoach.ui.screens.auth.WelcomeScreen
import com.axiom.aicoach.ui.screens.progress.BodyMeasurementsScreen
import com.axiom.aicoach.ui.screens.progress.PhotoGalleryScreen
import com.axiom.aicoach.ui.screens.progress.ProgressOverviewScreen
import com.axiom.aicoach.ui.screens.progress.WeightHistoryScreen
import com.axiom.aicoach.ui.screens.settings.AiConsentScreen
import com.axiom.aicoach.ui.screens.settings.NotificationSettingsScreen
import com.axiom.aicoach.ui.screens.settings.PrivacyScreen
import com.axiom.aicoach.ui.screens.settings.ProfileScreen
import com.axiom.aicoach.ui.screens.settings.SettingsScreen
import com.axiom.aicoach.ui.screens.settings.SubscriptionScreen
import com.axiom.aicoach.ui.screens.workout.ExerciseDetailScreen
import com.axiom.aicoach.ui.screens.workout.WorkoutFormScreen
import com.axiom.aicoach.ui.screens.workout.WorkoutPlanScreen
import com.axiom.aicoach.ui.screens.workout.WorkoutSessionScreen

sealed class Screen(val route: String) {
    // Auth
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object PasswordReset : Screen("password_reset")

    // Onboarding
    object Onboarding : Screen("onboarding")
    object Paywall : Screen("paywall")

    // Main tabs
    object Dashboard : Screen("dashboard")
    object WorkoutPlan : Screen("workout_plan")
    object WorkoutSession : Screen("workout_session/{planId}") {
        fun createRoute(planId: String) = "workout_session/$planId"
    }
    object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }
    object NutritionDashboard : Screen("nutrition_dashboard")
    object FoodSearch : Screen("food_search")
    object BarcodeScanner : Screen("barcode_scan")
    object FoodDetail : Screen("food_detail/{foodId}") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
    object MealLog : Screen("meal_log")
    object WaterTracking : Screen("water_tracking")
    object ProgressOverview : Screen("progress_overview")
    object WeightHistory : Screen("weight_history")
    object BodyMeasurements : Screen("body_measurements")
    object PhotoGallery : Screen("photo_gallery")
    object WorkoutForm : Screen("workout_form")
    object CoachChat : Screen("coach_chat")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object Subscription : Screen("subscription")
    object NotificationSettings : Screen("notification_settings")
    object Privacy : Screen("privacy")
    object AiConsent : Screen("ai_consent")
}

@Composable
fun AxiomNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route,
) {
    val enterTransition = fadeIn(tween(240)) + slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(240)
    )
    val exitTransition = fadeOut(tween(240)) + slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(240)
    )
    val popEnterTransition = fadeIn(tween(240)) + slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.End, tween(240)
    )
    val popExitTransition = fadeOut(tween(240)) + slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.End, tween(240)
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition },
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSignIn = { navController.navigate(Screen.SignIn.route) },
                onSignUp = { navController.navigate(Screen.SignUp.route) },
                onContinueWithGoogle = { navController.navigate(Screen.Onboarding.route) },
            )
        }
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.PasswordReset.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.Onboarding.route) { popUpTo(0) } },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.PasswordReset.route) {
            PasswordResetScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = { navController.navigate(Screen.Paywall.route) { popUpTo(0) } },
            )
        }
        composable(Screen.Paywall.route) {
            PaywallScreen(
                onContinue = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } },
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToWorkout = { navController.navigate(Screen.WorkoutPlan.route) },
                onNavigateToNutrition = { navController.navigate(Screen.NutritionDashboard.route) },
                onNavigateToProgress = { navController.navigate(Screen.ProgressOverview.route) },
                onNavigateToCoach = { navController.navigate(Screen.CoachChat.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToWater = { navController.navigate(Screen.WaterTracking.route) },
            )
        }
        composable(Screen.WorkoutPlan.route) {
            WorkoutPlanScreen(
                onStartWorkout = { planId -> navController.navigate(Screen.WorkoutSession.createRoute(planId)) },
                onExerciseDetail = { exerciseId -> navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId)) },
                onFormAnalysis = { navController.navigate(Screen.WorkoutForm.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.WorkoutForm.route) {
            WorkoutFormScreen(onBack = { navController.popBackStack() })
        }
        composable(
            Screen.WorkoutSession.route,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStack ->
            WorkoutSessionScreen(
                planId = backStack.arguments?.getString("planId") ?: "",
                onFinish = { navController.popBackStack() },
                onExerciseDetail = { exerciseId -> navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId)) },
            )
        }
        composable(
            Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStack ->
            ExerciseDetailScreen(
                exerciseId = backStack.arguments?.getString("exerciseId") ?: "",
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.NutritionDashboard.route) {
            NutritionDashboardScreen(
                onLogFood = { navController.navigate(Screen.FoodSearch.route) },
                onBarcodeScan = { navController.navigate(Screen.BarcodeScanner.route) },
                onWater = { navController.navigate(Screen.WaterTracking.route) },
                onMealLog = { navController.navigate(Screen.MealLog.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.FoodSearch.route) {
            FoodSearchScreen(
                onFoodSelected = { foodId -> navController.navigate(Screen.FoodDetail.createRoute(foodId)) },
                onBarcodeClick = { navController.navigate(Screen.BarcodeScanner.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.BarcodeScanner.route) {
            BarcodeScanScreen(
                onBarcodeDetected = { barcode ->
                    navController.navigate(Screen.FoodDetail.createRoute(barcode)) { popUpTo(Screen.FoodSearch.route) }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            Screen.FoodDetail.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) { backStack ->
            FoodDetailScreen(
                foodId = backStack.arguments?.getString("foodId") ?: "",
                onLogFood = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.MealLog.route) {
            MealLogScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.WaterTracking.route) {
            WaterTrackingScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.ProgressOverview.route) {
            ProgressOverviewScreen(
                onWeightHistory = { navController.navigate(Screen.WeightHistory.route) },
                onBodyMeasurements = { navController.navigate(Screen.BodyMeasurements.route) },
                onPhotoGallery = { navController.navigate(Screen.PhotoGallery.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.WeightHistory.route) {
            WeightHistoryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.BodyMeasurements.route) {
            BodyMeasurementsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.PhotoGallery.route) {
            PhotoGalleryScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.CoachChat.route) {
            CoachChatScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onProfile = { navController.navigate(Screen.Profile.route) },
                onSubscription = { navController.navigate(Screen.Subscription.route) },
                onNotifications = { navController.navigate(Screen.NotificationSettings.route) },
                onPrivacy = { navController.navigate(Screen.Privacy.route) },
                onAiConsent = { navController.navigate(Screen.AiConsent.route) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Subscription.route) {
            SubscriptionScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Privacy.route) {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AiConsent.route) {
            AiConsentScreen(onBack = { navController.popBackStack() })
        }
    }
}
