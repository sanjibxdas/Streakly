package com.streakly.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.streakly.ui.theme.AccentIndigo
import com.streakly.ui.theme.PrimaryBlue
import com.streakly.ui.theme.SecondaryGreen

@Composable
fun StreaklyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 14.dp,
    gradientColors: List<Color> = listOf(PrimaryBlue, AccentIndigo),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit = {}
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sw = strokeWidth.toPx()
            val diameter = this.size.minDimension - sw
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(sw / 2f, sw / 2f)

            // Background circle
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = sw, cap = StrokeCap.Round)
            )

            // Progress arc
            if (animatedProgress.value > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(gradientColors),
                    startAngle = -90f,
                    sweepAngle = animatedProgress.value * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = sw, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

@Composable
fun MiniBarChart(
    data: List<Int>,
    modifier: Modifier = Modifier.fillMaxWidth().height(80.dp),
    barColor: Color = PrimaryBlue,
    highlightColor: Color = SecondaryGreen
) {
    if (data.isEmpty()) return
    val maxVal = (data.maxOrNull() ?: 1).coerceAtLeast(1)

    Canvas(modifier = modifier) {
        val totalBars = data.size
        val barSpacing = 4.dp.toPx()
        val availableWidth = size.width - (barSpacing * (totalBars - 1))
        val barWidth = (availableWidth / totalBars).coerceAtLeast(2f)

        data.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / maxVal) * size.height
            val x = index * (barWidth + barSpacing)
            val y = size.height - barHeight

            val color = if (index == data.size - 1) highlightColor else barColor

            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
fun MiniLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier.fillMaxWidth().height(80.dp),
    lineColor: Color = PrimaryBlue,
    fillColor: Color = PrimaryBlue.copy(alpha = 0.15f)
) {
    if (data.size < 2) return
    val minVal = data.minOrNull() ?: 0f
    val maxVal = (data.maxOrNull() ?: 1f).coerceAtLeast(minVal + 0.1f)

    Canvas(modifier = modifier) {
        val stepX = size.width / (data.size - 1)
        val path = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val normY = 1f - ((value - minVal) / (maxVal - minVal))
            val x = index * stepX
            val y = normY * size.height

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(fillPath, brush = Brush.verticalGradient(listOf(fillColor, Color.Transparent)))
        drawPath(path, color = lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun StackedBar(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier.fillMaxWidth().height(16.dp)
) {
    val total = segments.sumOf { it.first.toDouble() }.toFloat().coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        var currentX = 0f
        segments.forEach { (weight, color) ->
            val segWidth = (weight / total) * size.width
            drawRect(
                color = color,
                topLeft = Offset(currentX, 0f),
                size = Size(segWidth, size.height)
            )
            currentX += segWidth
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        if (!actionText.isNullOrBlank() && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}
