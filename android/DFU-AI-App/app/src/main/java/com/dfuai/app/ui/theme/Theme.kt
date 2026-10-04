package com.dfuai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = DFUPrimary,
    onPrimary = DFUSurface,
    primaryContainer = Color(0xFFD4EEF5),
    onPrimaryContainer = DFUPrimaryDark,
    secondary = DFUSecondary,
    onSecondary = DFUSurface,
    background = DFUBackground,
    onBackground = DFUTextPrimary,
    surface = DFUSurface,
    onSurface = DFUTextPrimary,
    error = DFUDanger,
    onError = DFUSurface
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8DD4E8),
    onPrimary = DFUPrimaryDark,
    primaryContainer = Color(0xFF145A70),
    onPrimaryContainer = Color(0xFFD4EEF5),
    secondary = Color(0xFFA5D6D6),
    onSecondary = Color(0xFF123536),
    background = Color(0xFF10171C),
    onBackground = Color(0xFFE7EEF2),
    surface = Color(0xFF182127),
    onSurface = Color(0xFFE7EEF2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun DFUAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = DFUTypography,
        content = content
    )
}