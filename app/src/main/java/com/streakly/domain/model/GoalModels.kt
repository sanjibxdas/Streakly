package com.streakly.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val date: String,
    val completed: Boolean = false,
    val notificationTime: String? = null
)

data class ChecklistItem(
    val id: Long = 0,
    val title: String,
    val date: String,
    val completed: Boolean = false,
    val notificationTime: String? = null
)

data class DailyChecklist(
    val goals: List<Goal> = emptyList(),
    val items: List<ChecklistItem> = emptyList()
)
