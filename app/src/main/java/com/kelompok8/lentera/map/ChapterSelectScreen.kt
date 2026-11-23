package com.kelompok8.lentera.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.Chapter
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.ui.theme.mPlus1p
import com.kelompok8.lentera.ui.theme.museoModerno

// PJ: Hilal — pilih chapter 1-5, tampilkan progres tiap chapter
@Composable
fun ChapterSelectScreen(
    island: String,
    onChapterClick: (String) -> Unit
) {
    val currentIsland = remember(island) {
        Island.entries.firstOrNull {
            it.name.equals(island, ignoreCase = true) || it.displayName.equals(island, ignoreCase = true)
        } ?: Island.JAWA
    }

    val chapters = remember(currentIsland) {
        getChaptersForIsland(currentIsland)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
        ) {
            Header(
                title = "Pulau ${currentIsland.displayName}",
                countCoin = currentIsland.coin,
                pfp = R.drawable.avatar1png
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 10.dp)
                .padding(top = 90.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pilih Level Permainan",
                fontFamily = museoModerno,
                fontSize = 24.sp,
                color = Color(0xFFEEFF00),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Selesaikan 10 soal kuis di setiap chapter untuk kumpulkan poin!",
                fontFamily = mPlus1p,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color(0xFFB0B7DB),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(chapters) { chapter ->
                    ChapterGameCard(
                        chapter = chapter,
                        onChapterClick = onChapterClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterGameCard(
    chapter: Chapter,
    onChapterClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF8A1DDE), RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF131A6B), Color(0xFF090D4D))))
            .clickable { onChapterClick(chapter.id) }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column {
                    Text(
                        text = "CHAPTER ${chapter.number}",
                        fontFamily = museoModerno,
                        fontSize = 18.sp,
                        color = Color(0xFFEEFF00),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chapter.title,
                        fontFamily = mPlus1p,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "10 Soal Kuis",
                        fontFamily = mPlus1p,
                        fontSize = 11.sp,
                        color = Color(0xFF9AA3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            ElevatedButton(
                onClick = { onChapterClick(chapter.id) },
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color(0xFF8951B5)),
                modifier = Modifier
                    .width(76.dp)
                    .height(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Color(0xFF9F56D8), Color(0xFF57019A)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PLAY",
                        fontFamily = mPlus1p,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun getChaptersForIsland(island: Island): List<Chapter> {
    val islandPrefix = island.name.lowercase()
    val titles = when (island) {
        Island.JAWA -> listOf(
            "Tantangan Budaya Jawa 1",
            "Tantangan Budaya Jawa 2",
            "Tantangan Budaya Jawa 3",
            "Tantangan Budaya Jawa 4",
            "Tantangan Budaya Jawa 5"
        )
        Island.SUMATRA -> listOf(
            "Tantangan Budaya Sumatra 1",
            "Tantangan Budaya Sumatra 2",
            "Tantangan Budaya Sumatra 3",
            "Tantangan Budaya Sumatra 4",
            "Tantangan Budaya Sumatra 5"
        )
        Island.KALIMANTAN -> listOf(
            "Tantangan Budaya Kalimantan 1",
            "Tantangan Budaya Kalimantan 2",
            "Tantangan Budaya Kalimantan 3",
            "Tantangan Budaya Kalimantan 4",
            "Tantangan Budaya Kalimantan 5"
        )
    }
    return (1..5).map { number ->
        Chapter(
            id = "${islandPrefix}_$number",
            island = island,
            number = number,
            title = titles.getOrElse(number - 1) { "Chapter $number" }
        )
    }
}

@Preview
@Composable
fun ChapterSelectScreenPreview() {
    ChapterSelectScreen(
        island = "JAWA",
        onChapterClick = {}
    )
}
