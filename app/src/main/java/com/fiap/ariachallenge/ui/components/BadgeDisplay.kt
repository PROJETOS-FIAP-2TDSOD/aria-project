package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.domain.model.Badge
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme

fun Badge.toIcon(): ImageVector = when (this) {
    Badge.FIRST_IDEA -> Icons.Filled.Lightbulb
    Badge.INNOVATOR_5 -> Icons.Filled.EmojiEvents
    Badge.APPROVED_IDEA -> Icons.Filled.Star
    Badge.HIGH_SCORER -> Icons.Filled.MilitaryTech
    Badge.PROJECT_CREATOR -> Icons.Filled.Rocket
}

@Composable
fun BadgeDisplay(
    badge: Badge,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val iconTint = if (unlocked) {
        colorScheme.primary
    } else {
        colorScheme.outline.copy(alpha = 0.3f)
    }
    val textColor = if (unlocked) {
        colorScheme.onSurface
    } else {
        colorScheme.onSurface.copy(alpha = 0.5f)
    }

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = badge.toIcon(),
            contentDescription = stringResource(badge.nameRes),
            modifier = Modifier.size(48.dp),
            tint = iconTint,
        )
        Text(
            text = stringResource(badge.nameRes),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgeDisplayPreview() {
    AriaChallengeTheme {
        BadgeDisplay(badge = Badge.FIRST_IDEA, unlocked = true)
    }
}
