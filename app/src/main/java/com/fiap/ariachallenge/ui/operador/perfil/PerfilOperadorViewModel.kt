package com.fiap.ariachallenge.ui.operador.perfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.domain.gamification.ProfileMetricsCalculator
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import com.fiap.ariachallenge.ui.operador.home.HomeMetrics
import com.fiap.ariachallenge.util.formatCurrencyBrl
import com.fiap.ariachallenge.util.formatCurrencyCompact
import javax.inject.Inject

data class PerfilOperadorUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val metrics: HomeMetrics = HomeMetrics(),
    val userPoints: Int = 0,
    val userBadges: List<String> = emptyList(),
    val attributedRoiLabel: String = formatCurrencyBrl(0.0),
)

@HiltViewModel
class PerfilOperadorViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
    private val authRepository: IAuthRepository,
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilOperadorUiState())
    val uiState: StateFlow<PerfilOperadorUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        loadProfile()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update { state ->
                    if (state.user == user) state else state.copy(user = user)
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            val ideas = ideaRepository.getIdeasByAuthor(user.id).first()
            val projects = projectRepository.getAllProjects().first()
            val badges = userRepository.calculateUserBadges(user.id)
            badgeUnlockTracker.syncBadges(user.id, badges)
            val attributedRoi = ProfileMetricsCalculator.calculateAttributedRoiReais(ideas, projects, user.id)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = user,
                    metrics = HomeMetrics(
                        totalIdeas = ideas.size,
                        inAnalysis = ideas.count { i -> i.status == IdeaStatus.EM_ANALISE },
                        approved = ideas.count { i -> i.status == IdeaStatus.APROVADA },
                        inProject = ideas.count { i -> i.status == IdeaStatus.EM_PROJETO },
                    ),
                    userPoints = userRepository.calculateUserPoints(user.id),
                    userBadges = badges,
                    attributedRoiLabel = formatCurrencyCompact(attributedRoi),
                )
            }
        }
    }

    fun onAvatarPicked(uri: Uri) {
        viewModelScope.launch {
            userRepository.updateAvatarFromContentUri(uri.toString())
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
