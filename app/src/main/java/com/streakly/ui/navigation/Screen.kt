package com.streakly.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Today : Screen("today", "Today", Icons.Default.Home)
    object Fitness : Screen("fitness", "Fitness", Icons.Default.DirectionsRun)
    object Sleep : Screen("sleep", "Sleep", Icons.Default.Bedtime)
    object Health : Screen("health", "Health", Icons.Default.Favorite)
    object Journal : Screen("journal", "Journal", Icons.Default.Book)
    object Habits : Screen("habits", "Habits", Icons.Default.CheckCircle)
    object Chat : Screen("chat", "AI Coach", Icons.Default.Chat)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Onboarding : Screen("onboarding", "Welcome")

    companion object {
        val bottomItems = listOf(Today, Fitness, Sleep, Health, Journal)
    }
}
