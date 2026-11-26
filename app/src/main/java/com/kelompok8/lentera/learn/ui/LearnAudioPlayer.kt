package com.kelompok8.lentera.learn.ui

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.settings.SettingsRepository
import kotlinx.coroutines.delay
import java.util.Locale

private val PlayerBg = Color(0xFF151E4A)
private val PlayerBorder = Color(0xFF33418A)
private val AccentNeon = Color(0xFFDDFF00)
private val AccentPurpleTop = Color(0xFF8F2CE0)
private val AccentPurpleBottom = Color(0xFF5A12A6)
private val MutedBlue = Color(0xFF8C97CE)

@SuppressLint("DiscouragedApi")
@Composable
internal fun rememberRawAudioId(name: String?): Int {
    val context = LocalContext.current
    return remember(name) {
        if (name.isNullOrBlank()) 0
        else context.resources.getIdentifier(name, "raw", context.packageName)
    }
}

@Stable
internal class LearnAudioController(private val appContext: Context) {
    private var mediaPlayer: MediaPlayer? = null

    var activeItemId by mutableStateOf<String?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentPositionMs by mutableIntStateOf(0)
        private set

    var durationMs by mutableIntStateOf(0)
        private set

    fun togglePlayPause(itemId: String, rawResId: Int) {
        if (rawResId == 0) return

        if (activeItemId == itemId && mediaPlayer != null) {
            val player = mediaPlayer ?: return
            if (player.isPlaying) {
                player.pause()
                currentPositionMs = player.currentPosition
                isPlaying = false
            } else {
                applySavedMusicVolume(player)
                player.start()
                isPlaying = true
            }
            return
        }

        startNewAudio(itemId, rawResId)
    }

    fun replay(itemId: String, rawResId: Int) {
        if (rawResId == 0) return
        if (activeItemId == itemId && mediaPlayer != null) {
            val player = mediaPlayer ?: return
            applySavedMusicVolume(player)
            player.seekTo(0)
            currentPositionMs = 0
            if (!player.isPlaying) {
                player.start()
            }
            isPlaying = true
        } else {
            startNewAudio(itemId, rawResId)
        }
    }

    fun seekTo(itemId: String, fraction: Float) {
        if (activeItemId != itemId) return
        val player = mediaPlayer ?: return
        val dur = durationMs
        if (dur <= 0) return
        val target = (fraction.coerceIn(0f, 1f) * dur).toInt()
        player.seekTo(target)
        currentPositionMs = target
    }

    fun refreshPosition() {
        val player = mediaPlayer ?: return
        if (isPlaying) {
            runCatching {
                currentPositionMs = player.currentPosition
                if (durationMs <= 0) durationMs = player.duration.coerceAtLeast(0)
            }
        }
    }

    fun pauseIfPlaying() {
        val player = mediaPlayer ?: return
        runCatching {
            if (player.isPlaying) {
                player.pause()
                currentPositionMs = player.currentPosition
                isPlaying = false
            }
        }
    }

    fun stopAndRelease() {
        runCatching {
            mediaPlayer?.stop()
        }
        runCatching {
            mediaPlayer?.release()
        }
        mediaPlayer = null
        activeItemId = null
        isPlaying = false
        currentPositionMs = 0
        durationMs = 0
    }

    private fun startNewAudio(itemId: String, rawResId: Int) {
        stopAndRelease()
        val created = runCatching { MediaPlayer.create(appContext, rawResId) }.getOrNull() ?: return
        applySavedMusicVolume(created)
        created.setOnCompletionListener { player ->
            runCatching { player.seekTo(0) }
            isPlaying = false
            currentPositionMs = 0
        }
        mediaPlayer = created
        activeItemId = itemId
        durationMs = created.duration.coerceAtLeast(0)
        currentPositionMs = 0
        created.start()
        isPlaying = true
    }

    private fun applySavedMusicVolume(player: MediaPlayer) {
        val volume = runCatching { SettingsRepository(appContext).load().musicVolume }
            .getOrDefault(0.68f)
            .coerceIn(0f, 1f)
        player.setVolume(volume, volume)
    }
}

@Composable
internal fun rememberLearnAudioController(): LearnAudioController {
    val context = LocalContext.current.applicationContext
    val controller = remember(context) { LearnAudioController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(controller.activeItemId, controller.isPlaying) {
        while (controller.isPlaying) {
            controller.refreshPosition()
            delay(250)
        }
    }

    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                controller.pauseIfPlaying()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.stopAndRelease()
        }
    }

    return controller
}

