package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Luxury Black Palette
val DeepBlackBackground = Color(0xFF09090B)
val DeepBlackSurface = Color(0xFF121216)
val DeepBlackCard = Color(0xFF18181E)
val DeepBlackCardElevated = Color(0xFF22222B)
val DeepBlackBorder = Color(0xFF2E2E3A)
val DeepBlackDivider = Color(0xFF1E1E26)

// Champagne Gold Palette
val GoldChampagne = Color(0xFFD4AF37)
val GoldLight = Color(0xFFF3E5AB)
val GoldChampagneMuted = Color(0xFFC5A059)
val GoldDark = Color(0xFF997A15)
val GoldGlow = Color(0x33D4AF37)
val GoldSubtleBorder = Color(0x66D4AF37)

// Sophisticated White & Neutral Palette
val SophisticatedWhite = Color(0xFFFDFDFE)
val OffWhite = Color(0xFFE8E8EE)
val TextMuted = Color(0xFF9E9EAF)
val TextDim = Color(0xFF636375)

// Status & Accent Colors
val SuccessEmerald = Color(0xFF2ECC71)
val AlertRuby = Color(0xFFE74C3C)
val WarningAmber = Color(0xFFF39C12)
val InfoSapphire = Color(0xFF3498DB)

// Luxury Gradients
val GoldLinearGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFF3E5AB),
        Color(0xFFD4AF37),
        Color(0xFFA67C1E),
        Color(0xFFE5C378)
    )
)

val GoldDiagonalGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF3E5AB),
        Color(0xFFD4AF37),
        Color(0xFFB8860B)
    )
)

val DarkSurfaceGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1E1E26),
        Color(0xFF121216)
    )
)

val CardGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x333A3A48),
        Color(0x1A14141A)
    )
)
