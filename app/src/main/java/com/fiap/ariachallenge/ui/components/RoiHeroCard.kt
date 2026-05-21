package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.ui.theme.DarkSuccess
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.ui.theme.Spacing

@Composable
fun RoiHeroCard(
    eyebrow: String,
    currencyPrefix: String,
    valueIntegerPart: String,
    valueSuffix: String,
    deltaText: String,
    deltaPositive: Boolean,
    sparklinePoints: List<Float>,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
        )
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(gradient)
                .padding(horizontal = Spacing.md, vertical = Spacing.md)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currencyPrefix,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Text(
                            text = valueIntegerPart,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 56.sp,
                            lineHeight = 60.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = valueSuffix,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    DeltaPill(text = deltaText, positive = deltaPositive)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Sparkline(
                        points = sparklinePoints,
                        lineColor = MaterialTheme.colorScheme.onPrimary,
                        fillTopColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeltaPill(text: String, positive: Boolean) {
    val accent = DarkSuccess
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.copy(alpha = 0.18f))
            .padding(horizontal = Spacing.xs, vertical = 6.dp)
    ) {
        Icon(
            imageVector = if (positive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
            contentDescription = null,
            tint = accent
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            textAlign = TextAlign.Center
        )
    }
}
