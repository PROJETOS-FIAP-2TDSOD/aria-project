package com.fiap.ariachallenge.ui.lider.criar_orientacao

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.Orientation
import com.fiap.ariachallenge.domain.model.OrientationPriority
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.domain.repository.IAiRepository
import com.fiap.ariachallenge.domain.repository.IOrientationRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import com.fiap.ariachallenge.util.OrientationKeyMetricParser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CriarOrientacaoUiState(
    val editOrientationId: String? = null,
    val code: String = "",
    val title: String = "",
    val periodOptionIndex: Int = 0,
    val visibilityOptionIndex: Int = 0,
    val priority: OrientationPriority = OrientationPriority.ALTA,
    val objective: String = "",
    val keyMetrics: List<KeyMetricFormItem> = emptyList(),
    val expandedMetricId: String? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val isSuccess: Boolean = false,
    @StringRes val errorRes: Int? = null,
)

@HiltViewModel
class CriarOrientacaoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val orientationRepository: IOrientationRepository,
    private val userRepository: IUserRepository,
    private val aiRepository: IAiRepository,
) : ViewModel() {

    private val editId: String? = savedStateHandle["orientationId"]

    private val _uiState = MutableStateFlow(CriarOrientacaoUiState(editOrientationId = editId))
    val uiState: StateFlow<CriarOrientacaoUiState> = _uiState.asStateFlow()

    init {
        if (editId != null) loadForEdit(editId)
    }

    private fun loadForEdit(id: String) {
        viewModelScope.launch {
            val orientation = orientationRepository.getOrientationById(id).first() ?: return@launch
            val periodIndex = periodIndexFromLabel(orientation.period)
            val metrics = orientation.keyMetrics.map { KeyMetricFormItem.fromDomain(it) }
            _uiState.update {
                it.copy(
                    code = orientation.code.ifBlank { "#${id.takeLast(2)}" },
                    title = orientation.title,
                    objective = orientation.description,
                    keyMetrics = metrics,
                    priority = orientation.priority,
                    periodOptionIndex = periodIndex,
                )
            }
        }
    }

    fun onCodeChange(v: String) = _uiState.update { it.copy(code = v) }
    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v, errorRes = null) }
    fun onPeriodIndexChange(index: Int) = _uiState.update { it.copy(periodOptionIndex = index.coerceAtLeast(0)) }
    fun onVisibilityIndexChange(index: Int) = _uiState.update { it.copy(visibilityOptionIndex = index.coerceAtLeast(0)) }
    fun onObjectiveChange(v: String) = _uiState.update { it.copy(objective = v) }

    fun onKeyMetricNameChange(id: String, value: String) = updateMetric(id) { it.copy(name = value) }
    fun onKeyMetricAchievedChange(id: String, value: String) = updateMetric(id) { it.copy(achieved = value) }
    fun onKeyMetricTargetChange(id: String, value: String) = updateMetric(id) { it.copy(target = value) }

    fun toggleMetricExpanded(id: String) {
        _uiState.update { state ->
            state.copy(expandedMetricId = if (state.expandedMetricId == id) null else id)
        }
    }

    fun addKeyMetric() {
        val newItem = KeyMetricFormItem.empty()
        _uiState.update {
            it.copy(
                keyMetrics = it.keyMetrics + newItem,
                expandedMetricId = newItem.id,
            )
        }
    }

    fun removeKeyMetric(id: String) {
        _uiState.update { state ->
            state.copy(
                keyMetrics = state.keyMetrics.filter { item -> item.id != id },
                expandedMetricId = if (state.expandedMetricId == id) null else state.expandedMetricId,
            )
        }
    }

    private fun updateMetric(id: String, transform: (KeyMetricFormItem) -> KeyMetricFormItem) {
        _uiState.update { state ->
            state.copy(
                keyMetrics = state.keyMetrics.map { item ->
                    if (item.id == id) transform(item) else item
                },
            )
        }
    }

    fun onGenerateClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            val assist = aiRepository.getOrientationAssist()
            delay(300)
            val suggestedMetrics = if (assist.keyMetrics.isNotBlank()) {
                OrientationKeyMetricParser.fromText(assist.keyMetrics)
                    .map { KeyMetricFormItem.fromDomain(it) }
            } else {
                emptyList()
            }
            _uiState.update { state ->
                state.copy(
                    isGenerating = false,
                    objective = state.objective.ifBlank { assist.objective },
                    keyMetrics = if (state.keyMetrics.isEmpty()) suggestedMetrics else state.keyMetrics,
                )
            }
        }
    }

    fun publish(periodLabels: List<String>) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorRes = R.string.create_orientation_error_required) }
            return
        }
        val domainMetrics = state.keyMetrics.toDomainMetrics()
        if (domainMetrics.any { it.name.isBlank() }) {
            _uiState.update { it.copy(errorRes = R.string.create_orientation_metrics_name_required) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            val author = userRepository.getCurrentUser().first()
            val targetRoles = when (state.visibilityOptionIndex) {
                0 -> listOf(UserRole.OPERADOR, UserRole.GESTOR)
                1 -> listOf(UserRole.GESTOR)
                else -> listOf(UserRole.OPERADOR)
            }
            val period = periodLabels.getOrElse(state.periodOptionIndex) { "" }
            val expiresAt = LocalDateTime.now().plusMonths(
                when (state.periodOptionIndex) {
                    0 -> 3L
                    1 -> 6L
                    else -> 12L
                },
            )
            val existing = editId?.let { orientationRepository.getOrientationById(it).first() }

            val orientation = Orientation(
                id = editId.orEmpty(),
                code = state.code.ifBlank { existing?.code.orEmpty() },
                title = state.title,
                description = state.objective.ifBlank { state.title },
                author = author,
                category = existing?.category ?: IdeaCategory.PROCESSO,
                priority = existing?.priority ?: state.priority,
                period = period,
                targetRoles = targetRoles,
                keyMetrics = domainMetrics,
                ideasCount = existing?.ideasCount ?: 0,
                ideasDelta = existing?.ideasDelta ?: 0,
                projectsActive = existing?.projectsActive ?: 0,
                roiCompact = existing?.roiCompact ?: "0k",
                roiDeltaPercent = existing?.roiDeltaPercent ?: 0,
                progress = existing?.progress ?: 0f,
                expiresAt = expiresAt,
            )
            val result = if (editId != null) {
                orientationRepository.updateOrientation(orientation.copy(id = editId))
            } else {
                orientationRepository.createOrientation(orientation)
            }
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) } },
                onFailure = { _uiState.update { it.copy(isLoading = false, errorRes = R.string.create_orientation_error_required) } },
            )
        }
    }

    private fun periodIndexFromLabel(period: String): Int {
        if (period.contains("Q3", ignoreCase = true) && period.contains("2027")) return 0
        if (period.contains("Q4", ignoreCase = true)) return 1
        return 2
    }
}
