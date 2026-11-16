package com.kelompok8.lentera.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.ui.theme.AvatarPink
import com.kelompok8.lentera.ui.theme.AvatarPurple
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.LenteraPink
import com.kelompok8.lentera.ui.theme.NuesoModerno
import com.kelompok8.lentera.ui.theme.TextLight

// Komponen visual bersama sesuai Figma. Silakan dipakai di halaman masing-masing
// supaya tampilan seluruh aplikasi seragam.

@Composable
fun LenteraBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(color = Color(0xFF0C126B))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(870.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF0A1059),
                            radius = size.minDimension / 2
                        )
                    }
            )
            Box(
                modifier = Modifier
                    .requiredSize(774.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF070B4A),
                            radius = size.minDimension / 2
                        )
                    }
            )
            Box(
                modifier = Modifier
                    .requiredSize(644.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF060A3F),
                            radius = size.minDimension / 2
                        )
                    }
            )
            Box(
                modifier = Modifier
                    .requiredSize(498.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF050938),
                            radius = size.minDimension / 2
                        )
                    }
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

// Sementara masih teks. Kalau logo PNG dari Figma sudah ada, ganti Text pertama dengan Image
@Composable
fun LenteraLogo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "LENTERA",
            color = LenteraLime,
            fontSize = 52.sp,
            fontFamily = NuesoModerno,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Lestarikan Budaya Nusantara!",
            color = TextLight,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = LenteraPink,
            contentColor = TextLight,
            disabledContainerColor = LenteraPink.copy(alpha = 0.6f),
            disabledContentColor = TextLight
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = TextLight,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(text.uppercase(), fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun LinkText(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = TextLight,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textDecoration = TextDecoration.Underline,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    )
}

// Avatar masih pakai emoji. Setelah gambar avatar dari Figma di-export ke res/drawable,
// cukup ubah isi Box di bawah jadi Image(painterResource(...)) sesuai avatarId.
val avatarEmojis = listOf("😎", "🧔", "🧕", "👧")

@Composable
fun AvatarImage(
    avatarId: Int,
    modifier: Modifier = Modifier,
    highlighted: Boolean = true,
    emojiSize: TextUnit = 36.sp
) {
    val shape = RoundedCornerShape(12.dp)
    val background: Brush =
        if (highlighted) Brush.verticalGradient(listOf(AvatarPink, AvatarPurple)) else SolidColor(FieldBlue)
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(1.dp, FieldBorder, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(avatarEmojis.getOrElse(avatarId) { avatarEmojis[0] }, fontSize = emojiSize)
    }
}
