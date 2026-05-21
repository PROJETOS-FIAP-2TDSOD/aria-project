package com.fiap.ariachallenge.ui.operador.ideias

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaCategoryTag
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaFab
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaScoreBadge
import com.fiap.ariachallenge.ui.aria.AriaStatus
import com.fiap.ariachallenge.ui.aria.AriaStatusBadge
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.operador.OperadorBadgeCelebrationHost
import com.fiap.ariachallenge.ui.operador.operadorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.localizedName
import com.fiap.ariachallenge.util.toTimeAgo

@Composable
fun MinhasIdeiasScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToDetalhes: (String) -> Unit,
    viewModel: MinhasIdeiasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    OperadorBadgeCelebrationHost {
    Scaffold(
        containerColor = AriaTheme.colors.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.ideas_screen_title),
                sub = stringResource(R.string.ideas_submitted_count, uiState.ideas.size),
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
        val filterTabs = listOf(
            stringResource(R.string.action_filter_all),
            stringResource(R.string.idea_status_in_analysis),
            stringResource(R.string.idea_status_approved),
            stringResource(R.string.idea_status_in_project),
            stringResource(R.string.idea_status_rejected),
        )
        val filterStatuses: List<IdeaStatus?> = listOf(
            null,
            IdeaStatus.EM_ANALISE,
            IdeaStatus.APROVADA,
            IdeaStatus.EM_PROJETO,
            IdeaStatus.REJEITADA,
        )
        val selectedFilterTab = filterStatuses.indexOf(uiState.selectedFilter).coerceAtLeast(0)

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AriaTabs(
                items = filterTabs,
                selected = selectedFilterTab,
                scrollable = true,
                onSelect = { index -> viewModel.setFilter(filterStatuses[index]) },
            )
            when {
                uiState.isLoading -> AriaLoadingSkeleton()
                uiState.error != null -> AriaErrorState(onRetry = viewModel::refresh)
                uiState.filteredIdeas.isEmpty() -> AriaEmptyState(
                    icon = Icons.Outlined.Lightbulb,
                    title = stringResource(R.string.my_ideas_empty_filter_title),
                    sub = stringResource(R.string.my_ideas_empty_filter_sub),
                    cta = stringResource(R.string.ideas_new_button),
                    onCta = onNavigateToNovaIdeia,
                )
                else -> IdeasList(
                    ideas = uiState.filteredIdeas,
                    onIdeaClick = onNavigateToDetalhes,
                )
            }
        }
    }
    }
}

@Composable
private fun IdeasList(
    ideas: List<Idea>,
    onIdeaClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(ideas) { idea ->
            IdeaListCard(idea = idea, onClick = { onIdeaClick(idea.id) })
        }
    }
}

@Composable
private fun IdeaListCard(idea: Idea, onClick: () -> Unit) {
    val c = AriaTheme.colors
    AriaCard(padding = 14.dp, onClick = onClick) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = idea.title, style = AriaText.titleMd, color = c.textPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${idea.updatedAt.toTimeAgo().uppercase()} · ${idea.category.localizedName().uppercase()}",
                style = AriaText.labelMd,
                color = c.textTertiary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = idea.description,
                style = AriaText.bodyMd,
                color = c.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AriaStatusBadge(status = idea.status.toAriaStatus())
                idea.score?.let { AriaScoreBadge(value = it) }
                AriaCategoryTag(text = idea.category.localizedName())
            }
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

