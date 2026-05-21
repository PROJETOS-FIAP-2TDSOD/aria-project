package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.IBMPlexSansFontFamily

@Composable
fun AriaField(
    label: String?,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    helper: String? = null,
    counter: String? = null,
    content: @Composable () -> Unit,
) {
    val c = AriaTheme.colors
    Column(modifier = modifier.padding(bottom = 18.dp)) {
        if (label != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label.uppercase(),
                    style = AriaText.labelLg,
                    color = c.textSecondary,
                )
                if (required) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "*", style = AriaText.labelLg, color = c.error)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        content()
        if (helper != null || counter != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = helper.orEmpty(),
                    style = TextStyle(
                        fontFamily = IBMPlexSansFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                    ),
                    color = c.textTertiary,
                )
                if (counter != null) Text(
                    text = counter,
                    style = AriaText.labelMd,
                    color = c.textTertiary,
                )
            }
        }
    }
}

@Composable
fun AriaInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    val c = AriaTheme.colors
    val visual: VisualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(c.surface)
            .border(BorderStroke(0.5.dp, c.borderSecondary), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp)
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, tint = c.textTertiary, modifier = Modifier.size(18.dp))
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = visual,
                cursorBrush = SolidColor(c.accentMain),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = IBMPlexSansFontFamily,
                    fontSize = 14.sp,
                    color = c.textPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            if (value.isEmpty() && placeholder != null) {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontFamily = IBMPlexSansFontFamily,
                        fontSize = 14.sp,
                        color = c.textTertiary,
                    )
                )
            }
        }
        if (trailingIcon != null) {
            val mod = if (onTrailingClick != null) Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onTrailingClick)
                .padding(2.dp)
            else Modifier
            Icon(imageVector = trailingIcon, contentDescription = null, tint = c.textTertiary, modifier = mod.size(18.dp))
        }
    }
}

@Composable
fun AriaTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    minHeight: Dp = 100.dp,
) {
    val c = AriaTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .clip(RoundedCornerShape(8.dp))
            .background(c.surface)
            .border(BorderStroke(0.5.dp, c.borderSecondary), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            cursorBrush = SolidColor(c.accentMain),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = IBMPlexSansFontFamily,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = c.textPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (value.isEmpty() && placeholder != null) {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = IBMPlexSansFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = c.textTertiary,
                )
            )
        }
    }
}

@Composable
fun AriaSelect(
    value: String?,
    placeholder: String = "Selecione",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(c.surface)
            .border(BorderStroke(0.5.dp, c.borderSecondary), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = value ?: placeholder,
            modifier = Modifier.weight(1f),
            color = if (value != null) c.textPrimary else c.textTertiary,
            style = TextStyle(
                fontFamily = IBMPlexSansFontFamily,
                fontSize = 14.sp,
            ),
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = c.textTertiary,
            modifier = Modifier.size(18.dp),
        )
    }
}

object AriaInputIcons {
    val Eye get() = Icons.Outlined.RemoveRedEye
}
