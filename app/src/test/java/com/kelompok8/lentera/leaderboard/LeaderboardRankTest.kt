package com.kelompok8.lentera.leaderboard

import com.kelompok8.lentera.core.model.LeaderboardEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class LeaderboardRankTest {
    private fun entry(name: String, score: Int) = LeaderboardEntry(uid = name, rank = 0, name = name, totalScore = score)

    @Test
    fun skorSamaMendapatPeringkatSama() {
        val ranked = rankByScore(listOf(entry("a", 900), entry("b", 700), entry("c", 700), entry("d", 500)))
        assertEquals(listOf(1, 2, 2, 4), ranked.map { it.rank })
    }

    @Test
    fun daftarKosongTetapKosong() {
        assertEquals(emptyList<LeaderboardEntry>(), rankByScore(emptyList()))
    }
}
