package com.kelompok8.lentera.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
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
        Spacer(Modifier.height(15.dp))
        LenteraTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            placeholder = "Email",
            icon =  { Icon(
                painter = painterResource(R.drawable.baseline_email_24),
                contentDescription = "Email",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(20.dp)
            ) },
            keyboardType = KeyboardType.Email
        )
        LenteraPasswordField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() }
        )
        ErrorText(state.errorMessage)
        Spacer(Modifier.height(20.dp))
        PinkButton(text = "Masuk", isLoading = state.isLoading, onClick = { viewModel.login(email, password) })
        Spacer(Modifier.height(50.dp))
        LinkText(text = "Belum punya akun? Daftar", onClick = onGoToRegister)
        LinkText(text = "Kembali ke Menu Utama", onClick = onBackToMenu)
    }
}
