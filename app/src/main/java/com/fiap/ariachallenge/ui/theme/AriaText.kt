package com.fiap.ariachallenge.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AriaText {
    val displayLg = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 8.sp,
    )
    val displayMd = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 6.sp,
    )
    val titleLg = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
    )
    val titleMd = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.3.sp,
    )
    val bodyLg = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    )
    val bodyMd = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp,
    )
    val labelLg = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.6.sp,
    )
    val labelMd = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
    )
    val metric = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp,
    )
    val metricSm = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.3).sp,
    )
}
