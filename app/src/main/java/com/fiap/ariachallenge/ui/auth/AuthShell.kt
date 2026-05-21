package com.fiap.ariachallenge.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.IBMPlexSansFontFamily
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

@Composable
fun AuthShell(
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = AriaTheme.colors
    val scroll = rememberScrollState()
    val version = stringResource(R.string.auth_app_version)
    val isDark = isSystemInDarkTheme()
    val heroBgRes = if (isDark) R.drawable.background_dark else R.drawable.background_light

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(c.bgPrimary)
            .imePadding(),
    ) {
        Image(
            painter = painterResource(heroBgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
        )

        if (showBack && onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 4.dp, top = 4.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = if (isDark) Color.White else c.textPrimary,
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 80.dp, bottom = 0.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AuthLogoBlock()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
            )

            Text(
                text = stringResource(R.string.auth_footer_version, version),
                style = AriaText.labelMd.copy(fontSize = 11.sp, fontFamily = IBMPlexSansFontFamily),
                color = c.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun AuthLogoBlock() {
    val isDark = isSystemInDarkTheme()
    val subColor = if (isDark) Color.White.copy(alpha = 0.78f) else AriaTheme.colors.textTertiary
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = stringResource(R.string.app_name),
        modifier = Modifier
            .height(96.dp)
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentScale = ContentScale.Fit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    val c = AriaTheme.colors
        OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        label = { Text(text = label, style = AriaText.labelMd, color = c.textSecondary) },
        placeholder = if (placeholder != null) {
            { Text(text = placeholder, style = AriaText.bodyMd, color = c.textTertiary) }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = AriaText.bodyMd.copy(color = c.textPrimary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = c.textPrimary,
            unfocusedTextColor = c.textPrimary,
            focusedBorderColor = c.accentMain,
            unfocusedBorderColor = c.borderSecondary,
            cursorColor = c.accentMain,
            focusedLabelColor = c.accentMain,
            unfocusedLabelColor = c.textTertiary,
            focusedPlaceholderColor = c.textTertiary,
            unfocusedPlaceholderColor = c.textTertiary,
            errorBorderColor = c.error,
            errorLabelColor = c.error,
            errorSupportingTextColor = c.error,
            focusedTrailingIconColor = c.textSecondary,
            unfocusedTrailingIconColor = c.textSecondary,
        ),
    )
}

@Composable
fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val c = AriaTheme.colors
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = c.accentMain,
            contentColor = c.textOnAccent,
            disabledContainerColor = c.accentMain.copy(alpha = 0.45f),
            disabledContentColor = c.textOnAccent.copy(alpha = 0.7f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = c.textOnAccent,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text.uppercase(),
                style = AriaText.labelLg.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    fontFamily = OutfitFontFamily,
                ),
            )
        }
    }
}
