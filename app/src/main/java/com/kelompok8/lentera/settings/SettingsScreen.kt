package com.kelompok8.lentera.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.ui.theme.DarkButton
import com.kelompok8.lentera.ui.theme.LenteraPink
import com.kelompok8.lentera.ui.theme.NavyDeep
import com.kelompok8.lentera.ui.theme.SaveGreen
import com.kelompok8.lentera.ui.theme.SettingsBorder
import com.kelompok8.lentera.ui.theme.SettingsCard
import com.kelompok8.lentera.ui.theme.SliderGreen
import com.kelompok8.lentera.ui.theme.SwitchBlue
import com.kelompok8.lentera.ui.theme.TextLight
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    onAbout: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val settings = state.settings

    // Pesan "Pengaturan disimpan" hilang sendiri setelah 2 detik
    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            delay(2000)
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pengaturan", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextLight)
        Spacer(Modifier.height(48.dp))

        val cardShape = RoundedCornerShape(20.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(SettingsCard)
                .border(1.dp, SettingsBorder, cardShape)
        ) {
            SettingsBanner()
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                VolumeRow("Permainan", settings.gameVolume, viewModel::setGameVolume)
                VolumeRow("Musik", settings.musicVolume, viewModel::setMusicVolume)
                VibrationRow(settings.vibration, viewModel::setVibration)

                Column {
                    SettingsButton("Simpan", SaveGreen, viewModel::save)
                    SettingsButton("Tentang Kami", DarkButton, onAbout)
                }
                state.savedMessage?.let {
                    Text(it, color = SliderGreen, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        // Tidak ada di Figma, tapi dibutuhkan untuk mengakhiri sesi pengguna
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                AppContainer.authRepository.logout()
                UserSession.clear()
                onLogout()
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = LenteraPink, contentColor = TextLight),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("KELUAR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Sementara pakai gradasi. Ganti dengan gambar pemandangan dari Figma kalau aset sudah di-export
@Composable
private fun SettingsBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF7EC8F2), Color(0xFFCDE9B0), Color(0xFF5DA83C))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("🏞️", fontSize = 64.sp)
    }
}

@Composable
private fun SettingIcon(icon: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(NavyDeep)
            .border(1.dp, SliderGreen.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 20.sp)
    }
}

@Composable
private fun VolumeRow(label: String, value: Float, onChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingIcon("🔊")
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row {
                Text(label, color = SliderGreen, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("${(value * 100).roundToInt()}%", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Slider(
                value = value,
                onValueChange = onChange,
                colors = SliderDefaults.colors(
                    thumbColor = SliderGreen,
                    activeTrackColor = SliderGreen,
                    inactiveTrackColor = Color(0xFF1C2270)
                )
            )
        }
    }
}

@Composable
private fun VibrationRow(enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingIcon("📳")
        Spacer(Modifier.width(12.dp))
        Text("Getaran", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Switch(
            checked = enabled,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SwitchBlue,
                checkedThumbColor = TextLight
            )
        )
    }
}

@Composable
private fun SettingsButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = TextLight),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text.uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
