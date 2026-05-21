package com.fiap.ariachallenge.ui.gestor.criar_projeto

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectMilestone
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.ProjectTeamMember
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CriarProjetoUiState(
    val title: String = "",
    val description: String = "",
    val selectedIdeaId: String = "",
    val selectedSponsorIndex: Int = 0,
    val selectedOrientationIndex: Int = 0,
    val expectedEndDateEpochMillis: Long? = null,
    val predictedRoiMil: String = "",
    val milestones: List<MilestoneFormItem> = emptyList(),
    val expandedMilestoneId: String? = null,
    val teamMembers: List<TeamMemberFormItem> = emptyList(),
    val expandedTeamMemberId: String? = null,
    val assignableUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val approvedIdeas: List<Idea> = emptyList(),
)

@HiltViewModel
class CriarProjetoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: IProjectRepository,
    private val ideaRepository: IIdeaRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {

    private val preselectedIdeaId: String? = savedStateHandle.get<String>("ideaId")?.takeIf { it.isNotBlank() }

    private val _uiState = MutableStateFlow(CriarProjetoUiState())
    val uiState: StateFlow<CriarProjetoUiState> = _uiState.asStateFlow()

    init {
        loadApprovedIdeas()
        loadAssignableUsers()
    }

    fun loadApprovedIdeas() {
        viewModelScope.launch {
            val approved = ideaRepository.getIdeasByStatus(IdeaStatus.APROVADA).first()
            val preselected = preselectedIdeaId?.let { id -> approved.find { it.id == id } }
            _uiState.update {
                it.copy(
                    approvedIdeas = approved,
                    selectedIdeaId = preselected?.id ?: it.selectedIdeaId,
                    title = preselected?.title?.takeIf { t -> it.title.isBlank() } ?: it.title,
                    description = preselected?.description?.takeIf { d -> it.description.isBlank() } ?: it.description,
                )
            }
        }
    }

    private fun loadAssignableUsers() {
        viewModelScope.launch {
            val users = userRepository.getProjectAssignableUsers().first()
            _uiState.update { it.copy(assignableUsers = users) }
        }
    }

    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v, error = null) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(description = v, error = null) }
    fun onIdeaSelected(id: String) = _uiState.update { it.copy(selectedIdeaId = id, error = null) }
    fun onSponsorIndexPicked(index: Int) = _uiState.update { it.copy(selectedSponsorIndex = index.coerceAtLeast(0), error = null) }
    fun onOrientationIndexPicked(index: Int) = _uiState.update { it.copy(selectedOrientationIndex = index.coerceAtLeast(0), error = null) }
    fun onDeliveryDateMillis(millis: Long) = _uiState.update { it.copy(expectedEndDateEpochMillis = millis, error = null) }
    fun onPredictedRoiChange(v: String) = _uiState.update { it.copy(predictedRoiMil = v.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }, error = null) }

    fun toggleMilestoneExpanded(id: String) = _uiState.update {
        it.copy(expandedMilestoneId = if (it.expandedMilestoneId == id) null else id)
    }

    fun onMilestoneTitleChange(id: String, value: String) = updateMilestone(id) { m -> m.copy(title = value) }

    fun onMilestoneDueDateChange(id: String, millis: Long) = updateMilestone(id) { m -> m.copy(dueDateEpochMillis = millis) }

    fun onMilestoneStatusChange(id: String, status: MilestoneStatus) = updateMilestone(id) { m -> m.copy(status = status) }

    fun addMilestone() {
        val newItem = MilestoneFormItem.empty()
        _uiState.update {
            it.copy(
                milestones = it.milestones + newItem,
                expandedMilestoneId = newItem.id,
            )
        }
    }

    fun removeMilestone(id: String) {
        _uiState.update { state ->
            state.copy(
                milestones = state.milestones.filter { it.id != id },
                expandedMilestoneId = state.expandedMilestoneId.takeIf { exp -> exp != id },
            )
        }
    }

    fun toggleTeamMemberExpanded(id: String) = _uiState.update {
        it.copy(expandedTeamMemberId = if (it.expandedTeamMemberId == id) null else id)
    }

    fun onTeamMemberUserChange(formId: String, userId: String) = updateTeamMember(formId) { m -> m.copy(userId = userId) }

    fun onTeamMemberRoleIndexChange(formId: String, roleIndex: Int) = updateTeamMember(formId) { m -> m.copy(roleIndex = roleIndex) }

    fun addTeamMember() {
        val newItem = TeamMemberFormItem.empty()
        _uiState.update {
            it.copy(
                teamMembers = it.teamMembers + newItem,
                expandedTeamMemberId = newItem.id,
            )
        }
    }

    fun removeTeamMember(id: String) {
        _uiState.update { state ->
            state.copy(
                teamMembers = state.teamMembers.filter { it.id != id },
                expandedTeamMemberId = state.expandedTeamMemberId.takeIf { exp -> exp != id },
            )
        }
    }

    fun createProject(
        roleLabels: List<String>,
        sponsorLabels: List<String>,
        orientationLabels: List<String>,
    ) {
        val state = _uiState.value
        if (state.title.isBlank() || state.selectedIdeaId.isBlank() || state.description.isBlank() || state.expectedEndDateEpochMillis == null) {
            _uiState.update { it.copy(error = "FILL_REQUIRED") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val manager = userRepository.getCurrentUser().first()
            val originIdea = state.approvedIdeas.find { it.id == state.selectedIdeaId }
            if (originIdea == null) {
                _uiState.update { it.copy(isLoading = false, error = "IDEA_NOT_FOUND") }
                return@launch
            }
            val usersById = state.assignableUsers.associateBy { it.id }
            val endMillis = state.expectedEndDateEpochMillis!!
            val expectedEnd = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val roiK = state.predictedRoiMil.replace(',', '.').toDoubleOrNull() ?: 0.0
            val roi = roiK * 1000.0
            val milestones = normalizeMilestones(state.milestones.toDomainMilestones())
            val teamMembers = state.teamMembers.toDomainTeamMembers(usersById, roleLabels)
            val project = Project(
                id = "",
                title = state.title,
                description = state.description,
                originIdea = originIdea,
                manager = manager,
                status = ProjectStatus.PLANEJAMENTO,
                progress = 0,
                estimatedRoi = roi,
                sponsorLabel = sponsorLabels.getOrNull(state.selectedSponsorIndex).orEmpty(),
                strategicOrientationLabel = orientationLabels.getOrNull(state.selectedOrientationIndex).orEmpty(),
                teamMembers = teamMembers,
                milestones = milestones,
                startDate = LocalDateTime.now(),
                expectedEndDate = expectedEnd,
            )
            projectRepository.createProject(project).fold(
                onSuccess = {
                    ideaRepository.updateIdea(originIdea.copy(status = IdeaStatus.EM_PROJETO))
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } },
            )
        }
    }

    private fun normalizeMilestones(milestones: List<ProjectMilestone>): List<ProjectMilestone> {
        if (milestones.isEmpty()) return milestones
        if (milestones.any { it.status == MilestoneStatus.IN_PROGRESS }) return milestones
        val firstOpenIndex = milestones.indexOfFirst { it.status != MilestoneStatus.COMPLETED }
        if (firstOpenIndex < 0) return milestones
        return milestones.mapIndexed { index, milestone ->
            if (index == firstOpenIndex) milestone.copy(status = MilestoneStatus.IN_PROGRESS) else milestone
        }
    }

    private fun updateMilestone(id: String, transform: (MilestoneFormItem) -> MilestoneFormItem) {
        _uiState.update { state ->
            state.copy(
                milestones = state.milestones.map { item ->
                    if (item.id == id) transform(item) else item
                },
                error = null,
            )
        }
    }

    private fun updateTeamMember(id: String, transform: (TeamMemberFormItem) -> TeamMemberFormItem) {
        _uiState.update { state ->
            state.copy(
                teamMembers = state.teamMembers.map { item ->
                    if (item.id == id) transform(item) else item
                },
                error = null,
            )
        }
    }
}
