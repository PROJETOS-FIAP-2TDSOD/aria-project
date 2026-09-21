package com.fiap.ariachallenge.ui.gestor.analisar

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.fiap.ariachallenge.domain.model.AiAnalyzeBrief
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaScoreRing
import com.fiap.ariachallenge.ui.aria.AriaScoreRingDefaults
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.localizedName

@Composable
fun AnalisarIdeiaScreen(
    onBack: () -> Unit,
    onNavigateToCriarProjeto: (String) -> Unit = {},
    viewModel: AnalisarIdeiaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    var selectedTab by remember { mutableIntStateOf(1) }
    var showResourcesDialog by remember { mutableStateOf(false) }
    val idea = uiState.idea

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onBack()
    }

    if (uiState.approvedForProject && idea != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearApprovedForProject()
                onBack()
            },
            title = {
                Text(
                    text = stringResource(R.string.analyze_idea_approved_title),
                    style = AriaText.titleMd,
                    color = c.textPrimary,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.analyze_idea_approved_message),
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val ideaId = idea.id
                        viewModel.clearApprovedForProject()
                        onNavigateToCriarProjeto(ideaId)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.analyze_idea_approved_create_project),
                        color = c.primaryMain,
                        style = AriaText.labelMd,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.clearApprovedForProject()
                        onBack()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.action_later),
                        color = c.textSecondary,
                        style = AriaText.labelMd,
                    )
                }
            },
        )
    }

    if (showResourcesDialog && idea?.recursos?.isNotBlank() == true) {
        AlertDialog(
            onDismissRequest = { showResourcesDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.analyze_idea_more_info_title),
                    style = AriaText.titleMd,
                    color = c.textPrimary,
                )
            },
            text = {
                Text(
                    text = idea.recursos,
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = { showResourcesDialog = false }) {
                    Text(
                        text = stringResource(R.string.action_confirm),
                        color = c.accentMain,
                        style = AriaText.labelMd,
                    )
                }
            },
            containerColor = c.surface,
        )
    }

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = { AriaTopBar(title = stringResource(R.string.analyze_screen_title), onBack = onBack) },
        bottomBar = {
            if (!uiState.isLoading && idea != null) {
                ActionBar(
                    onReject = viewModel::reject,
                    onApprove = viewModel::approve,
                    onRequestMoreInfo = { showResourcesDialog = true },
                    showMoreInfo = idea.recursos.isNotBlank(),
                    submitting = uiState.isSubmitting,
                )
            }
        },
    ) { padding ->
        when {
            uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(padding))
            idea == null -> AriaErrorState(onRetry = {}, modifier = Modifier.padding(padding))
            else -> {
                val tabLabels = listOf(
                    stringResource(R.string.analyze_idea_tab_info),
                    stringResource(R.string.analyze_idea_tab_score),
                    stringResource(R.string.analyze_idea_tab_orient),
                )
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                    HeaderBlock(idea = idea)
                    AriaTabs(
                        items = tabLabels,
                        selected = selectedTab,
                        onSelect = { selectedTab = it },
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        val brief = uiState.aiBrief
                        when (selectedTab) {
                            0 -> infoContent(idea)
                            1 -> scoreContent(
                                score = uiState.score.takeIf { it > 0 } ?: idea.score ?: 0,
                                brief = brief,
                            )
                            else -> orientContent(brief)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBlock(idea: Idea) {
    val c = AriaTheme.colors
    val days = java.time.Duration.between(idea.createdAt, java.time.LocalDateTime.now()).toDays().toInt().coerceAtLeast(1)
    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(c.error))
            Text(text = stringResource(R.string.gestor_sla_critical, days), style = AriaText.labelMd, color = c.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = idea.title,
            style = AriaText.titleLg.copy(fontSize = 22.sp, lineHeight = 28.sp),
            color = c.textPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(
                R.string.format_author_category_id,
                idea.author.name,
                idea.category.localizedName(),
                idea.id.takeLast(4).padStart(4, '0'),
            ),
            style = AriaText.bodyMd,
            color = c.textTertiary,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.scoreContent(score: Int, brief: AiAnalyzeBrief?) {
    item {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AriaScoreRing(value = score, size = AriaScoreRingDefaults.Detail)
        }
    }
    item { ScoreBreakdownCard(brief = brief) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.infoContent(idea: Idea) {
    item {
        val descLabel = stringResource(R.string.analyze_section_description)
        val benefitsLabel = stringResource(R.string.analyze_section_benefits)
        val problemLabel = stringResource(R.string.analyze_section_problem)
        val resourcesLabel = stringResource(R.string.analyze_section_resources)
        Column {
            InfoSection(descLabel, idea.description)
            if (idea.beneficios.isNotBlank()) {
                AriaDivider()
                Spacer(modifier = Modifier.height(20.dp))
                InfoSection(benefitsLabel, idea.beneficios)
            }
            if (idea.problema.isNotBlank()) {
                AriaDivider()
                Spacer(modifier = Modifier.height(20.dp))
                InfoSection(problemLabel, idea.problema)
            }
            if (idea.recursos.isNotBlank()) {
                AriaDivider()
                Spacer(modifier = Modifier.height(20.dp))
                InfoSection(resourcesLabel, idea.recursos)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.orientContent(brief: AiAnalyzeBrief?) {
    if (brief == null) return
    val data = brief
    item {
        AriaCard(padding = 14.dp, accent = true) {
            Column {
                Text(text = data.orientationMatchLabel, style = AriaText.labelMd, color = AriaTheme.colors.accentMain)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = data.orientationTitle, style = AriaText.titleMd, color = AriaTheme.colors.textPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = data.orientationBody, style = AriaText.bodyMd, color = AriaTheme.colors.textSecondary)
            }
        }
    }
}

@Composable
private fun ScoreBreakdownCard(brief: AiAnalyzeBrief?) {
    val data = brief ?: return
    val c = AriaTheme.colors
    AriaCard(padding = 16.dp) {
        Column {
            DotLabel(color = c.success, text = stringResource(R.string.analyze_strengths))
            Spacer(modifier = Modifier.height(8.dp))
            data.strengths.forEach { BulletRow(text = it) }
            Spacer(modifier = Modifier.height(16.dp))
            DotLabel(color = c.warning, text = stringResource(R.string.analyze_attention_points))
            Spacer(modifier = Modifier.height(8.dp))
            data.attentionPoints.forEach { BulletRow(text = it) }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.bgSecondary)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null, tint = c.accentMain, modifier = Modifier.size(14.dp))
                Text(text = data.recommendation, style = AriaText.bodyMd, color = c.textPrimary)
            }
        }
    }
}

@Composable
private fun DotLabel(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(text = text, style = AriaText.labelLg, color = color)
    }
}

@Composable
private fun BulletRow(text: String) {
    val c = AriaTheme.colors
    Row(modifier = Modifier.padding(start = 14.dp, bottom = 4.dp)) {
        Text(text = "•  ", style = AriaText.bodyMd, color = c.textSecondary)
        Text(text = text, style = AriaText.bodyMd, color = c.textPrimary)
    }
}

@Composable
private fun InfoSection(label: String, value: String) {
    val c = AriaTheme.colors
    Text(text = label, style = AriaText.labelLg, color = c.textSecondary)
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = value, style = AriaText.bodyLg, color = c.textPrimary)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun ActionBar(
    onReject: () -> Unit,
    onRequestMoreInfo: () -> Unit,
    onApprove: () -> Unit,
    showMoreInfo: Boolean,
    submitting: Boolean,
) {
    val c = AriaTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 20.dp)
            .background(c.surface),
    ) {
        AriaHairline()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionButton(
                text = stringResource(R.string.analyze_idea_action_reject),
                bg = c.errorBg,
                fg = c.error,
                onClick = onReject,
                enabled = !submitting,
                modifier = Modifier.weight(1f),
            )
            if (showMoreInfo) {
                ActionButton(
                    text = stringResource(R.string.analyze_idea_action_more_info),
                    bg = c.bgTertiary,
                    fg = c.textPrimary,
                    onClick = onRequestMoreInfo,
                    enabled = !submitting,
                    modifier = Modifier.weight(1f),
                )
            }
            ActionButton(
                text = stringResource(R.string.analyze_idea_action_approve),
                bg = c.success,
                fg = Color.White,
                onClick = onApprove,
                enabled = !submitting,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            color = fg,
            style = TextStyle(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.5.sp,
            ),
        )
    }
}
