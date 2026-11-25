package com.kelompok8.lentera.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
import com.kelompok8.lentera.auth.ErrorText
import com.kelompok8.lentera.auth.genderOptions
import com.kelompok8.lentera.auth.provinceOptions
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.core.ui.avatarEmojis
import com.kelompok8.lentera.ui.theme.AvatarPink
import com.kelompok8.lentera.ui.theme.AvatarPurple
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.TextLight
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
            CloseCircleButton(onClick = onBack)
            Text(
                "Ubah profil",
                color = Color.White,
                fontFamily = mPlus1p,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(40.dp))
        }

        if (!initialized) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = LenteraLime) }
        } else {
            val previewShape = RoundedCornerShape(22.dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(116.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(previewShape)
                    .background(Brush.verticalGradient(listOf(AvatarPink, AvatarPurple)))
                    .border(2.dp, Color(0x99FFFFFF), previewShape)
            ) {
                Image(
                    painter = painterResource(avatarEmojis.getOrElse(avatarId) { avatarEmojis[0] }),
                    contentDescription = "Avatar terpilih",
                    modifier = Modifier.size(92.dp)
                )
            }

            ProfileAvatarPicker(selectedId = avatarId, onSelect = { avatarId = it })

            LabeledField("Nama") {
                ProfileTextField(
                    value = name,
                    onValueChange = { name = it; viewModel.clearError() },
                    placeholder = "Nama"
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                LabeledField("Gender", modifier = Modifier.weight(1f)) {
                    ProfileDropdown(
                        selected = gender,
                        options = genderOptions,
                        onSelected = { gender = it; viewModel.clearError() },
                        placeholder = "Gender"
                    )
                }
                LabeledField("Asal provinsi", modifier = Modifier.weight(1f)) {
                    ProfileDropdown(
                        selected = location,
                        options = provinceOptions,
                        onSelected = { location = it; viewModel.clearError() },
                        placeholder = "Asal Provinsi"
                    )
                }
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
                        .border(1.dp, Color(0xFF262E9E), RoundedCornerShape(10.dp))
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
            Spacer(Modifier.height(12.dp))
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
private fun ProfileAvatarPicker(selectedId: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        avatarEmojis.indices.forEach { id ->
            val selected = id == selectedId
            val shape = RoundedCornerShape(14.dp)
            val bg = if (selected) {
                Brush.verticalGradient(listOf(AvatarPink, AvatarPurple))
            } else {
                SolidColor(FieldBlue)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(shape)
                    .background(bg)
                    .border(
                        width = if (selected) 2.5.dp else 1.dp,
                        color = if (selected) LenteraLime else FieldBorder,
                        shape = shape
                    )
                    .clickable { onSelect(id) }
            ) {
                Image(
                    painter = painterResource(avatarEmojis[id]),
                    contentDescription = "Pilihan avatar ${id + 1}",
                    modifier = Modifier.size(58.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val fieldShape = RoundedCornerShape(10.dp)
    val textStyle = TextStyle(fontFamily = mPlus1p, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = textStyle) },
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        shape = fieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBlue,
            unfocusedContainerColor = FieldBlue,
            focusedBorderColor = LenteraLime,
            unfocusedBorderColor = FieldBorder,
            focusedTextColor = TextLight,
            unfocusedTextColor = TextLight,
            focusedPlaceholderColor = TextLight.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = TextLight.copy(alpha = 0.7f),
            cursorColor = LenteraLime
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ProfileDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }
    val fieldShape = RoundedCornerShape(10.dp)
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(fieldShape)
                .background(FieldBlue)
                .border(1.dp, FieldBorder, fieldShape)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.ifEmpty { placeholder },
                fontFamily = mPlus1p,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected.isEmpty()) TextLight.copy(alpha = 0.7f) else TextLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1B4B))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White, fontFamily = mPlus1p) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    field: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = modifier) {
        Text(label, color = ProfileMuted, fontFamily = mPlus1p, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        field()
    }
}
