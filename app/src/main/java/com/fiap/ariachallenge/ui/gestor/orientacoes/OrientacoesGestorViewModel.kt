package com.fiap.ariachallenge.ui.gestor.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import javax.inject.Inject

data class OrientacoesGestorUiState(
    val isLoading: Boolean = true,
    val orientations: List<Orientation> = emptyList(),
    val filteredOrientations: List<Orientation> = emptyList(),
    val selectedFilter: OrientationPriority? = null,
    val error: String? = null
)

@HiltViewModel
class OrientacoesGestorViewModel @Inject constructor(
    private val orientationRepository: IOrientationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrientacoesGestorUiState())
    val uiState: StateFlow<OrientacoesGestorUiState> = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load()

    fun setFilter(priority: OrientationPriority?) {
        _uiState.update {
            it.copy(
                selectedFilter = priority,
                filteredOrientations = if (priority == null) it.orientations
                else it.orientations.filter { o -> o.priority == priority }
            )
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val list = orientationRepository.getAllOrientations().first()
                _uiState.update { it.copy(isLoading = false, orientations = list, filteredOrientations = list) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
