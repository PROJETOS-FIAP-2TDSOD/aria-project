package com.fiap.ariachallenge.ui.operador.detalhes_ideia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.domain.model.AiInsightKind
import com.fiap.ariachallenge.domain.model.AiScoreBreakdownItem
import com.fiap.ariachallenge.domain.model.AiTextInsight
import com.fiap.ariachallenge.domain.model.AiTimelineEvent
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import androidx.compose.ui.res.stringResource
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaScoreBadge
import com.fiap.ariachallenge.ui.aria.AriaScoreRing
import com.fiap.ariachallenge.ui.aria.AriaScoreRingDefaults
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaStatus
import com.fiap.ariachallenge.ui.aria.AriaStatusBadge
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.localizedName


@Composable
fun DetalhesIdeiaScreen(
    onBack: () -> Unit,
    viewModel: DetalhesIdeiaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(
                    R.string.idea_title_format,
                    uiState.idea?.id?.takeLast(4)?.padStart(4, '0') ?: uiState.displayCode,
                ),
                onBack = onBack,
            )
        },
    ) { padding ->
        val idea = uiState.idea
        if (idea == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(text = androidx.compose.ui.res.stringResource(com.fiap.ariachallenge.R.string.label_loading), style = AriaText.bodyMd, color = c.textTertiary)
            }
            return@Scaffold
        }
        var selectedTab by remember { mutableIntStateOf(1) }
        val tabLabels = listOf(
            stringResource(R.string.idea_tab_info),
            stringResource(R.string.idea_tab_score),
            stringResource(R.string.idea_tab_timeline),
        )
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            HeaderBlock(idea = idea)
            AriaTabs(items = tabLabels, selected = selectedTab, onSelect = { selectedTab = it })
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (selectedTab) {
                    0 -> infoContentItems(idea)
                    1 -> scoreContentItems(idea, uiState.scoreBreakdown, uiState.aiInsights)
                    else -> timelineContentItems(uiState.timeline)
                }
            }
        }
    }
}

@Composable
private fun HeaderBlock(idea: Idea) {
    val c = AriaTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
    ) {
        Text(
            text = stringResource(
                R.string.format_category_and_id,
                idea.category.localizedName().uppercase(),
                idea.id.takeLast(4).padStart(4, '0'),
            ),
            style = AriaText.labelMd,
            color = c.textTertiary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = idea.title,
            style = AriaText.titleLg.copy(fontSize = 22.sp, lineHeight = 28.sp),
            color = c.textPrimary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AriaStatusBadge(status = idea.status.toAriaStatus())
            idea.score?.let { AriaScoreBadge(value = it) }
        }
    }
}


private fun androidx.compose.foundation.lazy.LazyListScope.scoreContentItems(
    idea: Idea,
    breakdown: List<AiScoreBreakdownItem>,
    insights: List<AiTextInsight>,
) {
    val score = idea.score ?: 0
    item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AriaScoreRing(value = score, size = AriaScoreRingDefaults.Hero, label = null)
        }
    }
    item { AriaSectionTitle(text = stringResource(R.string.idea_details_breakdown)) }
    item { ScoreBreakdownCard(breakdown = breakdown) }
    item { AriaSectionTitle(text = stringResource(R.string.idea_details_ai_analysis)) }
    item { AiAnalysisCard(insights = insights) }
}

@Composable
private fun ScoreBreakdownCard(breakdown: List<AiScoreBreakdownItem>) {
    if (breakdown.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_score_breakdown))
        return
    }
    AriaCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            breakdown.forEach { item ->
                BreakdownRow(label = item.label, value = item.value)
            }
        }
    }
}

@Composable
private fun AiAnalysisCard(insights: List<AiTextInsight>) {
    if (insights.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_idea_ai_analysis))
        return
    }
    AriaCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            insights.forEach { insight ->
                val (icon, tone) = insight.kind.toPresentation()
                AiInsightRow(icon = icon, tone = tone, text = insight.message)
            }
        }
    }
}

private fun AiInsightKind.toPresentation(): Pair<ImageVector, ToneKind> = when (this) {
    AiInsightKind.Technical -> Icons.Outlined.Check to ToneKind.Success
    AiInsightKind.Alignment -> Icons.Outlined.Explore to ToneKind.Primary
    AiInsightKind.Investment -> Icons.Outlined.LocalFireDepartment to ToneKind.Warning
    AiInsightKind.Payback -> Icons.Outlined.Info to ToneKind.Info
    AiInsightKind.General -> Icons.Outlined.Info to ToneKind.Info
}

