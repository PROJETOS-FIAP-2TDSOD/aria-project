package com.fiap.ariachallenge.ui.gestor.projeto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.Idea
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.aria.AriaTextArea
import com.fiap.ariachallenge.ui.gestor.criar_projeto.MilestoneFormItem
import com.fiap.ariachallenge.ui.gestor.criar_projeto.MilestonesEditor
import com.fiap.ariachallenge.ui.gestor.criar_projeto.TeamMemberFormItem
import com.fiap.ariachallenge.ui.gestor.criar_projeto.TeamMembersEditor
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

data class ProjetoFormContentState(
    val title: String,
    val description: String,
    val selectedIdeaId: String,
    val selectedSponsorIndex: Int,
    val selectedOrientationIndex: Int,
    val expectedEndDateEpochMillis: Long?,
    val predictedRoiMil: String,
    val milestones: List<MilestoneFormItem>,
    val expandedMilestoneId: String?,
    val teamMembers: List<TeamMemberFormItem>,
    val expandedTeamMemberId: String?,
    val assignableUsers: List<User>,
    val selectableIdeas: List<Idea>,
    val editProgress: Int? = null,
    val editActualRoiK: String? = null,
    val editStatusLabel: String? = null,
)

data class ProjetoFormContentCallbacks(
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onIdeaClick: () -> Unit,
    val onSponsorClick: () -> Unit,
    val onDeliveryDateClick: () -> Unit,
    val onPredictedRoiChange: (String) -> Unit,
    val onOrientationClick: () -> Unit,
    val onToggleMilestoneExpanded: (String) -> Unit,
    val onMilestoneTitleChange: (String, String) -> Unit,
    val onMilestoneDueDateClick: (String) -> Unit,
    val onMilestoneStatusClick: (String) -> Unit,
    val onAddMilestone: () -> Unit,
    val onRemoveMilestone: (String) -> Unit,
    val onToggleTeamMemberExpanded: (String) -> Unit,
    val onTeamMemberUserClick: (String) -> Unit,
    val onTeamMemberRoleClick: (String) -> Unit,
    val onAddTeamMember: () -> Unit,
    val onRemoveTeamMember: (String) -> Unit,
    val onProgressChange: ((Int) -> Unit)? = null,
    val onActualRoiKChange: ((String) -> Unit)? = null,
    val onStatusClick: (() -> Unit)? = null,
)

@Composable
fun ProjetoFormContent(
    state: ProjetoFormContentState,
    callbacks: ProjetoFormContentCallbacks,
    sponsorLabels: List<String>,
    orientationLabels: List<String>,
    teamRoleLabels: List<String>,
    milestoneStatusLabels: Map<MilestoneStatus, String>,
    deliveryDateLabel: String?,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val selectedIdea = state.selectableIdeas.find { it.id == state.selectedIdeaId }
    val sponsorLabel = sponsorLabels.getOrNull(state.selectedSponsorIndex.coerceIn(0, sponsorLabels.lastIndex.coerceAtLeast(0)))
    val orientationLabel = orientationLabels.getOrNull(state.selectedOrientationIndex.coerceIn(0, orientationLabels.lastIndex.coerceAtLeast(0)))

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(0.dp)) {
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

        AriaField(label = stringResource(R.string.project_create_field_name), required = true, counter = "${state.title.length} / 80") {
            AriaInput(
                value = state.title,
                onValueChange = callbacks.onTitleChange,
                placeholder = stringResource(R.string.project_create_placeholder_name),
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_idea), required = true) {
            AriaSelect(
                value = selectedIdea?.title,
                placeholder = stringResource(R.string.project_create_placeholder_idea),
                onClick = {
                    if (state.selectableIdeas.isNotEmpty()) callbacks.onIdeaClick()
                },
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_sponsor), required = true) {
            AriaSelect(
                value = sponsorLabel,
                placeholder = stringResource(R.string.picker_title_sponsor),
                onClick = callbacks.onSponsorClick,
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_delivery), required = true) {
            AriaSelect(
                value = deliveryDateLabel,
                placeholder = stringResource(R.string.project_create_placeholder_delivery),
                onClick = callbacks.onDeliveryDateClick,
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_roi), required = true, helper = stringResource(R.string.project_create_roi_helper)) {
            AriaInput(
                value = state.predictedRoiMil,
                onValueChange = callbacks.onPredictedRoiChange,
                placeholder = stringResource(R.string.project_create_placeholder_roi),
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_orientation), required = true) {
            AriaSelect(
                value = orientationLabel,
                placeholder = stringResource(R.string.picker_title_orientation),
                onClick = callbacks.onOrientationClick,
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_description), required = true) {
            AriaTextArea(
                value = state.description,
                onValueChange = callbacks.onDescriptionChange,
                placeholder = stringResource(R.string.project_create_placeholder_description),
                minHeight = 100.dp,
            )
        }

        if (state.editProgress != null && callbacks.onProgressChange != null) {
            AriaField(label = stringResource(R.string.project_update_progress)) {
                AriaInput(
                    value = state.editProgress.toString(),
                    onValueChange = { v -> v.toIntOrNull()?.let(callbacks.onProgressChange) },
                )
            }
        }
        if (state.editActualRoiK != null && callbacks.onActualRoiKChange != null) {
            AriaField(label = stringResource(R.string.project_update_roi)) {
                AriaInput(
                    value = state.editActualRoiK,
                    onValueChange = callbacks.onActualRoiKChange,
                )
            }
        }
        if (state.editStatusLabel != null && callbacks.onStatusClick != null) {
            AriaField(label = stringResource(R.string.project_update_status)) {
                AriaSelect(
                    value = state.editStatusLabel,
                    onClick = callbacks.onStatusClick,
                )
            }
        }

        AriaField(label = stringResource(R.string.project_create_field_milestones)) {
            MilestonesEditor(
                milestones = state.milestones,
                expandedMilestoneId = state.expandedMilestoneId,
                statusLabels = milestoneStatusLabels,
                onToggleExpanded = callbacks.onToggleMilestoneExpanded,
                onTitleChange = callbacks.onMilestoneTitleChange,
                onDueDateClick = callbacks.onMilestoneDueDateClick,
                onStatusClick = callbacks.onMilestoneStatusClick,
                onAdd = callbacks.onAddMilestone,
                onRemove = callbacks.onRemoveMilestone,
            )
        }
        AriaField(label = stringResource(R.string.project_create_field_team)) {
            TeamMembersEditor(
                members = state.teamMembers,
                assignableUsers = state.assignableUsers,
                roleLabels = teamRoleLabels,
                expandedMemberId = state.expandedTeamMemberId,
                onToggleExpanded = callbacks.onToggleTeamMemberExpanded,
                onUserClick = callbacks.onTeamMemberUserClick,
                onRoleClick = callbacks.onTeamMemberRoleClick,
                onAdd = callbacks.onAddTeamMember,
                onRemove = callbacks.onRemoveTeamMember,
            )
        }
    }
}
