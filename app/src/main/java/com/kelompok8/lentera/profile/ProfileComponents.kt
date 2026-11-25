package com.kelompok8.lentera.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kelompok8.lentera.R
import kotlin.math.cos
import kotlin.math.sin

// Warna khusus halaman profil (sisanya pakai warna di ui/theme)
val ProfilePanel = Color(0xFF0C1170)
val ProfilePanelBorder = Color(0xFF262E9E)
val ProfileMuted = Color(0xFFB9BEF2)
val ProfileTrack = Color(0xFF1E2590)
val MedalGold = Color(0xFFF7B500)
val MedalGoldLight = Color(0xFFFFE680)
val MedalGoldDark = Color(0xFFC98200)
val HeaderPill = Color(0xFF1620A7)
val CardPurpleTop = Color(0xFFA23BF2)
val CardPurpleMid = Color(0xFF6A1CC9)
val CardPurpleBottom = Color(0xFF4A12A3)

@Composable
fun CircleIconButton(iconRes: Int, contentDescription: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(HeaderPill)
            .border(1.dp, Color(0x38FFFFFF), CircleShape)
            .clickable(onClickLabel = contentDescription, role = Role.Button, onClick = onClick)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier
                .size(16.dp)
                .offset(x = if (iconRes == BackIcon) 3.dp else 0.dp)
        )
    }
}

@Composable
fun EditCircleButton(enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(HeaderPill)
            .border(1.dp, Color(0x38FFFFFF), CircleShape)
            .clickable(enabled = enabled, onClickLabel = "Ubah profil", role = Role.Button, onClick = onClick)
    ) {
        PencilGlyph(color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f))
    }
}

@Composable
fun CloseCircleButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(HeaderPill)
            .border(1.dp, Color(0x38FFFFFF), CircleShape)
            .clickable(onClickLabel = "Tutup", role = Role.Button, onClick = onClick)
    ) {
        CloseGlyph(color = Color.White)
    }
}

@Composable
private fun PencilGlyph(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.22f, h * 0.60f)
            lineTo(w * 0.70f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.30f)
            lineTo(w * 0.40f, h * 0.78f)
            close()
        }
        drawPath(path = path, color = color, style = stroke)
        drawLine(
            color = color,
            start = Offset(w * 0.58f, h * 0.24f),
            end = Offset(w * 0.76f, h * 0.42f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CloseGlyph(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val w = size.width
        val h = size.height
        val sw = 2.dp.toPx()
        drawLine(
            color = color,
            start = Offset(w * 0.15f, h * 0.15f),
            end = Offset(w * 0.85f, h * 0.85f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.85f, h * 0.15f),
            end = Offset(w * 0.15f, h * 0.85f),
            strokeWidth = sw,
            cap = StrokeCap.Round
        )
    }
}

// Bar kemajuan sederhana; dibuat sendiri karena API LinearProgressIndicator
// sering berubah antar versi Material 3
@Composable
fun ProgressBar(fraction: Float, color: Color, trackColor: Color, height: Dp = 8.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

@Composable
fun MedalBadge(earned: Boolean, modifier: Modifier = Modifier) {
    if (earned) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(MedalGoldLight, MedalGold, MedalGoldDark)))
                .border(2.5.dp, Color(0xFFFFD84D), CircleShape)
        ) {
            StarGlyph(color = Color(0xFF7A4A00))
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Color(0xFF141B82))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.8.dp.toPx()
                drawCircle(
                    color = Color(0xFF3B45DB),
                    radius = (size.minDimension - strokeWidth) / 2f,
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )
            }
            LockGlyph(color = Color(0xFF8F96E0))
        }
    }
}

@Composable
private fun StarGlyph(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerR = size.minDimension * 0.46f
        val innerR = outerR * 0.45f
        val path = Path()
        for (i in 0 until 10) {
            val r = if (i % 2 == 0) outerR else innerR
            val angle = Math.toRadians((-90.0 + i * 36.0))
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun LockGlyph(color: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.28f, h * 0.08f),
            size = Size(w * 0.44f, h * 0.56f),
            style = stroke
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.18f, h * 0.44f),
            size = Size(w * 0.64f, h * 0.48f),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = stroke
        )
    }
}

// Ikon panah kembali yang sudah ada di drawable
val BackIcon = R.drawable.baseline_arrow_back_ios_24
