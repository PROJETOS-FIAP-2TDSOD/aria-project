package com.fiap.ariachallenge.ui.operador.celebration

import androidx.lifecycle.ViewModel
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.domain.model.Badge
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class BadgeCelebrationViewModel @Inject constructor(
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : ViewModel() {
    val currentBadge: StateFlow<Badge?> = badgeUnlockTracker.currentCelebration

    fun dismissCelebration() {
        badgeUnlockTracker.dismissCelebration()
    }
}
