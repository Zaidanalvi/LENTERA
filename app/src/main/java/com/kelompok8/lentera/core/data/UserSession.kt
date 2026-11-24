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

    suspend fun refresh() {
        _profile.value = runCatching { AppContainer.authRepository.getProfile() }.getOrNull()
    }

    fun set(user: User?) {
        _profile.value = user
    }

    fun clear() {
        _profile.value = null
    }
}
