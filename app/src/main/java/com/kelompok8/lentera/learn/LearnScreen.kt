package com.kelompok8.lentera.learn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.learn.data.GlossaryRepositoryImpl
import com.kelompok8.lentera.learn.ui.LearnRoute
import com.kelompok8.lentera.learn.ui.LearnViewModel

// Pintu masuk menu Belajar dari bottom bar. Isi halamannya ada di learn/ui (punya Noya)
@Composable
fun LearnScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val factory = remember { LearnViewModel.factory(GlossaryRepositoryImpl(context)) }
    val viewModel: LearnViewModel = viewModel(factory = factory)
    LearnRoute(viewModel = viewModel, onExit = onExit)
}
