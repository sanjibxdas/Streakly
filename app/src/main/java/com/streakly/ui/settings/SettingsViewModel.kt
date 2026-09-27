package com.streakly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.model.ThemeMode
import com.streakly.domain.model.UserProfile
import com.streakly.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = settingsRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(mode)
        }
    }

    fun updateWaterGoal(goalMl: Int) {
        viewModelScope.launch {
            settingsRepository.updateWaterGoal(goalMl)
        }
    }

    fun updateStepGoal(steps: Int) {
        viewModelScope.launch {
            settingsRepository.updateStepGoal(steps)
        }
    }

    fun updateSleepGoal(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateSleepGoal(minutes)
        }
    }

    fun updateCalorieGoal(calories: Int) {
        viewModelScope.launch {
            settingsRepository.updateCalorieGoal(calories)
        }
    }

    fun setNvidiaApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsRepository.updateNvidiaApiKey(apiKey)
        }
    }

    fun setSelectedModel(model: String) {
        viewModelScope.launch {
            settingsRepository.updateSelectedModel(model)
        }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleDailyReminder(enabled)
        }
    }

    fun toggleHydrationReminder(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleHydrationReminder(enabled)
        }
    }

    fun toggleHealthConnectSync(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleHealthConnectSync(enabled)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            settingsRepository.resetAllData()
        }
    }
}
