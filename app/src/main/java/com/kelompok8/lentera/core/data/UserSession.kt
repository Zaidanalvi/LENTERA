package com.kelompok8.lentera.core.data

import com.kelompok8.lentera.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Profil pengguna yang sedang login, disimpan di satu tempat supaya header
// di semua halaman (koin dan avatar) selalu menampilkan data yang sama.
object UserSession {
    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()

    private val _medalCount = MutableStateFlow(0)
    val medalCount: StateFlow<Int> = _medalCount.asStateFlow()

    suspend fun refresh() {
        val user = runCatching { AppContainer.authRepository.getProfile() }.getOrNull()
        _profile.value = user
        _medalCount.value = if (user == null) 0 else runCatching {
            AppContainer.scoreRepository.getEarnedMedals(user.uid).size
        }.getOrDefault(_medalCount.value)
    }

    fun setMedalCount(count: Int) {
        _medalCount.value = count
    }

    fun set(user: User?) {
        _profile.value = user
    }

    fun clear() {
        _profile.value = null
        _medalCount.value = 0
    }
}
