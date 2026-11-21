package com.kelompok8.lentera.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.ui.theme.AvatarPink
import com.kelompok8.lentera.ui.theme.AvatarPurple
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.LenteraPink
import com.kelompok8.lentera.ui.theme.museoModerno
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p

// Komponen visual bersama sesuai Figma. Silakan dipakai di halaman masing-masing
// supaya tampilan seluruh aplikasi seragam.

@Composable
fun LenteraBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.background(color = Color(0xFF0C126B)).fillMaxSize(),
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
            fontSize = 60.sp,
            fontFamily = museoModerno,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            modifier = Modifier.offset(y = (-20).dp),
            text = "Lestarikan Budaya Nusantara!",
            color = TextLight,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = mPlus1p
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
        fontFamily = mPlus1p,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
    )
}

// Avatar masih pakai emoji. Setelah gambar avatar dari Figma di-export ke res/drawable,
// cukup ubah isi Box di bawah jadi Image(painterResource(...)) sesuai avatarId.
val avatarEmojis = listOf(
    R.drawable.avatar1png,
    R.drawable.avatar2png,
    R.drawable.avatar3png,
    R.drawable.avatar4png
)

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
        Image(
            painterResource(avatarEmojis.getOrElse(avatarId) { avatarEmojis[0] }),
            contentDescription = null,
            Modifier.size(60.dp)
        )
    }
}

@Composable
fun Header(
    title: String,
    countCoin: Int,
    pfp: Int
) {
    Row(
        modifier = Modifier.width(347.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(Color(0xFF1620A7), shape = RoundedCornerShape(20.dp))
                .border(1.dp, color = Color(0x38FFFFFF), shape = RoundedCornerShape(20.dp))
                .padding(vertical = 3.dp, horizontal = 10.dp)
        ) {
            Image(painterResource(R.drawable.coin), contentDescription = "Coin", modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text = countCoin.toString(), color = Color(0xFFFFFFFF), fontFamily = mPlus1p, fontSize = 16.sp)
        }
        Text(text = title, color = Color(0xFFFFFFFF), fontFamily = mPlus1p, fontSize = 20.sp)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF1620A7), shape = RoundedCornerShape(100.dp))
                .border(1.dp, color = Color(0x38FFFFFF), shape = RoundedCornerShape(100.dp))
        ) {
            Image(painterResource(
                pfp),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(31.dp)
                    .clip(RoundedCornerShape(100.dp))
            )
        }
    }
}

@Composable
fun IslandCard(
    islandName: String,
    coin: Int,
    onIslandClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(174.35.dp)
            .height(168.01.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFa568e3), RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF8A1DDE), Color(0xFF4B1078))))
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Pulau $islandName",
                fontFamily = mPlus1p,
                fontSize = if (islandName.length <= 5) 20.sp else 15.sp,
                color = Color(0xFFFFFFFF)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.width(100.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = "Coin",
                    modifier = Modifier.size(41.dp)
                )
                Text("$coin",
                    fontFamily = mPlus1p,
                    fontSize = 40.sp,
                    color = Color(0xFFFFFFFF)
                )
            }
            ElevatedButton(
                onClick = onIslandClick,
                shape = RoundedCornerShape(7.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                border = BorderStroke(1.dp, Color(0xFF8951b5)),
                modifier = Modifier
                    .width(150.58.dp)
                    .height(36.46.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Color(0xFF9f56d8), Color(0xFF57019A)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PLAY",
                        fontFamily = mPlus1p,
                        fontSize = 16.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun IslandCardPreview() {
    IslandCard(
        "Jawa",
        50,
        onIslandClick = {}
    )
}