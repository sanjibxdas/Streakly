package com.streakly.ui.fitness

import com.streakly.domain.model.StepsByDay
import com.streakly.domain.model.VitalsSummary
import com.streakly.domain.model.WorkoutSession

data class FitnessUiState(
    val isLoading: Boolean = false,
    val weeklySteps: List<StepsByDay> = emptyList(),
    val recentWorkouts: List<WorkoutSession> = emptyList(),
    val vitals: VitalsSummary? = null,
    val cardioMinutesThisWeek: Int = 112,
    val cardioTargetMinutes: Int = 150
)
