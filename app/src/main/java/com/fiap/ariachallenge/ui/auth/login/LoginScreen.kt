package com.fiap.ariachallenge.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.ui.auth.AuthOutlinedTextField
import com.fiap.ariachallenge.ui.auth.AuthPrimaryButton
import com.fiap.ariachallenge.ui.auth.AuthShell
import com.fiap.ariachallenge.ui.theme.AriaChallengeTheme
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.test.AriaTestTags

@Composable
fun LoginScreen(
    onNavigateToRecover: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (UserRole) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        uiState.loginSuccess?.let { onLoginSuccess(it) }
    }

    LoginContent(
        email = uiState.email,
        password = uiState.password,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLogin = viewModel::login,
        onNavigateToRecover = onNavigateToRecover,
        onNavigateToRegister = onNavigateToRegister,
    )
}

@Composable
internal fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onNavigateToRecover: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberPassword by remember { mutableStateOf(false) }
    val c = AriaTheme.colors

    val errorText = when (error) {
        "ERR_EMAIL" -> stringResource(R.string.auth_error_email_required)
        "ERR_PASSWORD" -> stringResource(R.string.auth_error_password_required)
        "ERR_INVALID_CREDENTIALS" -> stringResource(R.string.auth_error_invalid_credentials)
        null -> null
        else -> error
    }

    AuthShell(showBack = false, onBack = null) {


        Spacer(modifier = Modifier.height(56.dp))

        AuthOutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.auth_email_label),
            placeholder = stringResource(R.string.auth_email_placeholder),
            keyboardType = KeyboardType.Email,
            modifier = Modifier.testTag(AriaTestTags.LoginEmail),
        )
        Spacer(modifier = Modifier.height(14.dp))
        AuthOutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.auth_password_label),
            placeholder = stringResource(R.string.auth_password_placeholder),
            modifier = Modifier.testTag(AriaTestTags.LoginPassword),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) stringResource(R.string.auth_hide_password) else stringResource(R.string.auth_show_password),
                        tint = c.textSecondary,
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Checkbox(
                    checked = rememberPassword,
                    onCheckedChange = { rememberPassword = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = c.accentMain,
                        uncheckedColor = c.borderSecondary,
                    ),
                )
                Text(
                    text = stringResource(R.string.auth_remember_password),
                    style = AriaText.bodyMd,
                    color = c.textSecondary,
                    modifier = Modifier.clickable { rememberPassword = !rememberPassword },
                )
            }
            TextButton(onClick = onNavigateToRecover) {
                Text(
                    text = stringResource(R.string.auth_login_forgot_link),
                    style = AriaText.labelMd,
                    color = c.accentMain,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (errorText != null) {
            Text(
                text = errorText,
                color = c.error,
                style = AriaText.bodyMd,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        AuthPrimaryButton(
            text = if (isLoading) stringResource(R.string.label_loading) else stringResource(R.string.auth_login_button),
            onClick = onLogin,
            enabled = !isLoading,
            loading = isLoading,
            modifier = Modifier.testTag(AriaTestTags.LoginSubmit),
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.auth_register_prompt),
            style = AriaText.labelMd,
            color = c.accentMain,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToRegister)
                .padding(vertical = 12.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    AriaChallengeTheme {
        LoginContent(
            email = "rafael.costa@aguiabranca.com",
            password = "12345678",
            isLoading = false,
            error = null,
            onEmailChange = {},
            onPasswordChange = {},
            onLogin = {},
            onNavigateToRecover = {},
            onNavigateToRegister = {},
        )
    }
}
