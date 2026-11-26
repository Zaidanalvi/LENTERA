package com.kelompok8.lentera.score

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.model.ChapterResult
import com.kelompok8.lentera.core.ui.Header
import com.kelompok8.lentera.core.ui.LenteraBackground
import com.kelompok8.lentera.ui.theme.LENTERATheme
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Panji — tampilkan skor, simpan hasil, update nilai terbaik dan medali
@Composable
fun ScoreScreen(chapterId: String, correctCount: Int, onDone: () -> Unit) {
    val result = ChapterResult(chapterId = chapterId, correctCount = correctCount)

    val currentUser = remember {
        runCatching { AppContainer.authRepository.currentUser() }.getOrNull()
    }

    // Simpan skor otomatis ke Firestore saat halaman dibuka
    LaunchedEffect(chapterId, correctCount, currentUser?.uid) {
        val uid = currentUser?.uid
        if (!uid.isNullOrBlank()) {
            AppContainer.scoreRepository.saveResult(uid, result)
            UserSession.refresh()
            runCatching { AppContainer.leaderboardRepository.syncPlayer(uid) }
        }
    }

    val islandName = when {
        chapterId.contains("jawa", ignoreCase = true) -> "Pulau Jawa"
        chapterId.contains("sumatra", ignoreCase = true) -> "Pulau Sumatra"
        chapterId.contains("kalimantan", ignoreCase = true) -> "Pulau Kalimantan"
        else -> "Pulau Jawa"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header("Skor Akhir", 50, R.drawable.avatar1png)

        // --- CENTER CARD ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .verticalScroll(rememberScrollState())
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF040842))
                    .border(2.dp, Color(0xFF16248C), RoundedCornerShape(28.dp))
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SKOR KAMU",
                    fontFamily = mPlus1p,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Angka skor besar
                Text(
                    text = "${result.score}",
                    maxLines = 1,
                    style = TextStyle(
                        fontFamily = mPlus1p,
                        fontSize = 128.sp,
                        lineHeight = 132.sp,
                        fontWeight = FontWeight.Bold,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF42FF9E),
                                Color(0xFF0072FF)
                            )
                        )
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Teks info lokasi & soal
                Text(
                    text = "$islandName : ${result.totalQuestions} Soal",
                    fontFamily = mPlus1p,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA6FF00)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (result.score >= 60) "MEMENANGKAN PERMAINAN" else "COBA LAGI",
                    fontFamily = mPlus1p,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA6FF00)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // LANJUTKAN Button
                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B2382),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.5.dp, Color(0xFF3B4BCB), RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "LANJUTKAN",
                        fontFamily = mPlus1p,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ULANGI Button
                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA2B000),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "ULANGI",
                        fontFamily = mPlus1p,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// --- COMPOSE PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    LENTERATheme {
        LenteraBackground {
            ScoreScreen(
                chapterId = "jawa_1",
                correctCount = 7,
                onDone = {}
            )
        }
    }
}