private fun androidx.compose.foundation.lazy.LazyListScope.infoContentItems(idea: Idea) {
    item {
        InfoBlock(label = stringResource(R.string.idea_details_section_category), value = idea.category.localizedName())
    }
    item {
        InfoBlock(label = stringResource(R.string.idea_details_section_description), value = idea.description)
    }
    if (idea.problema.isNotBlank()) {
        item {
            InfoBlock(label = stringResource(R.string.idea_details_section_problem), value = idea.problema)
        }
    }
    if (idea.beneficios.isNotBlank()) {
        item {
            InfoBlock(label = stringResource(R.string.idea_details_section_benefits), value = idea.beneficios)
        }
    }
    if (idea.recursos.isNotBlank()) {
        item {
            InfoBlock(label = stringResource(R.string.idea_details_section_resources), value = idea.recursos)
        }
    }
    item { AriaDivider() }
    item {
        Text(
            text = stringResource(
                R.string.idea_details_dates_line,
                idea.createdAt.toLocalDate().toString(),
                idea.updatedAt.toLocalDate().toString(),
            ),
            style = AriaText.labelMd,
            color = AriaTheme.colors.textTertiary,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.timelineContentItems(events: List<AiTimelineEvent>) {
    item {
        TimelineSection(events = events)
    }
}

@Composable
private fun TimelineSection(events: List<AiTimelineEvent>) {
    if (events.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_timeline))
        return
    }
    events.forEachIndexed { index, event ->
        TimelineItem(
            e = TimelineEvent(title = event.title, sub = event.subtitle, live = event.isLive),
            isLast = index == events.lastIndex,
        )
    }
}

@Composable
private fun BreakdownRow(label: String, value: Int) {
    val c = AriaTheme.colors
    val color = when {
        value < 41 -> c.error
        value < 71 -> c.warning
        else -> c.success
    }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = AriaText.bodyMd, color = c.textSecondary, modifier = Modifier.weight(1f))
            Text(
                text = value.toString(),
                style = TextStyle(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
                color = c.textPrimary,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        AriaProgressLine(value = value / 100f, color = color)
    }
}

private enum class ToneKind { Success, Primary, Warning, Info }

@Composable
private fun AiInsightRow(icon: ImageVector, tone: ToneKind, text: String) {
    val c = AriaTheme.colors
    val (bg, fg) = when (tone) {
        ToneKind.Success -> c.successBg to c.success
        ToneKind.Primary -> c.primarySubtle to c.primaryMain
        ToneKind.Warning -> c.warningBg to c.accentDark
        ToneKind.Info -> c.infoBg to c.info
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(c.bgSecondary)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
        }
        Text(text = text, style = AriaText.bodyMd, color = c.textPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun InfoBlock(label: String, value: String) {
    val c = AriaTheme.colors
    Column(modifier = Modifier.padding(bottom = 18.dp)) {
        Text(text = label.uppercase(), style = AriaText.labelLg, color = c.textSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, style = AriaText.bodyLg, color = c.textPrimary)
    }
}

private data class TimelineEvent(val title: String, val sub: String, val live: Boolean = false)

@Composable
private fun TimelineItem(e: TimelineEvent, isLast: Boolean) {
    val c = AriaTheme.colors
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(bottom = 18.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (e.live) c.accentMain else c.primaryMain)
            )
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(c.borderSecondary)
                )
            }
        }
        Column {
            Text(text = e.title, style = AriaText.bodyLg, color = c.textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = e.sub, style = AriaText.labelMd, color = c.textTertiary)
        }
    }
}

private fun IdeaStatus.toAriaStatus(): AriaStatus = when (this) {
    IdeaStatus.AGUARDANDO_ANALISE -> AriaStatus.Pending
    IdeaStatus.EM_ANALISE -> AriaStatus.Review
    IdeaStatus.APROVADA -> AriaStatus.Approved
    IdeaStatus.REJEITADA -> AriaStatus.Rejected
    IdeaStatus.EM_PROJETO -> AriaStatus.Project
}
