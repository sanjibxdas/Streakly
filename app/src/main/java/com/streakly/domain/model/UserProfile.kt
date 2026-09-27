package com.streakly.domain.model

data class UserProfile(
    val name: String = "Streakly User",
    val email: String = "",
    val photoUrl: String? = null,
    val memberSince: String = "2024"
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
