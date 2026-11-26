package com.kelompok8.lentera.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.QuestionRepository
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.model.Question
import com.kelompok8.lentera.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnswerInput(
    val selectedOption: Int? = null,
    val pickedIndices: List<Int?> = emptyList()
) {
    val filledCount: Int get() = pickedIndices.count { it != null }
}

data class GameUiState(
    val isLoading: Boolean = true,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val input: AnswerInput = AnswerInput(),
    val checkedCorrect: Boolean? = null,
    val correctCount: Int = 0,
    val medalCount: Int = 0,
    val avatarId: Int = 0,
    val isFinished: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isChecked: Boolean get() = checkedCorrect != null
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val canCheck: Boolean get() = !isChecked && currentQuestion?.let { AnswerChecker.isComplete(it, input) } == true
    val medalTotal: Int get() = Island.entries.size
}

object AnswerChecker {
    fun requiredPicks(question: Question): Int = when (question) {
        is Question.MultipleChoice -> 0
        is Question.WordBlock -> question.answer.size
        is Question.LetterArrange -> question.answer.length
    }

    fun isComplete(question: Question, input: AnswerInput): Boolean = when (question) {
        is Question.MultipleChoice -> input.selectedOption != null
        else -> input.filledCount == requiredPicks(question)
    }

    fun isCorrect(question: Question, input: AnswerInput): Boolean = when (question) {
        is Question.MultipleChoice -> input.selectedOption == question.correctIndex
        is Question.WordBlock -> input.pickedIndices.map { it?.let(question.blocks::getOrNull) } == question.answer
        is Question.LetterArrange ->
            input.pickedIndices.mapNotNull { it?.let(question.letters::getOrNull) }.joinToString("")
                .equals(question.answer, ignoreCase = true)
    }
}

class GameViewModel(
    private val chapterId: String,
    private val repository: QuestionRepository,
    private val loadProfile: suspend () -> User? = { AppContainer.authRepository.getProfile() },
    private val loadMedalCount: suspend (String) -> Int = { uid -> AppContainer.scoreRepository.getEarnedMedals(uid).size }
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
        loadHeader()
    }

    private fun loadHeader() {
        viewModelScope.launch {
            val user = try { loadProfile() } catch (e: Exception) { null } ?: return@launch
            _uiState.update { it.copy(avatarId = user.avatarId) }
            val medals = try { loadMedalCount(user.uid) } catch (e: Exception) { 0 }
            _uiState.update { it.copy(medalCount = medals) }
        }
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { GameUiState(isLoading = true, medalCount = it.medalCount, avatarId = it.avatarId) }
            try {
                val questions = repository.getQuestions(chapterId)
                _uiState.update {
                    if (questions.isEmpty()) {
                        it.copy(isLoading = false, errorMessage = "Soal untuk chapter ini belum tersedia")
                    } else {
                        it.copy(isLoading = false, questions = questions)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat soal") }
            }
        }
    }

    fun selectOption(index: Int) = editInput { it.copy(selectedOption = index) }

    fun pick(index: Int) {
        val question = _uiState.value.currentQuestion ?: return
        editInput { input ->
            val picked = input.pickedIndices
            when {
                index in picked -> input
                input.filledCount >= AnswerChecker.requiredPicks(question) -> input
                question is Question.WordBlock && null in picked -> {
                    val slot = picked.indexOf(null)
                    input.copy(pickedIndices = picked.toMutableList().also { it[slot] = index })
                }
                else -> input.copy(pickedIndices = picked + index)
            }
        }
    }

    fun unpick(position: Int) {
        val question = _uiState.value.currentQuestion ?: return
        editInput { input ->
            if (position !in input.pickedIndices.indices) return@editInput input
            val picked = input.pickedIndices.toMutableList()
            if (question is Question.WordBlock) picked[position] = null else picked.removeAt(position)
            input.copy(pickedIndices = picked)
        }
    }

    fun clearInput() = editInput { AnswerInput() }

    fun checkAnswer() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (!state.canCheck) return
        val correct = AnswerChecker.isCorrect(question, state.input)
        _uiState.update {
            it.copy(
                checkedCorrect = correct,
                correctCount = if (correct) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun next() {
        val state = _uiState.value
        if (!state.isChecked) return
        if (state.isLastQuestion) {
            _uiState.update { it.copy(isFinished = true) }
        } else {
            _uiState.update {
                it.copy(currentIndex = it.currentIndex + 1, input = AnswerInput(), checkedCorrect = null)
            }
        }
    }

    private fun editInput(transform: (AnswerInput) -> AnswerInput) {
        _uiState.update { if (it.isChecked) it else it.copy(input = transform(it.input)) }
    }
}
