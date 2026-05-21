package com.fiap.ariachallenge.ui.gestor.projetos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaFab
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily
import com.fiap.ariachallenge.util.formatCurrencyCompact


@Composable
fun ProjetosScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToCriarProjeto: () -> Unit,
    onNavigateToDetalhesProjeto: (String) -> Unit,
    viewModel: ProjetosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
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
                items = gestorBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
        floatingActionButton = { AriaFab(onClick = onNavigateToCriarProjeto) },
    ) { padding ->
        var selectedTab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableIntStateOf(0) }
        val tabLabels = listOf(
            stringResource(R.string.projects_tab_all),
            stringResource(R.string.projects_tab_running),
            stringResource(R.string.projects_tab_planning),
        )
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AriaTabs(items = tabLabels, selected = selectedTab, onSelect = { selectedTab = it })
            when {
                uiState.isLoading -> AriaLoadingSkeleton()
                uiState.error != null -> AriaErrorState(onRetry = viewModel::refresh)
                else -> {
                    val list = when (selectedTab) {
                        1 -> uiState.projects.filter { it.status == ProjectStatus.EM_ANDAMENTO }
                        2 -> uiState.projects.filter { it.status == ProjectStatus.PLANEJAMENTO }
                        else -> uiState.projects
                    }
                    if (list.isEmpty()) {
                        AriaEmptyState(
                            icon = Icons.Outlined.Folder,
                            title = stringResource(R.string.projects_empty_category_title),
                            sub = stringResource(R.string.projects_empty_category_sub),
                            cta = stringResource(R.string.projects_new),
                            onCta = onNavigateToCriarProjeto,
                        )
                    } else {
                        ProjectsList(items = list, onOpen = onNavigateToDetalhesProjeto)
                    }
                }
            }
        }
    }
}


@Composable
private fun ProjectsList(items: List<Project>, onOpen: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items) { p -> ProjectCardItem(project = p, onClick = { onOpen(p.id) }) }
    }
}

@Composable
private fun ProjectCardItem(project: Project, onClick: () -> Unit) {
    val c = AriaTheme.colors
    val isRunning = project.status == ProjectStatus.EM_ANDAMENTO
    val stageBg = if (isRunning) c.infoBg else c.bgTertiary
    val stageFg = if (isRunning) c.info else c.textSecondary
    val stageLabel = stringResource(if (isRunning) R.string.projects_status_in_progress else R.string.projects_status_planning)
    val progress = project.progress
    val risk = when {
        progress < 20 && !isRunning -> RiskTone(c.error)
        progress in 20..49 -> RiskTone(c.warning)
        else -> RiskTone(null)
    }
    val due = project.expectedEndDate.toLocalDate()

    AriaCard(padding = 16.dp, onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    AriaPillLabel(text = stageLabel, bg = stageBg, fg = stageFg)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = project.title, style = AriaText.titleMd, color = c.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stringResource(R.string.projects_delivery_label, due.toString()), style = AriaText.labelMd, color = c.textTertiary)
                }
                risk.color?.let {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(it),
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row {
                Text(text = stringResource(R.string.projects_progress), style = AriaText.labelMd, color = c.textTertiary, modifier = Modifier.weight(1f))
                Text(
                    text = "$progress%",
                    style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                    color = c.textPrimary,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            AriaProgressLine(value = progress / 100f, color = risk.color ?: c.primaryMain)
            Spacer(modifier = Modifier.height(12.dp))
            AriaDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                RoiBlock(label = stringResource(R.string.projects_roi_expected), value = formatCurrencyCompact(project.estimatedRoi))
                RoiBlock(
                    label = stringResource(R.string.projects_roi_realized),
                    value = formatCurrencyCompact(project.actualRoi ?: 0.0),
                    valueColor = if ((project.actualRoi ?: 0.0) >= project.estimatedRoi * (progress / 100.0)) c.success else c.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun RoiBlock(label: String, value: String, valueColor: Color? = null) {
    val c = AriaTheme.colors
    Column {
        Text(text = label, style = AriaText.labelMd, color = c.textTertiary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = TextStyle(fontFamily = OutfitFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
            color = valueColor ?: c.textPrimary,
        )
    }
}

private data class RiskTone(val color: Color?)
