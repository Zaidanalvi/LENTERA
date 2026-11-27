package com.kelompok8.lentera.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.ui.theme.LenteraPink
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val CardColor = Color(0xFF060D25)
private val CardEdge = Color(0xFF2B326A)
private val IconCircle = Color(0xFF1E1B4B)
private val IconEdge = Color(0xFF4E5A8C)
private val LabelGreen = Color(0xFF79F492)
private val IconGreen = Color(0xFF4AD069)
private val TrackActive = Color(0xFF3CCB6B)
private val TrackInactive = Color(0xFF1B2A5E)
private val SwitchOn = Color(0xFF3D6BF5)
private val SaveTop = Color(0xFF14BD92)
private val SaveBottom = Color(0xFF079A76)
private val AboutColor = Color(0xFF0F1F68)

@Composable
fun SettingsScreen(
    onAbout: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val settings = state.settings

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
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pengaturan", fontFamily = mPlus1p, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextLight)
        Spacer(Modifier.height(72.dp))

        val cardShape = RoundedCornerShape(12.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(CardColor)
                .border(1.dp, CardEdge, cardShape)
        ) {
            Image(
                painter = painterResource(R.drawable.settings_banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            )
            Column(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                VolumeRow("Permainan", settings.gameVolume, viewModel::setGameVolume)
                VolumeRow("Musik", settings.musicVolume, viewModel::setMusicVolume)
                VibrationRow(settings.vibration, viewModel::setVibration)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsButton(
                        text = "SIMPAN",
                        background = Brush.verticalGradient(listOf(SaveTop, SaveBottom)),
                        onClick = viewModel::save
                    )
                    SettingsButton(
                        text = "TENTANG KAMI",
                        background = Brush.verticalGradient(listOf(AboutColor, AboutColor)),
                        onClick = onAbout
                    )
                }
                state.savedMessage?.let {
                    Text(
                        it,
                        color = LabelGreen,
                        fontFamily = mPlus1p,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SettingsButton(
            text = "KELUAR",
            background = Brush.verticalGradient(listOf(LenteraPink, LenteraPink)),
            onClick = {
                AppContainer.authRepository.logout()
                UserSession.clear()
                onLogout()
            }
        )
    }
}

@Composable
private fun SettingIcon(iconRes: Int, tint: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(IconCircle)
            .border(1.5.dp, IconEdge, CircleShape)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun VolumeRow(label: String, value: Float, onChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingIcon(R.drawable.ic_settings_volume, IconGreen)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label,
                    color = LabelGreen,
                    fontFamily = mPlus1p,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${(value * 100).roundToInt()}%",
                    color = TextLight,
                    fontFamily = mPlus1p,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            ThinSlider(value = value, onChange = onChange, label = label)
        }
    }
}

@Composable
private fun ThinSlider(value: Float, onChange: (Float) -> Unit, label: String) {
    val update by rememberUpdatedState(onChange)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .semantics {
                contentDescription = "Volume $label"
                progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f)
                setProgress { target ->
                    update(target.coerceIn(0f, 1f))
                    true
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { update((it.x / size.width).coerceIn(0f, 1f)) }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    update((change.position.x / size.width).coerceIn(0f, 1f))
                }
            }
    ) {
        val thumbRadius = 7.dp.toPx()
        val trackHeight = 4.dp.toPx()
        val start = thumbRadius
        val end = size.width - thumbRadius
        val y = size.height / 2f
        val thumbX = start + (end - start) * value.coerceIn(0f, 1f)
        val corner = CornerRadius(trackHeight / 2f)
        drawRoundRect(
            color = TrackInactive,
            topLeft = Offset(start, y - trackHeight / 2f),
            size = Size(end - start, trackHeight),
            cornerRadius = corner
        )
        drawRoundRect(
            color = TrackActive,
            topLeft = Offset(start, y - trackHeight / 2f),
            size = Size(thumbX - start, trackHeight),
            cornerRadius = corner
        )
        drawCircle(color = TrackActive, radius = thumbRadius, center = Offset(thumbX, y))
        drawCircle(color = Color(0x33FFFFFF), radius = thumbRadius * 0.45f, center = Offset(thumbX, y))
    }
}

@Composable
private fun VibrationRow(enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SettingIcon(R.drawable.ic_settings_vibration, TextLight)
        Spacer(Modifier.width(12.dp))
        Text(
            "Getaran",
            color = TextLight,
            fontFamily = mPlus1p,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = enabled,
            onCheckedChange = onChange,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextLight,
                checkedTrackColor = SwitchOn,
                checkedBorderColor = SwitchOn,
                uncheckedThumbColor = IconEdge,
                uncheckedTrackColor = TrackInactive,
                uncheckedBorderColor = IconEdge
            )
        )
    }
}

@Composable
private fun SettingsButton(text: String, background: Brush, onClick: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(shape)
            .background(background)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Text(
            text,
            color = TextLight,
            fontFamily = mPlus1p,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
