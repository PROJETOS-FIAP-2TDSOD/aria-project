package com.fiap.ariachallenge.ui.gestor.detalhes_projeto

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.IdeaStatus
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.Project
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.domain.repository.IIdeaRepository
import com.fiap.ariachallenge.domain.repository.IProjectRepository
import com.fiap.ariachallenge.domain.repository.IUserRepository
import com.fiap.ariachallenge.ui.gestor.criar_projeto.MilestoneFormItem
import com.fiap.ariachallenge.ui.gestor.criar_projeto.TeamMemberFormItem
import com.fiap.ariachallenge.ui.gestor.criar_projeto.toDomainMilestones
import com.fiap.ariachallenge.ui.gestor.criar_projeto.toDomainTeamMembers
import com.fiap.ariachallenge.ui.gestor.projeto.indexOfLabel
import com.fiap.ariachallenge.ui.gestor.projeto.toEpochMillisAtStartOfDay
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

data class DetalhesProjetoUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val editTitle: String = "",
    val editDescription: String = "",
    val editSelectedIdeaId: String = "",
    val editSponsorIndex: Int = 0,
    val editOrientationIndex: Int = 0,
    val editExpectedEndDateEpochMillis: Long? = null,
    val editEstimatedRoiMil: String = "",
    val editProgress: Int = 0,
    val editActualRoiK: String = "",
    val editStatus: ProjectStatus = ProjectStatus.PLANEJAMENTO,
    val editMilestones: List<MilestoneFormItem> = emptyList(),
    val expandedMilestoneId: String? = null,
    val editTeamMembers: List<TeamMemberFormItem> = emptyList(),
    val expandedTeamMemberId: String? = null,
    val assignableUsers: List<User> = emptyList(),
    val selectableIdeas: List<Idea> = emptyList(),
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DetalhesProjetoViewModel @Inject constructor(
    private val projectRepository: IProjectRepository,
    private val ideaRepository: IIdeaRepository,
    private val userRepository: IUserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val projectId: String = savedStateHandle["projectId"] ?: ""

    private val _uiState = MutableStateFlow(DetalhesProjetoUiState())
    val uiState: StateFlow<DetalhesProjetoUiState> = _uiState.asStateFlow()

    init {
        loadProject()
        loadAssignableUsers()
        loadSelectableIdeas()
    }

    fun refresh() {
        loadProject()
        loadSelectableIdeas()
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun deleteProject(onSuccess: () -> Unit) {
        if (projectId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }
            projectRepository.deleteProject(projectId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isDeleting = false, error = e.message) }
                },
            )
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(editTitle = value, error = null) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(editDescription = value, error = null) }
    fun onIdeaSelected(id: String) = _uiState.update { it.copy(editSelectedIdeaId = id, error = null) }
    fun onSponsorIndexPicked(index: Int) = _uiState.update { it.copy(editSponsorIndex = index.coerceAtLeast(0), error = null) }
    fun onOrientationIndexPicked(index: Int) = _uiState.update { it.copy(editOrientationIndex = index.coerceAtLeast(0), error = null) }
    fun onDeliveryDateMillis(millis: Long) = _uiState.update { it.copy(editExpectedEndDateEpochMillis = millis, error = null) }
    fun onEstimatedRoiChange(value: String) = _uiState.update {
        it.copy(editEstimatedRoiMil = value.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' }, error = null)
    }
    fun onProgressChange(value: Int) = _uiState.update { it.copy(editProgress = value.coerceIn(0, 100)) }
    fun onActualRoiKChange(value: String) = _uiState.update {
        it.copy(editActualRoiK = value.filter { ch -> ch.isDigit() || ch == '.' || ch == ',' })
    }
    fun onStatusChange(status: ProjectStatus) = _uiState.update { it.copy(editStatus = status) }

    fun toggleMilestoneExpanded(id: String) = _uiState.update {
        it.copy(expandedMilestoneId = if (it.expandedMilestoneId == id) null else id)
    }

    fun onMilestoneTitleChange(id: String, value: String) = updateMilestone(id) { m -> m.copy(title = value) }
    fun onMilestoneDueDateChange(id: String, millis: Long) = updateMilestone(id) { m -> m.copy(dueDateEpochMillis = millis) }
    fun onMilestoneStatusChange(id: String, status: MilestoneStatus) = updateMilestone(id) { m -> m.copy(status = status) }

    fun addMilestone() {
        val newItem = MilestoneFormItem.empty()
        _uiState.update {
            it.copy(editMilestones = it.editMilestones + newItem, expandedMilestoneId = newItem.id)
        }
    }

    fun removeMilestone(id: String) {
        _uiState.update { state ->
            state.copy(
                editMilestones = state.editMilestones.filter { it.id != id },
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
            it.copy(editTeamMembers = it.editTeamMembers + newItem, expandedTeamMemberId = newItem.id)
        }
    }

    fun removeTeamMember(id: String) {
        _uiState.update { state ->
            state.copy(
                editTeamMembers = state.editTeamMembers.filter { it.id != id },
                expandedTeamMemberId = state.expandedTeamMemberId.takeIf { exp -> exp != id },
            )
        }
    }

    fun saveChanges(
        roleLabels: List<String>,
        sponsorLabels: List<String>,
        orientationLabels: List<String>,
    ) {
        val state = _uiState.value
        val project = state.project ?: return
        if (state.editTitle.isBlank() || state.editSelectedIdeaId.isBlank() ||
            state.editDescription.isBlank() || state.editExpectedEndDateEpochMillis == null
        ) {
            _uiState.update { it.copy(error = "FILL_REQUIRED") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, saveSuccess = false) }
            val originIdea = state.selectableIdeas.find { it.id == state.editSelectedIdeaId }
            if (originIdea == null) {
                _uiState.update { it.copy(isSaving = false, error = "IDEA_NOT_FOUND") }
                return@launch
            }
            val usersById = state.assignableUsers.associateBy { it.id }
            val endMillis = state.editExpectedEndDateEpochMillis!!
            val expectedEnd = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val estimatedRoiK = state.editEstimatedRoiMil.replace(',', '.').toDoubleOrNull() ?: 0.0
            val estimatedRoi = estimatedRoiK * 1000.0
            val actualRoi = state.editActualRoiK.replace(',', '.').toDoubleOrNull()?.times(1000)
            val updated = project.copy(
                title = state.editTitle.trim(),
                description = state.editDescription.trim(),
                originIdea = originIdea,
                progress = state.editProgress,
                estimatedRoi = estimatedRoi,
                actualRoi = actualRoi,
                status = state.editStatus,
                sponsorLabel = sponsorLabels.getOrNull(state.editSponsorIndex).orEmpty(),
                strategicOrientationLabel = orientationLabels.getOrNull(state.editOrientationIndex).orEmpty(),
                expectedEndDate = expectedEnd,
                milestones = state.editMilestones.toDomainMilestones(),
                teamMembers = state.editTeamMembers.toDomainTeamMembers(usersById, roleLabels),
                updatedAt = LocalDateTime.now(),
            )
            projectRepository.updateProject(updated).fold(
                onSuccess = { saved -> applyProject(saved, roleLabels) },
                onFailure = { e -> _uiState.update { it.copy(isSaving = false, error = e.message) } },
            )
        }
    }

    fun bindRoleLabels(roleLabels: List<String>, sponsorLabels: List<String>, orientationLabels: List<String>) {
        val project = _uiState.value.project ?: return
        _uiState.update {
            it.copy(
                editTeamMembers = project.teamMembers.map { member ->
                    TeamMemberFormItem.fromDomain(member, roleLabels)
                },
                editSponsorIndex = indexOfLabel(sponsorLabels, project.sponsorLabel),
                editOrientationIndex = indexOfLabel(orientationLabels, project.strategicOrientationLabel),
            )
        }
    }

    private fun loadProject() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val project = projectRepository.getProjectById(projectId).first()
            if (project == null) {
                _uiState.update { it.copy(isLoading = false, project = null) }
                return@launch
            }
            populateEditFields(project)
            _uiState.update { it.copy(isLoading = false, project = project) }
        }
    }

    private fun populateEditFields(project: Project) {
        val estimatedK = (project.estimatedRoi / 1000.0).let { value ->
            if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
        }
        _uiState.update {
            it.copy(
                editTitle = project.title,
                editDescription = project.description,
                editSelectedIdeaId = project.originIdea.id,
                editExpectedEndDateEpochMillis = project.expectedEndDate.toEpochMillisAtStartOfDay(),
                editEstimatedRoiMil = estimatedK,
                editProgress = project.progress,
                editActualRoiK = ((project.actualRoi ?: 0.0) / 1000.0).toInt().toString(),
                editStatus = project.status,
                editMilestones = project.milestones.map(MilestoneFormItem::fromDomain),
                editTeamMembers = project.teamMembers.map { member ->
                    TeamMemberFormItem.fromDomain(member, emptyList())
                },
            )
        }
    }

    private fun loadSelectableIdeas() {
        viewModelScope.launch {
            val approved = ideaRepository.getIdeasByStatus(IdeaStatus.APROVADA).first()
            val current = _uiState.value.project?.originIdea
            val ideas = buildList {
                addAll(approved)
                if (current != null && none { it.id == current.id }) add(current)
            }
            _uiState.update { it.copy(selectableIdeas = ideas) }
        }
    }

    private fun loadAssignableUsers() {
        viewModelScope.launch {
            val users = userRepository.getProjectAssignableUsers().first()
            _uiState.update { it.copy(assignableUsers = users) }
        }
    }

    private fun applyProject(project: Project, roleLabels: List<String>) {
        populateEditFields(project)
        _uiState.update {
            it.copy(
                isSaving = false,
                saveSuccess = true,
                project = project,
                editTeamMembers = project.teamMembers.map { member ->
                    TeamMemberFormItem.fromDomain(member, roleLabels)
                },
            )
        }
    }

    private fun updateMilestone(id: String, transform: (MilestoneFormItem) -> MilestoneFormItem) {
        _uiState.update { state ->
            state.copy(
                editMilestones = state.editMilestones.map { item ->
                    if (item.id == id) transform(item) else item
                },
            )
        }
    }

    private fun updateTeamMember(id: String, transform: (TeamMemberFormItem) -> TeamMemberFormItem) {
        _uiState.update { state ->
            state.copy(
                editTeamMembers = state.editTeamMembers.map { item ->
                    if (item.id == id) transform(item) else item
                },
            )
        }
    }
}
