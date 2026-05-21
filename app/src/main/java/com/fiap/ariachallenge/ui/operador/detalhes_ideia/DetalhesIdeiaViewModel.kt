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
    val error: String? = null,
)

@HiltViewModel
class DetalhesIdeiaViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val aiRepository: IAiRepository,
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
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        idea = ideaDeferred.await(),
                        aiInsights = insightsDeferred.await(),
                        scoreBreakdown = breakdownDeferred.await(),
                        timeline = timelineDeferred.await(),
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
