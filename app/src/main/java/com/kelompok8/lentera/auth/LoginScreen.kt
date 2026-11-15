package com.kelompok8.lentera.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.ui.LinkText
import com.kelompok8.lentera.core.ui.PinkButton

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onBackToMenu: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Pindah ke Beranda begitu login berhasil
    LaunchedEffect(state.loggedInUser) {
        if (state.loggedInUser != null) onLoginSuccess()
    }

    AuthLayout(heading = "Selamat Datang Kembali!") {
        LenteraTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            placeholder = "Email",
            icon = "✉️",
            keyboardType = KeyboardType.Email
        )
        LenteraPasswordField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() }
        )
        ErrorText(state.errorMessage)
        PinkButton(text = "Masuk", isLoading = state.isLoading, onClick = { viewModel.login(email, password) })
        LinkText(text = "Belum punya akun? Daftar", onClick = onGoToRegister)
        LinkText(text = "Kembali ke Menu Utama", onClick = onBackToMenu)
    }
}
