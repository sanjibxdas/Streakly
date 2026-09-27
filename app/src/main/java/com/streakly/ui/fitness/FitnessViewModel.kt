package com.streakly.ui.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FitnessViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FitnessUiState())
    val uiState: StateFlow<FitnessUiState> = _uiState.asStateFlow()

    init {
        loadFitnessData()
    }

    private fun loadFitnessData() {
        viewModelScope.launch {
            combine(
                healthRepository.getWeeklySteps(),
                healthRepository.getRecentWorkouts(),
                healthRepository.getVitalsSummary()
            ) { weeklySteps, workouts, vitals ->
                val totalActiveMins = workouts.sumOf { it.durationMinutes }
                _uiState.update { current ->
                    current.copy(
                        weeklySteps = weeklySteps,
                        recentWorkouts = workouts,
                        vitals = vitals,
                        cardioMinutesThisWeek = totalActiveMins.coerceAtLeast(60),
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }
}
