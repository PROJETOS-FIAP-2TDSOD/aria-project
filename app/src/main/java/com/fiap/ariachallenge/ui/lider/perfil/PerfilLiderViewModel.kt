package com.fiap.ariachallenge.ui.lider.perfil

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
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.repository.IAuthRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.gamification.ProfileMetricsCalculator
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import com.fiap.ariachallenge.util.formatCurrencyBrl
import com.fiap.ariachallenge.util.formatCurrencyCompact
import javax.inject.Inject

data class PerfilLiderUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val totalOrientations: Int = 0,
    val totalIdeasReviewed: Int = 0,
    val totalProjects: Int = 0,
    val approvalRatePercent: Int = 0,
    val managedRoiLabel: String = formatCurrencyBrl(0.0),
)

@HiltViewModel
class PerfilLiderViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val orientationRepository: IOrientationRepository,
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilLiderUiState())
    val uiState: StateFlow<PerfilLiderUiState> = _uiState.asStateFlow()

    init {
        observeCurrentUser()
        load()
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

    private fun load() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            val orientations = orientationRepository.getAllOrientations().first()
            val ideas = ideaRepository.getAllIdeas().first()
            val projects = projectRepository.getAllProjects().first()
            val submitted = ideas.size
            val approved = ideas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO }
            val approvalRate = if (submitted == 0) 0 else (approved * 100) / submitted
            val managedRoi = ProfileMetricsCalculator.calculateManagedRoiReais(projects)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = user,
                    totalOrientations = orientations.size,
                    totalIdeasReviewed = ideas.count { idea -> idea.score != null },
                    totalProjects = projects.size,
                    approvalRatePercent = approvalRate,
                    managedRoiLabel = formatCurrencyCompact(managedRoi),
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
