package com.kelompok8.lentera.home

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.IslandCard
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Hilal — homepage dengan peta Jawa, Sumatra, Kalimantan
@Composable
fun HomeScreen(onIslandClick: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 30.dp)
        ) {
            Header("Beranda", 50, R.drawable.avatar1png)
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .offset(y = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeBanner()
            Spacer(modifier = Modifier.height(35.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pilihan Peta Nusantara",
                    fontFamily = mPlus1p,
                    fontSize = 20.sp,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .align(Alignment.Start))
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
                    islandName = "Papua",
                    coin = 50,
                    onIslandClick = {}
                )
            }
        }
//        Island.entries.forEach { island ->
//            Button(onClick = { onIslandClick(island.name) }) { Text(island.displayName) }
//        }
    }
}

@Composable
fun WelcomeBanner() {
    Box(
        modifier = Modifier
            .width(365.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFa568e3), RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFC21AFF), Color(0xFF102999))))
            .padding(15.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painterResource(R.drawable.banner_image),
            contentDescription = "Banner Image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(92.dp, 20.dp)
                .fillMaxSize()
        )
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
            color = Color(0xFFEEFF00),
            modifier = Modifier
                .offset(y = 30.dp)
        )
    }
}
