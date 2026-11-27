package com.kelompok8.lentera.leaderboard

import com.kelompok8.lentera.core.model.LeaderboardEntry
import com.kelompok8.lentera.core.model.LeaderboardFilter
import com.kelompok8.lentera.core.model.OriginRegion
import com.kelompok8.lentera.core.model.originRegionOf
import org.junit.Assert.assertEquals
import org.junit.Test

class LeaderboardRankTest {
    private fun entry(name: String, score: Int, location: String = "") =
        LeaderboardEntry(uid = name, rank = 0, name = name, totalScore = score, location = location)

    @Test
    fun skorSamaMendapatPeringkatSama() {
        val ranked = rankByScore(listOf(entry("a", 900), entry("b", 700), entry("c", 700), entry("d", 500)))
        assertEquals(listOf(1, 2, 2, 4), ranked.map { it.rank })
    }

    @Test
    fun daftarKosongTetapKosong() {
        assertEquals(emptyList<LeaderboardEntry>(), rankByScore(emptyList()))
    }

    @Test
    fun pemetaanProvinsiKePulauAsalSesuai() {
        assertEquals(OriginRegion.JAWA, originRegionOf("DI Yogyakarta"))
        assertEquals(OriginRegion.SUMATRA, originRegionOf("Aceh"))
        assertEquals(OriginRegion.KALIMANTAN, originRegionOf("Kalimantan Barat"))
        assertEquals(OriginRegion.LAINNYA, originRegionOf("Bali"))
        assertEquals(null, originRegionOf(""))
    }

    @Test
    fun filterProvinsiDanPulauMenghitungUlangRanking() {
        val all = listOf(
            entry("AcehTop", 1200, "Aceh"),
            entry("Jogja1", 900, "DI Yogyakarta"),
            entry("Jogja2", 900, "DI Yogyakarta"),
            entry("Jabar", 700, "Jawa Barat"),
            entry("Jogja3", 600, "di yogyakarta"),
            entry("Kalbar", 500, "Kalimantan Barat")
        )

        val byJogja = filterAndRankEntries(all, LeaderboardFilter.MyProvince("DI Yogyakarta"))
        assertEquals(listOf("Jogja1", "Jogja2", "Jogja3"), byJogja.map { it.name })
        assertEquals(listOf(1, 1, 3), byJogja.map { it.rank })

        val byJawa = filterAndRankEntries(all, LeaderboardFilter.ByRegion(OriginRegion.JAWA))
        assertEquals(listOf("Jogja1", "Jogja2", "Jabar", "Jogja3"), byJawa.map { it.name })
        assertEquals(listOf(1, 1, 3, 4), byJawa.map { it.rank })
    }
}
