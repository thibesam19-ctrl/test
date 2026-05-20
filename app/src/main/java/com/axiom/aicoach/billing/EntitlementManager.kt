package com.axiom.aicoach.billing

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class EntitlementManager @Inject constructor() {

    private val _tierFlow = MutableStateFlow(SubscriptionTier.FREE)

    init {
        refreshTier()
    }

    /**
     * Returns a [StateFlow] that emits the current [SubscriptionTier] and any future updates.
     * Exposed as [Flow] in the public API to satisfy interface expectations; callers that need
     * direct [StateFlow] access can use [SubscriptionManager.currentTier].
     */
    fun getCurrentTier(): Flow<SubscriptionTier> = _tierFlow.asStateFlow()

    /** Internal accessor that preserves [StateFlow] type for [SubscriptionManager]. */
    internal fun getCurrentTierAsStateFlow(): StateFlow<SubscriptionTier> = _tierFlow.asStateFlow()

    /**
     * Returns whether the given named feature is available under the current subscription tier.
     *
     * Supported feature names:
     *  - "ai_coach"
     *  - "body_analysis"
     *  - "form_analysis"
     *  - "food_scan"
     *  - "advanced_insights"
     *  - "export"
     */
    fun canUseFeature(feature: String): Boolean {
        val tierFeatures = _tierFlow.value.features()
        return when (feature) {
            "ai_coach" -> tierFeatures.dailyAiMessages == -1 || tierFeatures.dailyAiMessages > 0
            "body_analysis" -> tierFeatures.bodyAnalysisEnabled
            "form_analysis" -> tierFeatures.formAnalysisEnabled
            "food_scan" -> tierFeatures.foodScansPerDay == -1 || tierFeatures.foodScansPerDay > 0
            "advanced_insights" -> tierFeatures.advancedInsightsEnabled
            "export" -> tierFeatures.exportEnabled
            else -> false
        }
    }

    /** Restores previous purchases from RevenueCat and refreshes the cached tier. */
    suspend fun restorePurchases() {
        try {
            val customerInfo = suspendCoroutine { cont ->
                Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
                    override fun onReceived(customerInfo: CustomerInfo) {
                        cont.resume(customerInfo)
                    }

                    override fun onError(error: PurchasesError) {
                        cont.resume(null)
                    }
                })
            }
            if (customerInfo != null) {
                _tierFlow.value = customerInfo.toSubscriptionTier()
            }
        } catch (e: Exception) {
            // RevenueCat not configured or other error — keep current tier
        }
    }

    // ── Internal helpers ────────────────────────────────────────────────────

    private fun refreshTier() {
        try {
            Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    _tierFlow.value = customerInfo.toSubscriptionTier()
                }

                override fun onError(error: PurchasesError) {
                    _tierFlow.value = SubscriptionTier.FREE
                }
            })
        } catch (e: Exception) {
            // RevenueCat not yet configured (e.g. during tests) — default to FREE
            _tierFlow.value = SubscriptionTier.FREE
        }
    }

    private fun CustomerInfo.toSubscriptionTier(): SubscriptionTier {
        val active = entitlements.active
        return when {
            active.containsKey("unlimited") -> SubscriptionTier.UNLIMITED
            active.containsKey("premium") -> SubscriptionTier.PREMIUM
            else -> SubscriptionTier.FREE
        }
    }
}
