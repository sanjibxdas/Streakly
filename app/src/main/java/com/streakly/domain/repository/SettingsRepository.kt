package com.streakly.domain.repository

import com.streakly.domain.model.AppearanceSettings
import com.streakly.domain.model.NotificationSettings
import com.streakly.domain.model.UserGoals
import com.streakly.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userProfile: Flow<UserProfile>
    val userGoals: Flow<UserGoals>
    val notificationSettings: Flow<NotificationSettings>
    val appearanceSettings: Flow<AppearanceSettings>
    val aiApiKey: Flow<String>
    val aiBaseUrl: Flow<String>
    val aiModel: Flow<String>

    suspend fun updateProfile(profile: UserProfile)
    suspend fun updateGoals(goals: UserGoals)
    suspend fun updateNotificationSettings(settings: NotificationSettings)
    suspend fun updateAppearanceSettings(settings: AppearanceSettings)
    suspend fun updateAiConfig(apiKey: String, baseUrl: String, model: String)
    suspend fun setNightNotificationTime(time: String)
}
