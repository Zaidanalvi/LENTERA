package com.kelompok8.lentera.score

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kelompok8.lentera.core.data.ScoreRepository
import com.kelompok8.lentera.core.model.BestScore
import com.kelompok8.lentera.core.model.ChapterResult
import com.kelompok8.lentera.core.model.Island
import kotlinx.coroutines.tasks.await

class FirebaseScoreRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ScoreRepository {

    override suspend fun saveResult(uid: String, result: ChapterResult) {
        if (uid.isBlank()) return
        runCatching {
            val userScoreRef = db.collection("users")
                .document(uid)
                .collection("scores")
                .document(result.chapterId)

            val snapshot = userScoreRef.get().await()
            val currentBest = snapshot.getLong("bestScore")?.toInt() ?: 0
            val newBest = maxOf(currentBest, result.score)

            // 1. Simpan/update hasil skor di sub-koleksi user
            val scoreData = mapOf(
                "chapterId" to result.chapterId,
                "lastScore" to result.score,
                "bestScore" to newBest,
                "correctCount" to result.correctCount,
                "totalQuestions" to result.totalQuestions,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            userScoreRef.set(scoreData, SetOptions.merge()).await()

            // 2. Simpan di koleksi global `best_scores` untuk peringkat/leaderboard
            val globalBestRef = db.collection("best_scores")
                .document("${uid}_${result.chapterId}")

            globalBestRef.set(
                mapOf(
                    "uid" to uid,
                    "chapterId" to result.chapterId,
                    "bestScore" to newBest,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()

            // 3. Cek & buka medali secara otomatis jika skor 100 dan semua chapter di pulau bernilai 100
            val island = getIslandFromChapterId(result.chapterId)
            if (island != null && newBest == 100) {
                checkAndUnlockMedal(uid, island)
            }
        }
    }

    override suspend fun getBestScores(uid: String): List<BestScore> {
        if (uid.isBlank()) return emptyList()
        return runCatching {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("scores")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val chapterId = doc.getString("chapterId") ?: doc.id
                val bestScore = doc.getLong("bestScore")?.toInt() ?: 0
                BestScore(uid = uid, chapterId = chapterId, bestScore = bestScore)
            }
        }.getOrDefault(emptyList())
    }

    override suspend fun hasMedal(uid: String, island: Island): Boolean {
        if (uid.isBlank()) return false
        return runCatching {
            val doc = db.collection("users")
                .document(uid)
                .collection("medals")
                .document(island.name)
                .get()
                .await()

            doc.exists() && (doc.getBoolean("unlocked") ?: true)
        }.getOrDefault(false)
    }

    /**
     * Memeriksa apakah semua 5 chapter dari suatu pulau sudah mendapat nilai 100.
     * Jika ya dan belum pernah mendapatkan medali, simpan medali baru ke Firebase.
     */
    override suspend fun checkAndUnlockMedal(uid: String, island: Island): Boolean {
        if (uid.isBlank()) return false

        // Jika sudah punya medali untuk pulau ini, tidak perlu diproses ulang
        if (hasMedal(uid, island)) return true

        val prefix = island.name.lowercase() // "jawa", "sumatra", "kalimantan"
        val requiredChapterIds = (1..5).map { "${prefix}_$it" }

        val scores = getBestScores(uid)
        val islandScores = scores.filter { it.chapterId.startsWith(prefix, ignoreCase = true) }

        // Syarat: Semua 5 chapter dari pulau ini sudah bernilai 100
        val allPerfect = requiredChapterIds.all { reqId ->
            islandScores.any { it.chapterId.equals(reqId, ignoreCase = true) && it.bestScore == 100 }
        }

        if (allPerfect) {
            runCatching {
                db.collection("users")
                    .document(uid)
                    .collection("medals")
                    .document(island.name)
                    .set(
                        mapOf(
                            "island" to island.name,
                            "islandDisplayName" to island.displayName,
                            "unlocked" to true,
                            "earnedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    ).await()
            }
            return true
        }

        return false
    }

    override suspend fun getEarnedMedals(uid: String): List<Island> {
        if (uid.isBlank()) return emptyList()
        return runCatching {
            val snapshot = db.collection("users")
                .document(uid)
                .collection("medals")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val islandStr = doc.getString("island") ?: doc.id
                Island.entries.find { it.name.equals(islandStr, ignoreCase = true) }
            }
        }.getOrDefault(emptyList())
    }

    private fun getIslandFromChapterId(chapterId: String): Island? {
        return when {
            chapterId.startsWith("jawa", ignoreCase = true) -> Island.JAWA
            chapterId.startsWith("sumatra", ignoreCase = true) -> Island.SUMATRA
            chapterId.startsWith("kalimantan", ignoreCase = true) -> Island.KALIMANTAN
            else -> null
        }
    }
}
