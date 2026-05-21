package com.fiap.ariachallenge.ui.operador.nova_ideia

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.data.local.BadgeUnlockTracker
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NovaIdeiaUiState(
    val currentStep: Int = 0,
    val title: String = "",
    val category: IdeaCategory? = null,
    val description: String = "",
    val problema: String = "",
    val beneficios: String = "",
    val recursos: String = "",
    @StringRes val titleErrorRes: Int? = null,
    @StringRes val descriptionErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
)

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    private val ideaRepository: IIdeaRepository,
    private val userRepository: IUserRepository,
    private val badgeUnlockTracker: BadgeUnlockTracker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaIdeiaUiState())
    val uiState: StateFlow<NovaIdeiaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            _uiState.update { it.copy(currentUser = user) }
        }
    }

    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v, titleErrorRes = null) }
    fun onCategoryChange(v: IdeaCategory) = _uiState.update { it.copy(category = v) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v, descriptionErrorRes = null) }
    fun onProblemaChange(v: String) = _uiState.update { it.copy(problema = v) }
    fun onBeneficiosChange(v: String) = _uiState.update { it.copy(beneficios = v) }
    fun onRecursosChange(v: String) = _uiState.update { it.copy(recursos = v) }

    fun nextStep() {
        val state = _uiState.value
        if (state.currentStep == 0) {
            if (state.title.isBlank()) {
                _uiState.update { it.copy(titleErrorRes = R.string.new_idea_error_title_required) }
                return
            }
            if (state.category == null) {
                _uiState.update { it.copy(titleErrorRes = R.string.new_idea_error_category_required) }
                return
            }
        }
        if (state.currentStep == 1 && state.description.isBlank()) {
            _uiState.update { it.copy(descriptionErrorRes = R.string.new_idea_error_description_required) }
            return
        }
        _uiState.update { it.copy(currentStep = it.currentStep + 1) }
    }

    fun previousStep() = _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }

    fun submit() {
        val state = _uiState.value
        val user = state.currentUser ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val idea = Idea(
                id = "",
                title = state.title,
                author = user,
                category = state.category ?: IdeaCategory.PROCESSO,
                description = state.description,
                problema = state.problema,
                beneficios = state.beneficios,
                recursos = state.recursos,
                status = IdeaStatus.AGUARDANDO_ANALISE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
            ideaRepository.submitIdea(idea).fold(
                onSuccess = {
                    val badges = userRepository.calculateUserBadges(user.id)
                    badgeUnlockTracker.syncBadges(user.id, badges)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
            )
        }
    }
}
