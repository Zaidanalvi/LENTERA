package com.kelompok8.lentera.map

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.IslandCard
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Hilal — halaman Main (pilih pulau untuk bermain)
@Composable
fun PlayScreen(onIslandClick: (String) -> Unit, onLeaderboardClick: () -> Unit) {
    var jawaProgress by remember { mutableIntStateOf(0) }
    var sumatraProgress by remember { mutableIntStateOf(0) }
    var kalimantanProgress by remember { mutableIntStateOf(0) }

    val uid = AppContainer.authRepository.currentUser()?.uid.orEmpty()

    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            val scores = AppContainer.scoreRepository.getBestScores(uid)
            val chapterIds = scores.map { it.chapterId.lowercase() }.distinct()
            jawaProgress = chapterIds.count { it.startsWith("jawa_") }.coerceAtMost(5)
            sumatraProgress = chapterIds.count { it.startsWith("sumatra_") }.coerceAtMost(5)
            kalimantanProgress = chapterIds.count { it.startsWith("kalimantan_") }.coerceAtMost(5)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Bermain", 50, R.drawable.avatar1png)

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Selesaikan Soal\ndan Dapatkan Poin",
                    fontFamily = mPlus1p,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                    color = Color(0xFFDDFF00),
                )
                IconButton(
                    onClick = onLeaderboardClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_leaderboard_24),
                        contentDescription = "Leaderboard",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = Island.JAWA.displayName,
                    progressText = "$jawaProgress/5",
                    isAvailable = true,
                    modifier = Modifier.weight(1f),
                    onIslandClick = { onIslandClick(Island.JAWA.name) }
                )
                IslandCard(
                    islandName = Island.SUMATRA.displayName,
                    progressText = "$sumatraProgress/5",
                    isAvailable = true,
                    modifier = Modifier.weight(1f),
                    onIslandClick = { onIslandClick(Island.SUMATRA.name) }
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = Island.KALIMANTAN.displayName,
                    progressText = "$kalimantanProgress/5",
                    isAvailable = true,
                    modifier = Modifier.weight(1f),
                    onIslandClick = { onIslandClick(Island.KALIMANTAN.name) }
                )
                IslandCard(
                    islandName = "Sulawesi",
                    progressText = "0/5",
                    isAvailable = false,
                    modifier = Modifier.weight(1f),
                    onIslandClick = {}
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = "Sunda Kecil",
                    progressText = "0/5",
                    isAvailable = false,
                    modifier = Modifier.weight(1f),
                    onIslandClick = {}
                )
                IslandCard(
                    islandName = "Papua",
                    progressText = "0/5",
                    isAvailable = false,
                    modifier = Modifier.weight(1f),
                    onIslandClick = {}
                )
            }
        }
    }
}
