package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

private val PrimaryBtnTextStyle = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 13.sp,
    letterSpacing = 2.sp,
)

private val SmallBtnTextStyle = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    letterSpacing = 1.5.sp,
)

@Composable
fun AriaPrimaryBtn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
    compact: Boolean = false,
) {
    val c = AriaTheme.colors
    val bg = when {
        !enabled -> c.bgTertiary
        accent -> c.accentMain
        else -> c.primaryMain
    }
    val fg = if (enabled) Color.White else c.textDisabled
    val mod = (if (fullWidth) modifier.fillMaxWidth() else modifier)
        .height(48.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(bg)
        .clickable(enabled = enabled, onClick = onClick)
        .padding(horizontal = 24.dp)
    Box(modifier = mod, contentAlignment = Alignment.Center) {
        Text(
            text = text.uppercase(),
            color = fg,
            style = if (compact) SmallBtnTextStyle else PrimaryBtnTextStyle,
        )
    }
}

@Composable
fun AriaSecondaryBtn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
) {
    val c = AriaTheme.colors
    val shape = RoundedCornerShape(8.dp)
    val mod = (if (fullWidth) modifier.fillMaxWidth() else modifier)
        .height(48.dp)
        .clip(shape)
        .background(c.surface)
        .border(BorderStroke(1.dp, c.borderSecondary), shape)
        .clickable(onClick = onClick)
        .padding(horizontal = 24.dp)
    Box(modifier = mod, contentAlignment = Alignment.Center) {
        Text(
            text = text.uppercase(),
            color = c.primaryMain,
            style = PrimaryBtnTextStyle,
        )
    }
}

@Composable
fun AriaTextBtn(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null,
    trailingArrow: Boolean = false,
) {
    val c = AriaTheme.colors
    val tone = color ?: c.accentMain
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = tone,
            style = TextStyle(
                fontFamily = com.fiap.ariachallenge.ui.theme.IBMPlexSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                letterSpacing = 0.3.sp,
            ),
        )
        if (trailingArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = tone,
                modifier = Modifier.size(17.dp),
            )
        }
    }
}

@Composable
fun AriaFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    contentDescription: String = stringResource(R.string.cd_new_idea_fab),
) {
    val c = AriaTheme.colors
    Box(
        modifier = modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(c.accentMain)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
