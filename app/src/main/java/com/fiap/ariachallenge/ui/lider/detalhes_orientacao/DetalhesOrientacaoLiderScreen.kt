package com.fiap.ariachallenge.ui.lider.detalhes_orientacao

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaScoreBadge
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.aria.AriaTrendDir
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.localizedName

@Composable
fun DetalhesOrientacaoLiderScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onIdeaClick: (String) -> Unit,
    readOnly: Boolean = false,
    viewModel: DetalhesOrientacaoLiderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val d = uiState.detail
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && d != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.delete_orientation_title),
                    style = AriaText.titleMd,
                    color = c.textPrimary,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_orientation_message, d.title),
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteOrientation(onSuccess = onBack)
                    },
                ) {
                    Text(
                        text = stringResource(R.string.delete_orientation_confirm),
                        color = c.error,
                        style = AriaText.labelMd,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = c.textSecondary,
                        style = AriaText.labelMd,
                    )
                }
            },
        )
    }

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = d?.title.orEmpty(),
                onBack = onBack,
                trailing = {
                    if (d != null && !readOnly) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.cd_edit_button),
                                tint = c.textTertiary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onEdit(d.id) },
                            )
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.cd_delete_button),
                                tint = c.error,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { showDeleteDialog = true },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (d == null) return@Scaffold

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroOrientation(d)
            Spacer(modifier = Modifier.height(20.dp))
            MetricsRow(d, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                AlignedIdeasSection(d, onIdeaClick = onIdeaClick)
                Spacer(modifier = Modifier.height(28.dp))
                KeyMetricsSection(d)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeroOrientation(d: OrientationDetailUi) {
    val c = AriaTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.primaryMain)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = d.code,
                color = Color.White,
                style = TextStyle(
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                ),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(text = d.priority.localizedName().uppercase(), color = Color.White, style = AriaText.labelMd)
            }
        }
        if (d.period.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = d.period, color = Color.White.copy(alpha = 0.65f), style = AriaText.labelMd)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = d.title, color = Color.White, style = AriaText.titleLg.copy(fontSize = 22.sp, lineHeight = 28.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = d.description, color = Color.White.copy(alpha = 0.78f), style = AriaText.bodyLg)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(R.string.orientations_progress_label), style = AriaText.labelMd, color = Color.White.copy(alpha = 0.6f))
            Text(
                text = "${(d.progress * 100).toInt()}%",
                color = Color.White,
                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(d.progress)
                    .height(8.dp)
                    .background(c.accentLight),
            )
        }
    }
}

@Composable
private fun MetricsRow(d: OrientationDetailUi, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MiniMetric(
            label = stringResource(R.string.orientation_details_metric_ideas),
            value = "${d.ideasCount}",
            trend = orientationTrendLabel(d.ideasDelta, percent = false),
            trendDir = orientationTrendDir(d.ideasDelta),
            modifier = Modifier.weight(1f),
        )
        MiniMetric(label = stringResource(R.string.orientation_details_metric_projects), value = "%02d".format(d.projectsActive), sub = stringResource(R.string.orientation_details_metric_projects_active), modifier = Modifier.weight(1f))
        MiniMetric(
            label = stringResource(R.string.orientation_details_metric_roi),
            value = d.roiCompact,
            trend = orientationTrendLabel(d.roiDeltaPercent, percent = true),
            trendDir = orientationTrendDir(d.roiDeltaPercent),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MiniMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trend: String? = null,
    trendDir: AriaTrendDir = AriaTrendDir.None,
    sub: String? = null,
) {
    com.fiap.ariachallenge.ui.aria.AriaMetricCard(
        label = label,
        value = value,
        trend = trend,
        trendDir = trendDir,
        sub = sub,
        modifier = modifier,
    )
}

@Composable
private fun AlignedIdeasSection(d: OrientationDetailUi, onIdeaClick: (String) -> Unit) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.orientation_details_aligned_ideas))
    if (d.alignedIdeas.isEmpty()) {
        AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_aligned_ideas))
        return
    }
    AriaCard(padding = 4.dp) {
        Column {
            d.alignedIdeas.forEachIndexed { i, idea ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIdeaClick(idea.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(imageVector = Icons.Outlined.Lightbulb, contentDescription = null, tint = c.textTertiary, modifier = Modifier.size(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = idea.title, style = AriaText.bodyMd, color = c.textPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = idea.author.uppercase(), style = AriaText.labelMd, color = c.textTertiary)
                    }
                    AriaScoreBadge(value = idea.score)
                }
                if (i < d.alignedIdeas.lastIndex) AriaHairline()
            }
        }
    }
}

@Composable
private fun KeyMetricsSection(d: OrientationDetailUi) {
    val c = AriaTheme.colors
    AriaSectionTitle(text = stringResource(R.string.orientation_details_key_metrics))
    AriaCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (d.keyMetrics.isEmpty()) {
                Text(
                    text = stringResource(R.string.state_empty_key_metrics),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            }
            d.keyMetrics.forEach { metric ->
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = metric.name, style = AriaText.bodyMd, color = c.textSecondary)
                        Row {
                            Text(
                                text = metric.achieved,
                                color = c.textPrimary,
                                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                            )
                            Text(
                                text = " / ${metric.target}",
                                color = c.textTertiary,
                                style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    AriaProgressLine(value = metric.progress, color = c.success)
                }
            }
        }
    }
}

private fun orientationTrendLabel(delta: Int, percent: Boolean): String? {
    if (delta == 0) return null
    val sign = if (delta > 0) "+" else ""
    val suffix = if (percent) "%" else ""
    return "$sign$delta$suffix"
}

private fun orientationTrendDir(delta: Int): AriaTrendDir = when {
    delta > 0 -> AriaTrendDir.Up
    delta < 0 -> AriaTrendDir.Down
    else -> AriaTrendDir.None
}
