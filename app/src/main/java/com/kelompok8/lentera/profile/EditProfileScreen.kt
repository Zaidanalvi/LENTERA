package com.kelompok8.lentera.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
import com.kelompok8.lentera.auth.AvatarPicker
import com.kelompok8.lentera.auth.ErrorText
import com.kelompok8.lentera.auth.LenteraDropdown
import com.kelompok8.lentera.auth.LenteraTextField
import com.kelompok8.lentera.auth.genderOptions
import com.kelompok8.lentera.auth.provinceOptions
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.ui.AvatarImage
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.mPlus1p

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val user by UserSession.profile.collectAsState()

    var initialized by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var avatarId by rememberSaveable { mutableIntStateOf(0) }

    // Isi form dengan data lama sekali saja, supaya ketikan user tidak tertimpa
    LaunchedEffect(user) {
        val current = user
        if (current == null) {
            UserSession.refresh()
        } else if (!initialized) {
            name = current.name
            gender = current.gender
            location = current.location
            avatarId = current.avatarId
            initialized = true
        }
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleIconButton(BackIcon, "Batal, kembali ke profil", onBack)
            Text(
                "Ubah profil",
                color = Color.White,
                fontFamily = mPlus1p,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(44.dp))
        }

        if (!initialized) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = LenteraLime) }
        } else {

        AvatarImage(
            avatarId = avatarId,
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        AvatarPicker(selectedId = avatarId, onSelect = { avatarId = it })

        LabeledField("Nama") {
        LenteraTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            placeholder = "Nama",
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_person_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        }

        LabeledField("Gender") {
        LenteraDropdown(
            selected = gender,
            options = genderOptions,
            onSelected = { gender = it; viewModel.clearError() },
            placeholder = "Gender",
            icon = if (gender == "Wanita") painterResource(R.drawable.outline_female_24) else painterResource(R.drawable.outline_male_24)
        )
        }

        LabeledField("Asal provinsi") {
        LenteraDropdown(
            selected = location,
            options = provinceOptions,
            onSelected = { location = it; viewModel.clearError() },
            placeholder = "Asal Provinsi",
            icon = painterResource(R.drawable.baseline_location_on_24)
        )
        }

        LabeledField("Email") {
            Text(
                user?.email.orEmpty(),
                color = ProfileMuted,
                fontFamily = mPlus1p,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ProfilePanel)
                    .border(1.dp, Color(0xFF3B45DB), RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            )
            Text(
                "Email dipakai untuk masuk, jadi tidak bisa diubah di sini.",
                color = Color(0xFF8F96E0),
                fontFamily = mPlus1p,
                fontSize = 12.sp
            )
        }

        ErrorText(state.errorMessage)
        PinkButton(
            text = "Simpan perubahan",
            isLoading = state.isSaving,
            onClick = { viewModel.save(name, gender, location, avatarId) }
        )
        Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LabeledField(label: String, field: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        Text(label, color = ProfileMuted, fontFamily = mPlus1p, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        field()
    }
}
