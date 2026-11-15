package com.kelompok8.lentera.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Aplikasi selalu pakai tema gelap biru tua, jadi tidak ikut dynamic color HP
private val LenteraColorScheme = darkColorScheme(
    primary = LenteraPink,
    secondary = LenteraLime,
    tertiary = LenteraGreen,
    background = NavyDeep,
    surface = NavySurface,
    surfaceVariant = FieldBlue,
    onPrimary = TextLight,
    onSecondary = NavyDeep,
    onTertiary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight,
    onSurfaceVariant = TextLight
)

@Composable
fun LENTERATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LenteraColorScheme,
        typography = Typography,
        content = content
    )
}