@Composable
internal fun SongPlayerBar(
    item: GlossaryItem,
    controller: LearnAudioController,
    modifier: Modifier = Modifier,
) {
    val rawByImageName = rememberRawAudioId(item.imageName)
    val rawById = rememberRawAudioId(item.id)
    val rawResId = if (rawByImageName != 0) rawByImageName else rawById
    val hasAudio = rawResId != 0

    val isCurrent = controller.activeItemId == item.id
    val isPlayingCurrent = isCurrent && controller.isPlaying
    val currentMs = if (isCurrent) controller.currentPositionMs else 0
    val totalMs = if (isCurrent) controller.durationMs else 0
    val progress = if (totalMs > 0) (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f) else 0f

    val shape = RoundedCornerShape(12.dp)
    if (!hasAudio) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(PlayerBg.copy(alpha = 0.65f))
                .border(1.dp, PlayerBorder.copy(alpha = 0.6f), shape)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF212B63)),
            ) {
                PlayGlyph(color = MutedBlue.copy(alpha = 0.6f), modifier = Modifier.size(13.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Audio lagu belum tersedia",
                color = MutedBlue,
                fontFamily = LearnFonts.Heading,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PlayerBg)
            .border(
                width = 1.dp,
                color = if (isCurrent) AccentNeon.copy(alpha = 0.65f) else PlayerBorder,
                shape = shape,
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(AccentPurpleTop, AccentPurpleBottom)))
                    .border(1.dp, Color(0x66FFFFFF), CircleShape)
                    .clickable(
                        onClickLabel = if (isPlayingCurrent) "Jeda lagu" else "Putar lagu",
                        role = Role.Button,
                    ) {
                        controller.togglePlayPause(item.id, rawResId)
                    },
            ) {
                if (isPlayingCurrent) {
                    PauseGlyph(color = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    PlayGlyph(color = Color.White, modifier = Modifier.size(15.dp))
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isPlayingCurrent -> "Sedang diputar"
                        isCurrent -> "Dijeda"
                        else -> "Putar lagu daerah"
                    },
                    color = if (isPlayingCurrent) AccentNeon else Color.White,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (isCurrent && totalMs > 0) {
                        "${formatDuration(currentMs)} / ${formatDuration(totalMs)}"
                    } else {
                        "Ketuk tombol putar untuk mendengarkan"
                    },
                    color = MutedBlue,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            if (isCurrent) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF202B6B))
                        .border(1.dp, PlayerBorder, CircleShape)
                        .clickable(
                            onClickLabel = "Ulangi dari awal",
                            role = Role.Button,
                        ) {
                            controller.replay(item.id, rawResId)
                        },
                ) {
                    ReplayGlyph(color = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        if (isCurrent && totalMs > 0) {
            Spacer(Modifier.height(4.dp))
            Slider(
                value = progress,
                onValueChange = { fraction -> controller.seekTo(item.id, fraction) },
                colors = SliderDefaults.colors(
                    thumbColor = AccentNeon,
                    activeTrackColor = AccentNeon,
                    inactiveTrackColor = Color(0xFF2A367A),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
            )
        }
    }
}

private fun formatDuration(ms: Int): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSec / 60
    val seconds = totalSec % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

@Composable
private fun PlayGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.22f, h * 0.12f)
            lineTo(w * 0.90f, h * 0.50f)
            lineTo(w * 0.22f, h * 0.88f)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
private fun PauseGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val barW = size.width * 0.28f
        val barH = size.height * 0.84f
        val top = size.height * 0.08f
        val corner = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.14f, top),
            size = Size(barW, barH),
            cornerRadius = corner,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.58f, top),
            size = Size(barW, barH),
            cornerRadius = corner,
        )
    }
}

@Composable
private fun ReplayGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawArc(
            color = color,
            startAngle = -55f,
            sweepAngle = 275f,
            useCenter = false,
            topLeft = Offset(w * 0.14f, h * 0.14f),
            size = Size(w * 0.72f, h * 0.72f),
            style = stroke,
        )
        val arrow = Path().apply {
            moveTo(w * 0.62f, h * 0.08f)
            lineTo(w * 0.80f, h * 0.28f)
            lineTo(w * 0.54f, h * 0.34f)
        }
        drawPath(arrow, color = color, style = stroke)
    }
}
