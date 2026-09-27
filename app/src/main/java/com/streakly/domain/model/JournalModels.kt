package com.streakly.domain.model

enum class Mood(val score: Int, val emoji: String, val label: String) {
    GREAT(5, "", "Great"),
    GOOD(4, "", "Good"),
    NEUTRAL(3, "", "Neutral"),
    BAD(2, "", "Bad"),
    TERRIBLE(1, "", "Terrible");

    companion object {
        fun fromScore(score: Int): Mood {
            return entries.find { it.score == score } ?: NEUTRAL
        }
    }
}

data class HealthSnapshot(
    val steps: Int,
    val sleepScore: Int?,
    val activeMinutes: Int,
    val calories: Int
)

data class JournalEntry(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val body: String,
    val mood: Mood = Mood.NEUTRAL,
    val energyLevel: Int = 3,
    val tags: List<String> = emptyList(),
    val healthSnapshot: HealthSnapshot? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
