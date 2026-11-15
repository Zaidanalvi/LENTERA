package com.kelompok8.lentera.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Hilal — homepage dengan peta Jawa, Sumatra, Kalimantan
@Composable
fun HomeScreen(onIslandClick: (String) -> Unit) {
    PlaceholderScreen(title = "Beranda", owner = "Hilal") {
        Island.entries.forEach { island ->
            Button(onClick = { onIslandClick(island.name) }) { Text(island.displayName) }
        }
    }
}
