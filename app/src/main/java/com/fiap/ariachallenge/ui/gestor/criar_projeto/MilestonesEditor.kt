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
import com.fiap.ariachallenge.domain.model.MilestoneStatus
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun MilestonesEditor(
    milestones: List<MilestoneFormItem>,
    expandedMilestoneId: String?,
    statusLabels: Map<MilestoneStatus, String>,
    onToggleExpanded: (String) -> Unit,
    onTitleChange: (id: String, value: String) -> Unit,
    onDueDateClick: (id: String) -> Unit,
    onStatusClick: (id: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val removeCd = stringResource(R.string.project_remove_milestone_cd)
    val expandCd = stringResource(R.string.project_expand_milestone_cd)
    val collapseCd = stringResource(R.string.project_collapse_milestone_cd)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (milestones.isEmpty()) {
            AriaCard(padding = 16.dp) {
                Text(
                    text = stringResource(R.string.project_milestones_empty_hint),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            }
        } else {
            milestones.forEachIndexed { index, milestone ->
                CollapsibleMilestoneCard(
                    index = index,
                    milestone = milestone,
                    statusLabels = statusLabels,
                    isExpanded = expandedMilestoneId == milestone.id,
                    removeCd = removeCd,
                    expandCd = expandCd,
                    collapseCd = collapseCd,
                    onToggle = { onToggleExpanded(milestone.id) },
                    onRemove = { onRemove(milestone.id) },
                    onTitleChange = { onTitleChange(milestone.id, it) },
                    onDueDateClick = { onDueDateClick(milestone.id) },
                    onStatusClick = { onStatusClick(milestone.id) },
                )
            }
        }

        AriaPrimaryBtn(
            text = stringResource(R.string.project_add_milestone),
            onClick = onAdd,
            accent = false,
            compact = true,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun CollapsibleMilestoneCard(
    index: Int,
    milestone: MilestoneFormItem,
    statusLabels: Map<MilestoneStatus, String>,
    isExpanded: Boolean,
    removeCd: String,
    expandCd: String,
    collapseCd: String,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDueDateClick: () -> Unit,
    onStatusClick: () -> Unit,
) {
    val c = AriaTheme.colors
    val title = milestone.title.ifBlank {
        stringResource(R.string.project_milestone_item_label, index + 1)
    }
    val dueLabel = milestone.dueDateEpochMillis?.let { formatMediumDate(it) }
    val statusLabel = statusLabels[milestone.status].orEmpty()
    val summary = listOfNotNull(dueLabel, statusLabel.takeIf { it.isNotBlank() }).joinToString(" · ")
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "milestone_chevron",
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
                    AriaField(label = stringResource(R.string.project_milestone_field_title)) {
                        AriaInput(
                            value = milestone.title,
                            onValueChange = onTitleChange,
                            placeholder = stringResource(R.string.project_milestone_title_placeholder),
                        )
                    }
                    AriaField(label = stringResource(R.string.project_milestone_field_due_date), required = true) {
                        AriaSelect(
                            value = dueLabel,
                            placeholder = stringResource(R.string.project_create_placeholder_delivery),
                            onClick = onDueDateClick,
                        )
                    }
                    AriaField(label = stringResource(R.string.project_milestone_field_status)) {
                        AriaSelect(
                            value = statusLabel,
                            placeholder = stringResource(R.string.project_milestone_field_status),
                            onClick = onStatusClick,
                        )
                    }
                }
            }
        }
    }
}

private fun formatMediumDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(date)
}
