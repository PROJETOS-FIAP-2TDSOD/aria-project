package com.fiap.ariachallenge.ui.operador

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.ui.components.BadgeUnlockCelebrationOverlay
import com.fiap.ariachallenge.ui.operador.celebration.BadgeCelebrationViewModel

@Composable
fun OperadorBadgeCelebrationHost(
    content: @Composable () -> Unit,
) {
    val viewModel: BadgeCelebrationViewModel = hiltViewModel()
    val badge by viewModel.currentBadge.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        BadgeUnlockCelebrationOverlay(
            badge = badge,
            onDismiss = viewModel::dismissCelebration,
        )
    }
}
