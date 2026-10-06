package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.Screen
import com.example.games.adventure.WafaAdventureScreen
import com.example.games.city.WafaCityScreen
import com.example.games.cricket.WafaCricketScreen
import com.example.games.education.WafaQuizScreen
import com.example.games.farm.WafaFarmScreen
import com.example.games.football.WafaFootballScreen
import com.example.games.minigames.WafaMiniGamesScreen
import com.example.games.puzzle.WafaPuzzleScreen
import com.example.games.racing.WafaRacingScreen
import com.example.games.shop.WafaShopScreen
import com.example.ui.WafaMainViewModel
import com.example.ui.components.RewardDialog
import com.example.ui.components.WafaBottomNav
import com.example.ui.components.WafaTopBar
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WafaMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

                val player by viewModel.player.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()
                val lastReward by viewModel.lastReward.collectAsStateWithLifecycle()

                val isHubScreen = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Games.route,
                    Screen.Missions.route,
                    Screen.Profile.route,
                    Screen.Settings.route
                )

                // BackHandler on non-Home hub screens navigates back to Home
                if (isHubScreen && currentRoute != Screen.Home.route) {
                    BackHandler {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    topBar = {
                        if (isHubScreen) {
                            WafaTopBar(
                                player = player,
                                language = settings.language,
                                onProfileClick = {
                                    if (currentRoute != Screen.Profile.route) {
                                        navController.navigate(Screen.Profile.route)
                                    }
                                },
                                onChampionshipClick = {
                                    navController.navigate(Screen.Championship.route)
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (isHubScreen) {
                            WafaBottomNav(
                                currentRoute = currentRoute,
                                language = settings.language,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        ) {
                            composable(Screen.Splash.route) {
                                SplashScreen(
                                    onTimeout = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Home.route) {
                                HomeScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onNavigate = { route -> navController.navigate(route) }
                                )
                            }

                            composable(Screen.Games.route) {
                                GameLibraryScreen(
                                    language = settings.language,
                                    onNavigate = { route -> navController.navigate(route) }
                                )
                            }

                            composable(Screen.Missions.route) {
                                MissionsScreen(
                                    viewModel = viewModel,
                                    language = settings.language
                                )
                            }

                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    language = settings.language
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    language = settings.language
                                )
                            }

                            composable(Screen.Championship.route) {
                                BackHandler { navController.popBackStack() }
                                ChampionshipScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.WafaAi.route) {
                                BackHandler { navController.popBackStack() }
                                WafaAiScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // PLAYABLE GAMES
                            composable(Screen.Racing.route) {
                                BackHandler { navController.popBackStack() }
                                WafaRacingScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Cricket.route) {
                                BackHandler { navController.popBackStack() }
                                WafaCricketScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Football.route) {
                                BackHandler { navController.popBackStack() }
                                WafaFootballScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Puzzle.route) {
                                BackHandler { navController.popBackStack() }
                                WafaPuzzleScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Shop.route) {
                                BackHandler { navController.popBackStack() }
                                WafaShopScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Farm.route) {
                                BackHandler { navController.popBackStack() }
                                WafaFarmScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.City.route) {
                                BackHandler { navController.popBackStack() }
                                WafaCityScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.MiniGames.route) {
                                BackHandler { navController.popBackStack() }
                                WafaMiniGamesScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Quiz.route) {
                                BackHandler { navController.popBackStack() }
                                WafaQuizScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Adventure.route) {
                                BackHandler { navController.popBackStack() }
                                WafaAdventureScreen(
                                    viewModel = viewModel,
                                    language = settings.language,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Celebration Reward Dialog Overlay
                        lastReward?.let { rewardSummary ->
                            RewardDialog(
                                summary = rewardSummary,
                                language = settings.language,
                                onDismiss = { viewModel.clearLastReward() }
                            )
                        }
                    }
                }
            }
        }
    }
}
