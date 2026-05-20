package com.axiom.aicoach.billing

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionManager @Inject constructor(
    private val entitlementManager: EntitlementManager,
) {

    /** Exposes the current [SubscriptionTier] as a [StateFlow]. */
    val currentTier: StateFlow<SubscriptionTier> =
        entitlementManager.getCurrentTierAsStateFlow()

    /** Returns true when the user is on any paid tier. */
    val isPremium: Boolean
        get() = currentTier.value != SubscriptionTier.FREE

    /**
     * Returns whether the given named feature is unlocked for the current user.
     *
     * Delegates directly to [EntitlementManager.canUseFeature].
     */
    fun isFeatureUnlocked(feature: String): Boolean =
        entitlementManager.canUseFeature(feature)
}
