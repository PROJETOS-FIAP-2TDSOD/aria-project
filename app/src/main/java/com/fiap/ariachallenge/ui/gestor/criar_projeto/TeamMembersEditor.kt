package com.fiap.ariachallenge.ui.gestor.criar_projeto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.User
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun TeamMembersEditor(
    members: List<TeamMemberFormItem>,
    assignableUsers: List<User>,
    roleLabels: List<String>,
    expandedMemberId: String?,
    onToggleExpanded: (String) -> Unit,
    onUserClick: (id: String) -> Unit,
    onRoleClick: (id: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val removeCd = stringResource(R.string.project_remove_team_member_cd)
    val expandCd = stringResource(R.string.project_expand_team_member_cd)
    val collapseCd = stringResource(R.string.project_collapse_team_member_cd)
    val usersById = assignableUsers.associateBy { it.id }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (members.isEmpty()) {
            AriaCard(padding = 16.dp) {
                Text(
                    text = stringResource(R.string.project_team_empty_hint),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            }
        } else {
            members.forEachIndexed { index, member ->
                CollapsibleTeamMemberCard(
                    index = index,
                    member = member,
                    userName = usersById[member.userId]?.name,
                    roleLabel = roleLabels.getOrNull(member.roleIndex),
                    isExpanded = expandedMemberId == member.id,
                    removeCd = removeCd,
                    expandCd = expandCd,
                    collapseCd = collapseCd,
                    onToggle = { onToggleExpanded(member.id) },
                    onRemove = { onRemove(member.id) },
                    onUserClick = { onUserClick(member.id) },
                    onRoleClick = { onRoleClick(member.id) },
                )
            }
        }

        AriaPrimaryBtn(
            text = stringResource(R.string.project_add_team_member),
            onClick = onAdd,
            accent = false,
            compact = true,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun CollapsibleTeamMemberCard(
    index: Int,
    member: TeamMemberFormItem,
    userName: String?,
    roleLabel: String?,
    isExpanded: Boolean,
    removeCd: String,
    expandCd: String,
    collapseCd: String,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onUserClick: () -> Unit,
    onRoleClick: () -> Unit,
) {
    val c = AriaTheme.colors
    val title = userName ?: stringResource(R.string.project_team_member_item_label, index + 1)
    val summary = roleLabel.orEmpty()
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "team_member_chevron",
    )

    AriaCard(padding = 0.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .semantics {
                        contentDescription = if (isExpanded) collapseCd else expandCd
                    }
                    .padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = c.textTertiary,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(chevronRotation),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = AriaText.titleMd.copy(fontSize = 14.sp),
                        color = c.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!isExpanded && summary.isNotBlank()) {
                        Text(
                            text = summary,
                            style = AriaText.bodyMd,
                            color = c.textTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(36.dp)
                        .semantics { contentDescription = removeCd },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        tint = c.textTertiary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AriaField(label = stringResource(R.string.project_team_field_person), required = true) {
                        AriaSelect(
                            value = userName,
                            placeholder = stringResource(R.string.project_team_select_person),
                            onClick = onUserClick,
                        )
                    }
                    AriaField(label = stringResource(R.string.project_team_field_role), required = true) {
                        AriaSelect(
                            value = roleLabel,
                            placeholder = stringResource(R.string.project_team_select_role),
                            onClick = onRoleClick,
                        )
                    }
                }
            }
        }
    }
}
