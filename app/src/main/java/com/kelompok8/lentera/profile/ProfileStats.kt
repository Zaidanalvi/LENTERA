package com.kelompok8.lentera.profile

import com.kelompok8.lentera.core.model.BestScore
import com.kelompok8.lentera.core.model.Island

const val CHAPTERS_PER_ISLAND = 5
const val MAX_POINTS_PER_ISLAND = CHAPTERS_PER_ISLAND * 100

data class LevelInfo(
    val name: String,
    val minPoints: Int,
    val nextName: String?,
    val nextAt: Int?
) {
    // 0f..1f, seberapa jauh menuju level berikutnya
    fun progress(points: Int): Float {
        if (nextAt == null) return 1f
        return ((points - minPoints).toFloat() / (nextAt - minPoints)).coerceIn(0f, 1f)
    }
}

// Batas level berdasarkan total poin (maksimal 1.500 dari 15 chapter)
private val levels = listOf(
    0 to "Pemula",
    300 to "Penjelajah",
    700 to "Ahli Budaya",
    1200 to "Maestro Nusantara"
)

fun levelFor(points: Int): LevelInfo {
    val index = levels.indexOfLast { points >= it.first }.coerceAtLeast(0)
    val (min, name) = levels[index]
    val next = levels.getOrNull(index + 1)
    return LevelInfo(name = name, minPoints = min, nextName = next?.second, nextAt = next?.first)
}

data class IslandProgress(
    val island: Island,
    val chaptersPlayed: Int,
    val perfectChapters: Int,
    val points: Int,
    val hasMedal: Boolean
)

fun buildIslandProgress(scores: List<BestScore>, medals: Set<Island>): List<IslandProgress> =
    Island.entries.map { island ->
        val prefix = island.name.lowercase() + "_"
        val islandScores = scores.filter { it.chapterId.lowercase().startsWith(prefix) }
        IslandProgress(
            island = island,
            chaptersPlayed = islandScores.size.coerceAtMost(CHAPTERS_PER_ISLAND),
            perfectChapters = islandScores.count { it.bestScore >= 100 },
            points = islandScores.sumOf { it.bestScore }.coerceAtMost(MAX_POINTS_PER_ISLAND),
            hasMedal = island in medals
        )
    }
