package com.axiom.aicoach.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AxiomAnalytics {

    override fun track(event: AnalyticsEvent) {
        val (name, params) = event.toFirebaseEvent()
        firebaseAnalytics.logEvent(name, params)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String) {
        firebaseAnalytics.setUserProperty(key, value)
    }

    private fun AnalyticsEvent.toFirebaseEvent(): Pair<String, Bundle?> = when (this) {
        is AnalyticsEvent.OnboardingStarted ->
            "onboarding_started" to null

        is AnalyticsEvent.OnboardingStepCompleted ->
            "onboarding_step_completed" to Bundle().apply {
                putInt("step", step)
            }

        is AnalyticsEvent.OnboardingCompleted ->
            "onboarding_completed" to null

        is AnalyticsEvent.WorkoutStarted ->
            "workout_started" to Bundle().apply {
                putString("plan_id", planId)
            }

        is AnalyticsEvent.WorkoutCompleted ->
            "workout_completed" to Bundle().apply {
                putString("plan_id", planId)
                putInt("duration_minutes", durationMinutes)
            }

        is AnalyticsEvent.WorkoutFormAnalysisOpened ->
            "workout_form_analysis_opened" to null

        is AnalyticsEvent.AiCoachMessageSent ->
            "ai_coach_message_sent" to null

        is AnalyticsEvent.AiCoachResponseReceived ->
            "ai_coach_response_received" to Bundle().apply {
                putString("provider_type", providerType)
            }

        is AnalyticsEvent.FoodRecognitionUsed ->
            "food_recognition_used" to null

        is AnalyticsEvent.BarcodeScanned ->
            "barcode_scanned" to null

        is AnalyticsEvent.SubscriptionCtaClicked ->
            "subscription_cta_clicked" to Bundle().apply {
                putString("source", source)
            }

        is AnalyticsEvent.SubscriptionPurchased ->
            "subscription_purchased" to Bundle().apply {
                putString("tier", tier)
            }

        is AnalyticsEvent.ProgressPhotoAdded ->
            "progress_photo_added" to null

        is AnalyticsEvent.WeightLogged ->
            "weight_logged" to null

        is AnalyticsEvent.BodyMeasurementLogged ->
            "body_measurement_logged" to null

        is AnalyticsEvent.AiConsentGranted ->
            "ai_consent_granted" to null

        is AnalyticsEvent.AiConsentRevoked ->
            "ai_consent_revoked" to null

        is AnalyticsEvent.ScreenViewed ->
            FirebaseAnalytics.Event.SCREEN_VIEW to Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            }

        is AnalyticsEvent.AppErrorOccurred ->
            "app_error_occurred" to Bundle().apply {
                putString("error", error)
            }
    }
}
