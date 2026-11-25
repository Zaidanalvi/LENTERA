package com.kelompok8.lentera.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

// PJ: Hilal — homepage dengan peta Jawa, Sumatra, Kalimantan
@Composable
fun HomeScreen(onIslandClick: (String) -> Unit) {
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

    // Pulau paling aktif untuk ditampilkan di banner utama
    val (activeIsland, activeProgress) = remember(jawaProgress, sumatraProgress, kalimantanProgress) {
        when {
            sumatraProgress > jawaProgress && sumatraProgress >= kalimantanProgress -> Island.SUMATRA to sumatraProgress
            kalimantanProgress > jawaProgress -> Island.KALIMANTAN to kalimantanProgress
            else -> Island.JAWA to jawaProgress
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Beranda", 50, R.drawable.avatar1png)

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeBanner(
                activeIsland = activeIsland,
                activeProgress = activeProgress,
                onContinuePlay = { onIslandClick(activeIsland.name) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pilihan Peta Nusantara",
                fontFamily = mPlus1p,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(14.dp))

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

@Composable
fun WelcomeBanner(
    activeIsland: Island,
    activeProgress: Int,
    onContinuePlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFA568E3), RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFC21AFF), Color(0xFF102999))))
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painterResource(R.drawable.banner_image),
            contentDescription = "Banner Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .height(160.dp)
                .offset(x = 20.dp, y = 15.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.65f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Selamat Datang di LENTERA",
                fontFamily = mPlus1p,
                fontSize = 13.sp,
                color = Color(0xFFFFFFFF),
            )
            Text("Mari belajar\n" +
                    "Budaya Nusantara\n" +
                    "dengan cara yang\n" +
                    "asik!",
                fontFamily = mPlus1p,
                fontSize = 20.sp,
                color = Color(0xFFEEFF00)
            )

            Button(
                onClick = onContinuePlay,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1F6B)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "LANJUTKAN BERMAIN",
                    fontFamily = mPlus1p,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
