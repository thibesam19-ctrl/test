package com.axiom.aicoach.analytics

sealed class AnalyticsEvent {
    object OnboardingStarted : AnalyticsEvent()
    data class OnboardingStepCompleted(val step: Int) : AnalyticsEvent()
    object OnboardingCompleted : AnalyticsEvent()
    data class WorkoutStarted(val planId: String) : AnalyticsEvent()
    data class WorkoutCompleted(val planId: String, val durationMinutes: Int) : AnalyticsEvent()
    object WorkoutFormAnalysisOpened : AnalyticsEvent()
    object AiCoachMessageSent : AnalyticsEvent()
    data class AiCoachResponseReceived(val providerType: String) : AnalyticsEvent()
    object FoodRecognitionUsed : AnalyticsEvent()
    object BarcodeScanned : AnalyticsEvent()
    data class SubscriptionCtaClicked(val source: String) : AnalyticsEvent()
    data class SubscriptionPurchased(val tier: String) : AnalyticsEvent()
    object ProgressPhotoAdded : AnalyticsEvent()
    object WeightLogged : AnalyticsEvent()
    object BodyMeasurementLogged : AnalyticsEvent()
    object AiConsentGranted : AnalyticsEvent()
    object AiConsentRevoked : AnalyticsEvent()
    data class ScreenViewed(val screenName: String) : AnalyticsEvent()
    data class AppErrorOccurred(val error: String) : AnalyticsEvent()
}
