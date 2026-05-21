package com.fiap.ariachallenge.ui.gestor.detalhes_projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import com.fiap.ariachallenge.ui.aria.AriaAvatar
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.aria.AvatarTone
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.formatCurrencyBrl
import com.fiap.ariachallenge.util.formatCurrencyDisplayParts
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun DetalhesProjetoScreen(
    onBack: () -> Unit,
    canEdit: Boolean = true,
    onEdit: () -> Unit = {},
    viewModel: DetalhesProjetoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = uiState.project?.title ?: stringResource(R.string.project_title_fallback),
                onBack = onBack,
                trailing = {
                    if (canEdit && uiState.project != null) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.cd_edit_button),
                            tint = c.textTertiary,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = onEdit),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(padding))
            uiState.project == null -> AriaErrorState(
                onRetry = viewModel::refresh,
                title = stringResource(R.string.project_not_found),
                sub = stringResource(R.string.project_not_found_sub),
                modifier = Modifier.padding(padding),
            )
            else -> {
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabLabels = listOf(
                    stringResource(R.string.project_tab_overview),
                    stringResource(R.string.project_tab_milestones),
                    stringResource(R.string.project_tab_team),
                )
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                    HeroBlock(project = uiState.project!!)
                    AriaTabs(items = tabLabels, selected = selectedTab, onSelect = { selectedTab = it })
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        when (selectedTab) {
                            0 -> overviewContent(uiState.project!!)
                            1 -> milestonesContent(uiState.project!!)
                            else -> teamContent(uiState.project!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBlock(project: Project) {
    val c = AriaTheme.colors
    val due = project.expectedEndDate.toLocalDate()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.bgSecondary)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val isRunning = project.status == ProjectStatus.EM_ANDAMENTO
            AriaPillLabel(
                text = stringResource(if (isRunning) R.string.projects_status_in_progress else R.string.projects_status_planning),
                bg = if (isRunning) c.infoBg else c.bgTertiary,
                fg = if (isRunning) c.info else c.textSecondary,
            )
            AriaPillLabel(text = stringResource(R.string.project_hero_on_time), bg = c.successBg, fg = c.success)
        }
        Spacer(modifier = Modifier.height(12.dp))
        val realizedRoiParts = formatCurrencyDisplayParts(project.actualRoi ?: 0.0)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            HeroMetric(label = stringResource(R.string.label_progress).uppercase(), value = "${project.progress}", unit = "%", color = c.textPrimary)
            HeroMetric(
                label = stringResource(R.string.projects_roi_realized),
                value = realizedRoiParts.amount,
                unit = "",
                color = c.success,
                prefix = realizedRoiParts.symbol,
            )
            HeroMetric(label = stringResource(R.string.project_hero_delivery), value = due.dayOfMonth.toString(), unit = due.month.name.take(3))
        }
        Spacer(modifier = Modifier.height(16.dp))
        AriaProgressLine(value = project.progress / 100f, height = 8.dp)
    }
}

@Composable
private fun HeroMetric(label: String, value: String, unit: String, color: Color = AriaTheme.colors.textPrimary, prefix: String = "") {
    val c = AriaTheme.colors
    Column {
        Text(text = label, style = AriaText.labelMd, color = c.textTertiary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            if (prefix.isNotBlank()) {
                Text(
                    text = prefix.trim(),
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.ExtraLight, fontSize = 14.sp),
                    color = c.textTertiary,
                )
            }
            Text(
                text = value,
                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.ExtraLight, fontSize = 32.sp, lineHeight = 32.sp),
                color = color,
            )
            Text(
                text = unit,
                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
                color = c.textTertiary,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.overviewContent(project: Project) {
    item {
        val descLabel = stringResource(R.string.project_details_section_description)
        Column {
            Text(text = descLabel, style = AriaText.labelLg, color = AriaTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = project.description.ifBlank { stringResource(R.string.project_share_no_description) }, style = AriaText.bodyLg, color = AriaTheme.colors.textPrimary)
        }
    }
    item { AriaDivider() }
    item {
        val originLabel = stringResource(R.string.project_section_origin)
        val approvedLabel = stringResource(R.string.idea_status_approved)
        Column {
            Text(text = originLabel, style = AriaText.labelLg, color = AriaTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(10.dp))
            AriaCard(padding = 12.dp, accent = true) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Outlined.Lightbulb, contentDescription = null, tint = AriaTheme.colors.accentMain, modifier = Modifier.size(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = project.originIdea.title, style = AriaText.bodyMd.copy(fontWeight = FontWeight.Medium), color = AriaTheme.colors.textPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "${project.originIdea.author.name.uppercase()} · ${approvedLabel.uppercase()}", style = AriaText.labelMd, color = AriaTheme.colors.textTertiary)
                    }
                }
            }
        }
    }
    item { AriaDivider() }
    item {
        Column {
            Text(text = stringResource(R.string.label_roi), style = AriaText.labelLg, color = AriaTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(10.dp))
            RoiCard(project)
        }
    }
}

