package com.kelompok8.lentera.map

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Hilal — halaman Main (pilih pulau untuk bermain)
@Composable
fun PlayScreen(onIslandClick: (String) -> Unit, onLeaderboardClick: () -> Unit) {
    PlaceholderScreen(title = "Bermain", owner = "Hilal") {
        Island.entries.forEach { island ->
            Button(onClick = { onIslandClick(island.name) }) { Text(island.displayName) }
        }
        OutlinedButton(onClick = onLeaderboardClick) { Text("Leaderboard") }
    }
}
