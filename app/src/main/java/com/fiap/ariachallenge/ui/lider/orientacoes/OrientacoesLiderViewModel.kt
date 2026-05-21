package com.fiap.ariachallenge.ui.lider.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrientationListItemUi(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val progress: Float,
    val ideaCount: Int,
    val projectCount: Int,
)

data class OrientacoesLiderUiState(
    val isLoading: Boolean = true,
    val activeCount: Int = 0,
    val alignedIdeasCount: Int = 0,
    val items: List<OrientationListItemUi> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class OrientacoesLiderViewModel @Inject constructor(
    private val orientationRepository: IOrientationRepository,
    private val ideaRepository: IIdeaRepository,
    private val projectRepository: IProjectRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrientacoesLiderUiState())
    val uiState: StateFlow<OrientacoesLiderUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val orientations = orientationRepository.getAllOrientations().first()
                val ideas = ideaRepository.getAllIdeas().first()
                val projects = projectRepository.getAllProjects().first()

                val items = orientations.mapIndexed { index, orientation ->
                    val alignedIdeas = ideas.filter { it.category == orientation.category }
                    val alignedProjects = projects.filter { it.originIdea.category == orientation.category }
                    val approved = alignedIdeas.count { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO }
                    val progress = if (alignedIdeas.isEmpty()) 0f
                    else approved.toFloat() / alignedIdeas.size.toFloat()

                    OrientationListItemUi(
                        id = orientation.id,
                        code = "#${index + 1}",
                        title = orientation.title,
                        description = orientation.description,
                        progress = progress.coerceIn(0f, 1f),
                        ideaCount = alignedIdeas.size,
                        projectCount = alignedProjects.count { it.status == ProjectStatus.EM_ANDAMENTO },
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeCount = orientations.size,
                        alignedIdeasCount = ideas.count { i -> i.status != IdeaStatus.REJEITADA },
                        items = items,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
