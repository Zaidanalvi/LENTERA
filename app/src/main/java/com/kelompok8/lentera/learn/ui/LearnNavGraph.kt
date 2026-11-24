package com.kelompok8.lentera.learn.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.learn.ui.screens.MaterialDetailScreen
import com.kelompok8.lentera.learn.ui.screens.SelectMaterialScreen
import com.kelompok8.lentera.learn.ui.screens.SubMaterialScreen
import com.kelompok8.lentera.learn.ui.screens.TableOfContentsScreen

object LearnRoutes {
    const val PROVINCES = "learn/provinces"
    const val TOC = "learn/toc/{province}"
    const val SUB = "learn/sub/{province}/{category}"
    const val DETAIL = "learn/detail/{province}/{category}/{itemId}"

    fun toc(province: String) = "learn/toc/${Uri.encode(province)}"
    fun sub(province: String, category: GlossaryCategory) =
        "learn/sub/${Uri.encode(province)}/${category.name}"
    fun detail(province: String, category: GlossaryCategory, itemId: String) =
        "learn/detail/${Uri.encode(province)}/${category.name}/$itemId"
}

@Composable
fun LearnRoute(
    viewModel: LearnViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    when (val s = state) {
        LearnUiState.Loading -> CenteredBox(modifier) { CircularProgressIndicator() }
        is LearnUiState.Error -> CenteredBox(modifier) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(24.dp),
            ) {
                Text(s.message)
                Button(onClick = viewModel::load) { Text("Coba lagi") }
            }
        }
        is LearnUiState.Success -> LearnNavHost(viewModel, s, onExit, modifier)
    }
}

@Composable
private fun LearnNavHost(
    viewModel: LearnViewModel,
    data: LearnUiState.Success,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nav = rememberNavController()
    val knownProvinces = data.provincesByIsland.values.flatten().toSet()

    NavHost(navController = nav, startDestination = LearnRoutes.PROVINCES, modifier = modifier) {

        composable(LearnRoutes.PROVINCES) {
            SelectMaterialScreen(
                provincesByIsland = data.provincesByIsland,
                onProvinceClick = { nav.navigate(LearnRoutes.toc(it)) },
                onBack = onExit,
            )
        }

        composable(
            LearnRoutes.TOC,
            arguments = listOf(navArgument("province") { type = NavType.StringType }),
        ) { entry ->
            val province = entry.arguments?.getString("province")?.let(Uri::decode)
            if (province == null || province !in knownProvinces) {
                NotFound { nav.popBackStack() }
            } else {
                TableOfContentsScreen(
                    province = province,
                    onCategoryClick = { nav.navigate(LearnRoutes.sub(province, it)) },
                    onBack = { nav.popBackStack() },
                )
            }
        }

        composable(
            LearnRoutes.SUB,
            arguments = listOf(
                navArgument("province") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
            ),
        ) { entry ->
            val province = entry.arguments?.getString("province")?.let(Uri::decode)
            val category = entry.arguments?.getString("category")?.toCategoryOrNull()
            if (province == null || category == null || province !in knownProvinces) {
                NotFound { nav.popBackStack() }
            } else {
                val items by produceState<List<GlossaryItem>?>(null, province, category) {
                    value = viewModel.getItems(province, category)
                }
                SubMaterialScreen(
                    province = province,
                    category = category,
                    items = items,
                    onItemClick = { nav.navigate(LearnRoutes.detail(province, category, it.id)) },
                    onBack = { nav.popBackStack() },
                )
            }
        }

        composable(
            LearnRoutes.DETAIL,
            arguments = listOf(
                navArgument("province") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType },
            ),
        ) { entry ->
            val province = entry.arguments?.getString("province")?.let(Uri::decode)
            val category = entry.arguments?.getString("category")?.toCategoryOrNull()
            val itemId = entry.arguments?.getString("itemId")
            if (province == null || category == null || itemId == null || province !in knownProvinces) {
                NotFound { nav.popBackStack() }
            } else {
                val items by produceState<List<GlossaryItem>?>(null, province, category) {
                    value = viewModel.getItems(province, category)
                }
                val list = items
                if (list == null) {
                    CenteredBox { CircularProgressIndicator() }
                } else {
                    val item = list.firstOrNull { it.id == itemId }
                    if (item == null) {
                        NotFound { nav.popBackStack() }
                    } else {
                        MaterialDetailScreen(item = item, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}

private fun String.toCategoryOrNull(): GlossaryCategory? =
    GlossaryCategory.values().firstOrNull { it.name == this }

@Composable
private fun CenteredBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun NotFound(onBack: () -> Unit) {
    CenteredBox {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Materi tidak ditemukan")
            Button(onClick = onBack) { Text("Kembali") }
        }
    }
}
