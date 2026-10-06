package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = Color(0xFF00391F),
    primaryContainer = BengalGreen,
    onPrimaryContainer = Color(0xFFA7F3D0),

    secondary = CyberGold,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = CyberAmber,
    onSecondaryContainer = Color(0xFFFFF3CD),

    tertiary = CrimsonSun,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF8C1D18),
    onTertiaryContainer = Color(0xFFFFDAD6),

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = DarkBorder,
    outlineVariant = Color(0xFF2C436D)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Gaming apps look best in dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}
