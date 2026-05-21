package com.fiap.ariachallenge.ui.lider.criar_orientacao

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
import androidx.compose.material.icons.outlined.Add
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
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaProgressLine
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.OrientationKeyMetricParser

@Composable
fun KeyMetricsEditor(
    metrics: List<KeyMetricFormItem>,
    expandedMetricId: String?,
    onToggleExpanded: (String) -> Unit,
    onNameChange: (id: String, value: String) -> Unit,
    onAchievedChange: (id: String, value: String) -> Unit,
    onTargetChange: (id: String, value: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    val removeCd = stringResource(R.string.create_orientation_remove_metric_cd)
    val expandCd = stringResource(R.string.create_orientation_expand_metric_cd)
    val collapseCd = stringResource(R.string.create_orientation_collapse_metric_cd)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (metrics.isEmpty()) {
            AriaCard(padding = 16.dp) {
                Text(
                    text = stringResource(R.string.create_orientation_metrics_empty_hint),
                    style = AriaText.bodyMd,
                    color = c.textTertiary,
                )
            }
        } else {
            metrics.forEachIndexed { index, metric ->
                CollapsibleMetricCard(
                    index = index,
                    metric = metric,
                    isExpanded = expandedMetricId == metric.id,
                    removeCd = removeCd,
                    expandCd = expandCd,
                    collapseCd = collapseCd,
                    onToggle = { onToggleExpanded(metric.id) },
                    onRemove = { onRemove(metric.id) },
                    onNameChange = { onNameChange(metric.id, it) },
                    onAchievedChange = { onAchievedChange(metric.id, it) },
                    onTargetChange = { onTargetChange(metric.id, it) },
                )
            }
        }

        AriaPrimaryBtn(
            text = stringResource(R.string.create_orientation_add_metric),
            onClick = onAdd,
            accent = false,
            compact = true,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun CollapsibleMetricCard(
    index: Int,
    metric: KeyMetricFormItem,
    isExpanded: Boolean,
    removeCd: String,
    expandCd: String,
    collapseCd: String,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onNameChange: (String) -> Unit,
    onAchievedChange: (String) -> Unit,
    onTargetChange: (String) -> Unit,
) {
    val c = AriaTheme.colors
    val progress = OrientationKeyMetricParser.computeProgress(metric.achieved, metric.target)
    val title = metric.name.ifBlank {
        stringResource(R.string.create_orientation_metric_item_label, index + 1)
    }
    val summary = when {
        metric.achieved.isNotBlank() && metric.target.isNotBlank() ->
            "${metric.achieved} / ${metric.target}"
        metric.target.isNotBlank() -> metric.target
        else -> null
    }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "metric_chevron",
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
                    if (!isExpanded && summary != null) {
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
                    AriaField(label = stringResource(R.string.create_orientation_metric_name)) {
                        AriaInput(
                            value = metric.name,
                            onValueChange = onNameChange,
                            placeholder = stringResource(R.string.create_orientation_metric_name_placeholder),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AriaField(
                            label = stringResource(R.string.create_orientation_metric_achieved),
                            modifier = Modifier.weight(1f),
                        ) {
                            AriaInput(
                                value = metric.achieved,
                                onValueChange = onAchievedChange,
                                placeholder = stringResource(R.string.create_orientation_metric_achieved_placeholder),
                            )
                        }
                        AriaField(
                            label = stringResource(R.string.create_orientation_metric_target),
                            modifier = Modifier.weight(1f),
                        ) {
                            AriaInput(
                                value = metric.target,
                                onValueChange = onTargetChange,
                                placeholder = stringResource(R.string.create_orientation_metric_target_placeholder),
                            )
                        }
                    }
                    if (metric.achieved.isNotBlank() && metric.target.isNotBlank()) {
                        AriaProgressLine(
                            value = progress,
                            color = c.primaryMain,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
