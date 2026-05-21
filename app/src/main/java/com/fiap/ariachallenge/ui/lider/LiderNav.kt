package com.fiap.ariachallenge.ui.lider

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNavItem

@Composable
fun liderBottomNavItems(): List<AriaBottomNavItem> = listOf(
    AriaBottomNavItem(AriaDestination.LiderDashboard.route, stringResource(R.string.nav_dashboard), Icons.Outlined.GridView),
    AriaBottomNavItem(AriaDestination.LiderOrientacoes.route, stringResource(R.string.nav_orientations_short), Icons.Outlined.Explore),
    AriaBottomNavItem(AriaDestination.LiderProjetos.route, stringResource(R.string.nav_projects), Icons.Outlined.Folder),
    AriaBottomNavItem(AriaDestination.LiderAnalises.route, stringResource(R.string.nav_analyses), Icons.Outlined.TrendingUp),
    AriaBottomNavItem(AriaDestination.LiderPerfil.route, stringResource(R.string.nav_profile), Icons.Outlined.Person),
)
