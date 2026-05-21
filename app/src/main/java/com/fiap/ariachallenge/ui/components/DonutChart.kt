package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

data class DonutSlice(
    val label: String,
    val percent: Float,
    val color: Color,
)

@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    centerValue: String,
    centerLabel: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        val total = slices.sumOf { it.percent.toDouble() }.toFloat().takeIf { it > 0f } ?: 1f
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            var startAngle = -90f
            val gap = 2f
            slices.forEach { slice ->
                val sweep = (slice.percent / total) * 360f - gap
                if (sweep > 0f) {
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle + gap / 2f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
                startAngle += (slice.percent / total) * 360f
            }
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(strokeWidth + 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val innerMin = min(maxWidth.value, maxHeight.value).coerceAtLeast(1f)
            val valueSp = (innerMin * 0.34f).coerceIn(14f, 22f).sp
            val labelSp = (innerMin * 0.16f).coerceIn(8f, 11f).sp
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = centerValue,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = valueSp, lineHeight = valueSp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = centerLabel.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = labelSp, lineHeight = labelSp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
