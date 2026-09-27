package com.streakly.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.streakly.ui.auth.OnboardingScreen
import com.streakly.ui.chat.ChatScreen
import com.streakly.ui.fitness.FitnessScreen
import com.streakly.ui.habits.HabitTrackerScreen
import com.streakly.ui.health.HealthScreen
import com.streakly.ui.journal.JournalScreen
import com.streakly.ui.navigation.FloatingBottomNavBar
import com.streakly.ui.navigation.Screen
import com.streakly.ui.settings.SettingsScreen
import com.streakly.ui.sleep.SleepScreen
import com.streakly.ui.today.TodayScreen

@Composable
fun StreaklyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Today.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Today.route) {
                    TodayScreen(
                        onNavigateToHabits = { navController.navigate(Screen.Habits.route) },
                        onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }
                composable(Screen.Fitness.route) {
                    FitnessScreen()
                }
                composable(Screen.Sleep.route) {
                    SleepScreen()
                }
                composable(Screen.Health.route) {
                    HealthScreen()
                }
                composable(Screen.Journal.route) {
                    JournalScreen()
                }
                composable(Screen.Habits.route) {
                    HabitTrackerScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Chat.route) {
                    ChatScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onGetStarted = {
                            navController.navigate(Screen.Today.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
