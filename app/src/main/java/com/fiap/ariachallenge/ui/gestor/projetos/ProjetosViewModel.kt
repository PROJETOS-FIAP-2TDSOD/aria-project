package com.fiap.ariachallenge.ui.gestor.projetos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import javax.inject.Inject

data class ProjetosUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val selectedFilter: ProjectStatus? = null,
    val error: String? = null
)

@HiltViewModel
class ProjetosViewModel @Inject constructor(
    private val projectRepository: IProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetosUiState())
    val uiState: StateFlow<ProjetosUiState> = _uiState.asStateFlow()

    init { loadProjects() }

    fun refresh() = loadProjects()

    fun setFilter(status: ProjectStatus?) {
        _uiState.update {
            it.copy(
                selectedFilter = status,
                filteredProjects = if (status == null) it.projects else it.projects.filter { p -> p.status == status }
            )
        }
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val projects = projectRepository.getAllProjects().first()
                    .sortedByDescending { it.updatedAt }
                _uiState.update { it.copy(isLoading = false, projects = projects, filteredProjects = projects) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
