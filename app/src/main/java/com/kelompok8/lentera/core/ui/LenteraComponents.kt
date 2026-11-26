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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.model.Island
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
    countCoin: Int = 0,
    pfp: Int
) {
    // Kalau sudah login, medali dan avatar diambil dari profil asli, bukan nilai bawaan pemanggil
    val sessionUser by UserSession.profile.collectAsState()
    val medalCount by UserSession.medalCount.collectAsState()
    val avatarRes = sessionUser?.let { avatarEmojis.getOrElse(it.avatarId) { pfp } } ?: pfp
    val openProfile = LocalOpenProfile.current

    Row(
        modifier = Modifier.fillMaxWidth(0.88f),
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
            MedalIcon(Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = "$medalCount/${Island.entries.size}", color = Color(0xFFFFFFFF), fontFamily = mPlus1p, fontSize = 16.sp)
        }
        Text(text = title, color = Color(0xFFFFFFFF), fontFamily = mPlus1p, fontSize = 20.sp)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xFF1620A7), shape = RoundedCornerShape(100.dp))
                .border(1.dp, color = Color(0x38FFFFFF), shape = RoundedCornerShape(100.dp))
                .then(
                    if (openProfile != null) Modifier.clickable(onClickLabel = "Buka profil", onClick = openProfile)
                    else Modifier
                )
        ) {
            Image(painterResource(
                avatarRes),
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
    progressText: String = "0/5",
    isAvailable: Boolean = true,
    modifier: Modifier = Modifier.width(170.dp),
    onIslandClick: () -> Unit
) {
    val cardBackground = if (isAvailable) {
        Brush.linearGradient(listOf(Color(0xFF8A1DDE), Color(0xFF4B1078)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF35394E), Color(0xFF1E2133)))
    }
    val borderColor = if (isAvailable) Color(0xFFA568E3) else Color(0xFF4E5370)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(162.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(cardBackground)
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Pulau $islandName",
                fontFamily = mPlus1p,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = if (isAvailable) Color.White else Color(0xFFA5ABC7)
            )

            if (isAvailable) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = progressText,
                        fontFamily = museoModerno,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEEFF00)
                    )
                    Text(
                        text = "CHAPTER",
                        fontFamily = mPlus1p,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDCDFEF)
                    )
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF282B3D))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "COMING SOON",
                        fontFamily = mPlus1p,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        color = Color(0xFF8D93B4)
                    )
                }
            }

            ElevatedButton(
                onClick = { if (isAvailable) onIslandClick() },
                enabled = isAvailable,
                shape = RoundedCornerShape(7.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                border = BorderStroke(
                    1.dp,
                    if (isAvailable) Color(0xFF8951B5) else Color(0xFF42465F)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isAvailable) {
                                Brush.linearGradient(listOf(Color(0xFF9F56D8), Color(0xFF57019A)))
                            } else {
                                SolidColor(Color(0xFF26293A))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAvailable) "PLAY" else "LOCKED",
                        fontFamily = mPlus1p,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAvailable) Color.White else Color(0xFF707696)
                    )
                }
            }
        }
    }
}

@Composable
fun IslandCard(
    islandName: String,
    coin: Int,
    onIslandClick: () -> Unit
) {
    IslandCard(
        islandName = islandName,
        progressText = "0/5",
        isAvailable = true,
        onIslandClick = onIslandClick
    )
}

@Preview
@Composable
fun IslandCardPreview() {
    IslandCard(
        islandName = "Jawa",
        progressText = "2/5",
        isAvailable = true,
        onIslandClick = {}
    )
}