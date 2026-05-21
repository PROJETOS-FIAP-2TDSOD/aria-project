package com.fiap.ariachallenge.ui.operador

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.navigation.AriaDestination
import com.fiap.ariachallenge.ui.aria.AriaBottomNavItem

@Composable
fun operadorBottomNavItems(): List<AriaBottomNavItem> = listOf(
    AriaBottomNavItem(AriaDestination.OperadorHome.route, stringResource(R.string.nav_home), Icons.Outlined.Home),
    AriaBottomNavItem(AriaDestination.OperadorIdeias.route, stringResource(R.string.nav_ideas), Icons.Outlined.Lightbulb),
    AriaBottomNavItem(AriaDestination.OperadorPerfil.route, stringResource(R.string.nav_profile), Icons.Outlined.Person),
)
