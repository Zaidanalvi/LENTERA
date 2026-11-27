package com.kelompok8.lentera.learn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.learn.data.GlossaryRepositoryImpl
import com.kelompok8.lentera.learn.ui.LearnRoute
import com.kelompok8.lentera.learn.ui.LearnViewModel
import kotlinx.coroutines.CancellationException

@Composable
fun LearnScreen() {
    val context = LocalContext.current.applicationContext
    val repository = remember { GlossaryRepositoryImpl(context) }
    val learnViewModel: LearnViewModel = viewModel(factory = LearnViewModel.factory(repository))

    // Tampilkan data sesi dulu, lalu ganti dengan profil lengkap (koin dan avatar)
    val user by produceState(initialValue = AppContainer.authRepository.currentUser()) {
        try {
            AppContainer.authRepository.getProfile()?.let { value = it }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Profil gagal dimuat: tetap pakai data sesi
        }
    }

    LearnRoute(viewModel = learnViewModel, user = user)
}