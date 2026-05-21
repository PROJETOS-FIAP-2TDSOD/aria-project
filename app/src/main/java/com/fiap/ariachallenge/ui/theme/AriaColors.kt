package com.fiap.ariachallenge.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AriaColors(
    val bgPrimary: Color,
    val surface: Color,
    val bgSecondary: Color,
    val bgTertiary: Color,
    val primaryMain: Color,
    val primaryMedium: Color,
    val primaryLight: Color,
    val primarySubtle: Color,
    val accentMain: Color,
    val accentLight: Color,
    val accentSubtle: Color,
    val accentDark: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textOnPrimary: Color,
    val textOnAccent: Color,
    val success: Color,
    val successBg: Color,
    val error: Color,
    val errorBg: Color,
    val warning: Color,
    val warningBg: Color,
    val info: Color,
    val infoBg: Color,
    val borderPrimary: Color,
    val borderSecondary: Color,
    val borderTertiary: Color,
)

val LightAriaColors = AriaColors(
    bgPrimary = Color(0xFFF0EDE8),
    surface = Color(0xFFFFFFFF),
    bgSecondary = Color(0xFFFAF9F7),
    bgTertiary = Color(0xFFE6E3DC),
    primaryMain = Color(0xFF1A2540),
    primaryMedium = Color(0xFF2D4A8A),
    primaryLight = Color(0xFF4A6BB5),
    primarySubtle = Color(0xFFE8ECF5),
    accentMain = Color(0xFFC87D0E),
    accentLight = Color(0xFFE09A2F),
    accentSubtle = Color(0xFFFDF3E6),
    accentDark = Color(0xFF8A5A0A),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF5A5040),
    textTertiary = Color(0xFF7A7060),
    textDisabled = Color(0xFF9A9080),
    textOnPrimary = Color(0xFFFFFFFF),
    textOnAccent = Color(0xFFFFFFFF),
    success = Color(0xFF34A853),
    successBg = Color(0xFFE8F5E9),
    error = Color(0xFFC62828),
    errorBg = Color(0xFFFFEBEE),
    warning = Color(0xFFEF6C00),
    warningBg = Color(0xFFFFF3E0),
    info = Color(0xFFC87D0E),
    infoBg = Color(0xFFFDF3E6),
    borderPrimary = Color(0x1A1A1A2E),
    borderSecondary = Color(0x261A1A2E),
    borderTertiary = Color(0x141A1A2E),
)

val DarkAriaColors = AriaColors(
    bgPrimary = Color(0xFF0F1117),
    surface = Color(0xFF1A1D27),
    bgSecondary = Color(0xFF22262F),
    bgTertiary = Color(0xFF2A2D3A),
    primaryMain = Color(0xFFC87D0E),
    primaryMedium = Color(0xFFE09A2F),
    primaryLight = Color(0xFFF5B455),
    primarySubtle = Color(0xFF2A2210),
    accentMain = Color(0xFFC87D0E),
    accentLight = Color(0xFFE09A2F),
    accentSubtle = Color(0xFF2A1F0F),
    accentDark = Color(0xFF8A5A0A),
    textPrimary = Color(0xFFE0DDD6),
    textSecondary = Color(0xFFB8B4AA),
    textTertiary = Color(0xFF8A8780),
    textDisabled = Color(0xFF6A6760),
    textOnPrimary = Color(0xFFFFFFFF),
    textOnAccent = Color(0xFFFFFFFF),
    success = Color(0xFF66BB6A),
    successBg = Color(0xFF1A2E1A),
    error = Color(0xFFEF5350),
    errorBg = Color(0xFF2A1A1A),
    warning = Color(0xFFFF9800),
    warningBg = Color(0xFF2A1F0F),
    info = Color(0xFFE09A2F),
    infoBg = Color(0xFF2A2210),
    borderPrimary = Color(0x26FFFFFF),
    borderSecondary = Color(0x1FFFFFFF),
    borderTertiary = Color(0x14FFFFFF),
)

val LocalAriaColors = compositionLocalOf { LightAriaColors }

object AriaTheme {
    val colors: AriaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAriaColors.current
}
