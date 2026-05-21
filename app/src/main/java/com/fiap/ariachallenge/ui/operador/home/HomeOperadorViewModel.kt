package com.fiap.ariachallenge.ui.operador.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.AiSimilarIdeaSuggestion
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.domain.gamification.ProfileMetricsCalculator
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import com.fiap.ariachallenge.util.formatCurrencyCompact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeMetrics(
    val totalIdeas: Int = 0,
    val awaitingAnalysis: Int = 0,
    val inAnalysis: Int = 0,
    val approved: Int = 0,
    val inProject: Int = 0,
    val approvalRatePercent: Int = 0,
    val inProjectRoiLabel: String? = null,
)

data class HomeOperadorUiState(
    val isLoading: Boolean = true,
    val currentUser: User? = null,
    val metrics: HomeMetrics = HomeMetrics(),
    val userPoints: Int = 0,
    val userBadges: List<String> = emptyList(),
    val recentIdeas: List<Idea> = emptyList(),
    val recentUpdates: List<OperadorIdeaUpdate> = emptyList(),
    val orientations: List<Orientation> = emptyList(),
    val aiSuggestion: AiSimilarIdeaSuggestion? = null,
    val error: String? = null,
)

@HiltViewModel
class HomeOperadorViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val orientationRepository: IOrientationRepository,
    private val userRepository: IUserRepository,
    private val aiRepository: IAiRepository,
    private val projectRepository: IProjectRepository,
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeOperadorUiState())
    val uiState: StateFlow<HomeOperadorUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        loadData()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun refresh() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first()
                val userIdeas = ideaRepository.getIdeasByAuthor(user.id).first()
                val projects = projectRepository.getAllProjects().first()
                val total = userIdeas.size
                val approved = userIdeas.count { it.status == IdeaStatus.APROVADA }
                val inProject = userIdeas.count { it.status == IdeaStatus.EM_PROJETO }
                val approvalRate = if (total == 0) 0 else ((approved + inProject) * 100) / total
                val inProjectRoi = ProfileMetricsCalculator.calculateInProjectRoiReais(userIdeas, projects, user.id)
                val orientations = orientationRepository.getAllOrientations().first()
                    .filter { UserRole.OPERADOR in it.targetRoles }
                    .sortedByDescending { it.createdAt }
                    .take(3)

                val badges = userRepository.calculateUserBadges(user.id)
                badgeUnlockTracker.syncBadges(user.id, badges)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        metrics = HomeMetrics(
                            totalIdeas = total,
                            awaitingAnalysis = userIdeas.count { it.status == IdeaStatus.AGUARDANDO_ANALISE },
                            inAnalysis = userIdeas.count { it.status == IdeaStatus.EM_ANALISE },
                            approved = approved,
                            inProject = inProject,
                            approvalRatePercent = approvalRate,
                            inProjectRoiLabel = inProjectRoi.takeIf { it > 0.0 }?.let(::formatCurrencyCompact),
                        ),
                        userPoints = userRepository.calculateUserPoints(user.id),
                        userBadges = badges,
                        recentIdeas = userIdeas.sortedByDescending { i -> i.updatedAt }.take(3),
                        recentUpdates = buildOperadorRecentUpdates(userIdeas),
                        orientations = orientations,
                        aiSuggestion = aiRepository.getOperadorHomeSuggestion(user.id),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
