package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxDriveDarkColorScheme = darkColorScheme(
    primary = LuxGoldPrimary,
    onPrimary = Color(0xFF121006),
    primaryContainer = LuxGoldContainer,
    onPrimaryContainer = LuxGoldOnContainer,
    secondary = LuxGoldSecondary,
    onSecondary = Color(0xFF121006),
    secondaryContainer = Color(0xFF24221A),
    onSecondaryContainer = Color(0xFFEADBBA),
    tertiary = LuxInfoBlue,
    onTertiary = Color(0xFF001F2A),
    tertiaryContainer = Color(0xFF004D65),
    onTertiaryContainer = Color(0xFFBBE9FF),
    background = LuxDarkBackground,
    onBackground = LuxTextPrimary,
    surface = LuxSurfaceDark,
    onSurface = LuxTextPrimary,
    surfaceVariant = LuxSurfaceCard,
    onSurfaceVariant = LuxTextSecondary,
    outline = LuxSurfaceBorder,
    outlineVariant = Color(0xFF33394B),
    error = LuxYoutubeRed,
    onError = Color.White
)

@Composable
fun LuxDriveTheme(
    content: @Composable () -> Unit
) {
    // We enforce the intentional dark luxury automotive studio identity across all devices
    MaterialTheme(
        colorScheme = LuxDriveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
