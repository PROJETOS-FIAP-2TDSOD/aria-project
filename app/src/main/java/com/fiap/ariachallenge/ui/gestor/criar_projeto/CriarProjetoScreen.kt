package com.fiap.ariachallenge.ui.gestor.criar_projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaDatePickerDialog
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaPickerOption
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSecondaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.aria.AriaSearchableChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaSingleChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaTextArea
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private enum class ProjetoPickerSheet {
    None,
    Idea,
    Sponsor,
    Orientation,
    MilestoneStatus,
    TeamUser,
    TeamRole,
}

private fun formatMediumDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(date)
}

@Composable
fun CriarProjetoScreen(
    onBack: () -> Unit,
    viewModel: CriarProjetoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    var activeSheet by remember { mutableStateOf(ProjetoPickerSheet.None) }
    var showDatePicker by remember { mutableStateOf(false) }
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

    val selectedIdea = uiState.approvedIdeas.find { it.id == uiState.selectedIdeaId }
    val sponsorLabel = sponsorLabels.getOrNull(uiState.selectedSponsorIndex.coerceIn(0, sponsorLabels.lastIndex.coerceAtLeast(0)))
    val orientationLabel = orientationLabels.getOrNull(uiState.selectedOrientationIndex.coerceIn(0, orientationLabels.lastIndex.coerceAtLeast(0)))
    val deliveryLabel = uiState.expectedEndDateEpochMillis?.let(::formatMediumDate)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onBack()
    }

    AriaSingleChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.Idea,
        title = stringResource(R.string.picker_title_idea_origin),
        options = uiState.approvedIdeas.map { AriaPickerOption(it.id, it.title) },
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = viewModel::onIdeaSelected,
        selectedOptionId = uiState.selectedIdeaId.takeIf { it.isNotBlank() },
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.Sponsor,
        title = stringResource(R.string.picker_title_sponsor),
        options = sponsorLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onSponsorIndexPicked) },
        selectedOptionId = if (sponsorLabels.isEmpty()) null else uiState.selectedSponsorIndex.coerceIn(0, sponsorLabels.lastIndex).toString(),
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.Orientation,
        title = stringResource(R.string.picker_title_orientation),
        options = orientationLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onOrientationIndexPicked) },
        selectedOptionId = if (orientationLabels.isEmpty()) null else uiState.selectedOrientationIndex.coerceIn(0, orientationLabels.lastIndex).toString(),
    )
    AriaDatePickerDialog(
        visible = showDatePicker,
        initialSelectedDateMillis = uiState.expectedEndDateEpochMillis,
        onDismiss = { showDatePicker = false },
        onConfirm = viewModel::onDeliveryDateMillis,
    )
    AriaDatePickerDialog(
        visible = milestoneDateTargetId != null,
        initialSelectedDateMillis = milestoneDateTargetId?.let { id ->
            uiState.milestones.find { it.id == id }?.dueDateEpochMillis
        },
        onDismiss = { milestoneDateTargetId = null },
        onConfirm = { millis ->
            milestoneDateTargetId?.let { id -> viewModel.onMilestoneDueDateChange(id, millis) }
            milestoneDateTargetId = null
        },
    )
    val activeMilestone = pickerFormId?.let { id -> uiState.milestones.find { it.id == id } }
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.MilestoneStatus && activeMilestone != null,
        title = stringResource(R.string.picker_title_milestone_status),
        options = MilestoneStatus.entries.map {
            AriaPickerOption(it.name, milestoneStatusLabels[it].orEmpty())
        },
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = { statusName ->
            val formId = pickerFormId ?: return@AriaSingleChoiceBottomSheet
            MilestoneStatus.entries.find { it.name == statusName }?.let { status ->
                viewModel.onMilestoneStatusChange(formId, status)
            }
        },
        selectedOptionId = activeMilestone?.status?.name,
    )
    val activeTeamMember = pickerFormId?.let { id -> uiState.teamMembers.find { it.id == id } }
    AriaSearchableChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.TeamUser && activeTeamMember != null,
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
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = { userId ->
            val formId = pickerFormId ?: return@AriaSearchableChoiceBottomSheet
            viewModel.onTeamMemberUserChange(formId, userId)
        },
        selectedOptionId = activeTeamMember?.userId?.takeIf { it.isNotBlank() },
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == ProjetoPickerSheet.TeamRole && activeTeamMember != null,
        title = stringResource(R.string.picker_title_team_role),
        options = teamRoleLabels.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = ProjetoPickerSheet.None },
        onSelected = { index ->
            val formId = pickerFormId ?: return@AriaSingleChoiceBottomSheet
            index.toIntOrNull()?.let { viewModel.onTeamMemberRoleIndexChange(formId, it) }
        },
        selectedOptionId = activeTeamMember?.roleIndex?.toString(),
    )

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = { AriaTopBar(title = stringResource(R.string.project_create_title), onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp),
        ) {
            AriaCard(padding = 14.dp, accent = true) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(c.accentSubtle),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Outlined.Lightbulb, contentDescription = null, tint = c.accentMain, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedIdea != null) {
                                stringResource(
                                    R.string.project_create_origin_badge,
                                    selectedIdea.id.takeLast(4).padStart(4, '0'),
                                )
                            } else {
                                stringResource(R.string.project_create_origin_prompt)
                            },
                            style = AriaText.labelMd,
                            color = c.accentMain,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedIdea?.title ?: stringResource(R.string.project_create_no_idea_selected),
                            style = AriaText.titleMd.copy(fontSize = 14.sp),
                            color = c.textPrimary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AriaField(label = stringResource(R.string.project_create_field_name), required = true, counter = "${uiState.title.length} / 80") {
                AriaInput(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = stringResource(R.string.project_create_placeholder_name),
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_idea), required = true) {
                AriaSelect(
                    value = selectedIdea?.title,
                    placeholder = stringResource(R.string.project_create_placeholder_idea),
                    onClick = {
                        if (uiState.approvedIdeas.isNotEmpty()) {
                            activeSheet = ProjetoPickerSheet.Idea
                        }
                    },
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_sponsor), required = true) {
                AriaSelect(
                    value = sponsorLabel,
                    placeholder = stringResource(R.string.picker_title_sponsor),
                    onClick = { activeSheet = ProjetoPickerSheet.Sponsor },
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_delivery), required = true) {
                AriaSelect(
                    value = deliveryLabel,
                    placeholder = stringResource(R.string.project_create_placeholder_delivery),
                    onClick = { showDatePicker = true },
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_roi), required = true, helper = stringResource(R.string.project_create_roi_helper)) {
                AriaInput(
                    value = uiState.predictedRoiMil,
                    onValueChange = viewModel::onPredictedRoiChange,
                    placeholder = stringResource(R.string.project_create_placeholder_roi),
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_orientation), required = true) {
                AriaSelect(
                    value = orientationLabel,
                    placeholder = stringResource(R.string.picker_title_orientation),
                    onClick = { activeSheet = ProjetoPickerSheet.Orientation },
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_description), required = true) {
                AriaTextArea(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = stringResource(R.string.project_create_placeholder_description),
                    minHeight = 100.dp,
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_milestones)) {
                MilestonesEditor(
                    milestones = uiState.milestones,
                    expandedMilestoneId = uiState.expandedMilestoneId,
                    statusLabels = milestoneStatusLabels,
                    onToggleExpanded = viewModel::toggleMilestoneExpanded,
                    onTitleChange = viewModel::onMilestoneTitleChange,
                    onDueDateClick = { id ->
                        milestoneDateTargetId = id
                    },
                    onStatusClick = { id ->
                        pickerFormId = id
                        activeSheet = ProjetoPickerSheet.MilestoneStatus
                    },
                    onAdd = viewModel::addMilestone,
                    onRemove = viewModel::removeMilestone,
                )
            }
            AriaField(label = stringResource(R.string.project_create_field_team)) {
                TeamMembersEditor(
                    members = uiState.teamMembers,
                    assignableUsers = uiState.assignableUsers,
                    roleLabels = teamRoleLabels,
                    expandedMemberId = uiState.expandedTeamMemberId,
                    onToggleExpanded = viewModel::toggleTeamMemberExpanded,
                    onUserClick = { id ->
                        pickerFormId = id
                        activeSheet = ProjetoPickerSheet.TeamUser
                    },
                    onRoleClick = { id ->
                        pickerFormId = id
                        activeSheet = ProjetoPickerSheet.TeamRole
                    },
                    onAdd = viewModel::addTeamMember,
                    onRemove = viewModel::removeTeamMember,
                )
            }

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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AriaPrimaryBtn(
                    text = stringResource(R.string.project_create_btn_create),
                    onClick = { viewModel.createProject(teamRoleLabels, sponsorLabels, orientationLabels) },
                    accent = true,
                    enabled = !uiState.isLoading,
                )
                AriaSecondaryBtn(text = stringResource(R.string.action_cancel), onClick = onBack)
            }
        }
    }
}
