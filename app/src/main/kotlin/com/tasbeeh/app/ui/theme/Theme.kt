package com.tasbeeh.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark theme — emerald green + black + gold
val DarkBackground = Color(0xFF0A0F0A)
val DarkSurface = Color(0xFF111A11)
val DarkSurfaceVariant = Color(0xFF1C2B1C)
val EmeraldPrimary = Color(0xFF2E7D52)
val EmeraldLight = Color(0xFF4CAF80)
val GoldAccent = Color(0xFFD4AF37)
val GoldLight = Color(0xFFE8CC6A)
val OnDarkPrimary = Color(0xFFFFFFFF)
val OnDarkSurface = Color(0xFFE8F5E9)
val OnDarkSurfaceVariant = Color(0xFFB0BEC5)

// Light theme
val LightBackground = Color(0xFFF1F8F1)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE8F5E9)
val EmeraldPrimaryLight = Color(0xFF1B5E38)
val OnLightPrimary = Color(0xFFFFFFFF)
val OnLightSurface = Color(0xFF1A1A1A)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = OnDarkPrimary,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldAccent,
    onSecondary = Color(0xFF1A1A00),
    secondaryContainer = Color(0xFF2A2200),
    onSecondaryContainer = GoldLight,
    background = DarkBackground,
    onBackground = OnDarkSurface,
    surface = DarkSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,
    outline = EmeraldPrimary,
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = OnLightPrimary,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = EmeraldPrimaryLight,
    secondary = Color(0xFF8B6914),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFF0C0),
    onSecondaryContainer = Color(0xFF5A4000),
    background = LightBackground,
    onBackground = OnLightSurface,
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF3A5A3A),
    outline = EmeraldPrimaryLight,
    error = Color(0xFFB00020)
)

@Composable
fun TasbeehTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TasbeehTypography,
        content = content
    )
}
