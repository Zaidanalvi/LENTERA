package com.kelompok8.lentera.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.IslandCard
import com.kelompok8.lentera.ui.theme.museoModerno

// PJ: Hilal — halaman Main (pilih pulau untuk bermain)
@Composable
fun PlayScreen(onIslandClick: (String) -> Unit, onLeaderboardClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 30.dp)
        ) {
            Header("Bermain", 50, R.drawable.avatar1png)
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .offset(y = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selesaikan Soal\n" + "dan Dapatkan Poin",
                    fontFamily = museoModerno,
                    fontSize = 27.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFFDDFF00),
                )
                IconButton(
                    onClick = onLeaderboardClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_leaderboard_24),
                        contentDescription = "Leaderboard",
                        tint = Color(0xFFFFFFFF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = Island.JAWA.displayName,
                    coin = Island.JAWA.coin,
                    onIslandClick = { onIslandClick(Island.JAWA.name) }
                )
                IslandCard(
                    islandName = Island.SUMATRA.displayName,
                    coin = Island.SUMATRA.coin,
                    onIslandClick = { onIslandClick(Island.SUMATRA.name) }
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = Island.KALIMANTAN.displayName,
                    coin = Island.KALIMANTAN.coin,
                    onIslandClick = { onIslandClick(Island.KALIMANTAN.name) }
                )
                IslandCard(
                    islandName = "Sulawesi",
                    coin = 50,
                    onIslandClick = {}
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IslandCard(
                    islandName = "Sunda Kecil",
                    coin = 50,
                    onIslandClick = {}
                )
                IslandCard(
                    islandName = "Papua",
                    coin = 50,
                    onIslandClick = {}
                )
            }
        }
    }
}
