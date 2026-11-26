package com.kelompok8.lentera.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.core.ui.LenteraBackground
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok8.lentera.R
import com.kelompok8.lentera.auth.LoginScreen
import com.kelompok8.lentera.auth.RegisterScreen
import com.kelompok8.lentera.auth.WelcomeScreen
import com.kelompok8.lentera.core.data.AppContainer
import com.kelompok8.lentera.core.data.UserSession
import com.kelompok8.lentera.core.ui.LocalOpenProfile
import com.kelompok8.lentera.profile.EditProfileScreen
import com.kelompok8.lentera.profile.ProfileScreen
import com.kelompok8.lentera.game.GameScreen
import com.kelompok8.lentera.home.HomeScreen
import com.kelompok8.lentera.leaderboard.LeaderboardScreen
import com.kelompok8.lentera.leaderboard.LeaderboardViewModel
import com.kelompok8.lentera.learn.data.GlossaryRepositoryImpl
import com.kelompok8.lentera.learn.ui.LearnRoute
import com.kelompok8.lentera.learn.ui.LearnViewModel
import com.kelompok8.lentera.map.ChapterSelectScreen
import com.kelompok8.lentera.map.PlayScreen
import com.kelompok8.lentera.score.ScoreScreen
import com.kelompok8.lentera.settings.AboutScreen
import com.kelompok8.lentera.settings.SettingsScreen
import com.kelompok8.lentera.splash.SplashScreen

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Beranda", R.drawable.home),
    BottomItem(Routes.PLAY, "Bermain", R.drawable.game),
    BottomItem(Routes.LEARN, "Belajar", R.drawable.study),
    BottomItem(Routes.SETTINGS, "Pengaturan", R.drawable.settings)
)

@Composable
fun LenteraApp() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // Bottom bar cuma muncul di 4 halaman utama, bukan di login atau saat main
    val showBottomBar = bottomItems.any { it.route == currentRoute }

    // Kalau sesi login masih tersimpan, langsung buka Beranda tanpa login ulang
    val startDestination = remember {
        if (AppContainer.authRepository.currentUser() != null) Routes.HOME else Routes.WELCOME
    }

    // Muat profil sekali saat aplikasi dibuka, dipakai header di semua halaman
    LaunchedEffect(Unit) {
        val uid = AppContainer.authRepository.currentUser()?.uid ?: return@LaunchedEffect
        UserSession.refresh()
        runCatching { AppContainer.leaderboardRepository.syncPlayer(uid) }
    }

    var showSplash by rememberSaveable { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
    CompositionLocalProvider(LocalOpenProfile provides { navController.navigate(Routes.PROFILE) { launchSingleTop = true } }) {
    LenteraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    LenteraBottomBar(
                        items = bottomItems,
                        currentRoute = currentRoute,
                        onItemClick = { navController.navigateToTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            LenteraNavHost(navController, startDestination, Modifier.padding(innerPadding))
        }
    }
    }
    AnimatedVisibility(
        visible = showSplash,
        enter = EnterTransition.None,
        exit = fadeOut(tween(durationMillis = 500))
    ) {
        SplashScreen(onFinished = { showSplash = false })
    }
    }
}

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Setelah login/daftar, halaman Pengenalan, Login, dan Register dibuang dari riwayat
// supaya tombol back tidak kembali ke sana
private fun NavHostController.goHomeAfterAuth() {
    navigate(Routes.HOME) {
        popUpTo(graph.id) { inclusive = true }
    }
}

@Composable
private fun LenteraNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStart = { navController.navigate(Routes.LOGIN) },
                onAbout = { navController.navigate(Routes.ABOUT) }
            )
        }
        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.goHomeAfterAuth() },
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onBackToMenu = { navController.popBackStack(Routes.WELCOME, inclusive = false) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.goHomeAfterAuth() },
                onBackToMenu = { navController.popBackStack(Routes.WELCOME, inclusive = false) }
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
        composable(Routes.LEARN) {
            val context = LocalContext.current
            val learnViewModel: LearnViewModel = viewModel(
                factory = LearnViewModel.factory(GlossaryRepositoryImpl(context.applicationContext))
            )
            val user by UserSession.profile.collectAsState()
            LearnRoute(viewModel = learnViewModel, user = user)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onAbout = { navController.navigate(Routes.ABOUT) },
                onLogout = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
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
                onChapterClick = { chapterId -> navController.navigate(Routes.game(chapterId)) },
                onBack = { navController.popBackStack() }
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
                },
                onBack = { navController.popBackStack() }
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
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.EDIT_PROFILE) },
                onLoggedOut = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Routes.LEADERBOARD) {
            val leaderBoardViewModel: LeaderboardViewModel = viewModel()
            val leaderBoardUiState by leaderBoardViewModel.uiState.collectAsState()
            LeaderboardScreen(
                entries = leaderBoardUiState.entries,
                isLoading = leaderBoardUiState.isLoading,
                currentPage = leaderBoardUiState.currentPage,
                canGoPrev = leaderBoardUiState.canGoPrev,
                canGoNext = leaderBoardUiState.canGoNext,
                onPrevPage = { leaderBoardViewModel.prevPage() },
                onNextPage = { leaderBoardViewModel.nextPage() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
