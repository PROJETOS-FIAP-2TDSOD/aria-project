package com.fiap.ariachallenge.ui.operador.nova_ideia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.IdeaCategory
import com.fiap.ariachallenge.ui.aria.AriaField
import com.fiap.ariachallenge.ui.aria.AriaInput
import com.fiap.ariachallenge.ui.aria.AriaPickerOption
import com.fiap.ariachallenge.ui.aria.AriaPrimaryBtn
import com.fiap.ariachallenge.ui.aria.AriaSelect
import com.fiap.ariachallenge.ui.aria.AriaSingleChoiceBottomSheet
import com.fiap.ariachallenge.ui.aria.AriaTextArea
import com.fiap.ariachallenge.ui.operador.OperadorBadgeCelebrationHost
import com.fiap.ariachallenge.ui.operador.celebration.BadgeCelebrationViewModel
import com.fiap.ariachallenge.ui.aria.AriaTopBar
import com.fiap.ariachallenge.ui.test.AriaTestTags
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.util.localizedName
import com.fiap.ariachallenge.util.stringFromRes

@Composable
fun NovaIdeiaScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: NovaIdeiaViewModel = hiltViewModel(),
    celebrationViewModel: BadgeCelebrationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val celebrationBadge by celebrationViewModel.currentBadge.collectAsState()
    var showCategorySheet by remember { mutableStateOf(false) }

    AriaSingleChoiceBottomSheet(
        visible = showCategorySheet,
        title = stringResource(R.string.picker_title_category),
        options = IdeaCategory.entries.map { AriaPickerOption(it.name, stringResource(it.getDisplayNameRes())) },
        onDismiss = { showCategorySheet = false },
        onSelected = { id ->
            IdeaCategory.entries.find { it.name == id }?.let(viewModel::onCategoryChange)
        },
        selectedOptionId = uiState.category?.name,
    )

    LaunchedEffect(uiState.isSuccess, celebrationBadge) {
        if (uiState.isSuccess && celebrationBadge == null) onSuccess()
    }

    OperadorBadgeCelebrationHost {
    Scaffold(
        containerColor = AriaTheme.colors.bgPrimary,
        topBar = {
            AriaTopBar(
                title = stringResource(R.string.new_idea_title),
                onBack = onBack,
            )
        },
    ) { padding ->
        val validationError = stringFromRes(uiState.titleErrorRes)
            ?: stringFromRes(uiState.descriptionErrorRes)
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
            ) {
                item {
                    AriaField(label = stringResource(R.string.label_title), required = true, counter = "${uiState.title.length} / 100") {
                        AriaInput(
                            value = uiState.title,
                            onValueChange = viewModel::onTitleChange,
                            placeholder = stringResource(R.string.new_idea_field_title),
                            modifier = Modifier.testTag(AriaTestTags.NovaIdeiaTitle),
                        )
                    }
                }
                item {
                    AriaField(label = stringResource(R.string.label_category), required = true) {
                        AriaSelect(
                            value = uiState.category?.localizedName(),
                            placeholder = stringResource(R.string.picker_title_category),
                            onClick = { showCategorySheet = true },
                        )
                    }
                }
                item {
                    AriaField(
                        label = stringResource(R.string.label_description),
                        required = true,
                        counter = "${uiState.description.length} / 500",
                        helper = stringResource(R.string.new_idea_description_min_chars),
                    ) {
                        AriaTextArea(
                            value = uiState.description,
                            onValueChange = viewModel::onDescriptionChange,
                            placeholder = stringResource(R.string.new_idea_field_description_placeholder),
                            minHeight = 120.dp,
                        )
                    }
                }
                item {
                    AriaField(label = stringResource(R.string.new_idea_field_problem_resolves), required = true) {
                        AriaTextArea(
                            value = uiState.problema,
                            onValueChange = viewModel::onProblemaChange,
                            placeholder = stringResource(R.string.new_idea_field_problem_placeholder),
                            minHeight = 84.dp,
                        )
                    }
                }
                item {
                    AriaField(label = stringResource(R.string.label_benefits_expected), required = true) {
                        AriaTextArea(
                            value = uiState.beneficios,
                            onValueChange = viewModel::onBeneficiosChange,
                            placeholder = stringResource(R.string.new_idea_field_benefits_placeholder),
                            minHeight = 84.dp,
                        )
                    }
                }
                item {
                    AriaField(label = stringResource(R.string.label_resources_required), helper = stringResource(R.string.label_optional)) {
                        AriaTextArea(
                            value = uiState.recursos,
                            onValueChange = viewModel::onRecursosChange,
                            placeholder = stringResource(R.string.new_idea_field_resources_placeholder),
                            minHeight = 70.dp,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    AriaPrimaryBtn(
                        text = stringResource(R.string.new_idea_submit),
                        onClick = viewModel::submit,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.testTag(AriaTestTags.NovaIdeiaSubmit),
                    )
                }
                if (validationError != null || uiState.error != null) {
                    item {
                        Text(
                            text = validationError ?: uiState.error.orEmpty(),
                            style = AriaText.labelMd,
                            color = AriaTheme.colors.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        )
                    }
                }
            }
        }
    }
    }
}

