package com.axiom.aicoach.di

import android.content.Context
import androidx.room.Room
import com.axiom.aicoach.data.local.database.AxiomDatabase
import com.axiom.aicoach.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AxiomDatabase =
        Room.databaseBuilder(context, AxiomDatabase::class.java, "axiom.db")
            .build()

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
