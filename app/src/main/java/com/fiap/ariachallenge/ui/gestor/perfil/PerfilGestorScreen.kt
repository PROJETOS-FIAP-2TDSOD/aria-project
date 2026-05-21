package com.fiap.ariachallenge.ui.gestor.perfil

import androidx.compose.foundation.background
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
import com.fiap.ariachallenge.ui.components.EditableProfileAvatar
import com.fiap.ariachallenge.ui.aria.AriaBottomNav
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDivider
import com.fiap.ariachallenge.ui.aria.AriaPillLabel
import com.fiap.ariachallenge.ui.aria.AriaSecondaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSectionTitle
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.aria.AvatarTone
import com.fiap.ariachallenge.ui.gestor.gestorBottomNavItems
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.localizedName

@Composable
fun PerfilGestorScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilGestorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val c = AriaTheme.colors

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = { AriaTopBar(title = stringResource(R.string.profile_title)) },
        bottomBar = {
            AriaBottomNav(
                items = gestorBottomNavItems(),
                activeId = currentRoute,
                onSelect = onNavigate,
            )
        },
    ) { padding ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
            return@Scaffold
        }
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
                    name = user.name,
                    avatarLocalPath = user.avatarLocalPath,
                    onImagePicked = viewModel::onAvatarPicked,
                    size = 84.dp,
                    tone = AvatarTone.Accent,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = user.name, style = AriaText.titleLg.copy(fontSize = 20.sp), color = c.textPrimary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = user.email, style = AriaText.bodyMd, color = c.textSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    AriaPillLabel(
                        text = stringResource(R.string.profile_manager_dept, user.department.uppercase().ifBlank { "LOGÍSTICA" }),
                        bg = c.accentSubtle,
                        fg = c.accentMain,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            AriaDivider()
            Spacer(modifier = Modifier.height(24.dp))

            AriaSectionTitle(text = stringResource(R.string.gestor_profile_projects_under_mgmt))
            AriaCard(padding = 16.dp) {
                Column {
                    StatRow(label = stringResource(R.string.gestor_profile_metric_total), value = "${uiState.totalProjects}")
                    StatRow(label = stringResource(R.string.gestor_profile_metric_active), value = "${uiState.activeProjects}")
                    StatRow(label = stringResource(R.string.gestor_profile_metric_completed), value = "${uiState.completedProjects}", isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            AriaSectionTitle(text = stringResource(R.string.profile_info_title))
            AriaCard(padding = 16.dp) {
                Column {
                    InfoRow(label = stringResource(R.string.profile_label_email), value = user.email)
                    InfoRow(
                        label = stringResource(R.string.profile_label_department),
                        value = user.department.ifBlank { stringResource(R.string.label_not_informed) },
                    )
                    InfoRow(label = stringResource(R.string.profile_label_role), value = user.role.localizedName(), isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            AriaSecondaryBtn(
                text = stringResource(R.string.profile_logout),
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
            )
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
private fun StatRow(label: String, value: String, isLast: Boolean = false) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
    ) {
        Text(text = label, style = AriaText.bodyMd, color = c.textSecondary, modifier = Modifier.weight(1f))
        Text(text = value, style = AriaText.titleMd.copy(fontSize = 14.sp), color = c.textPrimary)
    }
    if (!isLast) Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(c.borderTertiary))
}

@Composable
private fun InfoRow(label: String, value: String, isLast: Boolean = false) {
    val c = AriaTheme.colors
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(text = label, style = AriaText.labelMd, color = c.textTertiary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = AriaText.bodyMd, color = c.textPrimary)
    }
    if (!isLast) Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(c.borderTertiary))
}

