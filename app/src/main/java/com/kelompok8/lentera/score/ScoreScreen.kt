package com.kelompok8.lentera.score

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.model.ChapterResult
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Panji — tampilkan skor, simpan hasil, update nilai terbaik dan medali
@Composable
fun ScoreScreen(chapterId: String, correctCount: Int, onDone: () -> Unit) {
    val result = ChapterResult(chapterId = chapterId, correctCount = correctCount)
    PlaceholderScreen(title = "Skor: ${result.score}", owner = "Panji") {
        Text("$correctCount dari ${result.totalQuestions} soal benar")
        Button(onClick = onDone) { Text("Kembali") }
    }
}
