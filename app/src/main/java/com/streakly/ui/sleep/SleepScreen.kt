package com.streakly.ui.sleep

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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.streakly.ui.common.MiniLineChart
import com.streakly.ui.common.ProgressRing
import com.streakly.ui.common.SectionHeader
import com.streakly.ui.common.StackedBar
import com.streakly.ui.common.StreaklyCard
import com.streakly.ui.theme.DeepNavy
import com.streakly.ui.theme.FlameOrange
import com.streakly.ui.theme.PrimaryBlue
import com.streakly.ui.theme.SecondaryGreen
import com.streakly.ui.theme.SlateBlue
import com.streakly.ui.theme.WaterBlue
import com.streakly.util.DateUtils

@Composable
fun SleepScreen(
    viewModel: SleepViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sleep = uiState.recentSleep

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Sleep",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Restorative sleep architecture and trends",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Sleep Card
        StreaklyCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val score = sleep?.score ?: 86
                ProgressRing(
                    progress = score / 100f,
                    size = 170.dp,
                    strokeWidth = 14.dp,
                    color = PrimaryBlue
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sleep Score",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = DateUtils.formatDuration(sleep?.durationMinutes ?: 458),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total Sleep Duration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sleep Stages Breakdown Card
        StreaklyCard {
            Column {
                SectionHeader(title = "Sleep Architecture", subtitle = "Breakdown across cycles")
                Spacer(modifier = Modifier.height(16.dp))

                val deepColor = DeepNavy
                val remColor = SlateBlue
                val lightColor = WaterBlue
                val awakeColor = FlameOrange

                StackedBar(
                    segments = listOf(
                        (sleep?.deepMinutes?.toFloat() ?: 95f) to deepColor,
                        (sleep?.remMinutes?.toFloat() ?: 110f) to remColor,
                        (sleep?.lightMinutes?.toFloat() ?: 220f) to lightColor,
                        (sleep?.awakeMinutes?.toFloat() ?: 33f) to awakeColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StageItem("Deep", "${sleep?.deepMinutes ?: 95}m", deepColor)
                    StageItem("REM", "${sleep?.remMinutes ?: 110}m", remColor)
                    StageItem("Light", "${sleep?.lightMinutes ?: 220}m", lightColor)
                    StageItem("Awake", "${sleep?.awakeMinutes ?: 33}m", awakeColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7-Night Trendline
        if (uiState.sleepHistory.isNotEmpty()) {
            StreaklyCard {
                Column {
                    SectionHeader(
                        title = "7-Night Trend",
                        subtitle = "Average duration: 7h 25m"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MiniLineChart(
                        data = uiState.sleepHistory.map { it.durationMinutes.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        lineColor = PrimaryBlue,
                        fillColor = PrimaryBlue.copy(alpha = 0.15f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Overnight Vitals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreaklyCard(modifier = Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Resting HR",
                            tint = Color(0xFFE05C5C),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Resting HR",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${uiState.vitals?.restingHeartRate ?: 58} bpm",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Optimal recovery",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGreen
                    )
                }
            }

            StreaklyCard(modifier = Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonitorHeart,
                            contentDescription = "HRV",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HRV",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${uiState.vitals?.heartRateVariability ?: 64.5} ms",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "High parasympathetic",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StageItem(label: String, value: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
