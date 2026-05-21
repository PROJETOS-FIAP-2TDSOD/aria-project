package com.fiap.ariachallenge.ui.lider.detalhes_orientacao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationKeyMetric
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlignedIdeaUi(
    val id: String,
    val title: String,
    val author: String,
    val score: Int,
)

data class KeyMetricUi(
    val name: String,
    val achieved: String,
    val target: String,
    val progress: Float,
)

data class OrientationDetailUi(
    val id: String,
    val code: String,
    val priority: OrientationPriority,
    val period: String,
    val title: String,
    val description: String,
    val progress: Float,
    val ideasCount: Int,
    val ideasDelta: Int,
    val projectsActive: Int,
    val roiCompact: String,
    val roiDeltaPercent: Int,
    val alignedIdeas: List<AlignedIdeaUi>,
    val keyMetrics: List<KeyMetricUi>,
)

data class DetalhesOrientacaoLiderUiState(
    val isLoading: Boolean = true,
    val detail: OrientationDetailUi? = null,
    val error: String? = null,
    val deleteSuccess: Boolean = false,
)

@HiltViewModel
class DetalhesOrientacaoLiderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orientationRepository: IOrientationRepository,
    private val ideaRepository: IIdeaRepository,
) : ViewModel() {

    private val orientationId: String = savedStateHandle["orientationId"] ?: ""

    private val _uiState = MutableStateFlow(DetalhesOrientacaoLiderUiState())
    val uiState: StateFlow<DetalhesOrientacaoLiderUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    fun deleteOrientation(onSuccess: () -> Unit) {
        viewModelScope.launch {
            orientationRepository.deleteOrientation(orientationId).fold(
                onSuccess = {
                    _uiState.update { it.copy(deleteSuccess = true) }
                    onSuccess()
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } },
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                orientationRepository.getAllOrientations().first()
                val orientation = orientationRepository.getOrientationById(orientationId).first()
                if (orientation == null) {
                    _uiState.update { it.copy(isLoading = false, error = "NOT_FOUND") }
                    return@launch
                }
                val ideas = ideaRepository.getAllIdeas().first()
                val alignedIdeas = ideas
                    .filter { it.category == orientation.category }
                    .sortedByDescending { it.score ?: 0 }
                    .take(5)

                val detail = orientation.toDetailUi(alignedIdeas)
                _uiState.update { it.copy(isLoading = false, detail = detail) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun Orientation.toDetailUi(
        alignedIdeas: List<com.fiap.ariachallenge.domain.model.Idea>,
    ): OrientationDetailUi = OrientationDetailUi(
        id = id,
        code = code.ifBlank { "#${id.takeLast(2)}" },
        priority = priority,
        period = period,
        title = title,
        description = description,
        progress = progress.coerceIn(0f, 1f),
        ideasCount = ideasCount,
        ideasDelta = ideasDelta,
        projectsActive = projectsActive,
        roiCompact = roiCompact,
        roiDeltaPercent = roiDeltaPercent,
        alignedIdeas = alignedIdeas.map { idea ->
            AlignedIdeaUi(
                id = idea.id,
                title = idea.title,
                author = idea.author.name,
                score = idea.score ?: 0,
            )
        },
        keyMetrics = keyMetrics.map { it.toUi() },
    )

    private fun OrientationKeyMetric.toUi() = KeyMetricUi(
        name = name,
        achieved = achieved,
        target = target,
        progress = progress.coerceIn(0f, 1f),
    )
}
