package com.streakly.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.streakly.data.remote.nvidia.NvidiaNimClient
import com.streakly.domain.model.AppearanceSettings
import com.streakly.domain.model.NotificationSettings
import com.streakly.domain.model.ThemeMode
import com.streakly.domain.model.UserGoals
import com.streakly.domain.model.UserProfile
import com.streakly.domain.repository.SettingsRepository
import com.streakly.util.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHOTO = stringPreferencesKey("user_photo")
        val MEMBER_SINCE = stringPreferencesKey("member_since")

        val GOAL_STEPS = intPreferencesKey("goal_steps")
        val GOAL_ACTIVE_MIN = intPreferencesKey("goal_active_min")
        val GOAL_SLEEP_MIN = intPreferencesKey("goal_sleep_min")
        val GOAL_WATER = intPreferencesKey("goal_water")
        val GOAL_CARDIO = intPreferencesKey("goal_cardio")

        val NOTIF_HYDRATION = booleanPreferencesKey("notif_hydration")
        val NOTIF_BEDTIME = booleanPreferencesKey("notif_bedtime")
        val NOTIF_STREAK = booleanPreferencesKey("notif_streak")
        val NOTIF_NIGHT_TIME = stringPreferencesKey("notif_night_time")

        val THEME_MODE = stringPreferencesKey("theme_mode")

        val AI_API_KEY = stringPreferencesKey("ai_api_key")
        val AI_BASE_URL = stringPreferencesKey("ai_base_url")
        val AI_MODEL = stringPreferencesKey("ai_model")
    }

    override val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[PreferencesKeys.USER_NAME] ?: "Streakly User",
            email = prefs[PreferencesKeys.USER_EMAIL] ?: "user@streakly.app",
            photoUrl = prefs[PreferencesKeys.USER_PHOTO],
            memberSince = prefs[PreferencesKeys.MEMBER_SINCE] ?: "2024"
        )
    }

    override val userGoals: Flow<UserGoals> = context.dataStore.data.map { prefs ->
        UserGoals(
            dailySteps = prefs[PreferencesKeys.GOAL_STEPS] ?: 10000,
            activeMinutes = prefs[PreferencesKeys.GOAL_ACTIVE_MIN] ?: 30,
            sleepDurationMinutes = prefs[PreferencesKeys.GOAL_SLEEP_MIN] ?: 480,
            waterGlasses = prefs[PreferencesKeys.GOAL_WATER] ?: 8,
            weeklyCardioMinutes = prefs[PreferencesKeys.GOAL_CARDIO] ?: 150
        )
    }

    override val notificationSettings: Flow<NotificationSettings> = context.dataStore.data.map { prefs ->
        NotificationSettings(
            hydrationReminders = prefs[PreferencesKeys.NOTIF_HYDRATION] ?: true,
            bedtimeReminders = prefs[PreferencesKeys.NOTIF_BEDTIME] ?: true,
            streakWarnings = prefs[PreferencesKeys.NOTIF_STREAK] ?: true,
            nightReminderTime = prefs[PreferencesKeys.NOTIF_NIGHT_TIME] ?: "21:00"
        )
    }

    override val appearanceSettings: Flow<AppearanceSettings> = context.dataStore.data.map { prefs ->
        val modeStr = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val mode = try {
            ThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
        AppearanceSettings(themeMode = mode)
    }

    override val aiApiKey: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AI_API_KEY] ?: ""
    }

    override val aiBaseUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AI_BASE_URL] ?: NvidiaNimClient.DEFAULT_BASE_URL
    }

    override val aiModel: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AI_MODEL] ?: NvidiaNimClient.DEFAULT_MODEL
    }

    override suspend fun updateProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = profile.name
            prefs[PreferencesKeys.USER_EMAIL] = profile.email
            if (profile.photoUrl != null) {
                prefs[PreferencesKeys.USER_PHOTO] = profile.photoUrl
            } else {
                prefs.remove(PreferencesKeys.USER_PHOTO)
            }
            prefs[PreferencesKeys.MEMBER_SINCE] = profile.memberSince
        }
    }

    override suspend fun updateGoals(goals: UserGoals) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GOAL_STEPS] = goals.dailySteps
            prefs[PreferencesKeys.GOAL_ACTIVE_MIN] = goals.activeMinutes
            prefs[PreferencesKeys.GOAL_SLEEP_MIN] = goals.sleepDurationMinutes
            prefs[PreferencesKeys.GOAL_WATER] = goals.waterGlasses
            prefs[PreferencesKeys.GOAL_CARDIO] = goals.weeklyCardioMinutes
        }
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIF_HYDRATION] = settings.hydrationReminders
            prefs[PreferencesKeys.NOTIF_BEDTIME] = settings.bedtimeReminders
            prefs[PreferencesKeys.NOTIF_STREAK] = settings.streakWarnings
            prefs[PreferencesKeys.NOTIF_NIGHT_TIME] = settings.nightReminderTime
        }
        if (settings.bedtimeReminders) {
            NotificationScheduler.scheduleNightReminder(context, settings.nightReminderTime)
        }
    }

    override suspend fun updateAppearanceSettings(settings: AppearanceSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = settings.themeMode.name
        }
    }

    override suspend fun updateAiConfig(apiKey: String, baseUrl: String, model: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AI_API_KEY] = apiKey
            prefs[PreferencesKeys.AI_BASE_URL] = baseUrl
            prefs[PreferencesKeys.AI_MODEL] = model
        }
    }

    override suspend fun setNightNotificationTime(time: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIF_NIGHT_TIME] = time
        }
        NotificationScheduler.scheduleNightReminder(context, time)
    }
}
