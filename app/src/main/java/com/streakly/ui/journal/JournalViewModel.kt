package com.streakly.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.domain.model.HealthSnapshot
import com.streakly.domain.model.JournalEntry
import com.streakly.domain.model.Mood
import com.streakly.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val searchQuery: String = "",
    val isCreateDialogOpen: Boolean = false
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    val entries: StateFlow<List<JournalEntry>> = _uiState
        .flatMapLatest { state ->
            if (state.searchQuery.isBlank()) {
                journalRepository.getAllEntries()
            } else {
                journalRepository.searchEntries(state.searchQuery)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = true) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(isCreateDialogOpen = false) }
    }

    fun createEntry(
        title: String,
        body: String,
        mood: Mood,
        energyLevel: Int,
        tags: List<String>,
        snapshot: HealthSnapshot? = null
    ) {
        viewModelScope.launch {
            val entry = JournalEntry(
                id = System.currentTimeMillis(),
                title = title,
                body = body,
                mood = mood,
                energyLevel = energyLevel,
                tags = tags,
                healthSnapshot = snapshot,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            journalRepository.saveEntry(entry)
            closeCreateDialog()
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            journalRepository.deleteEntry(id)
        }
    }
}
