package com.kelompok8.lentera.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.model.User
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.core.ui.avatarEmojis
import com.kelompok8.lentera.ui.theme.AvatarPink
import com.kelompok8.lentera.ui.theme.AvatarPurple
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.LenteraPink
import com.kelompok8.lentera.ui.theme.mPlus1p
import com.kelompok8.lentera.ui.theme.museoModerno

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    // Data dari UserSession dipakai supaya hasil "Ubah profil" langsung terlihat saat kembali
    val sessionUser by UserSession.profile.collectAsState()
    val user = sessionUser ?: state.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleIconButton(BackIcon, "Kembali", onBack)
            Text(
                "Profil",
                color = Color.White,
                fontFamily = mPlus1p,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onEdit, enabled = user != null) {
                Text("Ubah", color = LenteraLime, fontFamily = mPlus1p, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        when {
            state.isLoading && user == null -> Box(
                Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = LenteraLime) }

            user == null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    state.errorMessage ?: "Profil belum bisa dimuat.",
                    color = Color.White,
                    fontFamily = mPlus1p,
                    textAlign = TextAlign.Center
                )
                PinkButton(text = "Coba lagi", onClick = viewModel::load)
            }

            else -> {
                ExplorerCard(user = user, state = state)
                StatsRow(state)
                MedalSection(state.islands)
                IslandProgressSection(state.islands, isLoading = state.isLoading)
                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        onLoggedOut()
                    },
                    border = BorderStroke(1.dp, LenteraPink),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Keluar dari akun", color = Color(0xFFFF6E9C), fontFamily = mPlus1p, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// Versi besar dari kartu di halaman "Akun Anda Berhasil Dibuat"
@Composable
private fun ExplorerCard(user: User, state: ProfileUiState) {
    val level = state.level
    val cardShape = RoundedCornerShape(22.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Brush.linearGradient(listOf(CardPurpleTop, CardPurpleMid, CardPurpleBottom)))
            .border(1.dp, Color(0x47FFFFFF), cardShape)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.verticalGradient(listOf(AvatarPink, AvatarPurple)))
                    .border(2.dp, Color(0x99FFFFFF), RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(avatarEmojis.getOrElse(user.avatarId) { avatarEmojis[0] }),
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(user.name, color = Color.White, fontFamily = museoModerno, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, lineHeight = 28.sp)
                Text(
                    listOf(user.gender, user.location).filter { it.isNotBlank() }.joinToString(", "),
                    color = Color(0xFFF1E6FF),
                    fontFamily = mPlus1p,
                    fontSize = 14.sp
                )
                Text(
                    level.name,
                    color = Color(0xFF1B0B45),
                    fontFamily = mPlus1p,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LenteraLime)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x73050838))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${state.totalPoints} poin", color = Color.White, fontFamily = mPlus1p, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    if (level.nextAt != null) "${level.nextAt - state.totalPoints} poin lagi menuju ${level.nextName}"
                    else "Level tertinggi tercapai",
                    color = Color(0xFFE3D3FF),
                    fontFamily = mPlus1p,
                    fontSize = 12.sp
                )
            }
            ProgressBar(level.progress(state.totalPoints), LenteraLime, Color(0x2EFFFFFF), height = 10.dp)
        }
    }
}

@Composable
private fun StatsRow(state: ProfileUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        StatTile("${state.totalPoints}", null, "Total poin", LenteraLime, Modifier.weight(1f))
        StatTile("${state.chaptersPlayed}", "/15", "Chapter tamat", Color.White, Modifier.weight(1f))
        StatTile("${state.medalCount}", "/3", "Medali", MedalGold, Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(value: String, suffix: String?, label: String, color: Color, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ProfilePanel)
            .border(1.dp, ProfilePanelBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = color)) { append(value) }
                if (suffix != null) withStyle(SpanStyle(color = ProfileMuted, fontSize = 15.sp)) { append(suffix) }
            },
            fontFamily = museoModerno,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        )
        Text(label, color = ProfileMuted, fontFamily = mPlus1p, fontSize = 12.sp)
    }
}

@Composable
private fun MedalSection(islands: List<IslandProgress>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Medali pulau")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Island.entries.forEach { island ->
                val progress = islands.find { it.island == island }
                val earned = progress?.hasMedal == true
                val shape = RoundedCornerShape(18.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape)
                        .background(ProfilePanel)
                        .border(1.dp, if (earned) MedalGold else ProfilePanelBorder, shape)
                        .padding(horizontal = 6.dp, vertical = 14.dp)
                ) {
                    MedalBadge(earned)
                    Text(island.displayName, color = Color.White, fontFamily = mPlus1p, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Text(
                        if (earned) "Sudah didapat" else "${progress?.perfectChapters ?: 0} dari 5 sempurna",
                        color = if (earned) Color(0xFFFFD84D) else ProfileMuted,
                        fontFamily = mPlus1p,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun IslandProgressSection(islands: List<IslandProgress>, isLoading: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle("Progres tiap pulau")
        if (isLoading && islands.isEmpty()) {
            CircularProgressIndicator(color = LenteraLime, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        islands.forEach { progress ->
            val shape = RoundedCornerShape(16.dp)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(ProfilePanel)
                    .border(1.dp, ProfilePanelBorder, shape)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("Pulau ${progress.island.displayName}", color = Color.White, fontFamily = mPlus1p, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${progress.chaptersPlayed}/5 chapter, ${progress.points}/$MAX_POINTS_PER_ISLAND poin",
                        color = ProfileMuted,
                        fontFamily = mPlus1p,
                        fontSize = 12.sp
                    )
                }
                ProgressBar(
                    fraction = progress.points / MAX_POINTS_PER_ISLAND.toFloat(),
                    color = if (progress.hasMedal) MedalGold else LenteraLime,
                    trackColor = ProfileTrack
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontFamily = mPlus1p, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
}
