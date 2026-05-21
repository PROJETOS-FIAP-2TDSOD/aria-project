package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

data class AriaPickerOption(
    val id: String,
    val label: String,
    val searchText: String = label,
)

data class AriaSheetAction(val id: String, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaSingleChoiceBottomSheet(
    visible: Boolean,
    title: String,
    options: List<AriaPickerOption>,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    selectedOptionId: String? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = title,
            style = AriaText.titleMd,
            color = AriaTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        HorizontalDivider(color = AriaTheme.colors.borderSecondary)
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            items(options, key = { it.id }) { opt ->
                val isSelected = selectedOptionId != null && opt.id == selectedOptionId
                val fg = if (isSelected) AriaTheme.colors.primaryMain else AriaTheme.colors.textPrimary
                TextButton(
                    onClick = {
                        onSelected(opt.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = opt.label,
                        style = AriaText.bodyMd,
                        color = fg,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaSearchableChoiceBottomSheet(
    visible: Boolean,
    title: String,
    searchPlaceholder: String,
    emptyMessage: String,
    options: List<AriaPickerOption>,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    selectedOptionId: String? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }

    LaunchedEffect(visible) {
        if (!visible) query = ""
    }

    if (!visible) return

    val normalizedQuery = query.trim()
    val filtered = if (normalizedQuery.isBlank()) {
        options
    } else {
        options.filter { it.searchText.contains(normalizedQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Text(
            text = title,
            style = AriaText.titleMd,
            color = AriaTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        AriaInput(
            value = query,
            onValueChange = { query = it },
            placeholder = searchPlaceholder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        )
        HorizontalDivider(color = AriaTheme.colors.borderSecondary)
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = emptyMessage,
                        style = AriaText.bodyMd,
                        color = AriaTheme.colors.textTertiary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    )
                }
            } else {
                items(filtered, key = { it.id }) { opt ->
                    val isSelected = selectedOptionId != null && opt.id == selectedOptionId
                    val fg = if (isSelected) AriaTheme.colors.primaryMain else AriaTheme.colors.textPrimary
                    TextButton(
                        onClick = {
                            onSelected(opt.id)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = opt.label,
                            style = AriaText.bodyMd,
                            color = fg,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaActionsBottomSheet(
    visible: Boolean,
    title: String?,
    actions: List<AriaSheetAction>,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = AriaText.titleMd,
                color = AriaTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            HorizontalDivider(color = AriaTheme.colors.borderSecondary)
        }
        LazyColumn(modifier = Modifier.padding(bottom = 24.dp)) {
            items(actions, key = { it.id }) { action ->
                TextButton(
                    onClick = {
                        onAction(action.id)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = action.label,
                        style = AriaText.bodyMd,
                        color = AriaTheme.colors.textPrimary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AriaDatePickerDialog(
    visible: Boolean,
    initialSelectedDateMillis: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis ?: System.currentTimeMillis(),
    )
    if (!visible) return
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) onConfirm(millis)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    ) {
        Text(
            text = stringResource(R.string.date_picker_title),
            style = AriaText.titleMd,
            color = AriaTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        DatePicker(state = state)
    }
}
