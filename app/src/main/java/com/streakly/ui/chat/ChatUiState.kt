package com.streakly.ui.chat

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val activeToolStatus: String? = null,
    val errorMessage: String? = null
)
