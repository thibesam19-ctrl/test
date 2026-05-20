package com.axiom.aicoach.di

import com.axiom.aicoach.billing.EntitlementManager
import com.axiom.aicoach.billing.SubscriptionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideEntitlementManager(): EntitlementManager = EntitlementManager()

    @Provides
    @Singleton
    fun provideSubscriptionManager(em: EntitlementManager): SubscriptionManager =
        SubscriptionManager(em)
}
