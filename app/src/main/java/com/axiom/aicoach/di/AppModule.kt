package com.axiom.aicoach.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.axiom.aicoach.BuildConfig
import com.axiom.aicoach.data.local.database.AxiomDatabase
import com.axiom.aicoach.data.local.database.AxiomDatabase.Companion.MIGRATION_1_2
import com.google.firebase.auth.FirebaseAuth
import com.axiom.aicoach.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "axiom_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AxiomDatabase =
        Room.databaseBuilder(context, AxiomDatabase::class.java, "axiom.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides fun provideUserProfileDao(db: AxiomDatabase) = db.userProfileDao()
    @Provides fun provideFoodItemDao(db: AxiomDatabase) = db.foodItemDao()
    @Provides fun provideFoodLogDao(db: AxiomDatabase) = db.foodLogDao()
    @Provides fun provideExerciseDao(db: AxiomDatabase) = db.exerciseDao()
    @Provides fun provideWorkoutPlanDao(db: AxiomDatabase) = db.workoutPlanDao()
    @Provides fun provideWorkoutDao(db: AxiomDatabase) = db.workoutDao()
    @Provides fun provideWorkoutExerciseDao(db: AxiomDatabase) = db.workoutExerciseDao()
    @Provides fun provideWorkoutSessionDao(db: AxiomDatabase) = db.workoutSessionDao()
    @Provides fun provideExerciseLogDao(db: AxiomDatabase) = db.exerciseLogDao()
    @Provides fun provideSetLogDao(db: AxiomDatabase) = db.setLogDao()
    @Provides fun providePersonalRecordDao(db: AxiomDatabase) = db.personalRecordDao()
    @Provides fun provideWeightLogDao(db: AxiomDatabase) = db.weightLogDao()
    @Provides fun provideBodyMeasurementDao(db: AxiomDatabase) = db.bodyMeasurementDao()
    @Provides fun provideProgressPhotoDao(db: AxiomDatabase) = db.progressPhotoDao()
    @Provides fun provideWaterLogDao(db: AxiomDatabase) = db.waterLogDao()
    @Provides fun provideSleepLogDao(db: AxiomDatabase) = db.sleepLogDao()
    @Provides fun provideMoodLogDao(db: AxiomDatabase) = db.moodLogDao()
    @Provides fun provideStreakDao(db: AxiomDatabase) = db.streakDao()
    @Provides fun provideCoachMessageDao(db: AxiomDatabase) = db.coachMessageDao()
    @Provides fun provideNotificationPreferenceDao(db: AxiomDatabase) = db.notificationPreferenceDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                // Only log HTTP bodies in debug builds — never in release (keys would leak to logcat)
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("openai_api_key")
    fun provideOpenAiApiKey(): String = BuildConfig.OPENAI_API_KEY

    @Provides
    @Singleton
    @Named("claude_api_key")
    fun provideClaudeApiKey(): String = BuildConfig.CLAUDE_API_KEY

    @Provides
    @Singleton
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindNutritionRepository(impl: NutritionRepositoryImpl): NutritionRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: ProgressRepositoryImpl): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindCoachRepository(impl: CoachRepositoryImpl): CoachRepository
}
