package com.kelompok8.lentera.game

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Daud — 10 soal (4 pilihan ganda, 3 blok kata, 3 susun huruf), cek jawaban, pindah soal.
// Kalau semua soal selesai, panggil onFinished(jumlahBenar).
@Composable
fun GameScreen(chapterId: String, onFinished: (Int) -> Unit) {
    PlaceholderScreen(title = "Gameplay $chapterId", owner = "Daud") {
        Button(onClick = { onFinished(7) }) { Text("Selesai (simulasi 7 benar)") }
    }
}
