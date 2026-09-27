package com.streakly.ui.sleep

import com.streakly.domain.model.SleepSessionData
import com.streakly.domain.model.VitalsSummary

data class SleepUiState(
    val isLoading: Boolean = false,
    val recentSleep: SleepSessionData? = null,
    val sleepHistory: List<SleepSessionData> = emptyList(),
    val vitals: VitalsSummary? = null
)
