package com.axiom.aicoach.analytics

interface AxiomAnalytics {
    fun track(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
}

object NoOpAnalytics : AxiomAnalytics {
    override fun track(event: AnalyticsEvent) = Unit
    override fun setUserId(userId: String) = Unit
    override fun setUserProperty(key: String, value: String) = Unit
}
