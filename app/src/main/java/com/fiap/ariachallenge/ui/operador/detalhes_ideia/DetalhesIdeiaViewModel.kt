package com.fiap.ariachallenge.ui.operador.detalhes_ideia

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.AiScoreBreakdownItem
import com.fiap.ariachallenge.domain.model.AiTextInsight
import com.fiap.ariachallenge.domain.model.AiTimelineEvent
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalhesIdeiaUiState(
    val isLoading: Boolean = true,
    val displayCode: String = "",
    val idea: Idea? = null,
    val aiInsights: List<AiTextInsight> = emptyList(),
    val scoreBreakdown: List<AiScoreBreakdownItem> = emptyList(),
    val timeline: List<AiTimelineEvent> = emptyList(),
    val canDelete: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val error: String? = null,
)

@HiltViewModel
class DetalhesIdeiaViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val aiRepository: IAiRepository,
    private val userRepository: IUserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val ideaId: String = savedStateHandle["ideaId"] ?: ""

    private val displayCode = ideaId.takeLast(4).padStart(4, '0')

    private val _uiState = MutableStateFlow(DetalhesIdeiaUiState(displayCode = displayCode))
    val uiState: StateFlow<DetalhesIdeiaUiState> = _uiState.asStateFlow()

    init { loadIdea() }

    fun refresh() = loadIdea()

    private fun loadIdea() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val ideaDeferred = async { ideaRepository.getIdeaById(ideaId).first() }
                val insightsDeferred = async { aiRepository.getIdeaAnalysis(ideaId) }
                val breakdownDeferred = async { aiRepository.getScoreBreakdown(ideaId) }
                val timelineDeferred = async { aiRepository.getIdeaTimeline(ideaId) }
                val currentUserDeferred = async { userRepository.getCurrentUser().first() }
                val idea = ideaDeferred.await()
                val currentUser = currentUserDeferred.await()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        idea = idea,
                        aiInsights = insightsDeferred.await(),
                        scoreBreakdown = breakdownDeferred.await(),
                        timeline = timelineDeferred.await(),
                        canDelete = idea?.author?.id == currentUser.id,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteIdea(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, deleteError = null) }
            ideaRepository.deleteIdea(ideaId).fold(
                onSuccess = { onSuccess() },
                onFailure = { e -> _uiState.update { it.copy(isDeleting = false, deleteError = e.message) } },
            )
        }
    }
}