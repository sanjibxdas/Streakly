package com.streakly.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.repository.HealthRepository
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
    private val waterRepository: WaterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TodayUiState(
            userGreeting = DateUtils.getGreeting("Alex"),
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
                waterRepository.getTodayWater()
            ) { summary, hourlySteps, waterIntake ->
                _uiState.update { current ->
                    current.copy(
                        summary = summary,
                        hourlySteps = hourlySteps,
                        waterIntake = waterIntake,
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
