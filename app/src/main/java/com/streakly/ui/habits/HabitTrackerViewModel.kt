package com.streakly.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.model.Habit
import com.streakly.domain.model.HabitWithCompletion
import com.streakly.domain.model.TimeOfDay
import com.streakly.domain.repository.HabitRepository
import com.streakly.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitTrackerUiState(
    val isCreateDialogOpen: Boolean = false
)

@HiltViewModel
class HabitTrackerViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitTrackerUiState())
    val uiState: StateFlow<HabitTrackerUiState> = _uiState.asStateFlow()

    val habits: StateFlow<List<HabitWithCompletion>> = habitRepository.getHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun openCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = true) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false) }
    }

    fun toggleHabit(habitId: Long, date: String = DateUtils.getTodayIso()) {
        viewModelScope.launch {
            habitRepository.toggleHabitCompletion(habitId, date)
        }
    }

    fun createHabit(name: String, timeOfDay: TimeOfDay, colorHex: String) {
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                timeOfDay = timeOfDay,
                colorHex = colorHex
            )
            habitRepository.createHabit(habit)
            closeCreateDialog()
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habitId)
        }
    }
}
