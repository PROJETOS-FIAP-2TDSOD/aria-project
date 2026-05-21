package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun Sparkline(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    lineWidthPx: Float = 2.5f,
    fillTopColor: Color = Color.White.copy(alpha = 0.18f),
    fillBottomColor: Color = Color.Transparent
) {
    if (points.size < 2) return
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxValue = points.max()
        val minValue = points.min()
        val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f
        val stepX = width / (points.size - 1).toFloat()

        val mapped = points.mapIndexed { index, value ->
            val x = stepX * index
            val normalized = (value - minValue) / range
            val y = height - normalized * height * 0.85f - height * 0.05f
            Offset(x, y)
        }

        val linePath = Path().apply {
            moveTo(mapped.first().x, mapped.first().y)
            for (i in 1 until mapped.size) {
                val prev = mapped[i - 1]
                val curr = mapped[i]
                val midX = (prev.x + curr.x) / 2f
                cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
            }
        }

        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(mapped.last().x, height)
            lineTo(mapped.first().x, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillTopColor, fillBottomColor),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = lineWidthPx, cap = StrokeCap.Round)
        )
    }
}
