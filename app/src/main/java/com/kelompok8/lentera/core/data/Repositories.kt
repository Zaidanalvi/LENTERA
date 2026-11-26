package com.kelompok8.lentera.core.data

import com.kelompok8.lentera.core.model.BestScore
import com.kelompok8.lentera.core.model.ChapterResult
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.model.LeaderboardEntry
import com.kelompok8.lentera.core.model.Question
import com.kelompok8.lentera.core.model.User

// Interface antar fitur. Masing-masing PJ bikin implementasinya di package fiturnya.
// Selama implementasi asli belum jadi, boleh pakai versi dummy dulu.

// PJ: Zulfan
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        gender: String,
        location: String,
        avatarId: Int
    ): Result<User>
    suspend fun resetPassword(email: String): Result<Unit> = Result.success(Unit)
    fun currentUser(): User?                 // cepat, tapi hanya berisi uid, nama, dan email
    suspend fun getProfile(): User?          // profil lengkap dari Firestore
    suspend fun updateProfile(name: String, gender: String, location: String, avatarId: Int): Result<User>
    fun logout()
}

// PJ: Daud
interface QuestionRepository {
    suspend fun getQuestions(chapterId: String): List<Question>
}

// PJ: Panji
interface ScoreRepository {
    suspend fun saveResult(uid: String, result: ChapterResult): Island?
    suspend fun getBestScores(uid: String): List<BestScore>
    suspend fun hasMedal(uid: String, island: Island): Boolean
    suspend fun checkAndUnlockMedal(uid: String, island: Island): Boolean
    suspend fun getEarnedMedals(uid: String): List<Island>
}

data class LeaderboardResult(
    val entries: List<LeaderboardEntry>,
    val totalCount: Int
)

// PJ: Hilal
interface LeaderboardRepository {
    suspend fun getOverallLeaderboard(page: Int = 1, pageSize: Int = 10): LeaderboardResult
    suspend fun syncPlayer(uid: String) {}
}

// PJ: Noya
interface GlossaryRepository {
    suspend fun getProvinces(island: Island): List<String>
    suspend fun getItems(province: String, category: GlossaryCategory): List<GlossaryItem>
}
