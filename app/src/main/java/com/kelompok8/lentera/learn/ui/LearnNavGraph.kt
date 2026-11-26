package com.kelompok8.lentera.learn.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.User
import com.kelompok8.lentera.learn.data.ProvinceInfo
import com.kelompok8.lentera.learn.ui.screens.CategoryDetailScreen
import com.kelompok8.lentera.learn.ui.screens.IslandListScreen
import com.kelompok8.lentera.learn.ui.screens.ProvinceScreen

object LearnRoutes {
    const val ISLANDS = "learn/islands"
    const val PROVINCE = "learn/province/{province}"
    const val CATEGORY = "learn/category/{province}/{category}"

    fun province(province: String) = "learn/province/${Uri.encode(province)}"
    fun category(province: String, category: GlossaryCategory) =
        "learn/category/${Uri.encode(province)}/${category.name}"
}

@Composable
fun LearnRoute(
    viewModel: LearnViewModel,
    user: User?,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    when (val s = state) {
        LearnUiState.Loading -> LearnBackground(modifier) {
            CircularProgressIndicator(
                color = LearnColors.Neon,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        is LearnUiState.Error -> LearnBackground(modifier) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(s.message, color = Color.White)
                Button(onClick = viewModel::load) { Text("Coba lagi") }
            }
        }
        is LearnUiState.Success -> LearnNavHost(viewModel, s, user, modifier)
    }
}

@Composable
private fun LearnNavHost(
    viewModel: LearnViewModel,
    data: LearnUiState.Success,
    user: User?,
    modifier: Modifier = Modifier,
) {
    val nav = rememberNavController()
    val knownProvinces = data.provincesByIsland.values.flatten().toSet()

    NavHost(navController = nav, startDestination = LearnRoutes.ISLANDS, modifier = modifier) {

        composable(LearnRoutes.ISLANDS) {
            IslandListScreen(
                provincesByIsland = data.provincesByIsland,
                user = user,
                onProvinceClick = { nav.navigate(LearnRoutes.province(it)) },
            )
        }

        composable(
            LearnRoutes.PROVINCE,
            arguments = listOf(navArgument("province") { type = NavType.StringType }),
        ) { entry ->
            val province = entry.arguments?.getString("province")?.let(Uri::decode)
            if (province == null || province !in knownProvinces) {
                NotFound { nav.popBackStack() }
            } else {
                val info by produceState<ProvinceInfo?>(null, province) {
                    value = viewModel.getProvinceInfo(province)
                }
                val previews by produceState<Map<GlossaryCategory, List<GlossaryItem>>?>(null, province) {
                    value = viewModel.getCategoryItems(province)
                }
                ProvinceScreen(
                    province = province,
                    info = info,
                    previews = previews,
                    user = user,
                    onCategoryClick = { nav.navigate(LearnRoutes.category(province, it)) },
                )
            }
        }

        composable(
            LearnRoutes.CATEGORY,
            arguments = listOf(
                navArgument("province") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
            ),
        ) { entry ->
            val province = entry.arguments?.getString("province")?.let(Uri::decode)
            val category = GlossaryCategory.entries.firstOrNull {
                it.name == entry.arguments?.getString("category")
            }
            if (province == null || category == null || province !in knownProvinces) {
                NotFound { nav.popBackStack() }
            } else {
                val items by produceState<List<GlossaryItem>?>(null, province, category) {
                    value = viewModel.getItems(province, category)
                }
                CategoryDetailScreen(
                    province = province,
                    category = category,
                    items = items,
                    user = user,
                    onBack = { nav.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun NotFound(onBack: () -> Unit) {
    LearnBackground {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Materi tidak ditemukan", color = Color.White)
            Button(onClick = onBack) { Text("Kembali") }
        }
    }
}
