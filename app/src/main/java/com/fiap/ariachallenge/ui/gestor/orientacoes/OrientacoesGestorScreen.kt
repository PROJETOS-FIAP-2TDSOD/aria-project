package com.fiap.ariachallenge.ui.gestor.orientacoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaTabs
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.localizedName
import com.fiap.ariachallenge.util.toTimeAgo

@Composable
fun OrientacoesGestorScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onOrientationClick: (String) -> Unit,
    viewModel: OrientacoesGestorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.orientations_gestor_title),
                sub = stringResource(R.string.subtitle_gestor),
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
        when {
            uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(padding))
            uiState.error != null -> AriaErrorState(
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding),
            )
            else -> {
                val filterAllLabel = stringResource(R.string.label_all_fem)
                val emptyTitle = stringResource(R.string.state_empty_orientations_title)
                val emptyDesc = stringResource(R.string.state_empty_orientations_description)
                val byLabel = stringResource(R.string.label_by)
                val tabLabels = listOf(filterAllLabel) + OrientationPriority.entries.map { it.localizedName() }
                val selectedTab = when (val filter = uiState.selectedFilter) {
                    null -> 0
                    else -> OrientationPriority.entries.indexOf(filter) + 1
                }

                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                    AriaTabs(
                        items = tabLabels,
                        selected = selectedTab,
                        scrollable = true,
                        onSelect = { index ->
                            when (index) {
                                0 -> viewModel.setFilter(null)
                                else -> viewModel.setFilter(OrientationPriority.entries[index - 1])
                            }
                        },
                    )
                    if (uiState.filteredOrientations.isEmpty()) {
                        AriaEmptyState(
                            icon = Icons.Outlined.EmojiObjects,
                            title = emptyTitle,
                            sub = emptyDesc,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.filteredOrientations, key = { it.id }) { orientation ->
                                OrientationGestorCard(
                                    orientation = orientation,
                                    byLabel = byLabel,
                                    onClick = { onOrientationClick(orientation.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrientationGestorCard(
    orientation: Orientation,
    byLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val (priorityBg, priorityFg) = when (orientation.priority) {
        OrientationPriority.CRITICA -> c.errorBg to c.error
        OrientationPriority.ALTA -> c.warningBg to c.accentDark
        OrientationPriority.MEDIA -> c.infoBg to c.info
        OrientationPriority.BAIXA -> c.bgTertiary to c.textSecondary
    }

    AriaCard(modifier = modifier.fillMaxWidth(), padding = 16.dp, onClick = onClick) {
        Column {
            Text(text = orientation.title, style = AriaText.titleMd, color = c.textPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            AriaPillLabel(text = orientation.priority.localizedName(), bg = priorityBg, fg = priorityFg)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = orientation.description,
                style = AriaText.bodyMd,
                color = c.textSecondary,
                maxLines = 3,
            )
            Spacer(modifier = Modifier.height(12.dp))
            AriaHairline()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$byLabel ${orientation.author.name} · ${orientation.createdAt.toTimeAgo()}",
                style = AriaText.labelMd,
                color = c.textTertiary,
            )
        }
    }
}
