package com.fiap.ariachallenge.ui.operador.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.User
import android.net.Uri
import com.fiap.ariachallenge.ui.components.EditableProfileAvatar
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaHairline
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaSecondaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.components.AchievementsCard
import com.fiap.ariachallenge.ui.operador.OperadorBadgeCelebrationHost
import com.fiap.ariachallenge.ui.operador.home.HomeMetrics
import com.fiap.ariachallenge.ui.operador.operadorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun PerfilOperadorScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilOperadorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    OperadorBadgeCelebrationHost {
    Scaffold(
        containerColor = AriaTheme.colors.bgPrimary,
        topBar = { AriaTopBar(title = stringResource(R.string.profile_title)) },
        bottomBar = {
            AriaBottomNav(
                items = operadorBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
    ) { padding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            PerfilContent(
                user = user,
                metrics = uiState.metrics,
                userPoints = uiState.userPoints,
                userBadges = uiState.userBadges,
                attributedRoiLabel = uiState.attributedRoiLabel,
                onAvatarPicked = viewModel::onAvatarPicked,
                onLogout = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.padding(padding),
            )
        }
    }
    }
}

@Composable
private fun PerfilContent(
    user: User,
    metrics: HomeMetrics,
    userPoints: Int,
    userBadges: List<String>,
    attributedRoiLabel: String,
    onAvatarPicked: (Uri) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EditableProfileAvatar(
                name = user.name,
                avatarLocalPath = user.avatarLocalPath,
                onImagePicked = onAvatarPicked,
                size = 84.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = user.name,
                    style = AriaText.titleLg.copy(fontSize = 20.sp),
                    color = c.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
                Spacer(modifier = Modifier.height(10.dp))
                AriaPillLabel(
                    text = stringResource(R.string.profile_operator_dept, user.department.uppercase().ifBlank { "LOGÍSTICA ES" }),
                    bg = c.primarySubtle,
                    fg = c.primaryMain,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        AriaDivider()
        Spacer(modifier = Modifier.height(24.dp))

        AriaSectionTitle(text = stringResource(R.string.profile_achievements_title))
        AchievementsCard(
            userPoints = userPoints,
            userBadges = userBadges,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AriaSectionTitle(text = stringResource(R.string.profile_statistics_title))
        AriaCard(padding = 16.dp) {
            val approvalRate = if (metrics.totalIdeas > 0) {
                (metrics.approved * 100 / metrics.totalIdeas)
            } else 0
            val rows = listOf(
                stringResource(R.string.profile_stat_approval_rate) to "${approvalRate}%",
                stringResource(R.string.profile_stat_total_ideas) to "${metrics.totalIdeas}",
                stringResource(R.string.profile_stat_in_analysis) to "%02d".format(metrics.inAnalysis),
                stringResource(R.string.profile_stat_projects_generated) to "%02d".format(metrics.inProject),
                stringResource(R.string.profile_stat_roi_attributed) to attributedRoiLabel,
            )
            Column {
                rows.forEachIndexed { i, (label, value) ->
                    StatRow(label = label, value = value, isLast = i == rows.lastIndex)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AriaSecondaryBtn(text = stringResource(R.string.profile_logout), onClick = onLogout)
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

@Composable
private fun StatRow(label: String, value: String, isLast: Boolean) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        Text(text = label, style = AriaText.bodyMd, color = c.textSecondary, modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = AriaText.titleMd.copy(fontSize = 14.sp),
            color = c.textPrimary,
        )
    }
    if (!isLast) AriaHairline()
}
