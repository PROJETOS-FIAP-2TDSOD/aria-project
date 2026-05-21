package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme
import com.fiap.ariachallenge.ui.theme.LightSuccess
import com.fiap.ariachallenge.ui.theme.LightError
import com.fiap.ariachallenge.ui.theme.MetricValueStyle
import com.fiap.ariachallenge.ui.theme.Spacing

enum class MetricTrend { UP, DOWN, FLAT }

@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trendText: String? = null,
    trend: MetricTrend = MetricTrend.UP,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = value,
                style = MetricValueStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
            trendText?.let {
                val (icon, color) = when (trend) {
                    MetricTrend.UP -> Icons.AutoMirrored.Filled.TrendingUp to LightSuccess
                    MetricTrend.DOWN -> Icons.AutoMirrored.Filled.TrendingDown to LightError
                    MetricTrend.FLAT -> Icons.AutoMirrored.Filled.TrendingFlat to MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxxs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MetricCardPreview() {
    AriaChallengeTheme {
        MetricCard(
            label = "Ideias submetidas",
            value = "45",
            trendText = "+8 vs. abr"
        )
    }
}
