package com.kelompok8.lentera.settings

import android.content.Context

data class AppSettings(
    val gameVolume: Float = 0.35f,   // 0f..1f
    val musicVolume: Float = 0.68f,
    val vibration: Boolean = true
)

// Pengaturan cukup disimpan di HP (SharedPreferences), tidak perlu ke Firebase
class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("lentera_settings", Context.MODE_PRIVATE)

    fun load() = AppSettings(
        gameVolume = prefs.getFloat(KEY_GAME, AppSettings().gameVolume),
        musicVolume = prefs.getFloat(KEY_MUSIC, AppSettings().musicVolume),
        vibration = prefs.getBoolean(KEY_VIBRATION, AppSettings().vibration)
    )

    fun save(settings: AppSettings) {
        prefs.edit()
            .putFloat(KEY_GAME, settings.gameVolume)
            .putFloat(KEY_MUSIC, settings.musicVolume)
            .putBoolean(KEY_VIBRATION, settings.vibration)
            .apply()
    }

    private companion object {
        const val KEY_GAME = "game_volume"
        const val KEY_MUSIC = "music_volume"
        const val KEY_VIBRATION = "vibration"
    }
}
