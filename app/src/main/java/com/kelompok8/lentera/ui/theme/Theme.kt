package com.kelompok8.lentera.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Aplikasi selalu pakai tema gelap biru tua, jadi tidak ikut dynamic color HP
private val LenteraColorScheme = darkColorScheme(
    primary = LenteraPurple,
    secondary = LenteraPink,
    tertiary = LenteraGreen,
    background = NavyDark,
    surface = NavySurface,
    onPrimary = TextLight,
    onSecondary = TextLight,
    onTertiary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun LENTERATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LenteraColorScheme,
        typography = Typography,
        content = content
    )
}
