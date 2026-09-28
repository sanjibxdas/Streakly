package com.streakly.ui.health

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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.streakly.ui.common.ProgressRing
import com.streakly.ui.common.SectionHeader
import com.streakly.ui.common.StreaklyCard
import com.streakly.ui.theme.FlameOrange
import com.streakly.ui.theme.PrimaryBlue
import com.streakly.ui.theme.SecondaryGreen
import com.streakly.ui.theme.SlateBlue
import com.streakly.ui.theme.WaterBlue
import com.streakly.ui.today.TodayViewModel

@Composable
fun HealthScreen(
    todayViewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by todayViewModel.uiState.collectAsState()
    val readiness = uiState.summary?.readinessScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Health",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Autonomous readiness scoring and biometrics",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Readiness Score Card
        StreaklyCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProgressRing(
                        progress = (readiness?.score ?: 88) / 100f,
                        size = 100.dp,
                        strokeWidth = 10.dp,
                        color = SecondaryGreen
                    ) {
                        Text(
                            text = "${readiness?.score ?: 88}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "Daily Readiness",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = readiness?.description ?: "Optimal state for physical training and cognitive focus.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ReadinessFactorItem("Sleep", "${readiness?.sleepFactor ?: 90}%", SlateBlue)
                    ReadinessFactorItem("Activity", "${readiness?.activityFactor ?: 85}%", PrimaryBlue)
                    ReadinessFactorItem("Resting HR", "${readiness?.restingHrFactor ?: 89}%", SecondaryGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vitals Grid
        SectionHeader(title = "Biometrics & Vitals", subtitle = "Synced via Health Connect")
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VitalCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Favorite,
                title = "Resting HR",
                value = "58 bpm",
                status = "Normal (55-65)",
                iconColor = Color(0xFFE05C5C)
            )
            VitalCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Bloodtype,
                title = "SpO2 (Oxygen)",
                value = "98%",
                status = "Optimal",
                iconColor = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VitalCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.MonitorHeart,
                title = "HRV (RMSSD)",
                value = "64.5 ms",
                status = "+12% vs avg",
                iconColor = SecondaryGreen
            )
            VitalCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Air,
                title = "Respiration",
                value = "14.2 rpm",
                status = "Steady",
                iconColor = WaterBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nutrition & Macros Card
        StreaklyCard {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Nutrition",
                                tint = FlameOrange
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nutrition & Fuel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily macronutrient targets",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Text(
                        text = "1,840 / 2,200 kcal",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = FlameOrange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MacroProgressBar(name = "Protein", current = 142, target = 160, color = Color(0xFFE05C5C))
                Spacer(modifier = Modifier.height(10.dp))
                MacroProgressBar(name = "Carbohydrates", current = 210, target = 250, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(10.dp))
                MacroProgressBar(name = "Healthy Fats", current = 56, target = 70, color = SecondaryGreen)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ReadinessFactorItem(label: String, score: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun VitalCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    status: String,
    iconColor: Color
) {
    StreaklyCard(modifier = modifier) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MacroProgressBar(name: String, current: Int, target: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp
            )
            Text(
                text = "${current}g / ${target}g",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = (current.toFloat() / target).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}
