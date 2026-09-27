package com.streakly.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.repository.HealthRepository
import com.streakly.domain.repository.SettingsRepository
import com.streakly.domain.repository.WaterRepository
import com.streakly.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val waterRepository: WaterRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TodayUiState(
            userGreeting = DateUtils.getGreeting("Streakly User"),
            todayDateFormatted = DateUtils.getDisplayDate()
        )
    )
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                healthRepository.getTodayHealthSummary(),
                healthRepository.getHourlyStepsToday(),
                waterRepository.getTodayWater(),
                settingsRepository.userProfile
            ) { summary, hourlySteps, waterIntake, profile ->
                val greeting = DateUtils.getGreeting(profile.name.ifBlank { "User" })
                val adjustedSummary = summary?.copy(
                    targetSteps = profile.dailyStepGoal
                )
                _uiState.update { current ->
                    current.copy(
                        summary = adjustedSummary ?: summary,
                        hourlySteps = hourlySteps,
                        waterIntake = waterIntake.copy(
                            targetGlasses = (profile.dailyWaterGoalMl / 250).coerceAtLeast(1)
                        ),
                        userGreeting = greeting,
                        todayDateFormatted = DateUtils.getDisplayDate(),
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }

    fun addWater() {
        viewModelScope.launch {
            waterRepository.addGlass()
        }
    }

    fun removeWater() {
        viewModelScope.launch {
            waterRepository.removeGlass()
        }
    }
}
