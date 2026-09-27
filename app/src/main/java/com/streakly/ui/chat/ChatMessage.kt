package com.streakly.ui.chat

import com.streakly.data.remote.nvidia.NvidiaToolCall
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "tool", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolName: String? = null,
    val toolCallId: String? = null,
    val toolCalls: List<NvidiaToolCall>? = null,
    val isExecutingTool: Boolean = false
)
