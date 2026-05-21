package com.fiap.ariachallenge.ui.operador.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaAvatar
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.ui.aria.AriaFab
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaMetricCard
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaScoreBadge
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTextBtn
import com.fiap.ariachallenge.ui.aria.AriaTrendDir
import com.fiap.ariachallenge.ui.components.PerformanceWidget
import com.fiap.ariachallenge.ui.operador.OperadorBadgeCelebrationHost
import com.fiap.ariachallenge.ui.operador.operadorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun HomeOperadorScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToIdeiaDetalhes: (String) -> Unit,
    onNavigateToOrientacaoDetalhes: (String) -> Unit = {},
    viewModel: HomeOperadorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val firstName = uiState.currentUser?.name?.split(" ")?.firstOrNull().orEmpty()

    OperadorBadgeCelebrationHost {
    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            HomeHeader(
                firstName = firstName,
                userName = uiState.currentUser?.name.orEmpty(),
                avatarLocalPath = uiState.currentUser?.avatarLocalPath,
                onNotifClick = { onNavigate(AriaDestination.OperadorNotificacoes.route) },
                onAvatarClick = { onNavigate(AriaDestination.OperadorPerfil.route) },
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = operadorBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
        floatingActionButton = { AriaFab(onClick = onNavigateToNovaIdeia) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PerformanceWidget(
                    userPoints = uiState.userPoints,
                    userBadges = uiState.userBadges,
                )
            }
            item { MetricGrid(uiState) }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            if (uiState.orientations.isNotEmpty()) {
                item { AriaSectionTitle(text = stringResource(R.string.orientations_section_title)) }
                items(uiState.orientations) { orientation ->
                    AriaCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 14.dp,
                        onClick = { onNavigateToOrientacaoDetalhes(orientation.id) },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = orientation.title, style = AriaText.titleMd, color = c.textPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = orientation.description, style = AriaText.bodyMd, color = c.textTertiary, maxLines = 3)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
            item {
                AriaSectionTitle(text = stringResource(R.string.operador_home_updates_title))
            }
            item {
                if (uiState.recentUpdates.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.recentUpdates.forEach { update ->
                            OperadorUpdateCard(
                                update = update,
                                onClick = { onNavigateToIdeiaDetalhes(update.ideaId) },
                            )
                        }
                    }
                } else {
                    AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_updates))
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                AriaSectionTitle(
                    text = stringResource(R.string.operador_home_ai_suggestions),
                    sub = stringResource(R.string.operador_home_ai_suggestions_sub),
                )
            }
            item {
                val suggestion = uiState.aiSuggestion
                if (suggestion != null) {
                    AiSuggestionCard(suggestion = suggestion, onClick = {
                        onNavigateToIdeiaDetalhes(suggestion.sourceIdeaId)
                    })
                } else {
                    AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_ai_operador))
                }
            }
        }
    }
    }
}

@Composable
private fun HomeHeader(
    firstName: String,
    userName: String,
    avatarLocalPath: String?,
    onNotifClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    val c = AriaTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.greeting_good_morning, firstName), style = AriaText.titleMd, color = c.textPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = stringResource(R.string.role_operator), style = AriaText.labelMd, color = c.textTertiary)
            }
            AriaNotificationBell(onClick = onNotifClick)
            Box(modifier = Modifier.clickable(onClick = onAvatarClick)) {
                AriaAvatar(name = userName, size = 40.dp, avatarLocalPath = avatarLocalPath)
            }
        }
        AriaHairline()
    }
}

@Composable
private fun MetricGrid(uiState: HomeOperadorUiState) {
    val metrics = uiState.metrics
    val awaitingSub = metrics.awaitingAnalysis.takeIf { it > 0 }?.let { count ->
        stringResource(R.string.operador_home_metric_awaiting_sub, count)
    }
    val approvalTrend = metrics.approvalRatePercent.takeIf { metrics.totalIdeas > 0 }?.let { "$it%" }
    val roiSub = stringResource(R.string.label_roi)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.dashboard_funnel_submitted),
                value = "${metrics.totalIdeas}",
                sub = awaitingSub,
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.operador_home_metric_analysis),
                value = "%02d".format(metrics.inAnalysis),
                modifier = Modifier.weight(1f),
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.operador_home_metric_approved),
                value = "%02d".format(metrics.approved),
                trend = approvalTrend,
                trendDir = if (approvalTrend != null && metrics.approvalRatePercent > 0) {
                    AriaTrendDir.Up
                } else {
                    AriaTrendDir.None
                },
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.operador_home_metric_in_project),
                value = "%02d".format(metrics.inProject),
                trend = metrics.inProjectRoiLabel,
                trendDir = if (metrics.inProjectRoiLabel != null) AriaTrendDir.Up else AriaTrendDir.None,
                sub = metrics.inProjectRoiLabel?.let { roiSub },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OperadorUpdateCard(
    update: OperadorIdeaUpdate,
    onClick: () -> Unit,
) {
    val c = AriaTheme.colors
    val (icon, bg, fg, titleRes) = when (update.status) {
        IdeaStatus.APROVADA, IdeaStatus.EM_PROJETO -> Quad(
            Icons.Outlined.Check,
            c.successBg,
            c.success,
            R.string.operador_home_idea_approved,
        )
        IdeaStatus.REJEITADA -> Quad(
            Icons.Outlined.Close,
            c.errorBg,
            c.error,
            R.string.operador_home_idea_rejected,
        )
        else -> Quad(
            Icons.Outlined.AccessTime,
            c.warningBg,
            c.accentDark,
            R.string.operador_home_idea_analyzing_days,
        )
    }
    UpdateCard(
        icon = icon,
        bg = bg,
        fg = fg,
        title = stringResource(titleRes),
        body = "\"${update.title}\"",
        meta = update.meta,
        onClick = onClick,
    )
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
private fun UpdateCard(
    icon: ImageVector,
    bg: Color,
    fg: Color,
    title: String,
    body: String,
    meta: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val c = AriaTheme.colors
    AriaCard(padding = 14.dp, onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = AriaText.titleMd.copy(fontSize = 14.sp), color = c.textPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = body, style = AriaText.bodyMd, color = c.textSecondary)
                if (meta != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = meta, style = AriaText.labelMd, color = c.textTertiary)
                }
            }
        }
    }
}

@Composable
private fun AiSuggestionCard(
    suggestion: com.fiap.ariachallenge.domain.model.AiSimilarIdeaSuggestion,
    onClick: () -> Unit,
) {
    val c = AriaTheme.colors
    AriaCard(padding = 16.dp, accent = true, onClick = onClick) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = suggestion.label, style = AriaText.labelMd, color = c.accentMain)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = suggestion.title, style = AriaText.titleMd, color = c.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = suggestion.body, style = AriaText.bodyMd, color = c.textSecondary)
                }
                Text(text = "✦", color = c.accentMain, style = AriaText.titleLg)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AriaScoreBadge(value = suggestion.score)
                Text(text = suggestion.roiLabel, style = AriaText.labelMd, color = c.textTertiary)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

