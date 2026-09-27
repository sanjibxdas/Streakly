package com.streakly.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.streakly.data.local.dao.ChecklistItemDao
import com.streakly.data.local.dao.GoalDao
import com.streakly.data.local.dao.HabitDao
import com.streakly.data.local.dao.HabitLogDao
import com.streakly.data.local.dao.JournalEntryDao
import com.streakly.data.local.dao.WaterLogDao
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
    @ApplicationContext private val context: Context,
    private val waterLogDao: WaterLogDao,
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val journalEntryDao: JournalEntryDao,
    private val goalDao: GoalDao,
    private val checklistItemDao: ChecklistItemDao
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
        val DAILY_WATER_GOAL = intPreferencesKey("daily_water_goal")
        val DAILY_STEP_GOAL = intPreferencesKey("daily_step_goal")
        val DAILY_SLEEP_GOAL = intPreferencesKey("daily_sleep_goal")
        val DAILY_CALORIE_GOAL = intPreferencesKey("daily_calorie_goal")
        val NVIDIA_API_KEY = stringPreferencesKey("nvidia_api_key")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val HYDRATION_REMINDER_ENABLED = booleanPreferencesKey("hydration_reminder_enabled")
        val HEALTH_CONNECT_SYNC_ENABLED = booleanPreferencesKey("health_connect_sync_enabled")
    }

    override val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[PreferencesKeys.USER_NAME] ?: "Streakly User",
            email = prefs[PreferencesKeys.USER_EMAIL] ?: "user@streakly.app",
            photoUrl = prefs[PreferencesKeys.USER_PHOTO],
            memberSince = prefs[PreferencesKeys.MEMBER_SINCE] ?: "2024",
            nvidiaApiKey = prefs[PreferencesKeys.NVIDIA_API_KEY] ?: "",
            selectedModel = prefs[PreferencesKeys.SELECTED_MODEL] ?: "meta/llama-3.3-70b-instruct",
            isDailyReminderEnabled = prefs[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: true,
            isHydrationReminderEnabled = prefs[PreferencesKeys.HYDRATION_REMINDER_ENABLED] ?: true,
            isHealthConnectSyncEnabled = prefs[PreferencesKeys.HEALTH_CONNECT_SYNC_ENABLED] ?: false,
            dailyWaterGoalMl = prefs[PreferencesKeys.DAILY_WATER_GOAL] ?: 2500,
            dailyStepGoal = prefs[PreferencesKeys.DAILY_STEP_GOAL] ?: 10000,
            dailySleepGoalMinutes = prefs[PreferencesKeys.DAILY_SLEEP_GOAL] ?: 480,
            dailyCalorieGoal = prefs[PreferencesKeys.DAILY_CALORIE_GOAL] ?: 2200,
            themeMode = runCatching {
                ThemeMode.valueOf(prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM)
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
        AppearanceSettings(
            themeMode = runCatching {
                ThemeMode.valueOf(prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM)
        )
    }

    override val aiApiKey: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.AI_API_KEY] ?: "" }
    override val aiBaseUrl: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.AI_BASE_URL] ?: NvidiaNimClient.DEFAULT_BASE_URL }
    override val aiModel: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.AI_MODEL] ?: NvidiaNimClient.DEFAULT_MODEL }

    override suspend fun updateProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = profile.name
            prefs[PreferencesKeys.USER_EMAIL] = profile.email
            if (profile.photoUrl == null) prefs.remove(PreferencesKeys.USER_PHOTO) else prefs[PreferencesKeys.USER_PHOTO] = profile.photoUrl
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
        if (settings.bedtimeReminders) NotificationScheduler.scheduleNightReminder(context, settings.nightReminderTime)
    }

    override suspend fun updateAppearanceSettings(settings: AppearanceSettings) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = settings.themeMode.name }
    }

    override suspend fun updateAiConfig(apiKey: String, baseUrl: String, model: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AI_API_KEY] = apiKey
            prefs[PreferencesKeys.AI_BASE_URL] = baseUrl
            prefs[PreferencesKeys.AI_MODEL] = model
        }
    }

    override suspend fun setNightNotificationTime(time: String) {
        context.dataStore.edit { it[PreferencesKeys.NOTIF_NIGHT_TIME] = time }
        NotificationScheduler.scheduleNightReminder(context, time)
    }

    override suspend fun updateThemeMode(mode: ThemeMode) { context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name } }

    override suspend fun updateWaterGoal(goalMl: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.DAILY_WATER_GOAL] = goalMl
            it[PreferencesKeys.GOAL_WATER] = (goalMl / 250).coerceAtLeast(1)
        }
    }

    override suspend fun updateStepGoal(steps: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.DAILY_STEP_GOAL] = steps
            it[PreferencesKeys.GOAL_STEPS] = steps
        }
    }

    override suspend fun updateSleepGoal(minutes: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.DAILY_SLEEP_GOAL] = minutes
            it[PreferencesKeys.GOAL_SLEEP_MIN] = minutes
        }
    }

    override suspend fun updateCalorieGoal(calories: Int) {
        context.dataStore.edit { it[PreferencesKeys.DAILY_CALORIE_GOAL] = calories }
    }

    override suspend fun updateNvidiaApiKey(apiKey: String) {
        context.dataStore.edit {
            it[PreferencesKeys.NVIDIA_API_KEY] = apiKey
            it[PreferencesKeys.AI_API_KEY] = apiKey
        }
    }

    override suspend fun updateSelectedModel(model: String) {
        context.dataStore.edit {
            it[PreferencesKeys.SELECTED_MODEL] = model
            it[PreferencesKeys.AI_MODEL] = model
        }
    }

    override suspend fun toggleDailyReminder(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
            it[PreferencesKeys.NOTIF_BEDTIME] = enabled
        }
    }

    override suspend fun toggleHydrationReminder(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.HYDRATION_REMINDER_ENABLED] = enabled
            it[PreferencesKeys.NOTIF_HYDRATION] = enabled
        }
    }

    override suspend fun toggleHealthConnectSync(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HEALTH_CONNECT_SYNC_ENABLED] = enabled }
    }

    override suspend fun resetAllData() {
        waterLogDao.deleteAll()
        habitDao.deleteAll()
        habitLogDao.deleteAll()
        journalEntryDao.deleteAll()
        goalDao.deleteAll()
        checklistItemDao.deleteAll()

        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.DAILY_WATER_GOAL] = 2500
            prefs[PreferencesKeys.DAILY_STEP_GOAL] = 10000
            prefs[PreferencesKeys.DAILY_SLEEP_GOAL] = 480
            prefs[PreferencesKeys.DAILY_CALORIE_GOAL] = 2200
            prefs[PreferencesKeys.GOAL_STEPS] = 10000
            prefs[PreferencesKeys.GOAL_WATER] = 8
            prefs[PreferencesKeys.GOAL_SLEEP_MIN] = 480
            prefs[PreferencesKeys.NVIDIA_API_KEY] = ""
            prefs[PreferencesKeys.AI_API_KEY] = ""
            prefs[PreferencesKeys.SELECTED_MODEL] = "meta/llama-3.3-70b-instruct"
            prefs[PreferencesKeys.AI_MODEL] = "meta/llama-3.3-70b-instruct"
            prefs[PreferencesKeys.DAILY_REMINDER_ENABLED] = true
            prefs[PreferencesKeys.NOTIF_BEDTIME] = true
            prefs[PreferencesKeys.HYDRATION_REMINDER_ENABLED] = true
            prefs[PreferencesKeys.NOTIF_HYDRATION] = true
            prefs[PreferencesKeys.HEALTH_CONNECT_SYNC_ENABLED] = false
        }
    }
}
