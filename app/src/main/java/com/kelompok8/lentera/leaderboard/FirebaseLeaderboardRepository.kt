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
import com.kelompok8.lentera.core.model.LeaderboardFilter
import com.kelompok8.lentera.core.model.originRegionOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseLeaderboardRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : LeaderboardRepository {

    private val board get() = db.collection(COLLECTION)

    override suspend fun getOverallLeaderboard(
        page: Int,
        pageSize: Int,
        filter: LeaderboardFilter
    ): LeaderboardResult = coroutineScope {
        val skip = (page - 1).coerceAtLeast(0) * pageSize
        val fetchLimit = if (filter == LeaderboardFilter.Global) (skip + pageSize).coerceAtLeast(50) else 200
        val docs = board
            .orderBy(FIELD_TOTAL, Query.Direction.DESCENDING)
            .limit(fetchLimit.toLong())
            .get()
            .await()
            .documents

        val rawEntries = docs.map { it.toEntry() }
        // Akun lama yang belum punya field location di koleksi leaderboard dilengkapi otomatis dari users/{uid}
        val hydratedEntries = rawEntries.map { entry ->
            async {
                if (entry.location.isNotBlank()) return@async entry
                val profileLocation = runCatching {
                    db.collection("users").document(entry.uid).get().await().getString("location").orEmpty()
                }.getOrDefault("")
                if (profileLocation.isNotBlank()) {
                    runCatching {
                        board.document(entry.uid).set(mapOf("location" to profileLocation), SetOptions.merge()).await()
                    }
                    entry.copy(location = profileLocation)
                } else {
                    entry
                }
            }
        }.awaitAll()

        val ranked = filterAndRankEntries(hydratedEntries, filter)
        val total = if (filter == LeaderboardFilter.Global) {
            runCatching {
                board.count().get(AggregateSource.SERVER).await().count.toInt()
            }.getOrDefault(ranked.size)
        } else {
            ranked.size
        }
        LeaderboardResult(
            entries = ranked.drop(skip).take(pageSize),
            totalCount = maxOf(total, ranked.size)
        )
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
                "location" to profile.getString("location").orEmpty(),
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
        avatarId = getLong("avatarId")?.toInt() ?: 0,
        location = getString("location").orEmpty()
    )

    companion object {
        const val COLLECTION = "leaderboard"
        const val FIELD_TOTAL = "totalScore"
    }
}

internal fun filterAndRankEntries(
    sortedEntries: List<LeaderboardEntry>,
    filter: LeaderboardFilter
): List<LeaderboardEntry> {
    val filtered = when (filter) {
        LeaderboardFilter.Global -> sortedEntries
        is LeaderboardFilter.MyProvince -> {
            val target = filter.province.trim()
            if (target.isEmpty()) emptyList()
            else sortedEntries.filter { it.location.trim().equals(target, ignoreCase = true) }
        }
        is LeaderboardFilter.ByRegion -> {
            sortedEntries.filter { originRegionOf(it.location) == filter.region }
        }
    }
    return rankByScore(filtered)
}

internal fun rankByScore(sorted: List<LeaderboardEntry>): List<LeaderboardEntry> {
    var rank = 0
    return sorted.mapIndexed { i, entry ->
        if (i == 0 || entry.totalScore != sorted[i - 1].totalScore) rank = i + 1
        entry.copy(rank = rank)
    }
}
