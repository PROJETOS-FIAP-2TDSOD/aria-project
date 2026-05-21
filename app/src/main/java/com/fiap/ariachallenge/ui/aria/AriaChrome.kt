package com.fiap.ariachallenge.ui.aria

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import com.fiap.ariachallenge.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.ui.theme.AriaText
import com.fiap.ariachallenge.ui.theme.AriaTheme
import com.fiap.ariachallenge.ui.theme.OutfitFontFamily

@Composable
fun AriaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val c = AriaTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(c.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 12.dp)
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.cd_back_button),
                        tint = c.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AriaText.titleMd,
                    color = c.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (sub != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = sub.uppercase(), style = AriaText.labelMd, color = c.textTertiary)
                }
            }
            trailing?.invoke()
        }
        AriaHairline()
    }
}

@Composable
fun AriaHairline(color: Color? = null) {
    val c = AriaTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(color ?: c.borderTertiary)
    )
}

@Composable
fun AriaSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    val c = AriaTheme.colors
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = text.uppercase(), style = AriaText.labelLg, color = c.textSecondary)
            if (sub != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = sub, style = AriaText.bodyMd, color = c.textTertiary)
            }
        }
        action?.invoke()
    }
}

@Composable
fun AriaTabs(
    items: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    showDivider: Boolean = true,
) {
    val c = AriaTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(c.surface),
    ) {
        Row(
            modifier = if (scrollable) {
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            } else {
                Modifier.fillMaxWidth()
            },
        ) {
            items.forEachIndexed { i, label ->
                val active = i == selected
                Column(
                    modifier = Modifier
                        .then(
                            if (scrollable) {
                                Modifier.clickable { onSelect(i) }
                            } else {
                                Modifier
                                    .weight(1f)
                                    .clickable { onSelect(i) }
                            },
                        )
                        .then(
                            if (scrollable) {
                                Modifier.padding(horizontal = 20.dp)
                            } else {
                                Modifier
                            },
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = label,
                        color = if (active) c.textPrimary else c.textTertiary,
                        style = TextStyle(
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.padding(vertical = 12.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (active) c.primaryMain else Color.Transparent),
                    )
                }
            }
        }
        if (showDivider) {
            AriaHairline()
        }
    }
}

@Composable
fun AriaDivider(color: Color? = null) {
    val c = AriaTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color ?: c.borderTertiary)
    )
}

@Composable
fun AriaVerticalDivider(color: Color? = null) {
    val c = AriaTheme.colors
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .size(1.dp)
            .background(color ?: c.borderTertiary)
    )
}

data class AriaBottomNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun AriaBottomNav(
    items: List<AriaBottomNavItem>,
    activeId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = AriaTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        AriaHairline()
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = c.surface,
            contentColor = c.textPrimary,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets.navigationBars,
        ) {
            items.forEach { item ->
                val isActive = item.id == activeId
                NavigationBarItem(
                    selected = isActive,
                    onClick = { onSelect(item.id) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = AriaText.labelMd.copy(
                                fontSize = androidx.compose.ui.unit.TextUnit(10f, androidx.compose.ui.unit.TextUnitType.Sp),
                                letterSpacing = androidx.compose.ui.unit.TextUnit(1f, androidx.compose.ui.unit.TextUnitType.Sp),
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                            ),
                            maxLines = 1,
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = c.accentMain,
                        selectedTextColor = c.accentMain,
                        unselectedIconColor = c.textTertiary,
                        unselectedTextColor = c.textTertiary,
                        indicatorColor = c.accentSubtle,
                    ),
                )
            }
        }
    }
}
