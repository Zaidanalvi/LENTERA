package com.kelompok8.lentera.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private val MedalLight = Color(0xFFFFE680)
private val MedalMid = Color(0xFFF7B500)
private val MedalDark = Color(0xFFC98200)
private val MedalRim = Color(0xFFFFD84D)
private val MedalStar = Color(0xFF7A4A00)

@Composable
fun MedalIcon(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(MedalLight, MedalMid, MedalDark)))
            .border(1.dp, MedalRim, CircleShape)
            .semantics { contentDescription = "Medali" }
    ) {
        Canvas(Modifier.fillMaxSize(0.62f)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outer = size.minDimension / 2f
            val inner = outer * 0.45f
            val star = Path().apply {
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) outer else inner
                    val angle = Math.toRadians(-90.0 + i * 36.0)
                    val x = cx + (r * cos(angle)).toFloat()
                    val y = cy + (r * sin(angle)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(star, MedalStar)
        }
    }
}

@Preview
@Composable
private fun MedalIconPreview() {
    MedalIcon(Modifier.size(40.dp))
}
