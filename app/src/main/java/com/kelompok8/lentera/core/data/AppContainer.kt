package com.kelompok8.lentera.core.data

import com.kelompok8.lentera.auth.FirebaseAuthRepository
import com.kelompok8.lentera.leaderboard.FirebaseLeaderboardRepository
import com.kelompok8.lentera.score.FirebaseScoreRepository

// Tempat semua repository dibuat satu kali lalu dipakai bersama.
// Kalau repository punyamu sudah jadi, daftarkan di sini juga.
object AppContainer {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
    val leaderboardRepository: LeaderboardRepository by lazy { FirebaseLeaderboardRepository() }
    val scoreRepository: ScoreRepository by lazy { FirebaseScoreRepository() }
}
