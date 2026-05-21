package com.fiap.ariachallenge.ui.gestor.editar_projeto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.domain.model.ProjectStatus
import com.fiap.ariachallenge.ui.aria.AriaDatePickerDialog
import com.fiap.ariachallenge.ui.aria.AriaErrorState
import com.fiap.ariachallenge.ui.aria.AriaLoadingSkeleton
import com.fiap.ariachallenge.ui.aria.AriaPickerOption
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSecondaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSearchableChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaSingleChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.gestor.detalhes_projeto.DetalhesProjetoViewModel
import com.fiap.ariachallenge.ui.gestor.projeto.ProjetoFormContent
import com.fiap.ariachallenge.ui.gestor.projeto.ProjetoFormContentCallbacks
import com.fiap.ariachallenge.ui.gestor.projeto.ProjetoFormContentState
import com.fiap.ariachallenge.ui.gestor.projeto.formatProjetoMediumDate
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

private enum class EditProjetoPickerSheet {
    None,
    Idea,
    Sponsor,
    Orientation,
    Status,
    MilestoneStatus,
    TeamUser,
    TeamRole,
}

@Composable
fun EditarProjetoScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit = {},
    viewModel: DetalhesProjetoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf(EditProjetoPickerSheet.None) }
    var showDeliveryDatePicker by remember { mutableStateOf(false) }
    var milestoneDateTargetId by remember { mutableStateOf<String?>(null) }
    var pickerFormId by remember { mutableStateOf<String?>(null) }

    val sponsorLabels = stringArrayResource(R.array.project_sponsor_options).toList()
    val orientationLabels = stringArrayResource(R.array.project_orientation_options).toList()
    val teamRoleLabels = stringArrayResource(R.array.project_team_role_options).toList()
    val milestoneStatusLabels = mapOf(
        MilestoneStatus.PENDING to stringResource(R.string.milestone_status_pending),
        MilestoneStatus.IN_PROGRESS to stringResource(R.string.milestone_status_in_progress),
        MilestoneStatus.COMPLETED to stringResource(R.string.milestone_status_completed),
    )
    val deliveryLabel = uiState.editExpectedEndDateEpochMillis?.let(::formatProjetoMediumDate)

    LaunchedEffect(teamRoleLabels, sponsorLabels, orientationLabels, uiState.project?.id) {
        viewModel.bindRoleLabels(teamRoleLabels, sponsorLabels, orientationLabels)
    }

    val projectTitle = uiState.project?.title.orEmpty()
    if (showDeleteDialog && uiState.project != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.delete_project_title),
                    style = AriaText.titleMd,
                    color = c.textPrimary,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_project_message, projectTitle),
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteProject(onSuccess = onDeleted)
                    },
                    enabled = !uiState.isDeleting,
                ) {
                    Text(
                        text = stringResource(R.string.delete_project_confirm),
                        color = c.error,
                        style = AriaText.labelMd,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = c.textSecondary,
                        style = AriaText.labelMd,
                    )
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        viewModel.clearSaveSuccess()
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) onBack()
    }

    val activeMilestone = pickerFormId?.let { id -> uiState.editMilestones.find { it.id == id } }
    val activeTeamMember = pickerFormId?.let { id -> uiState.editTeamMembers.find { it.id == id } }

    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.Idea,
        title = stringResource(R.string.picker_title_idea_origin),
        options = uiState.selectableIdeas.map { AriaPickerOption(it.id, it.title) },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = viewModel::onIdeaSelected,
        selectedOptionId = uiState.editSelectedIdeaId.takeIf { it.isNotBlank() },
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.Sponsor,
        title = stringResource(R.string.picker_title_sponsor),
        options = sponsorLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onSponsorIndexPicked) },
        selectedOptionId = uiState.editSponsorIndex.coerceIn(0, sponsorLabels.lastIndex.coerceAtLeast(0)).toString(),
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.Orientation,
        title = stringResource(R.string.picker_title_orientation),
        options = orientationLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onOrientationIndexPicked) },
        selectedOptionId = uiState.editOrientationIndex.coerceIn(0, orientationLabels.lastIndex.coerceAtLeast(0)).toString(),
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.Status,
        title = stringResource(R.string.project_update_status),
        options = ProjectStatus.entries.map {
            AriaPickerOption(it.name, stringResource(it.getDisplayNameRes()))
        },
        selectedOptionId = uiState.editStatus.name,
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { id ->
            ProjectStatus.entries.find { it.name == id }?.let(viewModel::onStatusChange)
        },
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.MilestoneStatus && activeMilestone != null,
        title = stringResource(R.string.picker_title_milestone_status),
        options = MilestoneStatus.entries.map {
            AriaPickerOption(it.name, milestoneStatusLabels[it].orEmpty())
        },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { statusName ->
            val formId = pickerFormId ?: return@AriaSingleChoiceBottomSheet
            MilestoneStatus.entries.find { it.name == statusName }?.let { status ->
                viewModel.onMilestoneStatusChange(formId, status)
            }
        },
        selectedOptionId = activeMilestone?.status?.name,
    )
    AriaSearchableChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.TeamUser && activeTeamMember != null,
        title = stringResource(R.string.picker_title_team_member),
        searchPlaceholder = stringResource(R.string.picker_search_person_placeholder),
        emptyMessage = stringResource(R.string.picker_search_person_empty),
        options = uiState.assignableUsers.map { user ->
            AriaPickerOption(
                id = user.id,
                label = "${user.name} · ${user.department}",
                searchText = user.name,
            )
        },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { userId ->
            val formId = pickerFormId ?: return@AriaSearchableChoiceBottomSheet
            viewModel.onTeamMemberUserChange(formId, userId)
        },
        selectedOptionId = activeTeamMember?.userId?.takeIf { it.isNotBlank() },
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == EditProjetoPickerSheet.TeamRole && activeTeamMember != null,
        title = stringResource(R.string.picker_title_team_role),
        options = teamRoleLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = EditProjetoPickerSheet.None },
        onSelected = { index ->
            val formId = pickerFormId ?: return@AriaSingleChoiceBottomSheet
            index.toIntOrNull()?.let { viewModel.onTeamMemberRoleIndexChange(formId, it) }
        },
        selectedOptionId = activeTeamMember?.roleIndex?.toString(),
    )
    AriaDatePickerDialog(
        visible = showDeliveryDatePicker,
        initialSelectedDateMillis = uiState.editExpectedEndDateEpochMillis,
        onDismiss = { showDeliveryDatePicker = false },
        onConfirm = viewModel::onDeliveryDateMillis,
    )
    AriaDatePickerDialog(
        visible = milestoneDateTargetId != null,
        initialSelectedDateMillis = milestoneDateTargetId?.let { id ->
            uiState.editMilestones.find { it.id == id }?.dueDateEpochMillis
        },
        onDismiss = { milestoneDateTargetId = null },
        onConfirm = { millis ->
            milestoneDateTargetId?.let { id -> viewModel.onMilestoneDueDateChange(id, millis) }
            milestoneDateTargetId = null
        },
    )

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.project_edit_title),
                sub = uiState.project?.title,
                onBack = onBack,
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> AriaLoadingSkeleton(modifier = Modifier.padding(padding))
            uiState.project == null -> AriaErrorState(
                onRetry = viewModel::refresh,
                title = stringResource(R.string.project_not_found),
                sub = stringResource(R.string.project_not_found_sub),
                modifier = Modifier.padding(padding),
            )
            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.project_edit_subtitle),
                        style = AriaText.bodyMd,
                        color = c.textSecondary,
                    )
                    ProjetoFormContent(
                        state = ProjetoFormContentState(
                            title = uiState.editTitle,
                            description = uiState.editDescription,
                            selectedIdeaId = uiState.editSelectedIdeaId,
                            selectedSponsorIndex = uiState.editSponsorIndex,
                            selectedOrientationIndex = uiState.editOrientationIndex,
                            expectedEndDateEpochMillis = uiState.editExpectedEndDateEpochMillis,
                            predictedRoiMil = uiState.editEstimatedRoiMil,
                            milestones = uiState.editMilestones,
                            expandedMilestoneId = uiState.expandedMilestoneId,
                            teamMembers = uiState.editTeamMembers,
                            expandedTeamMemberId = uiState.expandedTeamMemberId,
                            assignableUsers = uiState.assignableUsers,
                            selectableIdeas = uiState.selectableIdeas,
                            editProgress = uiState.editProgress,
                            editActualRoiK = uiState.editActualRoiK,
                            editStatusLabel = stringResource(uiState.editStatus.getDisplayNameRes()),
                        ),
                        callbacks = ProjetoFormContentCallbacks(
                            onTitleChange = viewModel::onTitleChange,
                            onDescriptionChange = viewModel::onDescriptionChange,
                            onIdeaClick = { activeSheet = EditProjetoPickerSheet.Idea },
                            onSponsorClick = { activeSheet = EditProjetoPickerSheet.Sponsor },
                            onDeliveryDateClick = { showDeliveryDatePicker = true },
                            onPredictedRoiChange = viewModel::onEstimatedRoiChange,
                            onOrientationClick = { activeSheet = EditProjetoPickerSheet.Orientation },
                            onToggleMilestoneExpanded = viewModel::toggleMilestoneExpanded,
                            onMilestoneTitleChange = viewModel::onMilestoneTitleChange,
                            onMilestoneDueDateClick = { id -> milestoneDateTargetId = id },
                            onMilestoneStatusClick = { id ->
                                pickerFormId = id
                                activeSheet = EditProjetoPickerSheet.MilestoneStatus
                            },
                            onAddMilestone = viewModel::addMilestone,
                            onRemoveMilestone = viewModel::removeMilestone,
                            onToggleTeamMemberExpanded = viewModel::toggleTeamMemberExpanded,
                            onTeamMemberUserClick = { id ->
                                pickerFormId = id
                                activeSheet = EditProjetoPickerSheet.TeamUser
                            },
                            onTeamMemberRoleClick = { id ->
                                pickerFormId = id
                                activeSheet = EditProjetoPickerSheet.TeamRole
                            },
                            onAddTeamMember = viewModel::addTeamMember,
                            onRemoveTeamMember = viewModel::removeTeamMember,
                            onProgressChange = viewModel::onProgressChange,
                            onActualRoiKChange = viewModel::onActualRoiKChange,
                            onStatusClick = { activeSheet = EditProjetoPickerSheet.Status },
                        ),
                        sponsorLabels = sponsorLabels,
                        orientationLabels = orientationLabels,
                        teamRoleLabels = teamRoleLabels,
                        milestoneStatusLabels = milestoneStatusLabels,
                        deliveryDateLabel = deliveryLabel,
                    )

                    uiState.error?.let { err ->
                        val message = if (err == "FILL_REQUIRED") {
                            stringResource(R.string.project_create_error_fill_required)
                        } else {
                            err
                        }
                        Text(
                            text = message,
                            style = AriaText.bodyMd,
                            color = c.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    AriaPrimaryBtn(
                        text = stringResource(R.string.action_save),
                        onClick = { viewModel.saveChanges(teamRoleLabels, sponsorLabels, orientationLabels) },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!uiState.isDeleting) {
                        AriaSecondaryBtn(
                            text = stringResource(R.string.delete_project_action),
                            onClick = { showDeleteDialog = true },
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
