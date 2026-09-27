package com.streakly.di

import com.streakly.data.repository.GoalRepositoryImpl
import com.streakly.data.repository.HabitRepositoryImpl
import com.streakly.data.repository.HealthRepositoryImpl
import com.streakly.data.repository.JournalRepositoryImpl
import com.streakly.data.repository.SettingsRepositoryImpl
import com.streakly.data.repository.WaterRepositoryImpl
import com.streakly.domain.repository.GoalRepository
import com.streakly.domain.repository.HabitRepository
import com.streakly.domain.repository.HealthRepository
import com.streakly.domain.repository.JournalRepository
import com.streakly.domain.repository.SettingsRepository
import com.streakly.domain.repository.WaterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWaterRepository(impl: WaterRepositoryImpl): WaterRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository
}
