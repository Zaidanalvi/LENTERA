package com.kelompok8.lentera.map

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Hilal — pilih chapter 1-5, tampilkan progres tiap chapter
@Composable
fun ChapterSelectScreen(island: String, onChapterClick: (String) -> Unit) {
    PlaceholderScreen(title = "Pilih Chapter — $island", owner = "Hilal") {
        (1..5).forEach { number ->
            val chapterId = "${island.lowercase()}_$number"
            Button(onClick = { onChapterClick(chapterId) }) { Text("Chapter $number") }
        }
    }
}
