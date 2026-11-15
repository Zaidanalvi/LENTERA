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
    suspend fun register(name: String, email: String, password: String, school: String): Result<User>
    fun currentUser(): User?
    fun logout()
}

// PJ: Daud
interface QuestionRepository {
    suspend fun getQuestions(chapterId: String): List<Question>
}

// PJ: Panji
interface ScoreRepository {
    suspend fun saveResult(uid: String, result: ChapterResult)
    suspend fun getBestScores(uid: String): List<BestScore>
    suspend fun hasMedal(uid: String, island: Island): Boolean
}

// PJ: Hilal
interface LeaderboardRepository {
    suspend fun getIslandLeaderboard(island: Island): List<LeaderboardEntry>
    suspend fun getOverallLeaderboard(): List<LeaderboardEntry>
}

// PJ: Noya
interface GlossaryRepository {
    suspend fun getProvinces(island: Island): List<String>
    suspend fun getItems(province: String, category: GlossaryCategory): List<GlossaryItem>
}
