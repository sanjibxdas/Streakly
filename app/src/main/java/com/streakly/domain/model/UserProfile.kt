package com.streakly.domain.model

data class UserProfile(
    val name: String = "Streakly User",
    val email: String = "",
    val photoUrl: String? = null,
    val memberSince: String = "2024",
    val nvidiaApiKey: String = "",
    val selectedModel: String = "meta/llama-3.3-70b-instruct",
    val isDailyReminderEnabled: Boolean = true,
    val isHydrationReminderEnabled: Boolean = true,
    val isHealthConnectSyncEnabled: Boolean = false,
    val dailyWaterGoalMl: Int = 2500,
    val dailyStepGoal: Int = 10000,
    val dailySleepGoalMinutes: Int = 480,
    val dailyCalorieGoal: Int = 2200,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

data class UserGoals(
    val dailySteps: Int = 10000,
    val activeMinutes: Int = 30,
    val sleepDurationMinutes: Int = 480,
    val waterGlasses: Int = 8,
    val weeklyCardioMinutes: Int = 150
)

data class NotificationSettings(
    val hydrationReminders: Boolean = true,
    val bedtimeReminders: Boolean = true,
    val streakWarnings: Boolean = true,
    val nightReminderTime: String = "21:00"
)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

data class DisciplineBoardData(
    val completedCount: Int,
    val totalCount: Int,
    val percentage: Int,
    val nextMilestone: String
)
