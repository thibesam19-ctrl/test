package com.axiom.aicoach.di

import android.content.Context
import com.axiom.aicoach.network.ConnectivityObserver
import com.axiom.aicoach.network.SyncCoordinator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver =
        ConnectivityObserver(context)

    @Provides @Singleton
    fun provideSyncCoordinator(connectivityObserver: ConnectivityObserver): SyncCoordinator =
        SyncCoordinator(connectivityObserver)
}
