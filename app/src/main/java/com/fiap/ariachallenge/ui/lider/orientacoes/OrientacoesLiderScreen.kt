package com.fiap.ariachallenge.ui.lider.orientacoes

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaFab
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

@Composable
fun OrientacoesLiderScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onOrientationClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: OrientacoesLiderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(c.surface)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.orientations_lider_title),
                            style = AriaText.titleMd,
                            color = c.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.orientations_lider_eyebrow, uiState.activeCount, uiState.alignedIdeasCount),
                            style = AriaText.labelMd,
                            color = c.textTertiary,
                        )
                    }
                    AriaNotificationBell(onClick = { onNavigate(AriaDestination.LiderNotificacoes.route) })
                }
                AriaHairline()
            }
        },
        bottomBar = {
            AriaBottomNav(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
        floatingActionButton = { AriaFab(onClick = onCreateClick) },
    ) { padding ->
        if (uiState.items.isEmpty()) {
            AriaEmptyState(
                title = stringResource(R.string.state_empty_orientations_title),
                sub = stringResource(R.string.state_empty_orientations_lider_description),
                modifier = Modifier.padding(padding).fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    OrientationCard(item = item, onClick = { onOrientationClick(item.id) })
                }
            }
        }
    }
}

@Composable
private fun OrientationCard(item: OrientationListItemUi, onClick: () -> Unit) {
    val c = AriaTheme.colors
    AriaCard(padding = 16.dp, onClick = onClick) {
        Column {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.primarySubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.code,
                        color = c.primaryMain,
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        ),
                    )
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.title, style = AriaText.titleMd, color = c.textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = item.description, style = AriaText.bodyMd, color = c.textSecondary, maxLines = 2)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = stringResource(R.string.orientations_progress_label), style = AriaText.labelMd, color = c.textTertiary)
                Text(
                    text = "${(item.progress * 100).toInt()}%",
                    color = c.textPrimary,
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            AriaProgressLine(value = item.progress, color = c.primaryMain)
            Spacer(modifier = Modifier.height(14.dp))
            AriaHairline()
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FooterChip(icon = Icons.Outlined.Lightbulb, label = stringResource(R.string.orientations_footer_ideas, item.ideaCount))
                FooterChip(icon = Icons.Outlined.Folder, label = stringResource(R.string.orientations_footer_projects, item.projectCount))
            }
        }
    }
}

@Composable
private fun FooterChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    val c = AriaTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = c.textTertiary, modifier = Modifier.size(14.dp))
        Text(text = label, style = AriaText.bodyMd, color = c.textSecondary)
    }
}
