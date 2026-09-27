package com.streakly.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakly.data.remote.nvidia.NvidiaChatRequest
import com.streakly.data.remote.nvidia.NvidiaFunctionCall
import com.streakly.data.remote.nvidia.NvidiaMessage
import com.streakly.data.remote.nvidia.NvidiaNimClient
import com.streakly.data.remote.nvidia.NvidiaParameters
import com.streakly.data.remote.nvidia.NvidiaProperty
import com.streakly.data.remote.nvidia.NvidiaTool
import com.streakly.data.remote.nvidia.NvidiaToolCall
import com.streakly.data.remote.nvidia.NvidiaToolDefinition
import com.streakly.domain.model.Habit
import com.streakly.domain.model.JournalEntry
import com.streakly.domain.model.Mood
import com.streakly.domain.model.TimeOfDay
import com.streakly.domain.repository.GoalRepository
import com.streakly.domain.repository.HabitRepository
import com.streakly.domain.repository.HealthRepository
import com.streakly.domain.repository.JournalRepository
import com.streakly.domain.repository.SettingsRepository
import com.streakly.domain.repository.WaterRepository
import com.streakly.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val nvidiaNimClient: NvidiaNimClient,
    private val settingsRepository: SettingsRepository,
    private val habitRepository: HabitRepository,
    private val waterRepository: WaterRepository,
    private val goalRepository: GoalRepository,
    private val healthRepository: HealthRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMessage(
                    role = "assistant",
                    content = "Hello! I am your Streakly AI Coach. I can analyze your biometric trends, log water intake, track habits, review your sleep cycles, and guide your daily routines. What would you like to achieve today?"
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val tools = listOf(
        NvidiaTool(
            type = "function",
            function = NvidiaToolDefinition(
                name = "get_health_metrics",
                description = "Retrieve today's health metrics including steps, active calories, sleep duration, readiness score, and resting heart rate.",
                parameters = NvidiaParameters(
                    type = "object",
                    properties = emptyMap(),
                    required = emptyList()
                )
            )
        ),
        NvidiaTool(
            type = "function",
            function = NvidiaToolDefinition(
                name = "log_water",
                description = "Log water intake in milliliters (e.g., 250, 500, 750).",
                parameters = NvidiaParameters(
                    type = "object",
                    properties = mapOf(
                        "amount_ml" to NvidiaProperty(
                            type = "integer",
                            description = "The volume of water to log in milliliters"
                        )
                    ),
                    required = listOf("amount_ml")
                )
            )
        ),
        NvidiaTool(
            type = "function",
            function = NvidiaToolDefinition(
                name = "get_habits",
                description = "Retrieve user's active habits, completion status for today, and streak counts.",
                parameters = NvidiaParameters(
                    type = "object",
                    properties = emptyMap(),
                    required = emptyList()
                )
            )
        ),
        NvidiaTool(
            type = "function",
            function = NvidiaToolDefinition(
                name = "create_habit",
                description = "Create a new habit routine.",
                parameters = NvidiaParameters(
                    type = "object",
                    properties = mapOf(
                        "name" to NvidiaProperty(
                            type = "string",
                            description = "Name of the habit (e.g. 'Morning Stretch', 'Read 20 pages')"
                        ),
                        "time_of_day" to NvidiaProperty(
                            type = "string",
                            description = "Time of day: MORNING, AFTERNOON, EVENING, ANYTIME",
                            enum = listOf("MORNING", "AFTERNOON", "EVENING", "ANYTIME")
                        )
                    ),
                    required = listOf("name", "time_of_day")
                )
            )
        )
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(role = "user", content = userText)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val profile = settingsRepository.userProfile.first()
                val apiKey = profile.nvidiaApiKey.trim()

                if (apiKey.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages + ChatMessage(
                                role = "assistant",
                                content = "NVIDIA NIM API key is not configured. Please add your API key in Settings to enable the AI Coach."
                            )
                        )
                    }
                    return@launch
                }

                executeConversationLoop(apiKey, profile.selectedModel)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeToolStatus = null,
                        errorMessage = e.message ?: "Failed to generate AI response",
                        messages = it.messages + ChatMessage(
                            role = "assistant",
                            content = "I encountered an error connecting to the NVIDIA NIM inference service. Please check your network and API key."
                        )
                    )
                }
            }
        }
    }

    private suspend fun executeConversationLoop(apiKey: String, model: String) {
        var loopCount = 0
        val maxTurns = 3

        while (loopCount < maxTurns) {
            loopCount++

            val currentMessages = _uiState.value.messages.map { msg ->
                NvidiaMessage(
                    role = msg.role,
                    content = msg.content,
                    name = msg.toolName,
                    toolCallId = msg.toolCallId
                )
            }

            val systemMessage = NvidiaMessage(
                role = "system",
                content = "You are Streakly AI Coach, an expert health, fitness, habit, and wellbeing autonomous coach. You have direct access to user health biometrics, hydration tracking, habit formation systems, goals, and journal. Keep responses concise, supportive, actionable, and science-grounded. Use available tools when appropriate to inspect or record user data."
            )

            val request = NvidiaChatRequest(
                model = model.ifBlank { "meta/llama-3.3-70b-instruct" },
                messages = listOf(systemMessage) + currentMessages,
                tools = tools,
                temperature = 0.5,
                maxTokens = 1024
            )

            val response = nvidiaNimClient.createChatCompletion("Bearer $apiKey", request)
            val choice = response.choices.firstOrNull()

            if (choice == null) {
                _uiState.update { it.copy(isLoading = false) }
                break
            }

            val assistantMessage = choice.message
            val toolCalls = assistantMessage.toolCalls

            if (!toolCalls.isNullOrEmpty()) {
                // Handle Tool Calls
                for (toolCall in toolCalls) {
                    _uiState.update { it.copy(activeToolStatus = "Executing ${toolCall.function.name}...") }

                    val toolResult = executeTool(toolCall.function)

                    val toolMessage = ChatMessage(
                        role = "tool",
                        content = toolResult,
                        toolName = toolCall.function.name,
                        toolCallId = toolCall.id
                    )

                    _uiState.update { it.copy(messages = it.messages + toolMessage) }
                }
                // Continue loop with tool outputs
            } else {
                // Assistant returned final message
                val responseText = assistantMessage.content ?: "Done."
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeToolStatus = null,
                        messages = it.messages + ChatMessage(role = "assistant", content = responseText)
                    )
                }
                break
            }
        }

        _uiState.update { it.copy(isLoading = false, activeToolStatus = null) }
    }

    private suspend fun executeTool(functionCall: NvidiaFunctionCall): String {
        return try {
            when (functionCall.name) {
                "get_health_metrics" -> {
                    val summary = healthRepository.getTodayHealthSummary().first()
                    val json = JSONObject()
                    json.put("steps", summary.steps)
                    json.put("stepGoal", summary.stepGoal)
                    json.put("caloriesBurned", summary.caloriesBurned)
                    json.put("waterConsumedMl", summary.waterConsumedMl)
                    json.put("waterGoalMl", summary.waterGoalMl)
                    json.put("sleepDurationMinutes", summary.sleepDurationMinutes)
                    json.put("readinessScore", summary.readinessScore.score)
                    json.put("readinessDescription", summary.readinessScore.description)
                    json.toString()
                }
                "log_water" -> {
                    val args = JSONObject(functionCall.arguments)
                    val amount = args.optInt("amount_ml", 250)
                    waterRepository.logWater(amount)
                    val total = waterRepository.getTodayTotalMl(DateUtils.getTodayIso())
                    val json = JSONObject()
                    json.put("status", "success")
                    json.put("logged_ml", amount)
                    json.put("new_total_ml", total)
                    json.toString()
                }
                "get_habits" -> {
                    val habitList = habitRepository.getAllHabits()
                    val json = JSONObject()
                    val array = org.json.JSONArray()
                    for (h in habitList) {
                        val item = JSONObject()
                        item.put("id", h.id)
                        item.put("name", h.name)
                        item.put("timeOfDay", h.timeOfDay.name)
                        array.put(item)
                    }
                    json.put("habits", array)
                    json.toString()
                }
                "create_habit" -> {
                    val args = JSONObject(functionCall.arguments)
                    val name = args.getString("name")
                    val timeOfDayStr = args.optString("time_of_day", "ANYTIME")
                    val timeOfDay = try {
                        TimeOfDay.valueOf(timeOfDayStr.uppercase())
                    } catch (e: Exception) {
                        TimeOfDay.ANYTIME
                    }
                    val habitId = habitRepository.createHabit(
                        Habit(
                            name = name,
                            timeOfDay = timeOfDay,
                            colorHex = "#5B6EF5"
                        )
                    )
                    val json = JSONObject()
                    json.put("status", "success")
                    json.put("habit_id", habitId)
                    json.put("name", name)
                    json.toString()
                }
                else -> {
                    val json = JSONObject()
                    json.put("error", "Unknown tool: ${functionCall.name}")
                    json.toString()
                }
            }
        } catch (e: Exception) {
            val json = JSONObject()
            json.put("error", e.message ?: "Failed to execute tool")
            json.toString()
        }
    }
}
