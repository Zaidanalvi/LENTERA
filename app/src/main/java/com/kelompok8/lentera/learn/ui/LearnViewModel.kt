package com.kelompok8.lentera.learn.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kelompok8.lentera.core.data.GlossaryRepository
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.learn.data.GlossaryRepositoryImpl
import com.kelompok8.lentera.learn.data.ProvinceInfo
import com.kelompok8.lentera.learn.data.ProvinceInfoSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LearnUiState {
    object Loading : LearnUiState
    data class Error(val message: String) : LearnUiState
    data class Success(val provincesByIsland: Map<Island, List<String>>) : LearnUiState
}

class LearnViewModel(
    private val repository: GlossaryRepository,
    private val provinceInfoSource: ProvinceInfoSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LearnUiState>(LearnUiState.Loading)
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = LearnUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val preferred = listOf(Island.SUMATRA, Island.JAWA, Island.KALIMANTAN)
                val order = preferred + Island.entries.filterNot { it in preferred }
                LearnUiState.Success(
                    order.associateWith { repository.getProvinces(it) }.filterValues { it.isNotEmpty() },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LearnUiState.Error(e.message ?: "Gagal memuat data glosarium")
            }
        }
    }

    suspend fun getItems(province: String, category: GlossaryCategory): List<GlossaryItem> =
        repository.getItems(province, category)

    suspend fun getCategoryItems(province: String): Map<GlossaryCategory, List<GlossaryItem>> =
        GlossaryCategory.entries.associateWith { repository.getItems(province, it) }

    suspend fun getProvinceInfo(province: String): ProvinceInfo? =
        provinceInfoSource.getProvinceInfo(province)

    companion object {
        fun factory(repository: GlossaryRepositoryImpl): ViewModelProvider.Factory =
            viewModelFactory { initializer { LearnViewModel(repository, repository) } }
    }
}