@Composable
private fun RoiCard(project: Project) {
    val c = AriaTheme.colors
    val plan = project.estimatedRoi
    val actual = project.actualRoi ?: 0.0
    val pct = if (plan > 0) ((actual / plan) * 100.0).coerceIn(0.0, 100.0).toFloat() / 100f else 0f
    AriaCard(padding = 16.dp) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.project_roi_planned), style = AriaText.bodyMd, color = c.textSecondary, modifier = Modifier.weight(1f))
                Text(
                    text = formatCurrencyBrl(plan),
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = c.textPrimary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AriaProgressLine(value = 1f, color = c.bgTertiary)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.project_roi_realized_label), style = AriaText.bodyMd, color = c.textSecondary, modifier = Modifier.weight(1f))
                Text(
                    text = formatCurrencyBrl(actual),
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                    color = c.success,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AriaProgressLine(value = pct, color = c.success)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.milestonesContent(project: Project) {
    item {
        if (project.milestones.isEmpty()) {
            Text(
                text = stringResource(R.string.state_empty_milestones),
                style = AriaText.bodyMd,
                color = AriaTheme.colors.textTertiary,
            )
        } else {
            project.milestones.forEachIndexed { index, milestone ->
                MilestoneRow(milestone = milestone, isLast = index == project.milestones.lastIndex)
            }
        }
    }
}

@Composable
private fun MilestoneRow(milestone: ProjectMilestone, isLast: Boolean) {
    val c = AriaTheme.colors
    val done = milestone.status == MilestoneStatus.COMPLETED
    val current = milestone.status == MilestoneStatus.IN_PROGRESS
    val whenLabel = formatMilestoneDate(milestone)
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (done) c.success else if (current) c.accentMain else c.bgTertiary),
                contentAlignment = Alignment.Center,
            ) {
                if (done) {
                    Icon(imageVector = Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(1.dp).height(28.dp).background(c.borderSecondary))
            }
        }
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(text = milestone.title, style = AriaText.bodyLg, color = if (done) c.textTertiary else c.textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (current) {
                    stringResource(R.string.project_milestone_in_progress, whenLabel)
                } else {
                    whenLabel.uppercase()
                },
                style = AriaText.labelMd,
                color = if (current) c.accentMain else c.textTertiary,
            )
        }
    }
}

@Composable
private fun formatMilestoneDate(milestone: ProjectMilestone): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .format(milestone.dueDate)

private fun androidx.compose.foundation.lazy.LazyListScope.teamContent(project: Project) {
    item {
        if (project.teamMembers.isEmpty()) {
            Text(
                text = stringResource(R.string.state_empty_team),
                style = AriaText.bodyMd,
                color = AriaTheme.colors.textTertiary,
            )
        } else {
            project.teamMembers.forEachIndexed { index, member ->
                TeamRow(member = member, tone = teamAvatarTone(index), isLast = index == project.teamMembers.lastIndex)
            }
        }
    }
}

private fun teamAvatarTone(index: Int): AvatarTone = when (index % 3) {
    0 -> AvatarTone.Primary
    1 -> AvatarTone.Accent
    else -> AvatarTone.Subtle
}

@Composable
private fun TeamRow(member: ProjectTeamMember, tone: AvatarTone, isLast: Boolean) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
    ) {
        AriaAvatar(name = member.user.name, size = 40.dp, tone = tone)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.user.name, style = AriaText.bodyLg, color = c.textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = member.projectRole.uppercase(), style = AriaText.labelMd, color = c.textTertiary)
        }
    }
    if (!isLast) AriaDivider()
}
