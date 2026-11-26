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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
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
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var avatarId by rememberSaveable { mutableIntStateOf(0) }

    AuthLayout(heading = "Ayo Buat Akun Terlebih Dahulu!") {
        Spacer(Modifier.height(5.dp))
        LenteraTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            placeholder = "Email",
            icon = { Icon(
                painter = painterResource(R.drawable.baseline_email_24),
                contentDescription = "Password",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(20.dp))
            },
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
            icon = { Icon(
                painter = painterResource(R.drawable.baseline_person_24),
                contentDescription = "Password",
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(20.dp))
            }
        )
        LenteraDropdown(
            selected = gender,
            options = genderOptions,
            onSelected = { gender = it; viewModel.clearError() },
            placeholder = "Gender",
            icon = if (gender == "Wanita") painterResource(R.drawable.outline_female_24) else painterResource(R.drawable.outline_male_24)
        )
        LenteraDropdown(
            selected = location,
            options = provinceOptions,
            onSelected = { location = it; viewModel.clearError() },
            placeholder = "Asal Provinsi",
            icon = painterResource(R.drawable.baseline_location_on_24)
        )
        AvatarPicker(selectedId = avatarId, onSelect = { avatarId = it })
        ErrorText(errorMessage)
        PinkButton(
            text = "Daftar Sekarang",
            isLoading = isLoading,
            onClick = {
                if (SavedAccountsStore.isRememberMeEnabled(context) && email.isNotBlank() && password.length >= 6) {
                    SavedAccountsStore.saveAccount(
                        context = context,
                        email = email.trim(),
                        password = password,
                        name = name.trim(),
                        avatarId = avatarId
                    )
                }
                viewModel.register(email, password, name, gender, location, avatarId)
            }
        )
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
