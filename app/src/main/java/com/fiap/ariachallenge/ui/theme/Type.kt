package com.fiap.ariachallenge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as loadGoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.fiap.ariachallenge.R

private val ariaGoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private fun ariaGoogleFont(
    name: String,
    weight: FontWeight,
    style: FontStyle = FontStyle.Normal
): Font = loadGoogleFont(
    googleFont = GoogleFont(name),
    fontProvider = ariaGoogleFontProvider,
    weight = weight,
    style = style
)

val OutfitFontFamily: FontFamily = FontFamily(
    ariaGoogleFont("Outfit", FontWeight.ExtraLight),
    ariaGoogleFont("Outfit", FontWeight.Light),
    ariaGoogleFont("Outfit", FontWeight.Normal),
    ariaGoogleFont("Outfit", FontWeight.Medium),
    ariaGoogleFont("Outfit", FontWeight.SemiBold),
    ariaGoogleFont("Outfit", FontWeight.Bold)
)

val IBMPlexSansFontFamily: FontFamily = FontFamily(
    ariaGoogleFont("IBM Plex Sans", FontWeight.Light),
    ariaGoogleFont("IBM Plex Sans", FontWeight.Normal),
    ariaGoogleFont("IBM Plex Sans", FontWeight.Medium),
    ariaGoogleFont("IBM Plex Sans", FontWeight.SemiBold)
)

val AriaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 32.sp,
        letterSpacing = 0.25.em,
        lineHeight = 40.sp
    ),
    displayMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 24.sp,
        letterSpacing = 0.25.em,
        lineHeight = 32.sp
    ),
    displaySmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        letterSpacing = 0.2.em,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.1.em,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.0625.em,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.0357.em,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.03125.em,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.017857.em,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        letterSpacing = 0.020833.em,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.166667.em,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.136364.em,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = IBMPlexSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.1.em,
        lineHeight = 14.sp
    )
)

val MetricValueStyle = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    letterSpacing = 0.0357.em,
    lineHeight = 36.sp
)
