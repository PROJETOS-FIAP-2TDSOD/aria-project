package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.util.localizedName
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme
import com.fiap.ariachallenge.ui.theme.BorderRadius
import com.fiap.ariachallenge.ui.theme.LightError
import com.fiap.ariachallenge.ui.theme.LightErrorBackground
import com.fiap.ariachallenge.ui.theme.LightInfo
import com.fiap.ariachallenge.ui.theme.LightInfoBackground
import com.fiap.ariachallenge.ui.theme.LightSuccess
import com.fiap.ariachallenge.ui.theme.LightSuccessBackground
import com.fiap.ariachallenge.ui.theme.LightWarning
import com.fiap.ariachallenge.ui.theme.LightWarningBackground
import com.fiap.ariachallenge.ui.theme.Spacing

@Composable
fun StatusBadge(
    status: IdeaStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (status) {
        IdeaStatus.AGUARDANDO_ANALISE -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        IdeaStatus.EM_ANALISE -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        IdeaStatus.APROVADA -> Pair(LightSuccessBackground, LightSuccess)
        IdeaStatus.REJEITADA -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        IdeaStatus.EM_PROJETO -> Pair(LightInfoBackground, LightInfo)
    }
    BadgeSurface(text = status.localizedName(), background = bg, foreground = fg, modifier = modifier)
}

@Composable
fun StatusBadge(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (status) {
        ProjectStatus.PLANEJAMENTO -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        ProjectStatus.EM_ANDAMENTO -> Pair(LightInfoBackground, LightInfo)
        ProjectStatus.CONCLUIDO -> Pair(LightSuccessBackground, LightSuccess)
        ProjectStatus.SUSPENSO -> Pair(LightWarningBackground, LightWarning)
        ProjectStatus.CANCELADO -> Pair(LightErrorBackground, LightError)
    }
    BadgeSurface(text = status.localizedName(), background = bg, foreground = fg, modifier = modifier)
}

@Composable
fun StatusBadge(
    priority: OrientationPriority,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (priority) {
        OrientationPriority.BAIXA -> Pair(LightSuccessBackground, LightSuccess)
        OrientationPriority.MEDIA -> Pair(LightInfoBackground, LightInfo)
        OrientationPriority.ALTA -> Pair(LightWarningBackground, LightWarning)
        OrientationPriority.CRITICA -> Pair(LightErrorBackground, LightError)
    }
    BadgeSurface(text = priority.localizedName(), background = bg, foreground = fg, modifier = modifier)
}

@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    BadgeSurface(text = text, background = backgroundColor, foreground = textColor, modifier = modifier)
}

@Composable
private fun BadgeSurface(
    text: String,
    background: Color,
    foreground: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(BorderRadius.sm),
        contentColor = foreground
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = 4.dp)
        )
    }
}

@Preview
@Composable
private fun StatusBadgePreview() {
    AriaChallengeTheme {
        StatusBadge(status = IdeaStatus.EM_ANALISE)
    }
}
