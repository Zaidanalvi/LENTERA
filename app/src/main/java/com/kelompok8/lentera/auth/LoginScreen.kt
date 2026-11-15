package com.kelompok8.lentera.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kelompok8.lentera.core.ui.PlaceholderScreen

// PJ: Zulfan — nanti diganti form email/password + Firebase Auth
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoToRegister: () -> Unit) {
    PlaceholderScreen(title = "Login", owner = "Zulfan") {
        Button(onClick = onLoginSuccess) { Text("Masuk") }
        TextButton(onClick = onGoToRegister) { Text("Belum punya akun? Daftar") }
    }
}
