package com.kelompok8.lentera.leaderboard

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.kelompok8.lentera.core.data.LeaderboardRepository
import com.kelompok8.lentera.core.data.LeaderboardResult
import com.kelompok8.lentera.core.model.LeaderboardEntry
import kotlinx.coroutines.tasks.await

class FirebaseLeaderboardRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : LeaderboardRepository {

    private val board get() = db.collection(COLLECTION)

    override suspend fun getOverallLeaderboard(page: Int, pageSize: Int): LeaderboardResult {
        val skip = (page - 1).coerceAtLeast(0) * pageSize
        val docs = board
            .orderBy(FIELD_TOTAL, Query.Direction.DESCENDING)
            .limit((skip + pageSize).toLong())
            .get()
            .await()
            .documents
        val ranked = rankByScore(docs.map { it.toEntry() })
        val total = runCatching {
            board.count().get(AggregateSource.SERVER).await().count.toInt()
        }.getOrDefault(docs.size)
        return LeaderboardResult(entries = ranked.drop(skip), totalCount = maxOf(total, docs.size))
    }

    override suspend fun syncPlayer(uid: String) {
        if (uid.isBlank()) return
        val userRef = db.collection("users").document(uid)
        val profile = userRef.get().await()
        val totalScore = userRef.collection("scores").get().await()
            .documents
            .sumOf { it.getLong("bestScore") ?: 0L }
            .toInt()
        board.document(uid).set(
            mapOf(
                "uid" to uid,
                "name" to (profile.getString("name") ?: "Pemain"),
                "avatarId" to (profile.getLong("avatarId") ?: 0L),
                FIELD_TOTAL to totalScore,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
    }

    private fun DocumentSnapshot.toEntry() = LeaderboardEntry(
        uid = id,
        rank = 0,
        name = getString("name") ?: "Pemain",
        totalScore = getLong(FIELD_TOTAL)?.toInt() ?: 0,
        avatarId = getLong("avatarId")?.toInt() ?: 0
    )

    companion object {
        const val COLLECTION = "leaderboard"
        const val FIELD_TOTAL = "totalScore"
    }
}

internal fun rankByScore(sorted: List<LeaderboardEntry>): List<LeaderboardEntry> {
    var rank = 0
    return sorted.mapIndexed { i, entry ->
        if (i == 0 || entry.totalScore != sorted[i - 1].totalScore) rank = i + 1
        entry.copy(rank = rank)
    }
}
