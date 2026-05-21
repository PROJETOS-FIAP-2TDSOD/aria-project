package com.fiap.ariachallenge.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.auth.AuthOutlinedTextField
import com.fiap.ariachallenge.ui.auth.AuthPrimaryButton
import com.fiap.ariachallenge.ui.auth.AuthShell
import com.fiap.ariachallenge.domain.model.UserRole
import com.fiap.ariachallenge.ui.test.AriaTestTags
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: (UserRole) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    val c = AriaTheme.colors

    LaunchedEffect(uiState.registeredUserRole) {
        uiState.registeredUserRole?.let(onRegistered)
    }

    AuthShell(showBack = true, onBack = onBack) {
        Text(
            text = stringResource(R.string.auth_register_title),
            style = AriaText.titleLg,
            color = c.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Text(
            text = stringResource(R.string.auth_register_subtitle),
            style = AriaText.bodyMd,
            color = c.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        AuthOutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = stringResource(R.string.auth_name_label),
            placeholder = stringResource(R.string.auth_name_placeholder),
            modifier = Modifier.testTag(AriaTestTags.RegisterName),
        )
        Spacer(modifier = Modifier.height(14.dp))
        AuthOutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = stringResource(R.string.auth_email_label),
            placeholder = stringResource(R.string.auth_email_placeholder),
            keyboardType = KeyboardType.Email,
            modifier = Modifier.testTag(AriaTestTags.RegisterEmail),
        )
        Spacer(modifier = Modifier.height(14.dp))
        AuthOutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = stringResource(R.string.auth_password_label),
            placeholder = stringResource(R.string.auth_password_placeholder),
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
        Spacer(modifier = Modifier.height(14.dp))
        AuthOutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = stringResource(R.string.auth_confirm_password_label),
            placeholder = stringResource(R.string.auth_password_placeholder),
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmVisible = !confirmVisible }) {
                    Icon(
                        imageVector = if (confirmVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (confirmVisible) stringResource(R.string.auth_hide_password) else stringResource(R.string.auth_show_password),
                        tint = c.textSecondary,
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        uiState.error?.let { code ->
            val msg = when (code) {
                "ERR_NAME" -> stringResource(R.string.auth_error_name_required)
                "ERR_EMAIL" -> stringResource(R.string.auth_error_email_required)
                "ERR_MISMATCH" -> stringResource(R.string.auth_error_password_mismatch)
                "ERR_PASSWORD_SHORT" -> stringResource(R.string.auth_error_password_short)
                "ERR_EMAIL_EXISTS" -> stringResource(R.string.auth_error_email_exists)
                "ERR_INVALID" -> stringResource(R.string.auth_error_register_invalid)
                else -> code
            }
            Text(text = msg, color = c.error, style = AriaText.bodyMd, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        AuthPrimaryButton(
            text = if (uiState.isLoading) stringResource(R.string.label_loading) else stringResource(R.string.auth_register_button),
            onClick = viewModel::register,
            enabled = !uiState.isLoading,
            loading = uiState.isLoading,
            modifier = Modifier.testTag(AriaTestTags.RegisterSubmit),
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.auth_already_have_account),
            style = AriaText.labelMd,
            color = c.accentMain,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable(onClick = onBack)
                .padding(vertical = 8.dp),
        )
    }
}
