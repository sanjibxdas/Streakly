package com.streakly.domain.model

data class WaterIntake(
    val date: String,
    val glassesCount: Int,
    val targetGlasses: Int = 8,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class MacroNutrient(
    val name: String,
    val currentGrams: Int,
    val targetGrams: Int,
    val colorHex: String
)

data class DailyNutrition(
    val caloriesCurrent: Int,
    val caloriesTarget: Int = 2200,
    val macros: List<MacroNutrient> = emptyList()
)
