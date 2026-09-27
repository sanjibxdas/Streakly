package com.streakly.domain.model

enum class StreakType {
    OVERALL,
    WORKOUT,
    WATER,
    JOURNAL
}

data class StreakData(
    val type: StreakType,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCompletedDate: String,
    val isCompletedToday: Boolean
)
