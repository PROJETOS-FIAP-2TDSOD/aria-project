package com.fiap.ariachallenge.ui.lider.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.components.EditableProfileAvatar
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaNotificationBell
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaSecondaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.lider.liderBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

@Composable
fun PerfilLiderScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilLiderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val user = uiState.user

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.profile_title),
                trailing = {
                    AriaNotificationBell(onClick = { onNavigate(AriaDestination.LiderNotificacoes.route) })
                },
            )
        },
        bottomBar = {
            AriaBottomNav(
                items = liderBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 100.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EditableProfileAvatar(
                    name = user?.name ?: "Carlos Mendes",
                    avatarLocalPath = user?.avatarLocalPath,
                    onImagePicked = viewModel::onAvatarPicked,
                    size = 84.dp,
                )
                Text(
                    text = user?.name ?: "Carlos Mendes",
                    style = AriaText.titleLg.copy(fontSize = 20.sp),
                    color = c.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = user?.email ?: "carlos.mendes@aguiabranca.com",
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
                AriaPillLabel(
                    text = stringResource(R.string.profile_director_strategy),
                    bg = c.primarySubtle,
                    fg = c.primaryMain,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            AriaDivider()
            Spacer(modifier = Modifier.height(24.dp))

            AriaSectionTitle(text = stringResource(R.string.profile_statistics_title))
            AriaCard(padding = 16.dp) {
                Column {
                    StatRow(stringResource(R.string.profile_stat_orientations_active), "${uiState.totalOrientations}", divider = true)
                    StatRow(stringResource(R.string.profile_stat_ideas_reviewed), "${uiState.totalIdeasReviewed}", divider = true)
                    StatRow(stringResource(R.string.profile_stat_projects_portfolio), "${uiState.totalProjects}", divider = true)
                    StatRow(stringResource(R.string.profile_stat_roi_managed), uiState.managedRoiLabel, divider = true)
                    StatRow(stringResource(R.string.profile_stat_approval_rate), "${uiState.approvalRatePercent}%", divider = false)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AriaSecondaryBtn(text = stringResource(R.string.profile_logout), onClick = {
                viewModel.logout()
                onLogout()
            })

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.profile_app_footer),
                style = AriaText.labelMd,
                color = c.textTertiary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, divider: Boolean) {
    val c = AriaTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = AriaText.bodyMd, color = c.textSecondary)
        Text(
            text = value,
            color = c.textPrimary,
            style = TextStyle(
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        )
    }
    if (divider) AriaHairline()
}


