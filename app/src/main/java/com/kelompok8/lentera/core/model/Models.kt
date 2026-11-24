package com.kelompok8.lentera.core.model

// File ini adalah "kontrak" data bersama. Kalau mau ubah, diskusikan dulu di grup
// karena dipakai oleh hampir semua fitur.

data class User(
    val uid: String,
    val name: String,
    val coin: Int = 0,
    val email: String,
    val gender: String = "",
    val location: String = "",
    val avatarId: Int = 0,      // 0..3, urutan sama dengan pilihan avatar di halaman Register
    val level: String = "Pemula"
)

enum class Island(val displayName: String, val coin: Int) {
    JAWA("Jawa", 50),
    SUMATRA("Sumatra", 50),
    KALIMANTAN("Kalimantan", 50)
}

data class Chapter(
    val id: String,          // format: "jawa_1" sampai "kalimantan_5"
    val island: Island,
    val number: Int,         // 1..5
    val title: String
)

// Tiap chapter isinya 10 soal: 4 pilihan ganda, 3 blok kata, 3 susun huruf
sealed class Question {
    abstract val id: String
    abstract val prompt: String
    abstract val imageName: String?   // nama file gambar di drawable/assets, boleh null

    data class MultipleChoice(
        override val id: String,
        override val prompt: String,
        override val imageName: String? = null,
        val options: List<String>,
        val correctIndex: Int
    ) : Question()

    data class WordBlock(
        override val id: String,
        override val prompt: String,
        override val imageName: String? = null,
        val blocks: List<String>,        // blok kata yang ditampilkan (sudah termasuk pengecoh)
        val answer: List<String>         // urutan blok yang benar
    ) : Question()

    data class LetterArrange(
        override val id: String,
        override val prompt: String,
        override val imageName: String? = null,
        val letters: List<Char>,         // huruf acak yang ditampilkan
        val answer: String
    ) : Question()
}

// Hasil satu kali main chapter, dikirim dari fitur game ke fitur skor
data class ChapterResult(
    val chapterId: String,
    val correctCount: Int,
    val totalQuestions: Int = 10
) {
    val score: Int get() = correctCount * 10
}

// Nilai terbaik per chapter yang disimpan untuk progres, medali, dan leaderboard
data class BestScore(
    val uid: String,
    val chapterId: String,
    val bestScore: Int
)

// Data medali per pulau
data class Medal(
    val island: Island,
    val unlocked: Boolean = true
)

data class LeaderboardEntry(
    val uid: String,
    val rank: Int,
    val name: String,
    val totalScore: Int,
    val avatarId: Int = 0
) {
    val points: Int get() = totalScore
    val avatarRes: Int get() = when (Math.abs(avatarId) % 4) {
        0 -> com.kelompok8.lentera.R.drawable.avatar1png
        1 -> com.kelompok8.lentera.R.drawable.avatar2png
        2 -> com.kelompok8.lentera.R.drawable.avatar3png
        else -> com.kelompok8.lentera.R.drawable.avatar4png
    }
    val flagRes: Int get() = com.kelompok8.lentera.R.drawable.avatar1png
    val badgeRes: Int get() = com.kelompok8.lentera.R.drawable.coin
}

enum class GlossaryCategory(val displayName: String) {
    PAKAIAN_ADAT("Pakaian Adat"),
    RUMAH_ADAT("Rumah Adat"),
    MAKANAN("Makanan Tradisional"),
    TARIAN("Tarian Adat"),
    LAGU("Lagu Daerah")
}

data class GlossaryItem(
    val id: String,
    val province: String,
    val island: Island,
    val category: GlossaryCategory,
    val name: String,
    val description: String,
    val imageName: String? = null
)
