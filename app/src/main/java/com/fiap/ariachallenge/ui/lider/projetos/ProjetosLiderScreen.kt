package com.fiap.ariachallenge.ui.lider.projetos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaFab
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.gestor.projetos.ProjetosViewModel
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.formatCurrencyCompact

@Composable
fun ProjetosLiderScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToDetalhes: (String) -> Unit,
    onNavigateToCriarProjeto: () -> Unit,
    viewModel: ProjetosViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val total = uiState.projects.size
    val projectedRoiReais = uiState.projects.sumOf { it.estimatedRoi }

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.projects_screen_title),
                sub = stringResource(R.string.projects_summary, total, formatCurrencyCompact(projectedRoiReais)),
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
        floatingActionButton = { AriaFab(onClick = onNavigateToCriarProjeto) },
    ) { padding ->
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabLabels = listOf(
            stringResource(R.string.projects_tab_all),
            stringResource(R.string.projects_tab_running),
            stringResource(R.string.projects_tab_planning),
        )
        val filtered = when (selectedTab) {
            1 -> uiState.projects.filter { it.status == ProjectStatus.EM_ANDAMENTO }
            2 -> uiState.projects.filter { it.status == ProjectStatus.PLANEJAMENTO }
            else -> uiState.projects
        }

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AriaTabs(items = tabLabels, selected = selectedTab, onSelect = { selectedTab = it })
            when {
                uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(20.dp))
                uiState.error != null -> AriaErrorState(
                    onRetry = viewModel::refresh,
                    modifier = Modifier.padding(20.dp),
                )
                filtered.isEmpty() -> AriaEmptyState(
                    icon = Icons.Outlined.Folder,
                    title = stringResource(
                        if (uiState.projects.isEmpty()) {
                            R.string.state_empty_projects_title
                        } else {
                            R.string.projects_empty_category_title
                        },
                    ),
                    sub = stringResource(
                        if (uiState.projects.isEmpty()) {
                            R.string.state_empty_projects_description
                        } else {
                            R.string.projects_empty_category_sub
                        },
                    ),
                    cta = stringResource(R.string.projects_new),
                    onCta = onNavigateToCriarProjeto,
                    modifier = Modifier.padding(20.dp),
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(filtered, key = { it.id }) { project ->
                        ProjectCardLider(
                            project = project,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToDetalhes(project.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardLider(
    project: Project,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val c = AriaTheme.colors
    val isRunning = project.status == ProjectStatus.EM_ANDAMENTO
    AriaCard(modifier = modifier, padding = 16.dp, onClick = onClick) {
        Column {
            AriaPillLabel(
                text = stringResource(if (isRunning) R.string.projects_status_in_progress else R.string.projects_status_planning),
                bg = if (isRunning) c.infoBg else c.bgTertiary,
                fg = if (isRunning) c.info else c.textSecondary,
            )
            Text(
                text = project.title,
                style = AriaText.titleMd,
                color = c.textPrimary,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "${project.progress}% · ROI ${formatCurrencyCompact(project.actualRoi ?: 0.0)} / ${formatCurrencyCompact(project.estimatedRoi)}",
                style = AriaText.bodyMd,
                color = c.textTertiary,
                modifier = Modifier.padding(top = 4.dp),
            )
            AriaProgressLine(
                value = project.progress / 100f,
                color = c.primaryMain,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}
