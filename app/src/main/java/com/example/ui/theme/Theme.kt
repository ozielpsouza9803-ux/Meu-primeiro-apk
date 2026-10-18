package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LuxuryDarkColorScheme = darkColorScheme(
    primary = GoldChampagne,
    onPrimary = DeepBlackBackground,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = DeepBlackBackground,
    secondaryContainer = DeepBlackCardElevated,
    onSecondaryContainer = OffWhite,
    tertiary = GoldChampagneMuted,
    onTertiary = DeepBlackBackground,
    background = DeepBlackBackground,
    onBackground = SophisticatedWhite,
    surface = DeepBlackSurface,
    onSurface = SophisticatedWhite,
    surfaceVariant = DeepBlackCard,
    onSurfaceVariant = OffWhite,
    outline = DeepBlackBorder,
    outlineVariant = DeepBlackDivider
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryDarkColorScheme,
        typography = Typography,
        content = content
    )
}
