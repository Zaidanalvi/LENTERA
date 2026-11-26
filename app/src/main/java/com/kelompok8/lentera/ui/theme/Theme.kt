package com.kelompok8.lentera.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private const val DesignWidthDp = 411f
private const val DesignHeightDp = 860f

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
    val configuration = LocalConfiguration.current
    val baseDensity = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.takeIf { it > 0 }?.toFloat() ?: DesignWidthDp
    val screenHeight = configuration.screenHeightDp.takeIf { it > 0 }?.toFloat() ?: DesignHeightDp
    val uiScale = if (screenWidth < 600f) {
        minOf(screenWidth / DesignWidthDp, screenHeight / DesignHeightDp).coerceIn(0.75f, 1.08f)
    } else {
        1f
    }
    val appDensity = remember(baseDensity.density, uiScale) {
        Density(
            density = baseDensity.density * uiScale,
            fontScale = 1f
        )
    }

    CompositionLocalProvider(LocalDensity provides appDensity) {
        MaterialTheme(
            colorScheme = LenteraColorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun LenteraTheme(content: @Composable () -> Unit) = LENTERATheme(content)


