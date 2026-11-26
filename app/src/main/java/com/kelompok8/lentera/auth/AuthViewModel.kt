package com.kelompok8.lentera.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.AuthRepository
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInUser: User? = null,
    val isResettingPassword: Boolean = false,
    val resetPasswordError: String? = null,
    val resetPasswordSuccess: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AppContainer.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val error = when {
            !isValidEmail(email) -> "Format email tidak valid"
            password.isEmpty() -> "Password wajib diisi"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        runAuth { repository.login(email.trim(), password) }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        gender: String,
        location: String,
        avatarId: Int
    ) {
        val error = when {
            !isValidEmail(email) -> "Format email tidak valid"
            password.length < 6 -> "Password minimal 6 karakter"
            name.isBlank() -> "Nama wajib diisi"
            gender.isEmpty() -> "Pilih gender terlebih dahulu"
            location.isEmpty() -> "Pilih asal provinsi terlebih dahulu"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        runAuth { repository.register(email.trim(), password, name.trim(), gender, location, avatarId) }
    }

    fun sendPasswordReset(email: String) {
        val trimmed = email.trim()
        val error = when {
            trimmed.isEmpty() -> "Masukkan email akunmu terlebih dahulu"
            !isValidEmail(trimmed) -> "Format email tidak valid"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(resetPasswordError = error, resetPasswordSuccess = null) }
            return
        }
        if (_uiState.value.isResettingPassword) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isResettingPassword = true,
                    resetPasswordError = null,
                    resetPasswordSuccess = null
                )
            }
            repository.resetPassword(trimmed)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isResettingPassword = false,
                            resetPasswordSuccess = "Link reset password telah dikirim ke $trimmed. Silakan cek kotak masuk atau folder Spam email kamu."
                        )
                    }
                }
                .onFailure { e ->
                    val msg = when (e) {
                        is FirebaseAuthInvalidUserException -> "Email belum terdaftar di LENTERA"
                        is FirebaseNetworkException -> "Tidak ada koneksi internet"
                        is FirebaseTooManyRequestsException -> "Terlalu banyak permintaan, coba beberapa saat lagi"
                        else -> "Gagal mengirim email reset password, periksa kembali emailmu"
                    }
                    _uiState.update {
                        it.copy(
                            isResettingPassword = false,
                            resetPasswordError = msg
                        )
                    }
                }
        }
    }

    fun clearResetPasswordState() {
        _uiState.update {
            it.copy(
                isResettingPassword = false,
                resetPasswordError = null,
                resetPasswordSuccess = null
            )
        }
    }

    fun clearError() {
        if (_uiState.value.errorMessage != null) {
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun runAuth(action: suspend () -> Result<User>) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            action()
                .onSuccess { user ->
                    UserSession.set(user)
                    _uiState.update { it.copy(isLoading = false, loggedInUser = user) }
                    launch { UserSession.refresh() }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.toMessage()) } }
        }
    }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    // Pesan error bawaan Firebase berbahasa Inggris dan teknis, jadi diterjemahkan dulu
    private fun Throwable.toMessage(): String = when (this) {
        is FirebaseAuthUserCollisionException -> "Email sudah terdaftar, silakan login"
        is FirebaseAuthWeakPasswordException -> "Password terlalu lemah"
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Email atau password salah"
        is FirebaseNetworkException -> "Tidak ada koneksi internet"
        is FirebaseTooManyRequestsException -> "Terlalu banyak percobaan, tunggu sebentar"
        else -> message ?: "Terjadi kesalahan, coba lagi"
    }
}
