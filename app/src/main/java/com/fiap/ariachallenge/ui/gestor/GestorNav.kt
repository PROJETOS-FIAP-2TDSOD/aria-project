package com.fiap.ariachallenge.ui.gestor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNavItem

@Composable
fun gestorBottomNavItems(): List<AriaBottomNavItem> = listOf(
    AriaBottomNavItem(AriaDestination.GestorHome.route, stringResource(R.string.nav_home), Icons.Outlined.Home),
    AriaBottomNavItem(AriaDestination.GestorPendentes.route, stringResource(R.string.nav_pending), Icons.Outlined.Lightbulb),
    AriaBottomNavItem(AriaDestination.GestorProjetos.route, stringResource(R.string.nav_projects), Icons.Outlined.Folder),
    AriaBottomNavItem(AriaDestination.GestorOrientacoes.route, stringResource(R.string.nav_orientations_short), Icons.Outlined.Explore),
    AriaBottomNavItem(AriaDestination.GestorPerfil.route, stringResource(R.string.nav_profile), Icons.Outlined.Person),
)
