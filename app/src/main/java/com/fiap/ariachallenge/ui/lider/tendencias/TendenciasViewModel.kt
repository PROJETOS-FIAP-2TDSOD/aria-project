package com.fiap.ariachallenge.ui.lider.tendencias

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
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import javax.inject.Inject

data class TendenciasUiState(
    val isLoading: Boolean = true,
    val topCategories: List<Pair<IdeaCategory, Int>> = emptyList(),
    val recentApproved: List<Idea> = emptyList(),
    val approvalRate: Float = 0f,
    val error: String? = null
)

@HiltViewModel
class TendenciasViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TendenciasUiState())
    val uiState: StateFlow<TendenciasUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val ideas = ideaRepository.getAllIdeas().first()
                val total = ideas.size.coerceAtLeast(1)
                val approved = ideas.filter { it.status == IdeaStatus.APROVADA || it.status == IdeaStatus.EM_PROJETO }
                val approvalRate = approved.size.toFloat() / total
                val byCategory = IdeaCategory.entries
                    .map { cat -> cat to ideas.count { it.category == cat } }
                    .sortedByDescending { it.second }
                    .filter { it.second > 0 }
                    .take(5)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        topCategories = byCategory,
                        recentApproved = approved.take(5),
                        approvalRate = approvalRate
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
