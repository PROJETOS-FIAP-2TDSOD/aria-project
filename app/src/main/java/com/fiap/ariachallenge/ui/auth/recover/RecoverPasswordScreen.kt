package com.fiap.ariachallenge.ui.auth.recover

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.auth.AuthOutlinedTextField
import com.fiap.ariachallenge.ui.auth.AuthPrimaryButton
import com.fiap.ariachallenge.ui.auth.AuthShell
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun RecoverPasswordScreen(
    onBack: () -> Unit,
    viewModel: RecoverPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AuthShell(showBack = true, onBack = onBack) {
        if (uiState.isSuccess) {
            SuccessContent(onBack = onBack)
        } else {
            RecoverFormContent(
                email = uiState.email,
                isLoading = uiState.isLoading,
                error = uiState.errorRes?.let { stringResource(it) } ?: uiState.error,
                onEmailChange = viewModel::onEmailChange,
                onRecover = viewModel::recoverPassword,
                onBack = onBack,
            )
        }
    }
}

@Composable
private fun RecoverFormContent(
    email: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onRecover: () -> Unit,
    onBack: () -> Unit,
) {
    val c = AriaTheme.colors

    Text(
        text = stringResource(R.string.auth_recover_subtitle),
        style = AriaText.bodyMd,
        color = c.textSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 20.dp),
    )

    AuthOutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = stringResource(R.string.auth_email_label),
        placeholder = stringResource(R.string.auth_email_placeholder),
        keyboardType = KeyboardType.Email,
        isError = error != null,
        supportingText = if (error != null) {
            { Text(text = error, color = c.error, style = AriaText.labelMd) }
        } else {
            null
        },
    )

    Spacer(modifier = Modifier.height(20.dp))

    AuthPrimaryButton(
        text = if (isLoading) stringResource(R.string.label_loading) else stringResource(R.string.auth_recover_button),
        onClick = onRecover,
        enabled = !isLoading,
        loading = isLoading,
    )

    Spacer(modifier = Modifier.height(12.dp))
    TextButton(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.auth_back_to_login),
            style = AriaText.labelMd,
            color = c.accentMain,
        )
    }
}

@Composable
private fun SuccessContent(onBack: () -> Unit) {
    val c = AriaTheme.colors
    Icon(
        imageVector = Icons.Outlined.CheckCircle,
        contentDescription = null,
        modifier = Modifier
            .size(72.dp)
            .padding(bottom = 8.dp),
        tint = c.success,
    )
    Text(
        text = stringResource(R.string.auth_recover_success_title),
        style = AriaText.titleLg,
        color = c.textPrimary,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = stringResource(R.string.auth_recover_success_message),
        style = AriaText.bodyMd,
        color = c.textSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp),
    )
    Spacer(modifier = Modifier.height(28.dp))
    AuthPrimaryButton(
        text = stringResource(R.string.auth_back_to_login),
        onClick = onBack,
    )
}

@Preview(showBackground = true, name = "Recover – Light")
@Composable
private fun RecoverPreviewLight() {
    AriaChallengeTheme(darkTheme = false) {
        AuthShell(showBack = true, onBack = {}) {
            RecoverFormContent(
                email = "",
                isLoading = false,
                error = null,
                onEmailChange = {},
                onRecover = {},
                onBack = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Recover – Dark")
@Composable
private fun RecoverPreviewDark() {
    AriaChallengeTheme(darkTheme = true) {
        AuthShell(showBack = true, onBack = {}) {
            RecoverFormContent(
                email = "",
                isLoading = false,
                error = null,
                onEmailChange = {},
                onRecover = {},
                onBack = {},
            )
        }
    }
}
