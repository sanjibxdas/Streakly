package com.streakly.data.remote.nvidia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NvidiaChatRequest(
    val model: String,
    val messages: List<NvidiaMessage>,
    val tools: List<NvidiaTool>? = null,
    @Json(name = "tool_choice")
    val toolChoice: String? = if (tools != null) "auto" else null,
    val temperature: Double? = 0.7,
    @Json(name = "max_tokens")
    val maxTokens: Int? = 1024
)

@JsonClass(generateAdapter = true)
data class NvidiaMessage(
    val role: String,
    val content: String? = null,
    @Json(name = "tool_calls")
    val toolCalls: List<NvidiaToolCall>? = null,
    @Json(name = "tool_call_id")
    val toolCallId: String? = null,
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class NvidiaTool(
    val type: String = "function",
    val function: NvidiaFunction
)

@JsonClass(generateAdapter = true)
data class NvidiaFunction(
    val name: String,
    val description: String,
    val parameters: NvidiaParameters
)

@JsonClass(generateAdapter = true)
data class NvidiaParameters(
    val type: String = "object",
    val properties: Map<String, NvidiaProperty> = emptyMap(),
    val required: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class NvidiaProperty(
    val type: String,
    val description: String,
    val enum: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class NvidiaChatResponse(
    val id: String? = null,
    val choices: List<NvidiaChoice> = emptyList(),
    val usage: NvidiaUsage? = null
)

@JsonClass(generateAdapter = true)
data class NvidiaChoice(
    val index: Int = 0,
    val message: NvidiaMessage,
    @Json(name = "finish_reason")
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class NvidiaToolCall(
    val id: String,
    val type: String = "function",
    val function: NvidiaCallFunction
)

@JsonClass(generateAdapter = true)
data class NvidiaCallFunction(
    val name: String,
    val arguments: String
)

@JsonClass(generateAdapter = true)
data class NvidiaUsage(
    @Json(name = "prompt_tokens")
    val promptTokens: Int = 0,
    @Json(name = "completion_tokens")
    val completionTokens: Int = 0,
    @Json(name = "total_tokens")
    val totalTokens: Int = 0
)
