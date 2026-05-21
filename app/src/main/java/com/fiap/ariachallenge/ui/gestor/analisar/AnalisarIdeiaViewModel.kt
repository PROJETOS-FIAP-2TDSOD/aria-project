package com.fiap.ariachallenge.ui.gestor.analisar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.AiAnalyzeBrief
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import javax.inject.Inject

data class AnalisarIdeiaUiState(
    val isLoading: Boolean = true,
    val idea: Idea? = null,
    val score: Int = 50,
    val feedback: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val approvedForProject: Boolean = false,
    val aiBrief: AiAnalyzeBrief? = null,
    val error: String? = null,
)

@HiltViewModel
class AnalisarIdeiaViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val aiRepository: IAiRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val ideaId: String = savedStateHandle["ideaId"] ?: ""

    private val _uiState = MutableStateFlow(AnalisarIdeiaUiState())
    val uiState: StateFlow<AnalisarIdeiaUiState> = _uiState.asStateFlow()

    init { loadIdea() }

    fun onScoreChange(v: Int) = _uiState.update { it.copy(score = v) }
    fun onFeedbackChange(v: String) = _uiState.update { it.copy(feedback = v) }

    fun approve() = submitAnalysis(IdeaStatus.APROVADA)
    fun reject() = submitAnalysis(IdeaStatus.REJEITADA)

    fun clearApprovedForProject() {
        _uiState.update { it.copy(approvedForProject = false) }
    }

    private fun loadIdea() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idea = ideaRepository.getIdeaById(ideaId).first()
            val brief = aiRepository.getAnalyzeBrief(ideaId)
            _uiState.update {
                it.copy(isLoading = false, idea = idea, score = idea?.score ?: 50, aiBrief = brief)
            }
        }
    }

    private fun submitAnalysis(newStatus: IdeaStatus) {
        val state = _uiState.value
        val idea = state.idea ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val estimatedRoi = when {
                newStatus != IdeaStatus.APROVADA -> idea.estimatedRoi
                idea.estimatedRoi != null -> idea.estimatedRoi
                else -> state.score * 2_000.0
            }
            val updated = idea.copy(
                status = newStatus,
                score = state.score,
                gestorFeedback = state.feedback.ifBlank { null },
                estimatedRoi = estimatedRoi,
            )
            ideaRepository.updateIdea(updated).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isSuccess = newStatus != IdeaStatus.APROVADA,
                            approvedForProject = newStatus == IdeaStatus.APROVADA,
                        )
                    }
                },
                onFailure = { e -> _uiState.update { it.copy(isSubmitting = false, error = e.message) } },
            )
        }
    }
}
