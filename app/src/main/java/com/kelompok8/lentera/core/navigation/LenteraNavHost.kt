package com.kelompok8.lentera.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok8.lentera.auth.LoginScreen
import com.kelompok8.lentera.auth.RegisterScreen
import com.kelompok8.lentera.game.GameScreen
import com.kelompok8.lentera.home.HomeScreen
import com.kelompok8.lentera.leaderboard.LeaderboardScreen
import com.kelompok8.lentera.learn.LearnScreen
import com.kelompok8.lentera.map.ChapterSelectScreen
import com.kelompok8.lentera.map.PlayScreen
import com.kelompok8.lentera.score.ScoreScreen
import com.kelompok8.lentera.settings.SettingsScreen

private data class BottomItem(val route: String, val label: String, val icon: String)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Beranda", "🏠"),
    BottomItem(Routes.PLAY, "Bermain", "🎮"),
    BottomItem(Routes.LEARN, "Belajar", "📖"),
    BottomItem(Routes.SETTINGS, "Pengaturan", "⚙️")
)

@Composable
fun LenteraApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // Bottom bar cuma muncul di 4 halaman utama, bukan di login atau saat main
    val showBottomBar = bottomItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigateToTab(item.route) },
                            icon = { Text(item.icon) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LenteraNavHost(navController, Modifier.padding(innerPadding))
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun LenteraNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(onIslandClick = { island -> navController.navigate(Routes.chapterSelect(island)) })
        }
        composable(Routes.PLAY) {
            PlayScreen(
                onIslandClick = { island -> navController.navigate(Routes.chapterSelect(island)) },
                onLeaderboardClick = { navController.navigate(Routes.LEADERBOARD) }
            )
        }
        composable(Routes.LEARN) { LearnScreen() }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.CHAPTER_SELECT,
            arguments = listOf(navArgument("island") { type = NavType.StringType })
        ) { entry ->
            ChapterSelectScreen(
                island = entry.arguments?.getString("island").orEmpty(),
                onChapterClick = { chapterId -> navController.navigate(Routes.game(chapterId)) }
            )
        }
        composable(
            Routes.GAME,
            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
        ) { entry ->
            val chapterId = entry.arguments?.getString("chapterId").orEmpty()
            GameScreen(
                chapterId = chapterId,
                onFinished = { correct ->
                    navController.navigate(Routes.score(chapterId, correct)) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Routes.SCORE,
            arguments = listOf(
                navArgument("chapterId") { type = NavType.StringType },
                navArgument("correct") { type = NavType.IntType }
            )
        ) { entry ->
            ScoreScreen(
                chapterId = entry.arguments?.getString("chapterId").orEmpty(),
                correctCount = entry.arguments?.getInt("correct") ?: 0,
                onDone = { navController.popBackStack() }
            )
        }
        composable(Routes.LEADERBOARD) { LeaderboardScreen() }
    }
}
