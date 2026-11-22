package com.kelompok8.lentera.learn.data

import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.Island
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GlossaryRepositoryTest {

    private val repo = GlossaryRepositoryImpl(
        readJson = { File("src/main/assets/glossary.json").readText() },
    )

    @Test
    fun jumlahProvinsiPerPulau() = runBlocking {
        assertEquals(10, repo.getProvinces(Island.SUMATRA).size)
        assertEquals(6, repo.getProvinces(Island.JAWA).size)
        assertEquals(5, repo.getProvinces(Island.KALIMANTAN).size)
    }

    @Test
    fun setiapProvinsiPunyaSemuaKategori() = runBlocking {
        Island.values().forEach { island ->
            repo.getProvinces(island).forEach { province ->
                GlossaryCategory.values().forEach { category ->
                    assertTrue("$province $category", repo.getItems(province, category).isNotEmpty())
                }
            }
        }
    }

    @Test
    fun itemSesuaiProvinsiDanKategori() = runBlocking {
        val items = repo.getItems("Aceh", GlossaryCategory.MAKANAN)
        assertTrue(items.any { it.name == "Mie Aceh" })
        assertTrue(items.all { it.province == "Aceh" && it.category == GlossaryCategory.MAKANAN })
    }

    @Test
    fun provinsiTidakAdaMengembalikanKosong() = runBlocking {
        assertTrue(repo.getItems("Atlantis", GlossaryCategory.TARIAN).isEmpty())
    }
}
