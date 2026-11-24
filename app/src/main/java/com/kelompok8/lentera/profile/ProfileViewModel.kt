package com.kelompok8.lentera.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.AuthRepository
import com.kelompok8.lentera.core.data.ScoreRepository
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val totalPoints: Int = 0,
    val chaptersPlayed: Int = 0,
    val medalCount: Int = 0,
    val islands: List<IslandProgress> = emptyList(),
    val errorMessage: String? = null
) {
    val level: LevelInfo get() = levelFor(totalPoints)
}

class ProfileViewModel(
    private val auth: AuthRepository = AppContainer.authRepository,
    private val scores: ScoreRepository = AppContainer.scoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = runCatching { auth.getProfile() }.getOrNull()
            if (user == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Profil belum bisa dimuat. Periksa internet lalu coba lagi.")
                }
                return@launch
            }
            UserSession.set(user)

            // Skor dan medali diambil bersamaan supaya halaman lebih cepat terbuka
            val bestScores = async { scores.getBestScores(user.uid) }
            val medals = async { scores.getEarnedMedals(user.uid).toSet() }
            val islands = buildIslandProgress(bestScores.await(), medals.await())

            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = user,
                    islands = islands,
                    totalPoints = islands.sumOf { island -> island.points },
                    chaptersPlayed = islands.sumOf { island -> island.chaptersPlayed },
                    medalCount = islands.count { island -> island.hasMedal }
                )
            }
        }
    }

    fun logout() {
        auth.logout()
        UserSession.clear()
    }
}

data class EditProfileUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false
)

class EditProfileViewModel(
    private val auth: AuthRepository = AppContainer.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    fun save(name: String, gender: String, location: String, avatarId: Int) {
        val error = when {
            name.isBlank() -> "Nama wajib diisi"
            name.trim().length > 30 -> "Nama maksimal 30 karakter"
            gender.isEmpty() -> "Pilih gender terlebih dahulu"
            location.isEmpty() -> "Pilih asal provinsi terlebih dahulu"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            auth.updateProfile(name.trim(), gender, location, avatarId)
                .onSuccess { user ->
                    UserSession.set(user)
                    _uiState.update { it.copy(isSaving = false, saved = true) }
                }
                .onFailure {
                    _uiState.update { it.copy(isSaving = false, errorMessage = "Gagal menyimpan. Periksa internet lalu coba lagi.") }
                }
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) _uiState.update { it.copy(errorMessage = null) }
    }
}
