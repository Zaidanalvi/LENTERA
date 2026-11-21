package com.kelompok8.lentera.leaderboard

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Hilal — leaderboard keseluruhan
@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    isLoading: Boolean = false,
    currentPage: Int = 1,
    canGoPrev: Boolean = false,
    canGoNext: Boolean = false,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 30.dp)
        ) {
            Header("Papan Peringkat", 50, R.drawable.avatar1png)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(350.dp)
                    .height(620.dp)
                    .offset(y = (-8).dp)
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
                            text = "Tidak ada data",
                            color = Color.White,
                            fontFamily = mPlus1p,
                            fontSize = 16.sp
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
            Box(
            modifier = Modifier
                .width(180.dp)
                .size(50.dp)
                .offset(y = (-35).dp)
                .align(Alignment.BottomCenter)
            ) {
                PinkButton(
                    text = "Kembali",
                    onClick = onBack,
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
        )
        Text(
            text = "Nama Pemain",
            color = Color(0xFF00D0FF),
            fontSize = 14.sp,
            fontFamily = mPlus1p,
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(35.dp)
        )

        Row(
            modifier = Modifier
                .weight(0.44f)
                .padding(start = 6.dp)
                .offset(x = 11.dp),
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

            Text(
                text = entry.name,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = entry.points.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(0.22f)
                .padding(end = 8.dp)
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
