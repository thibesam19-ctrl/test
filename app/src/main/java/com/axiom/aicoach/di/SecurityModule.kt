package com.axiom.aicoach.di

import android.content.Context
import com.axiom.aicoach.security.SecureStorage
import com.axiom.aicoach.security.UserSession
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideSecureStorage(@ApplicationContext context: Context): SecureStorage =
        SecureStorage(context)

    @Provides
    @Singleton
    fun provideUserSession(auth: FirebaseAuth): UserSession = UserSession(auth)
}
