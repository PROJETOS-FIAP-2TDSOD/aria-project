package com.fiap.ariachallenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = Color.White,
    primaryContainer = LightAccentSubtle,
    onPrimaryContainer = LightAccentDark,
    secondary = LightPrimary,
    onSecondary = LightSurface,
    secondaryContainer = LightPrimarySubtle,
    onSecondaryContainer = LightPrimary,
    tertiary = LightAccentLight,
    onTertiary = LightPrimary,
    tertiaryContainer = LightAccentSubtle,
    onTertiaryContainer = LightAccentDark,
    background = LightBackgroundPrimary,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackgroundSecondary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderSecondary,
    outlineVariant = LightBorderPrimary,
    error = LightError,
    onError = LightSurface,
    errorContainer = LightErrorBackground,
    onErrorContainer = LightError,
    inverseSurface = LightPrimary,
    inverseOnSurface = LightSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = Color.White,
    primaryContainer = DarkAccentSubtle,
    onPrimaryContainer = DarkAccentLight,
    secondary = DarkBackgroundTertiary,
    onSecondary = DarkTextPrimary,
    secondaryContainer = Color(0xFF252830),
    onSecondaryContainer = DarkTextPrimary,
    tertiary = DarkAccentLight,
    onTertiary = Color(0xFF1A1206),
    tertiaryContainer = DarkAccentSubtle,
    onTertiaryContainer = DarkAccentLight,
    background = DarkBackgroundPrimary,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBackgroundSecondary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderSecondary,
    outlineVariant = DarkBorderPrimary,
    error = DarkError,
    onError = DarkBackgroundPrimary,
    errorContainer = DarkErrorBackground,
    onErrorContainer = DarkError,
    inverseSurface = DarkTextPrimary,
    inverseOnSurface = DarkSurface,
)

@Composable
fun AriaChallengeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val ariaColors = if (darkTheme) DarkAriaColors else LightAriaColors

    CompositionLocalProvider(LocalAriaColors provides ariaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AriaTypography,
            shapes = AriaShapes,
            content = content
        )
    }
}
