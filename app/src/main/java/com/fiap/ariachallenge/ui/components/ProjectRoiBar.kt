package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.ui.theme.LightAccent
import com.fiap.ariachallenge.ui.theme.LightSuccess

@Composable
fun ProjectRoiBar(
    name: String,
    realizedAmount: String,
    targetAmount: String,
    progress: Float,
    isAboveTarget: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isAboveTarget) LightSuccess else LightAccent
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                Text(
                    text = realizedAmount,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Text(
                    text = " / $targetAmount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AriaProgressBar(
            progress = progress,
            barColor = color,
            height = 5.dp
        )
    }
}
