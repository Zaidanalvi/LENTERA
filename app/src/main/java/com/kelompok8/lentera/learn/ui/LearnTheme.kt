package com.kelompok8.lentera.learn.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.User
import kotlin.math.abs

internal object LearnColors {
    val Background = Color(0xFF0B0B9B)
    val WaveLight = Color(0x990A0A5C)
    val WaveDark = Color(0xCC070745)
    val Neon = Color(0xFFCFFF1A)
    val CardStart = Color(0xFF8E1FD6)
    val CardEnd = Color(0xFF5A12A6)
    val ProvinceList = Color(0xFF3A1670)
    val Divider = Color(0x22FFFFFF)
    val CardBorder = Color(0x33FFFFFF)
    val Chip = Color(0xFFB794E8)
    val ChipText = Color(0xFF2B0A5E)
    val Green = Color(0xFF1FC03F)
    val CoinPill = Color(0xFF1A1AB0)
    val CoinPillBorder = Color(0xFF4B4BE0)
    val AvatarBorder = Color(0xFF7C6CFF)
    val DetailCard = Color(0xFF0B1344)
    val DetailBorder = Color(0xFF3A3F9E)
    val Chevron = Color(0xFFB79AE0)
}

internal object LearnFonts {
    val Heading: FontFamily = FontFamily.Default
    val Mono: FontFamily = FontFamily.Monospace
}

internal val CardBrush: Brush = Brush.horizontalGradient(
    listOf(LearnColors.CardStart, LearnColors.CardEnd),
)

@Composable
internal fun LearnBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxSize().background(LearnColors.Background)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val light = Path().apply {
                moveTo(0f, h * 0.20f)
                cubicTo(w * 0.30f, h * 0.10f, w * 0.70f, h * 0.32f, w, h * 0.18f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(light, LearnColors.WaveLight)
            val dark = Path().apply {
                moveTo(0f, h * 0.48f)
                cubicTo(w * 0.35f, h * 0.38f, w * 0.65f, h * 0.62f, w, h * 0.44f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(dark, LearnColors.WaveDark)
        }
        content()
    }
}

@DrawableRes
internal fun avatarDrawable(avatarId: Int): Int = when (abs(avatarId) % 4) {
    0 -> R.drawable.avatar1png
    1 -> R.drawable.avatar2png
    2 -> R.drawable.avatar3png
    else -> R.drawable.avatar4png
}

@Composable
internal fun rememberDrawableId(name: String?): Int {
    val context = LocalContext.current
    return remember(name) {
        if (name.isNullOrEmpty()) 0
        else context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}

@Composable
internal fun LearnTopBar(
    title: String?,
    user: User?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        CoinPill(user?.coin ?: 0, Modifier.align(Alignment.CenterStart))
        if (title != null) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        AvatarCircle(user?.avatarId ?: 0, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun CoinPill(coin: Int, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(LearnColors.CoinPill)
            .border(1.dp, LearnColors.CoinPillBorder, shape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.coin),
            contentDescription = "Koin",
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = coin.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AvatarCircle(avatarId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(avatarDrawable(avatarId)),
        contentDescription = "Avatar",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(2.dp, LearnColors.AvatarBorder, CircleShape),
    )
}

internal fun categoryEmoji(category: GlossaryCategory): String = when (category) {
    GlossaryCategory.PAKAIAN_ADAT -> "👘"
    GlossaryCategory.RUMAH_ADAT -> "🏠"
    GlossaryCategory.MAKANAN -> "🍲"
    GlossaryCategory.TARIAN -> "💃"
    GlossaryCategory.LAGU -> "🎵"
}
