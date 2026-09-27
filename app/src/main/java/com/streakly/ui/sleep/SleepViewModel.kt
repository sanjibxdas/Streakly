package com.streakly.ui.sleep

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
class SleepViewModel @Inject constructor(
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    init {
        loadSleepData()
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            combine(
                healthRepository.getRecentSleep(),
                healthRepository.getSleepHistory(7),
                healthRepository.getVitalsSummary()
            ) { recent, history, vitals ->
                _uiState.update { current ->
                    current.copy(
                        recentSleep = recent,
                        sleepHistory = history,
                        vitals = vitals,
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }
}
