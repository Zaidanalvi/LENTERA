package com.kelompok8.lentera.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.LeaderboardEntry
import com.kelompok8.lentera.core.model.LeaderboardFilter
import com.kelompok8.lentera.core.model.OriginRegion
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Hilal — leaderboard keseluruhan
@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    isLoading: Boolean = false,
    currentPage: Int = 1,
    canGoPrev: Boolean = false,
    canGoNext: Boolean = false,
    selectedFilter: LeaderboardFilter = LeaderboardFilter.Global,
    userProvince: String = "",
    onSelectFilter: (LeaderboardFilter) -> Unit = {},
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Papan Peringkat", 50, R.drawable.avatar1png)

        Spacer(Modifier.height(12.dp))

        LeaderboardFilterRow(
            selectedFilter = selectedFilter,
            userProvince = userProvince,
            onSelectFilter = onSelectFilter
        )

        Spacer(Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = Color(0xFF68749E), shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xFF060D25))
        ) {
            LeaderBoardHeader()

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00D0FF))
                }
            } else if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedFilter != LeaderboardFilter.Global) {
                            "Belum ada pemain dari wilayah ini"
                        } else {
                            "Tidak ada data"
                        },
                        color = Color.White,
                        fontFamily = mPlus1p,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(entries) { entry ->
                        LeaderboardRow(entry = entry)
                    }
                }
            }

            PaginationBar(
                page = currentPage,
                canGoPrev = canGoPrev,
                canGoNext = canGoNext,
                onPrev = onPrevPage,
                onNext = onNextPage
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(190.dp)
                .height(50.dp)
        ) {
            PinkButton(
                text = "Kembali",
                onClick = onBack,
            )
        }
    }
}

@Composable
private fun LeaderboardFilterRow(
    selectedFilter: LeaderboardFilter,
    userProvince: String,
    onSelectFilter: (LeaderboardFilter) -> Unit
) {
    val options = buildList<Pair<String, LeaderboardFilter>> {
        add("Global" to LeaderboardFilter.Global)
        if (userProvince.isNotBlank()) {
            add("Daerahku ($userProvince)" to LeaderboardFilter.MyProvince(userProvince))
        }
        OriginRegion.entries.forEach { region ->
            val label = if (region == OriginRegion.LAINNYA) "Lainnya" else region.displayName
            add(label to LeaderboardFilter.ByRegion(region))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (label, filter) ->
            val selected = selectedFilter == filter
            val shape = RoundedCornerShape(50)
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (selected) Color(0xFF281B78) else Color(0xFF0A1248))
                    .border(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) LenteraLime else Color(0x55FFFFFF),
                        shape = shape
                    )
                    .clickable { onSelectFilter(filter) }
                    .padding(horizontal = 13.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    color = if (selected) LenteraLime else Color(0xFFD6DCF5),
                    fontFamily = mPlus1p,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LeaderBoardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF02031C))
            .padding(vertical = 20.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "No.",
            color = Color(0xFF00D0FF),
            fontSize = 14.sp,
            fontFamily = mPlus1p,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Nama Pemain",
            color = Color(0xFF00D0FF),
            fontSize = 14.sp,
            fontFamily = mPlus1p,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = (-10).dp)
        )
        Text(
            text = "",
            color = Color(0xFF00D0FF),
            fontSize = 14.sp,
            fontFamily = mPlus1p,
        )
        Text(
            text = "Points",
            color = Color(0xFF00D0FF),
            fontSize = 14.sp,
            fontFamily = mPlus1p,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.rank.toString(),
            color = Color.White,
            fontFamily = mPlus1p,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(35.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(44.dp)) {
                Image(
                    painter = painterResource(entry.avatarRes),
                    contentDescription = entry.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF00D0FF), RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    color = Color.White,
                    fontFamily = mPlus1p,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (entry.location.isNotBlank()) {
                    Text(
                        text = entry.location,
                        color = Color(0xFF8CA5E8),
                        fontFamily = mPlus1p,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Text(
            text = entry.points.toString(),
            color = Color.White,
            fontFamily = mPlus1p,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp, end = 10.dp)
        )
    }
}

@Composable
private fun PaginationBar(
    page: Int,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrev,
            enabled = canGoPrev,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                contentDescription = "Halaman Sebelumnya",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Surface(
            color = Color(0xFF050714),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF283A66))
        ) {
            Text(
                text = page.toString(),
                color = Color.White,
                fontFamily = mPlus1p,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        IconButton(
            onClick = onNext,
            enabled = canGoNext,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                contentDescription = "Halaman Berikutnya",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewLeaderBoard() {
    LeaderboardScreen(
        entries = listOf(
            LeaderboardEntry("1", 1, "Player One", 1000),
            LeaderboardEntry("2", 2, "Player Two", 950)
        ),
        isLoading = false,
        currentPage = 1,
        canGoPrev = false,
        canGoNext = true,
        onPrevPage = {},
        onNextPage = {},
        onBack = {}
    )
}
