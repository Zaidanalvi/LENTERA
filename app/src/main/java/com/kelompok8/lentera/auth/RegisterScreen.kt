package com.kelompok8.lentera.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.model.User
import com.kelompok8.lentera.core.ui.AvatarImage
import com.kelompok8.lentera.core.ui.LinkText
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.TextLight

// Satu halaman untuk dua tampilan di Figma:
// "Register" (form) lalu "Register - Identify" (kartu profil setelah akun berhasil dibuat)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToMenu: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val newUser = state.loggedInUser

    if (newUser != null) {
        // Akun sudah dibuat, tombol back HP diarahkan ke Beranda juga
        BackHandler { onRegisterSuccess() }
        RegisterSuccessContent(user = newUser, onContinue = onRegisterSuccess)
    } else {
        RegisterForm(viewModel = viewModel, isLoading = state.isLoading, errorMessage = state.errorMessage, onBackToMenu = onBackToMenu)
    }
}

@Composable
private fun RegisterForm(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    errorMessage: String?,
    onBackToMenu: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var avatarId by rememberSaveable { mutableIntStateOf(0) }

    AuthLayout(heading = "Ayo Buat Akun Terlebih Dahulu!") {
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
        LenteraTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            placeholder = "Nama",
            icon = "✏️"
        )
        LenteraDropdown(
            selected = gender,
            options = genderOptions,
            onSelected = { gender = it; viewModel.clearError() },
            placeholder = "Gender",
            icon = "👤"
        )
        LenteraDropdown(
            selected = location,
            options = provinceOptions,
            onSelected = { location = it; viewModel.clearError() },
            placeholder = "Asal Provinsi",
            icon = "📍"
        )
        AvatarPicker(selectedId = avatarId, onSelect = { avatarId = it })
        ErrorText(errorMessage)
        PinkButton(
            text = "Daftar Sekarang",
            isLoading = isLoading,
            onClick = { viewModel.register(email, password, name, gender, location, avatarId) }
        )
        Spacer(Modifier.height(8.dp))
        LinkText(text = "Kembali ke Menu Utama", onClick = onBackToMenu)
    }
}

@Composable
private fun RegisterSuccessContent(user: User, onContinue: () -> Unit) {
    AuthLayout(heading = "") {
        Spacer(Modifier.height(80.dp))
        Text("Akun Anda Berhasil Dibuat!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)

        val cardShape = RoundedCornerShape(14.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(FieldBlue)
                .border(1.dp, FieldBorder, cardShape)
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(avatarId = user.avatarId, emojiSize = 56.sp, modifier = Modifier.size(120.dp))
            Column(
                modifier = Modifier.padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileLine("Nama", user.name)
                ProfileLine("Gender", user.gender)
                ProfileLine("Lokasi", user.location)
                ProfileLine("Level", user.level)
            }
        }
        Spacer(Modifier.height(24.dp))
        PinkButton(text = "Lanjutkan", onClick = onContinue)
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Text("$label: $value", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}
