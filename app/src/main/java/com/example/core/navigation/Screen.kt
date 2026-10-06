package com.example.core.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Games : Screen("games")
    object Missions : Screen("missions")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Championship : Screen("championship")
    object WafaAi : Screen("wafa_ai")

    // Playable Game Routes
    object Racing : Screen("game_racing")
    object Cricket : Screen("game_cricket")
    object Football : Screen("game_football")
    object Puzzle : Screen("game_puzzle")
    object Shop : Screen("game_shop")
    object Farm : Screen("game_farm")
    object City : Screen("game_city")
    object MiniGames : Screen("game_minigames")
    object Quiz : Screen("game_quiz")
    object Adventure : Screen("game_adventure")
}
