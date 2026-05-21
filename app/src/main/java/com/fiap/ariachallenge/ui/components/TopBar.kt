package com.fiap.ariachallenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme
import com.fiap.ariachallenge.ui.theme.Screen
import com.fiap.ariachallenge.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaTopBar(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
    val navigationIcon: @Composable () -> Unit = {
        if (showBack) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back_button),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
    if (subtitle != null) {
        TopAppBar(
            title = {
                Column(modifier = Modifier.padding(end = Spacing.xs)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }
            },
            navigationIcon = navigationIcon,
            actions = { actions() },
            colors = colors
        )
    } else {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = navigationIcon,
            actions = { actions() },
            colors = colors
        )
    }
}

@Composable
fun AriaUserTopBar(
    userName: String,
    userRole: String,
    userInitials: String,
    onNotificationsClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Screen.horizontalPadding, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = userRole.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxxs)
                ) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = stringResource(R.string.cd_notifications_bell),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = stringResource(R.string.cd_user_avatar),
                                onClick = onAvatarClick
                            )
                    ) {
                        Text(
                            text = userInitials,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Preview(name = "AriaTopBar – Light")
@Composable
private fun AriaTopBarPreview() {
    AriaChallengeTheme(darkTheme = false) {
        AriaTopBar(title = "Minhas Ideias", subtitle = "Operador")
    }
}

@Preview(name = "AriaTopBar – Dark", showBackground = true)
@Composable
private fun AriaTopBarDarkPreview() {
    AriaChallengeTheme(darkTheme = true) {
        AriaTopBar(title = "Detalhes da Ideia", subtitle = "Operador", showBack = true)
    }
}

@Preview(name = "AriaUserTopBar – Light", showBackground = true)
@Composable
private fun AriaUserTopBarPreview() {
    AriaChallengeTheme(darkTheme = false) {
        AriaUserTopBar(
            userName = "Vítor Mello",
            userRole = "Operador",
            userInitials = "VM",
            onNotificationsClick = {},
            onAvatarClick = {}
        )
    }
}

@Preview(name = "AriaUserTopBar – Dark", showBackground = true)
@Composable
private fun AriaUserTopBarDarkPreview() {
    AriaChallengeTheme(darkTheme = true) {
        AriaUserTopBar(
            userName = "Carlos Mendes",
            userRole = "Gestor",
            userInitials = "CM",
            onNotificationsClick = {},
            onAvatarClick = {}
        )
    }
}
