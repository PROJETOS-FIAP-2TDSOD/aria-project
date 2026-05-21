package com.fiap.ariachallenge.ui.gestor.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaAvatar
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaMetricCard
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaScoreRing
import com.fiap.ariachallenge.ui.aria.AriaScoreRingDefaults
import com.fiap.ariachallenge.ui.aria.AriaSectionEmptyCard
import com.fiap.ariachallenge.util.formatCurrencyCompact
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTextBtn
import com.fiap.ariachallenge.ui.aria.AriaTrendDir
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.localizedName

@Composable
fun HomeGestorScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToAnalisar: (String) -> Unit,
    onNavigateToDetalhesProjeto: (String) -> Unit,
    onNavigateToDetalhesOrientacao: (String) -> Unit,
    viewModel: HomeGestorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val firstName = uiState.currentUser?.name?.split(" ")?.firstOrNull() ?: "Ana"
    val departmentDefault = stringResource(R.string.dept_logistics_default)
    val department = uiState.currentUser?.department?.uppercase()?.ifBlank { departmentDefault } ?: departmentDefault

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            GestorHeader(
                firstName = firstName,
                userName = uiState.currentUser?.name ?: "Ana Silva",
                avatarLocalPath = uiState.currentUser?.avatarLocalPath,
                department = department,
                onNotifClick = { onNavigate(AriaDestination.GestorNotificacoes.route) },
                onAvatarClick = { onNavigate(AriaDestination.GestorPerfil.route) },
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = gestorBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { MetricGrid3x2(uiState) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            if (uiState.pendingIdeas.isNotEmpty()) {
                item {
                    AriaSectionTitle(
                        text = stringResource(R.string.gestor_home_urgent_ideas),
                        sub = stringResource(R.string.gestor_home_urgent_sub),
                    )
                }
                items(uiState.pendingIdeas.take(2)) { idea ->
                    UrgentIdeaCard(
                        idea = idea,
                        onAnalyze = { onNavigateToAnalisar(idea.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (uiState.activeProjects.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item { AriaSectionTitle(text = stringResource(R.string.nav_projects)) }
                items(uiState.activeProjects) { project ->
                    AriaCard(
                        modifier = Modifier.fillMaxWidth(),
                        padding = 14.dp,
                        onClick = { onNavigateToDetalhesProjeto(project.id) },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(text = project.title, style = AriaText.titleMd, color = c.textPrimary)
                            Text(
                                text = "${project.progress}% · ${stringResource(R.string.label_roi)} ${formatCurrencyCompact(project.estimatedRoi)}",
                                style = AriaText.bodyMd,
                                color = c.textTertiary,
                            )
                        }
                    }
                }
            }
            if (uiState.orientations.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    AriaSectionTitle(text = stringResource(R.string.orientations_section_title))
                }
                items(uiState.orientations) { orientation ->
                    OrientationRow(
                        title = orientation.title,
                        description = orientation.description,
                        onClick = { onNavigateToDetalhesOrientacao(orientation.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { AriaSectionTitle(text = stringResource(R.string.gestor_home_ai_insights)) }
            item {
                if (uiState.aiInsights.isNotEmpty()) {
                    InsightsCard(insights = uiState.aiInsights)
                } else {
                    AriaSectionEmptyCard(message = stringResource(R.string.state_section_empty_ai_insights))
                }
            }
        }
    }
}

@Composable
private fun GestorHeader(
    firstName: String,
    userName: String,
    avatarLocalPath: String?,
    department: String,
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
                Text(text = stringResource(R.string.role_manager_dept, department), style = AriaText.labelMd, color = c.textTertiary)
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
private fun MetricGrid3x2(state: HomeGestorUiState) {
    val m = state.metrics
    val pendingSub = m.inReviewCount.takeIf { it > 0 }?.let { count ->
        stringResource(R.string.gestor_home_metric_in_review_sub, count)
    }
    val approvalTrend = m.approvalRatePercent?.let { "$it%" }
    val activeTrend = m.activeProjectsDelta?.let { delta ->
        val sign = if (delta >= 0) "+" else ""
        "$sign$delta"
    }
    val activeTrendDir = when {
        m.activeProjectsDelta == null -> AriaTrendDir.None
        m.activeProjectsDelta >= 0 -> AriaTrendDir.Up
        else -> AriaTrendDir.Down
    }
    val roiValue = m.totalRoiLabel ?: "—"
    val roiTrend = m.roiMonthDeltaPercent?.let { delta ->
        val sign = if (delta >= 0) "+" else ""
        "$sign$delta%"
    }
    val roiTrendDir = when {
        m.roiMonthDeltaPercent == null -> AriaTrendDir.None
        m.roiMonthDeltaPercent >= 0 -> AriaTrendDir.Up
        else -> AriaTrendDir.Down
    }
    val statusValue = m.avgPendingDaysLabel ?: "—"
    val conversionValue = m.conversionPercent?.let { "$it%" } ?: "—"
    val conversionTrend = m.conversionMonthDelta?.let { delta ->
        val sign = if (delta >= 0) "+" else ""
        "$sign$delta"
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.gestor_home_metric_pending),
                value = "%02d".format(m.pendingIdeas),
                sub = pendingSub,
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.gestor_home_metric_approved),
                value = "%02d".format(m.approvedIdeas),
                trend = approvalTrend,
                trendDir = if (approvalTrend != null) AriaTrendDir.Up else AriaTrendDir.None,
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.nav_projects),
                value = "%02d".format(m.activeProjects),
                trend = activeTrend,
                trendDir = activeTrendDir,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AriaMetricCard(
                label = stringResource(R.string.label_roi),
                value = roiValue,
                trend = roiTrend,
                trendDir = roiTrendDir,
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.gestor_home_metric_avg_wait),
                value = statusValue,
                modifier = Modifier.weight(1f),
            )
            AriaMetricCard(
                label = stringResource(R.string.dashboard_metric_conversion),
                value = conversionValue,
                trend = conversionTrend,
                trendDir = when {
                    m.conversionMonthDelta == null -> AriaTrendDir.None
                    m.conversionMonthDelta >= 0 -> AriaTrendDir.Up
                    else -> AriaTrendDir.Down
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun UrgentIdeaCard(idea: Idea, onAnalyze: () -> Unit, modifier: Modifier = Modifier) {
    val c = AriaTheme.colors
    AriaCard(modifier = modifier, padding = 14.dp, onClick = onAnalyze) {
        Column {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(c.error))
                        Text(
                            text = stringResource(R.string.gestor_sla_critical, 10),
                            style = AriaText.labelMd,
                            color = c.error,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = idea.title, style = AriaText.titleMd, color = c.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${idea.author.name} · ${idea.category.localizedName()}",
                        style = AriaText.bodyMd,
                        color = c.textTertiary,
                    )
                }
                AriaScoreRing(value = idea.score ?: 82, size = AriaScoreRingDefaults.Card, label = null)
            }
            Spacer(modifier = Modifier.height(12.dp))
            AriaDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Outlined.Explore, contentDescription = null, tint = c.accentMain, modifier = Modifier.size(14.dp))
                Text(
                    text = stringResource(R.string.gestor_high_match_orientation),
                    style = AriaText.labelMd,
                    color = c.accentMain,
                    modifier = Modifier.weight(1f),
                )
                AriaTextBtn(text = stringResource(R.string.gestor_action_analyze), trailingArrow = true, onClick = onAnalyze)
            }
        }
    }
}

@Composable
private fun OrientationRow(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    AriaCard(modifier = modifier, padding = 16.dp, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.primarySubtle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = Icons.Outlined.Explore, contentDescription = null, tint = c.primaryMain, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = AriaText.titleMd, color = c.textPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = AriaText.bodyMd, color = c.textTertiary, maxLines = 2)
            }
        }
    }
}

@Composable
private fun InsightsCard(insights: List<com.fiap.ariachallenge.domain.model.AiTextInsight>) {
    val c = AriaTheme.colors
    AriaCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            insights.forEach { insight ->
                val (icon, bg, fg) = when (insight.tone) {
                    com.fiap.ariachallenge.domain.model.AiInsightTone.Accent ->
                        Triple(Icons.Outlined.AutoAwesome, c.accentSubtle, c.accentMain)
                    com.fiap.ariachallenge.domain.model.AiInsightTone.Success ->
                        Triple(Icons.AutoMirrored.Outlined.TrendingUp, c.successBg, c.success)
                    else ->
                        Triple(Icons.Outlined.Check, c.infoBg, c.info)
                }
                InsightLine(icon = icon, bg = bg, fg = fg, text = insight.message)
            }
        }
    }
}

@Composable
private fun InsightLine(icon: ImageVector, bg: Color, fg: Color, text: String) {
    val c = AriaTheme.colors
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
        }
        Text(text = text, style = AriaText.bodyMd, color = c.textPrimary, modifier = Modifier.weight(1f))
    }
}

