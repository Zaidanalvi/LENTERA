package com.kelompok8.lentera.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Zulfan — Register + Register-Identitas
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBack: () -> Unit) {
    PlaceholderScreen(title = "Register", owner = "Zulfan") {
        Button(onClick = onRegisterSuccess) { Text("Daftar") }
        TextButton(onClick = onBack) { Text("Kembali ke Login") }
    }
}
