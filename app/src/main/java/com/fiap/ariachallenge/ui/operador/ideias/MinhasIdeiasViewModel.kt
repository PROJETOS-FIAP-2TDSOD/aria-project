package com.fiap.ariachallenge.ui.operador.ideias

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
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import javax.inject.Inject

data class MinhasIdeiasUiState(
    val isLoading: Boolean = true,
    val ideas: List<Idea> = emptyList(),
    val filteredIdeas: List<Idea> = emptyList(),
    val selectedFilter: IdeaStatus? = null,
    val error: String? = null
)

@HiltViewModel
class MinhasIdeiasViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val userRepository: IUserRepository,
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MinhasIdeiasUiState())
    val uiState: StateFlow<MinhasIdeiasUiState> = _uiState.asStateFlow()

    init { loadIdeas() }

    fun refresh() = loadIdeas()

    fun setFilter(status: IdeaStatus?) {
        _uiState.update {
            it.copy(
                selectedFilter = status,
                filteredIdeas = if (status == null) it.ideas else it.ideas.filter { i -> i.status == status }
            )
        }
    }

    private fun loadIdeas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first()
                val ideas = ideaRepository.getIdeasByAuthor(user.id).first()
                    .sortedByDescending { it.updatedAt }
                val badges = userRepository.calculateUserBadges(user.id)
                badgeUnlockTracker.syncBadges(user.id, badges)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ideas = ideas,
                        filteredIdeas = if (it.selectedFilter == null) ideas
                        else ideas.filter { i -> i.status == it.selectedFilter }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
