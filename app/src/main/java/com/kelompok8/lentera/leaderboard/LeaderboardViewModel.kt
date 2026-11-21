package com.kelompok8.lentera.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.LeaderboardRepository
import com.kelompok8.lentera.core.model.LeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val canGoPrev: Boolean = false,
    val canGoNext: Boolean = false,
    val errorMessage: String? = null
)

class LeaderboardViewModel(
    private val repository: LeaderboardRepository = AppContainer.leaderboardRepository,
    private val pageSize: Int = 10
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        val targetPage = page.coerceAtLeast(1)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = repository.getOverallLeaderboard(page = targetPage, pageSize = pageSize)
                val totalCount = result.totalCount
                val computedTotalPages = ((totalCount + pageSize - 1) / pageSize).coerceAtLeast(1)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entries = result.entries,
                        currentPage = targetPage,
                        totalPages = computedTotalPages,
                        canGoPrev = targetPage > 1,
                        canGoNext = targetPage < computedTotalPages
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat papan peringkat"
                    )
                }
            }
        }
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.canGoNext && !state.isLoading) {
            loadPage(state.currentPage + 1)
        }
    }

    fun prevPage() {
        val state = _uiState.value
        if (state.canGoPrev && !state.isLoading) {
            loadPage(state.currentPage - 1)
        }
    }
}
