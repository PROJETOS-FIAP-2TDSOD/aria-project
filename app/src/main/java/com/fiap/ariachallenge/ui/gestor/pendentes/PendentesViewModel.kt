package com.fiap.ariachallenge.ui.gestor.pendentes

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
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import javax.inject.Inject

data class PendentesUiState(
    val isLoading: Boolean = true,
    val ideas: List<Idea> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PendentesViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendentesUiState())
    val uiState: StateFlow<PendentesUiState> = _uiState.asStateFlow()

    init { loadPendentes() }

    fun refresh() = loadPendentes()

    private fun loadPendentes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val ideas = ideaRepository.getPendingIdeas().first()
                _uiState.update { it.copy(isLoading = false, ideas = ideas) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
