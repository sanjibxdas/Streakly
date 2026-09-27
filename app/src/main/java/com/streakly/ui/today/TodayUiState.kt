package com.streakly.ui.today

import com.streakly.domain.model.DailyHealthSummary
import com.streakly.domain.model.HourlySteps
import com.streakly.domain.model.WaterIntake

data class TodayUiState(
    val isLoading: Boolean = false,
    val summary: DailyHealthSummary? = null,
    val hourlySteps: List<HourlySteps> = emptyList(),
    val waterIntake: WaterIntake = WaterIntake(date = "", glassesCount = 0),
    val userGreeting: String = "Good day, Alex",
    val todayDateFormatted: String = ""
)
