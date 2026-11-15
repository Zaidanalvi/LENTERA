package com.kelompok8.lentera.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val savedMessage: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState(settings = repository.load()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Perubahan slider/switch baru disimpan permanen saat tombol SIMPAN ditekan
    fun setGameVolume(value: Float) = edit { it.copy(gameVolume = value) }
    fun setMusicVolume(value: Float) = edit { it.copy(musicVolume = value) }
    fun setVibration(enabled: Boolean) = edit { it.copy(vibration = enabled) }

    fun save() {
        repository.save(_uiState.value.settings)
        _uiState.update { it.copy(savedMessage = "Pengaturan disimpan") }
    }

    fun clearMessage() = _uiState.update { it.copy(savedMessage = null) }

    private fun edit(change: (AppSettings) -> AppSettings) {
        _uiState.update { it.copy(settings = change(it.settings), savedMessage = null) }
    }
}
