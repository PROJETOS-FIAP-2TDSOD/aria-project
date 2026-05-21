package com.fiap.ariachallenge.ui.lider.tendencias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.components.AriaTopBar
import com.fiap.ariachallenge.ui.components.ErrorState
import com.fiap.ariachallenge.ui.components.IdeaCard
import com.fiap.ariachallenge.ui.components.LoadingState
import com.fiap.ariachallenge.ui.components.MetricCard
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.aria.AriaBottomNav as AriaBottomNavNew
import com.fiap.ariachallenge.ui.theme.Spacing
import com.fiap.ariachallenge.util.localizedName
import com.fiap.ariachallenge.util.toTimeAgo
import kotlin.math.roundToInt

@Composable
fun TendenciasScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onIdeaClick: (String) -> Unit = {},
    viewModel: TendenciasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.trends_screen_title),
                subtitle = stringResource(R.string.subtitle_lider),
                actions = {
                    AriaNotificationBell(onClick = { onNavigate(AriaDestination.LiderNotificacoes.route) })
                }
            )
        },
        bottomBar = {
            AriaBottomNavNew(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = viewModel::refresh,
                modifier = Modifier.padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    MetricCard(
                        label = stringResource(R.string.trends_metric_approval_rate),
                        value = "${(uiState.approvalRate * 100).roundToInt()}%",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (uiState.topCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.trends_top_categories),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.xs)
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.md),
                                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                            ) {
                                val max = uiState.topCategories
                                    .maxOfOrNull { it.second }
                                    ?.coerceAtLeast(1) ?: 1
                                uiState.topCategories.forEach { (category, count) ->
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(Spacing.xxxs)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = category.localizedName(),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = { count.toFloat() / max },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.recentApproved.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.trends_recent_approved),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(uiState.recentApproved) { idea ->
                        IdeaCard(
                            title = idea.title,
                            author = idea.author.name,
                            timeAgo = idea.createdAt.toTimeAgo(),
                            category = idea.category.localizedName(),
                            description = idea.description,
                            status = idea.status,
                            score = idea.score,
                            onClick = { onIdeaClick(idea.id) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
