package com.streakly.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.streakly.domain.model.ThemeMode
import com.streakly.ui.common.SectionHeader
import com.streakly.ui.common.StreaklyCard
import com.streakly.ui.theme.FlameOrange
import com.streakly.ui.theme.PrimaryBlue
import com.streakly.ui.theme.SecondaryGreen
import com.streakly.ui.theme.SlateBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    val models = listOf("meta/llama-3.3-70b-instruct", "meta/llama-3.1-405b-instruct", "mistralai/mixtral-8x22b-instruct-v0.1", "deepseek-ai/deepseek-r1")
    var isModelMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("Customize targets, AI keys, and appearance", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Appearance", subtitle = "App theme and display options")
        Spacer(modifier = Modifier.height(10.dp))
        StreaklyCard { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SettingLabel(Icons.Default.Palette, "Theme Mode", PrimaryBlue)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ThemeMode.entries.forEach { mode ->
                val selected = profile.themeMode == mode
                Button(onClick = { viewModel.setThemeMode(mode) }, colors = ButtonDefaults.buttonColors(containerColor = if (selected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)) { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
            } }
        } }
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "NVIDIA AI Engine", subtitle = "Inference & autonomous tool calling")
        Spacer(modifier = Modifier.height(10.dp))
        StreaklyCard { Column {
            SettingLabel(Icons.Default.AutoAwesome, "NVIDIA NIM API Key", PrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = profile.nvidiaApiKey, onValueChange = viewModel::setNvidiaApiKey, placeholder = { Text("nvapi-...") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(16.dp))
            SettingLabel(Icons.Default.DarkMode, "Model Architecture", SlateBlue)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = isModelMenuExpanded, onExpandedChange = { isModelMenuExpanded = !isModelMenuExpanded }) {
                OutlinedTextField(value = profile.selectedModel, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelMenuExpanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = isModelMenuExpanded, onDismissRequest = { isModelMenuExpanded = false }) { models.forEach { model -> DropdownMenuItem(text = { Text(model) }, onClick = { viewModel.setSelectedModel(model); isModelMenuExpanded = false }) } }
            }
        } }
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Daily Targets", subtitle = "Your baseline health & performance goals")
        Spacer(modifier = Modifier.height(10.dp))
        StreaklyCard { Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GoalEditRow(Icons.Default.DirectionsRun, "Daily Steps", "steps", profile.dailyStepGoal, PrimaryBlue, viewModel::updateStepGoal)
            GoalEditRow(Icons.Default.LocalDrink, "Hydration", "ml", profile.dailyWaterGoalMl, SecondaryGreen, viewModel::updateWaterGoal)
            GoalEditRow(Icons.Default.Nightlight, "Sleep Duration", "min", profile.dailySleepGoalMinutes, SlateBlue, viewModel::updateSleepGoal)
            GoalEditRow(Icons.Default.LocalFireDepartment, "Active Calories", "kcal", profile.dailyCalorieGoal, FlameOrange, viewModel::updateCalorieGoal)
        } }
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Notifications & Sync", subtitle = "Automated reminders & background health sync")
        Spacer(modifier = Modifier.height(10.dp))
        StreaklyCard { Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ToggleRow(Icons.Default.Notifications, "Daily Evening Summary", "8:00 PM review of streaks and goals", FlameOrange, profile.isDailyReminderEnabled, viewModel::toggleDailyReminder)
            ToggleRow(Icons.Default.LocalDrink, "Hydration Nudges", "Periodic alerts during waking hours", SecondaryGreen, profile.isHydrationReminderEnabled, viewModel::toggleHydrationReminder)
            ToggleRow(Icons.Default.Sync, "Health Connect Auto-Sync", "Background sync every 15 minutes", PrimaryBlue, profile.isHealthConnectSyncEnabled, viewModel::toggleHealthConnectSync)
        } }
        Spacer(modifier = Modifier.height(24.dp))
        StreaklyCard { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Reset All Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFE05C5C)); Text("Clear history, water logs, and habits", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
            Button(onClick = { showResetDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05C5C).copy(alpha = 0.15f), contentColor = Color(0xFFE05C5C))) { Text("Reset") }
        } }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Streakly v1.0.0 • Autonomous AI Health OS", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(80.dp))
    }
    if (showResetDialog) AlertDialog(onDismissRequest = { showResetDialog = false }, icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE05C5C)) }, title = { Text("Reset All Data?") }, text = { Text("This will permanently delete all your tracked water, habit records, streaks, and journals. This cannot be undone.") }, confirmButton = { Button(onClick = { viewModel.resetAllData(); showResetDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05C5C))) { Text("Delete Everything") } }, dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Cancel") } })
}

@Composable
private fun SettingLabel(icon: ImageVector, title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp)) }; Spacer(modifier = Modifier.width(10.dp)); Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun GoalEditRow(icon: ImageVector, title: String, unit: String, value: Int, color: Color, onValueChange: (Int) -> Unit) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(16.dp)) }; Spacer(modifier = Modifier.width(10.dp)); Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium) }
        Row(verticalAlignment = Alignment.CenterVertically) { OutlinedTextField(value = textValue, onValueChange = { input -> textValue = input; input.toIntOrNull()?.let(onValueChange) }, modifier = Modifier.width(100.dp), singleLine = true); Spacer(modifier = Modifier.width(8.dp)); Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
    }
}

@Composable
private fun ToggleRow(icon: ImageVector, title: String, subtitle: String, color: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) { Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp)) }; Spacer(modifier = Modifier.width(10.dp)); Column { Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium); Text(subtitle, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) } }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
