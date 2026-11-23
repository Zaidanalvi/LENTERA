package com.kelompok8.lentera.game

import com.kelompok8.lentera.core.model.Question
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GameLogicTest {
    private val bankJson = File("src/main/assets/${JsonQuestionRepository.BANK_FILE}").readText()
    private val repo = JsonQuestionRepository { bankJson }

    @Test
    fun fileBankSoalValidDanBerisiTigaPulau() {
        val banks = JsonQuestionRepository.parseBank(bankJson)
        assertEquals(setOf("jawa", "sumatra", "kalimantan"), banks.keys)
    }

    @Test
    fun kesalahanDiJsonDilaporkanDenganJelas() {
        fun errorOf(json: String): String =
            assertThrows(IllegalStateException::class.java) { JsonQuestionRepository.parseBank(json) }.message.orEmpty()

        val pg = """{"soal":"A?","pilihan":["a","b","c","d"],"jawaban":0}"""
        val bk = """{"soal":"Isi ___ .","jawaban":["x"],"pengecoh":["y"]}"""
        val sh = """{"soal":"Susun","jawaban":"KERIS"}"""
        fun bank(pg1: String = pg, bk1: String = bk, sh1: String = sh) =
            """{"jawa":{"pilihanGanda":[$pg1,$pg,$pg,$pg],"blokKata":[$bk1,$bk,$bk],"susunHuruf":[$sh1,$sh,$sh]}}"""

        assertEquals(setOf("jawa"), JsonQuestionRepository.parseBank(bank()).keys)
        assertTrue(errorOf(bank(pg1 = """{"soal":"A?","pilihan":["a","b"],"jawaban":5}""")).contains("jawa.pilihanGanda soal ke-1"))
        assertTrue(errorOf(bank(bk1 = """{"soal":"Isi ___ dan ___ .","jawaban":["x"]}""")).contains("jumlah \"___\""))
        assertTrue(errorOf(bank(sh1 = """{"soal":"Susun","jawaban":"RUMAH ADAT"}""")).contains("hanya boleh huruf"))
        assertTrue(errorOf("""{"jawa":{"pilihanGanda":[$pg]}}""").contains("minimal berisi"))
        assertTrue(errorOf("""{"jawa": [""").contains("Format JSON"))
    }

    @Test
    fun setiapChapterBerisi10SoalDenganKomposisiBenar() = runBlocking {
        for (island in listOf("jawa", "sumatra", "kalimantan")) {
            for (n in 1..5) {
                val qs = repo.getQuestions("${island}_$n")
                assertEquals(10, qs.size)
                assertEquals(4, qs.count { it is Question.MultipleChoice })
                assertEquals(3, qs.count { it is Question.WordBlock })
                assertEquals(3, qs.count { it is Question.LetterArrange })
                assertEquals("ID soal harus unik", 10, qs.map { it.id }.toSet().size)
            }
        }
    }

    @Test
    fun gridHurufBerisi30HurufDanMemuatSemuaHurufJawaban() = runBlocking {
        for (island in listOf("jawa", "sumatra", "kalimantan")) {
            repo.getQuestions("${island}_1").filterIsInstance<Question.LetterArrange>().forEach { q ->
                assertEquals(JsonQuestionRepository.GRID_SIZE, q.letters.size)
                val pool = q.letters.toMutableList()
                q.answer.forEach { ch -> assertTrue("$ch hilang di grid ${q.id}", pool.remove(ch)) }
            }
        }
    }

    @Test
    fun jumlahTempatKosongSamaDenganJumlahJawaban() = runBlocking {
        for (island in listOf("jawa", "sumatra", "kalimantan")) {
            for (n in 1..2) {
                repo.getQuestions("${island}_$n").filterIsInstance<Question.WordBlock>().forEach { q ->
                    val blanks = q.prompt.split(" ").count { it == JsonQuestionRepository.BLANK }
                    assertEquals(q.id, q.answer.size, blanks)
                    assertEquals("blok kata harus unik di ${q.id}", q.blocks.size, q.blocks.toSet().size)
                }
            }
        }
    }

    @Test
    fun jawabanBenarDariSetiapSoalLolosPemeriksaan() = runBlocking {
        repo.getQuestions("sumatra_2").forEach { q ->
            assertTrue(q.id, AnswerChecker.isCorrect(q, correctInput(q)))
        }
    }

    @Test
    fun pilihanGandaSalahDitolak() {
        val q = Question.MultipleChoice("x", "?", null, listOf("a", "b", "c", "d"), 2)
        assertFalse(AnswerChecker.isComplete(q, AnswerInput()))
        assertFalse(AnswerChecker.isCorrect(q, AnswerInput(selectedOption = 0)))
    }

    @Test
    fun blokKataUrutanSalahDitolak() {
        val q = Question.WordBlock("x", "?", null, listOf("khas", "Gudeg", "makanan"), listOf("Gudeg", "makanan", "khas"))
        assertTrue(AnswerChecker.isCorrect(q, AnswerInput(pickedIndices = listOf(1, 2, 0))))
        assertFalse(AnswerChecker.isCorrect(q, AnswerInput(pickedIndices = listOf(2, 1, 0))))
        val partial = AnswerInput(pickedIndices = listOf(1, null, 0))
        assertFalse(AnswerChecker.isComplete(q, partial))
        assertFalse(AnswerChecker.isCorrect(q, partial))
    }

    @Test
    fun susunHurufDenganHurufKembarTetapBenar() {
        val q = Question.LetterArrange("x", "?", null, listOf('N', 'A', 'S', 'X', 'M', 'A', 'Z'), "SAMAN")
        assertTrue(AnswerChecker.isComplete(q, AnswerInput(pickedIndices = listOf(0, 1, 2, 4, 5))))
        assertTrue(AnswerChecker.isCorrect(q, AnswerInput(pickedIndices = listOf(2, 5, 4, 1, 0))))
        assertTrue(AnswerChecker.isCorrect(q, AnswerInput(pickedIndices = listOf(2, 1, 4, 5, 0))))
        assertFalse(AnswerChecker.isCorrect(q, AnswerInput(pickedIndices = listOf(0, 1, 2, 4, 5))))
    }

    private fun correctInput(q: Question): AnswerInput = when (q) {
        is Question.MultipleChoice -> AnswerInput(selectedOption = q.correctIndex)
        is Question.WordBlock -> {
            val used = mutableSetOf<Int>()
            AnswerInput(pickedIndices = q.answer.map { word ->
                q.blocks.indices.first { it !in used && q.blocks[it] == word }.also { used += it }
            })
        }
        is Question.LetterArrange -> {
            val used = mutableSetOf<Int>()
            AnswerInput(pickedIndices = q.answer.map { ch ->
                q.letters.indices.first { it !in used && q.letters[it] == ch }.also { used += it }
            })
        }
    }
}
