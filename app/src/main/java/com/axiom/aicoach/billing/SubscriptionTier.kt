package com.axiom.aicoach.billing

enum class SubscriptionTier {
    FREE,        // Basic tracking, limited AI coach messages (5/day)
    PREMIUM,     // Full AI coach, insights, all features (no scan limit)
    UNLIMITED,   // Everything + unlimited food scans + priority AI
}

data class TierFeatures(
    val tier: SubscriptionTier,
    val dailyAiMessages: Int,      // -1 = unlimited
    val foodScansPerDay: Int,      // -1 = unlimited
    val bodyAnalysisEnabled: Boolean,
    val formAnalysisEnabled: Boolean,
    val advancedInsightsEnabled: Boolean,
    val exportEnabled: Boolean,
)

val FREE_FEATURES = TierFeatures(SubscriptionTier.FREE, 5, 3, false, false, false, false)
val PREMIUM_FEATURES = TierFeatures(SubscriptionTier.PREMIUM, -1, -1, true, true, true, true)
val UNLIMITED_FEATURES = TierFeatures(SubscriptionTier.UNLIMITED, -1, -1, true, true, true, true)

fun SubscriptionTier.features(): TierFeatures = when (this) {
    SubscriptionTier.FREE -> FREE_FEATURES
    SubscriptionTier.PREMIUM -> PREMIUM_FEATURES
    SubscriptionTier.UNLIMITED -> UNLIMITED_FEATURES
}
