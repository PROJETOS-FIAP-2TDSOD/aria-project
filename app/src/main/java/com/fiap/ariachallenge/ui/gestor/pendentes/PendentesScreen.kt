package com.fiap.ariachallenge.ui.gestor.pendentes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaScoreRing
import com.fiap.ariachallenge.ui.aria.AriaScoreRingDefaults
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.IBMPlexSansFontFamily
import com.fiap.ariachallenge.util.localizedName
import androidx.annotation.StringRes

private enum class SortKind(val id: String, @StringRes val labelRes: Int) {
    SLA("sla", R.string.pending_sort_sla),
    SCORE("score", R.string.pending_sort_score),
    MATCH("match", R.string.pending_sort_match),
}

@Composable
fun PendentesScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToAnalisar: (String) -> Unit,
    viewModel: PendentesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    var sort by remember { mutableStateOf(SortKind.SLA) }

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.pending_screen_title),
                sub = stringResource(R.string.pending_waiting_count, uiState.ideas.size),
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
        val sortTabs = SortKind.entries.map { stringResource(it.labelRes) }
        val selectedSortTab = SortKind.entries.indexOf(sort).coerceAtLeast(0)

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AriaTabs(
                items = sortTabs,
                selected = selectedSortTab,
                onSelect = { index -> sort = SortKind.entries[index] },
            )
            when {
                uiState.isLoading -> AriaLoadingSkeleton()
                uiState.error != null -> AriaErrorState(onRetry = viewModel::refresh)
                uiState.ideas.isEmpty() -> AriaEmptyState(
                    icon = Icons.Outlined.Lightbulb,
                    title = stringResource(R.string.state_empty_pending_title),
                    sub = stringResource(R.string.state_empty_pending_gestor_description),
                )
                else -> {
                    val sorted = when (sort) {
                        SortKind.SLA -> uiState.ideas.sortedByDescending { java.time.Duration.between(it.createdAt, java.time.LocalDateTime.now()).toDays() }
                        SortKind.SCORE -> uiState.ideas.sortedByDescending { it.score ?: 0 }
                        SortKind.MATCH -> uiState.ideas
                    }
                    PendingList(items = sorted, onAnalyse = onNavigateToAnalisar)
                }
            }
        }
    }
}

@Composable
private fun PendingList(items: List<Idea>, onAnalyse: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items) { idea ->
            PendingIdeaCard(idea = idea, onClick = { onAnalyse(idea.id) })
        }
    }
}

@Composable
private fun PendingIdeaCard(idea: Idea, onClick: () -> Unit) {
    val c = AriaTheme.colors
    val days = java.time.Duration.between(idea.createdAt, java.time.LocalDateTime.now()).toDays().toInt().coerceAtLeast(1)
    val sla = when {
        days >= 9 -> SlaTone(c.error, stringResource(R.string.gestor_sla_critical, days))
        days >= 6 -> SlaTone(c.warning, stringResource(R.string.gestor_sla_attention, days))
        else -> SlaTone(c.success, stringResource(R.string.gestor_sla_queue_days, days))
    }
    AriaCard(padding = 14.dp, onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(sla.color))
                        Text(text = sla.label, style = AriaText.labelMd, color = sla.color)
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
                AriaScoreRing(value = idea.score ?: 0, size = AriaScoreRingDefaults.Card, label = null)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.action_review),
                    tint = c.textTertiary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

private data class SlaTone(val color: Color, val label: String)
