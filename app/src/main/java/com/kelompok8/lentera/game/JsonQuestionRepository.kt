package com.kelompok8.lentera.game

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.kelompok8.lentera.core.data.QuestionRepository
import com.kelompok8.lentera.core.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class JsonQuestionRepository(
    private val loadJson: () -> String
) : QuestionRepository {
    constructor(context: Context) : this({
        context.applicationContext.assets.open(BANK_FILE).bufferedReader(Charsets.UTF_8).use { it.readText() }
    })

    private val banks: Map<String, QuestionBank> by lazy { parseBank(loadJson()) }

    override suspend fun getQuestions(chapterId: String): List<Question> = withContext(Dispatchers.IO) {
        val island = chapterId.substringBefore("_").lowercase()
        val number = chapterId.substringAfter("_").toIntOrNull() ?: 1
        val bank = banks[island] ?: error("Bank soal untuk pulau \"$island\" belum ada")
        val random = Random(chapterId.hashCode())

        val multipleChoice = bank.multipleChoice.rotate(number, MC_PER_CHAPTER)
            .mapIndexed { i, q -> q.copy(id = "${chapterId}_pg_${i + 1}") }

        val wordBlocks = bank.wordBlocks.rotate(number, WB_PER_CHAPTER)
            .mapIndexed { i, q -> q.copy(id = "${chapterId}_bk_${i + 1}", blocks = q.blocks.shuffled(random)) }

        val letterArranges = bank.letterArranges.rotate(number, LA_PER_CHAPTER)
            .mapIndexed { i, q -> q.copy(id = "${chapterId}_sh_${i + 1}", letters = letterGrid(q.answer, random)) }

        multipleChoice + wordBlocks + letterArranges
    }

    companion object {
        const val BANK_FILE = "bank_soal.json"

        const val MC_PER_CHAPTER = 4
        const val WB_PER_CHAPTER = 3
        const val LA_PER_CHAPTER = 3
        const val BLANK = "___"

        const val GRID_COLUMNS = 6
        const val GRID_SIZE = 30

        internal fun letterGrid(answer: String, random: Random): List<Char> {
            val letters = answer.uppercase().toList()
            val fillers = List((GRID_SIZE - letters.size).coerceAtLeast(0)) { 'A' + random.nextInt(26) }
            return (letters + fillers).shuffled(random)
        }

        internal fun parseBank(json: String): Map<String, QuestionBank> {
            val type = object : TypeToken<Map<String, IslandJson>>() {}.type
            val raw: Map<String, IslandJson> = try {
                Gson().fromJson(json, type)
            } catch (e: JsonParseException) {
                throw IllegalStateException("Format JSON bank soal salah: ${e.message}", e)
            } ?: error("Bank soal kosong")

            return raw.mapKeys { it.key.lowercase() }.mapValues { (island, data) ->
                QuestionBank(
                    multipleChoice = data.pilihanGanda.orEmpty()
                        .mapIndexed { i, q -> q.toQuestion(Where(island, "pilihanGanda", i)) }
                        .requireAtLeast(MC_PER_CHAPTER, island, "pilihanGanda"),
                    wordBlocks = data.blokKata.orEmpty()
                        .mapIndexed { i, q -> q.toQuestion(Where(island, "blokKata", i)) }
                        .requireAtLeast(WB_PER_CHAPTER, island, "blokKata"),
                    letterArranges = data.susunHuruf.orEmpty()
                        .mapIndexed { i, q -> q.toQuestion(Where(island, "susunHuruf", i)) }
                        .requireAtLeast(LA_PER_CHAPTER, island, "susunHuruf")
                )
            }
        }

        private fun <T> List<T>.rotate(chapterNumber: Int, count: Int): List<T> {
            val start = ((chapterNumber - 1).coerceAtLeast(0) * count) % size
            return List(count) { this[(start + it) % size] }
        }

        private fun <T> List<T>.requireAtLeast(min: Int, island: String, type: String): List<T> {
            check(size >= min) { "$island.$type minimal berisi $min soal (sekarang $size)" }
            return this
        }
    }
}

internal class QuestionBank(
    val multipleChoice: List<Question.MultipleChoice>,
    val wordBlocks: List<Question.WordBlock>,
    val letterArranges: List<Question.LetterArrange>
)

private class IslandJson(
    val pilihanGanda: List<PilihanGandaJson>?,
    val blokKata: List<BlokKataJson>?,
    val susunHuruf: List<SusunHurufJson>?
)

private class PilihanGandaJson(val soal: String?, val pilihan: List<String>?, val jawaban: Int?, val gambar: String?)
private class BlokKataJson(val soal: String?, val jawaban: List<String>?, val pengecoh: List<String>?)
private class SusunHurufJson(val soal: String?, val jawaban: String?, val gambar: String?)

private class Where(val island: String, val type: String, val index: Int) {
    override fun toString() = "$island.$type soal ke-${index + 1}"
}

private fun String?.requireText(where: Where, field: String): String {
    check(!isNullOrBlank()) { "$where: \"$field\" belum diisi" }
    return this
}

private fun PilihanGandaJson.toQuestion(where: Where): Question.MultipleChoice {
    val options = pilihan.orEmpty()
    check(options.size >= 2) { "$where: \"pilihan\" minimal 2" }
    val answer = jawaban ?: error("$where: \"jawaban\" belum diisi")
    check(answer in options.indices) { "$where: \"jawaban\" harus 0 sampai ${options.lastIndex}" }
    return Question.MultipleChoice(
        id = "",
        prompt = soal.requireText(where, "soal"),
        imageName = gambar,
        options = options,
        correctIndex = answer
    )
}

private fun BlokKataJson.toQuestion(where: Where): Question.WordBlock {
    val prompt = soal.requireText(where, "soal")
    val answers = jawaban.orEmpty()
    val blanks = prompt.split(JsonQuestionRepository.BLANK).size - 1
    check(answers.isNotEmpty()) { "$where: \"jawaban\" belum diisi" }
    check(blanks == answers.size) {
        "$where: jumlah \"${JsonQuestionRepository.BLANK}\" di soal ($blanks) harus sama dengan jumlah jawaban (${answers.size})"
    }
    val blocks = answers + pengecoh.orEmpty()
    check(blocks.size == blocks.toSet().size) { "$where: ada kata yang sama di jawaban/pengecoh" }
    return Question.WordBlock(id = "", prompt = prompt, blocks = blocks, answer = answers)
}

private fun SusunHurufJson.toQuestion(where: Where): Question.LetterArrange {
    val answer = jawaban.requireText(where, "jawaban").trim().uppercase()
    check(answer.all { it in 'A'..'Z' }) { "$where: \"jawaban\" hanya boleh huruf A-Z tanpa spasi" }
    check(answer.length <= JsonQuestionRepository.GRID_SIZE) { "$where: \"jawaban\" terlalu panjang" }
    return Question.LetterArrange(
        id = "",
        prompt = soal.requireText(where, "soal"),
        imageName = gambar,
        letters = emptyList(),
        answer = answer
    )
}
