package com.kelompok8.lentera.core.data

import com.kelompok8.lentera.auth.FirebaseAuthRepository

// Tempat semua repository dibuat satu kali lalu dipakai bersama.
// Kalau repository punyamu sudah jadi, daftarkan di sini juga.
object AppContainer {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
}
