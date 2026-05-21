package com.fiap.ariachallenge.ui.lider.criar_orientacao

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.aria.AriaCard
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaPickerOption
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.aria.AriaSingleChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaTextArea
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

private enum class OrientacaoPickerSheet {
    None,
    Period,
    Visibility,
}

@Composable
fun CriarOrientacaoScreen(
    onBack: () -> Unit,
    viewModel: CriarOrientacaoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = AriaTheme.colors
    val isEditMode = uiState.editOrientationId != null
    var activeSheet by remember { mutableStateOf(OrientacaoPickerSheet.None) }

    val periodOptions = stringArrayResource(R.array.orientation_period_options).toList()
    val visibilityOptions = stringArrayResource(R.array.orientation_visibility_options).toList()
    val periodLabel = periodOptions.getOrNull(uiState.periodOptionIndex.coerceIn(0, periodOptions.lastIndex.coerceAtLeast(0)))
    val visibilityLabel = visibilityOptions.getOrNull(uiState.visibilityOptionIndex.coerceIn(0, visibilityOptions.lastIndex.coerceAtLeast(0)))

    AriaSingleChoiceBottomSheet(
        visible = activeSheet == OrientacaoPickerSheet.Period,
        title = stringResource(R.string.picker_title_period),
        options = periodOptions.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = OrientacaoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onPeriodIndexChange) },
        selectedOptionId = if (periodOptions.isEmpty()) null else uiState.periodOptionIndex.coerceIn(0, periodOptions.lastIndex).toString(),
    )
    AriaSingleChoiceBottomSheet(
        visible = activeSheet == OrientacaoPickerSheet.Visibility,
        title = stringResource(R.string.picker_title_visibility),
        options = visibilityOptions.mapIndexed { index, label -> AriaPickerOption(index.toString(), label) },
        onDismiss = { activeSheet = OrientacaoPickerSheet.None },
        onSelected = { id -> id.toIntOrNull()?.let(viewModel::onVisibilityIndexChange) },
        selectedOptionId = if (visibilityOptions.isEmpty()) null else uiState.visibilityOptionIndex.coerceIn(0, visibilityOptions.lastIndex).toString(),
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onBack()
    }

    Scaffold(
        containerColor = c.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(
                    if (isEditMode) R.string.edit_orientation_title else R.string.create_orientation_title,
                ),
                onBack = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            AssistantCard(
                isGenerating = uiState.isGenerating,
                generated = uiState.objective.isNotBlank(),
                onGenerate = viewModel::onGenerateClick,
            )

            Spacer(modifier = Modifier.height(24.dp))

            AriaField(label = stringResource(R.string.create_orientation_field_code), required = true) {
                AriaInput(value = uiState.code, onValueChange = viewModel::onCodeChange)
            }

            AriaField(label = stringResource(R.string.create_orientation_field_title), required = true, counter = "${uiState.title.length} / 60") {
                AriaInput(value = uiState.title, onValueChange = viewModel::onTitleChange)
            }

            AriaField(label = stringResource(R.string.create_orientation_field_period), required = true) {
                AriaSelect(
                    value = periodLabel,
                    placeholder = stringResource(R.string.picker_title_period),
                    onClick = { activeSheet = OrientacaoPickerSheet.Period },
                )
            }

            AriaField(
                label = stringResource(R.string.create_orientation_field_objective),
                required = true,
                counter = "${uiState.objective.length} / 280",
                helper = stringResource(R.string.create_orientation_field_objective_helper),
            ) {
                AriaTextArea(
                    value = uiState.objective,
                    onValueChange = viewModel::onObjectiveChange,
                    placeholder = stringResource(R.string.create_orientation_field_objective_placeholder),
                    minHeight = 90.dp,
                )
            }

            Text(
                text = stringResource(R.string.create_orientation_field_metrics),
                style = AriaText.labelLg,
                color = c.textSecondary,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = stringResource(R.string.create_orientation_field_metrics_helper),
                style = AriaText.bodyMd,
                color = c.textTertiary,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            KeyMetricsEditor(
                metrics = uiState.keyMetrics,
                expandedMetricId = uiState.expandedMetricId,
                onToggleExpanded = viewModel::toggleMetricExpanded,
                onNameChange = viewModel::onKeyMetricNameChange,
                onAchievedChange = viewModel::onKeyMetricAchievedChange,
                onTargetChange = viewModel::onKeyMetricTargetChange,
                onAdd = viewModel::addKeyMetric,
                onRemove = viewModel::removeKeyMetric,
            )
            Spacer(modifier = Modifier.height(8.dp))

            AriaField(label = stringResource(R.string.create_orientation_field_visibility), required = true) {
                AriaSelect(
                    value = visibilityLabel,
                    placeholder = stringResource(R.string.picker_title_visibility),
                    onClick = { activeSheet = OrientacaoPickerSheet.Visibility },
                )
            }

            uiState.errorRes?.let { resId ->
                Text(
                    text = stringResource(resId),
                    color = c.error,
                    style = AriaText.bodyMd,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                )
            }

            AriaPrimaryBtn(
                text = stringResource(
                    if (isEditMode) R.string.edit_orientation_save else R.string.create_orientation_publish,
                ),
                onClick = { viewModel.publish(periodOptions) },
                accent = true,
                enabled = !uiState.isLoading,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AssistantCard(isGenerating: Boolean, generated: Boolean, onGenerate: () -> Unit) {
    val c = AriaTheme.colors
    AriaCard(padding = 14.dp, accent = true) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(c.accentSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "✦", color = c.accentMain, style = TextStyle(fontSize = 18.sp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.create_orientation_assistant_title), style = AriaText.titleMd.copy(fontSize = 14.sp), color = c.textPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = stringResource(R.string.create_orientation_assistant_description), style = AriaText.bodyMd, color = c.textTertiary)
            }
            val label = when {
                isGenerating -> stringResource(R.string.create_orientation_assistant_generating)
                generated -> stringResource(R.string.create_orientation_assistant_generated)
                else -> stringResource(R.string.create_orientation_assistant_action)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (generated) c.successBg else c.accentMain)
                    .clickable(enabled = !generated && !isGenerating, onClick = onGenerate)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    color = if (generated) c.success else androidx.compose.ui.graphics.Color.White,
                    style = TextStyle(
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                    ),
                )
            }
        }
    }
}
