package com.streakly.domain.model

enum class TimeOfDay {
    ANYTIME,
    MORNING,
    AFTERNOON,
    EVENING
}

data class Habit(
    val id: Long = 0,
    val name: String,
    val iconName: String = "star",
    val colorHex: String = "#5B6EF5",
    val frequency: List<String> = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"),
    val timeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    val linkedMetric: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val completed: Boolean,
    val completedAt: Long? = null
)

data class HabitDayStatus(
    val date: String,
    val dayLetter: String,
    val isCompleted: Boolean
)

data class HabitWithCompletion(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val past7Days: List<HabitDayStatus>
)
