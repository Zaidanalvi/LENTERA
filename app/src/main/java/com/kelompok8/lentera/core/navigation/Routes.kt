package com.kelompok8.lentera.core.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val HOME = "home"
    const val PLAY = "play"
    const val LEARN = "learn"
    const val SETTINGS = "settings"

    const val CHAPTER_SELECT = "chapter_select/{island}"
    const val GAME = "game/{chapterId}"
    const val SCORE = "score/{chapterId}/{correct}"
    const val LEADERBOARD = "leaderboard"

    fun chapterSelect(island: String) = "chapter_select/$island"
    fun game(chapterId: String) = "game/$chapterId"
    fun score(chapterId: String, correct: Int) = "score/$chapterId/$correct"
}
