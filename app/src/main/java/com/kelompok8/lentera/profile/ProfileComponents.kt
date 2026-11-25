package com.kelompok8.lentera.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R

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
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF1620A7))
            .border(1.dp, Color(0x38FFFFFF), CircleShape)
            .clickable(onClickLabel = "Kembali", role = Role.Button, onClick = onClick)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier
                .size(16.dp)
                .offset(x = 3.dp)
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
                .border(3.dp, Color(0xFFFFD84D), CircleShape)
        ) {
            Text("★", color = Color(0xFF7A4A00), fontSize = 28.sp)
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A2190))
                .border(2.dp, Color(0xFF3B45DB), CircleShape)
        ) {
            LockGlyph(color = Color(0xFF8F96E0))
        }
    }
}

@Composable
private fun LockGlyph(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.28f, h * 0.08f),
            size = Size(w * 0.44f, h * 0.56f),
            style = Stroke(width = 2.dp.toPx())
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.16f, h * 0.42f),
            size = Size(w * 0.68f, h * 0.5f),
            cornerRadius = CornerRadius(3.dp.toPx())
        )
    }
}

// Ikon panah kembali yang sudah ada di drawable
val BackIcon = R.drawable.baseline_arrow_back_ios_24
