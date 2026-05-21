package com.fiap.ariachallenge.ui.operador.notificacoes

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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Notification
import com.fiap.ariachallenge.domain.model.NotificationType
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaBottomNavItem
import com.fiap.ariachallenge.ui.aria.AriaEmptyState
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.operador.operadorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.IBMPlexSansFontFamily
import com.fiap.ariachallenge.util.toTimeAgo

@Composable
fun NotificacoesScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    bottomNavItems: List<AriaBottomNavItem> = operadorBottomNavItems(),
    onBack: (() -> Unit)? = null,
    viewModel: NotificacoesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val unread = uiState.notifications.count { !it.isRead }

    Scaffold(
        containerColor = AriaTheme.colors.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.notifications_title),
                sub = if (unread > 0) stringResource(R.string.notifications_unread_count, unread) else stringResource(R.string.notifications_all_done),
                onBack = onBack,
                trailing = {
                    if (uiState.notifications.any { !it.isRead }) {
                        Text(
                            text = stringResource(R.string.action_mark_all_read),
                            color = AriaTheme.colors.accentMain,
                            style = TextStyle(
                                fontFamily = IBMPlexSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.markAllRead() }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                },
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = bottomNavItems,
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(padding))
            uiState.notifications.isEmpty() -> AriaEmptyState(
                icon = Icons.Outlined.Notifications,
                title = stringResource(R.string.state_empty_notifications_title),
                sub = stringResource(R.string.state_empty_notifications_description),
                modifier = Modifier.padding(padding),
            )
            else -> NotificationList(
                items = uiState.notifications,
                onItemClick = viewModel::markRead,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun NotificationList(
    items: List<Notification>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
    ) {
        items(items) { n ->
            NotificationRow(n = n, onClick = { onItemClick(n.id) })
            AriaHairline()
        }
    }
}

private data class Tone(val bg: Color, val fg: Color)

@Composable
private fun toneFor(type: NotificationType): Tone {
    val c = AriaTheme.colors
    return when (type) {
        NotificationType.IDEIA_APROVADA -> Tone(c.successBg, c.success)
        NotificationType.IDEIA_REJEITADA -> Tone(c.errorBg, c.error)
        NotificationType.IDEIA_EM_ANALISE -> Tone(c.accentSubtle, c.accentMain)
        NotificationType.NOVA_ORIENTACAO -> Tone(c.primarySubtle, c.primaryMain)
        NotificationType.COMENTARIO -> Tone(c.infoBg, c.info)
        NotificationType.PROJETO_ATUALIZADO -> Tone(c.primarySubtle, c.primaryMain)
        NotificationType.SISTEMA -> Tone(c.bgTertiary, c.textSecondary)
    }
}

private fun iconFor(type: NotificationType): ImageVector = when (type) {
    NotificationType.IDEIA_APROVADA -> Icons.Outlined.Check
    NotificationType.IDEIA_REJEITADA -> Icons.Outlined.Close
    NotificationType.IDEIA_EM_ANALISE -> Icons.Outlined.AutoAwesome
    NotificationType.NOVA_ORIENTACAO -> Icons.Outlined.Folder
    NotificationType.COMENTARIO -> Icons.Outlined.Info
    NotificationType.PROJETO_ATUALIZADO -> Icons.Outlined.Folder
    NotificationType.SISTEMA -> Icons.Outlined.Info
}

@Composable
private fun NotificationRow(n: Notification, onClick: () -> Unit) {
    val c = AriaTheme.colors
    val tone = toneFor(n.type)
    val rowBg = if (!n.isRead) c.primarySubtle else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (!n.isRead) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 22.dp - 4.dp, start = 0.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(c.accentMain)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tone.bg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = iconFor(n.type), contentDescription = null, tint = tone.fg, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = n.title,
                    style = AriaText.titleMd.copy(
                        fontSize = 14.sp,
                        fontWeight = if (!n.isRead) FontWeight.SemiBold else FontWeight.Medium,
                    ),
                    color = c.textPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = n.message, style = AriaText.bodyMd, color = c.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = n.createdAt.toTimeAgo().uppercase(),
                    style = AriaText.labelMd,
                    color = c.textTertiary,
                )
            }
        }
    }
}
