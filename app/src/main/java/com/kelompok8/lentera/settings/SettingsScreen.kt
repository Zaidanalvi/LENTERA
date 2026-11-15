package com.kelompok8.lentera.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Zulfan — pengaturan, About, dan logout
@Composable
fun SettingsScreen(onLogout: () -> Unit) {
    PlaceholderScreen(title = "Pengaturan", owner = "Zulfan") {
        Button(onClick = onLogout) { Text("Keluar") }
    }
}
