package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LuxurySerif = FontFamily.Serif
val LuxurySans = FontFamily.SansSerif

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 1.2.sp,
        color = SophisticatedWhite
    ),
    displayMedium = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 1.sp,
        color = SophisticatedWhite
    ),
    displaySmall = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        color = SophisticatedWhite
    ),
    headlineMedium = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.6.sp,
        color = SophisticatedWhite
    ),
    headlineSmall = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = SophisticatedWhite
    ),
    titleLarge = TextStyle(
        fontFamily = LuxurySerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.5.sp,
        color = SophisticatedWhite
    ),
    titleMedium = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp,
        color = SophisticatedWhite
    ),
    titleSmall = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp,
        color = TextMuted
    ),
    bodyLarge = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
        color = OffWhite
    ),
    bodyMedium = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp,
        color = TextMuted
    ),
    bodySmall = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.2.sp,
        color = TextDim
    ),
    labelLarge = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 17.sp,
        letterSpacing = 1.0.sp,
        color = SophisticatedWhite
    ),
    labelMedium = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.8.sp,
        color = GoldChampagne
    ),
    labelSmall = TextStyle(
        fontFamily = LuxurySans,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.6.sp,
        color = TextMuted
    )
)
