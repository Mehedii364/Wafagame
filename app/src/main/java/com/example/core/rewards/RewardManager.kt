package com.example.core.rewards

import com.example.core.database.WafaVerseDatabase
import com.example.core.database.entities.GameProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GameResult(
    val gameId: String,
    val score: Int,
    val isWin: Boolean = true,
    val extraStatKey: String = "",
    val extraStatValue: Int = 0
)

data class RewardSummary(
    val xpGained: Int,
    val coinsGained: Int,
    val newLevel: Int,
    val didLevelUp: Boolean,
    val missionsUpdated: Int,
    val achievementsUnlocked: List<String>
)

class RewardManager(private val database: WafaVerseDatabase) {

    suspend fun processGameResult(result: GameResult): RewardSummary = withContext(Dispatchers.IO) {
        val playerDao = database.playerDao()
        val missionDao = database.missionDao()
        val achievementDao = database.achievementDao()
        val progressDao = database.gameProgressDao()

        val player = playerDao.getPlayerSync() ?: com.example.core.database.entities.PlayerEntity()

        // 1. Calculate Base Rewards
        val baseMultiplier = if (result.isWin) 1.5f else 1.0f
        val xpGained = when (result.gameId) {
            "RACING" -> ((result.score / 10) * baseMultiplier).toInt().coerceIn(30, 300)
            "FOOTBALL" -> (result.score * 50 * baseMultiplier).toInt().coerceIn(40, 250)
            "CRICKET" -> (result.score * 5 * baseMultiplier).toInt().coerceIn(30, 250)
            "PUZZLE" -> (result.score * 2 * baseMultiplier).toInt().coerceIn(25, 200)
            "SHOP" -> (result.score / 2).coerceIn(20, 200)
            "FARM" -> (result.score * 15).coerceIn(20, 200)
            "CITY" -> (result.score * 10).coerceIn(20, 200)
            "MINI_GAME" -> (result.score * 2).coerceIn(15, 150)
            "QUIZ" -> (result.score * 10).coerceIn(20, 200)
            "ADVENTURE" -> (result.score * 20).coerceIn(50, 300)
            else -> 30
        }

        val coinsGained = when (result.gameId) {
            "RACING" -> (xpGained * 1.5f).toInt()
            "FOOTBALL" -> (xpGained * 1.2f).toInt()
            "CRICKET" -> (xpGained * 1.3f).toInt()
            "PUZZLE" -> (xpGained * 1.0f).toInt()
            "SHOP" -> result.score // raw profit from shop
            "FARM" -> (result.score * 25)
            "CITY" -> (result.score * 15)
            "MINI_GAME" -> (xpGained * 1.0f).toInt()
            "QUIZ" -> (xpGained * 1.2f).toInt()
            "ADVENTURE" -> (xpGained * 2.0f).toInt()
            else -> 50
        }

        val newTotalXp = player.xp + xpGained
        val newLevel = calculateLevel(newTotalXp)
        val didLevelUp = newLevel > player.level

        // 2. Update Player Stats
        val newRacingWins = if (result.gameId == "RACING" && result.isWin) player.racingWins + 1 else player.racingWins
        val newFootballWins = if (result.gameId == "FOOTBALL" && result.isWin) player.footballWins + 1 else player.footballWins
        val newCricketWins = if (result.gameId == "CRICKET" && result.isWin) player.cricketWins + 1 else player.cricketWins
        val newPuzzlesCompleted = if (result.gameId == "PUZZLE" && result.isWin) player.puzzlesCompleted + 1 else player.puzzlesCompleted

        val champBoost = if (result.isWin) 25 else 10
        val updatedPlayer = player.copy(
            xp = newTotalXp,
            level = newLevel,
            coins = player.coins + coinsGained,
            totalGamesPlayed = player.totalGamesPlayed + 1,
            racingWins = newRacingWins,
            footballWins = newFootballWins,
            cricketWins = newCricketWins,
            puzzlesCompleted = newPuzzlesCompleted,
            championshipScore = player.championshipScore + champBoost
        )
        playerDao.updatePlayer(updatedPlayer)

        // 3. Update Game Progress
        val currentProgress = progressDao.getProgressSync(result.gameId)
        val newHighScore = maxOf(currentProgress?.highScore ?: 0, result.score)
        val updatedProgress = GameProgressEntity(
            gameId = result.gameId,
            highScore = newHighScore,
            stars = minOf(3, maxOf(1, result.score / 50)),
            totalPlays = (currentProgress?.totalPlays ?: 0) + 1,
            lastPlayedTime = System.currentTimeMillis(),
            unlockedLevel = maxOf(currentProgress?.unlockedLevel ?: 1, if (result.isWin) 2 else 1)
        )
        progressDao.saveProgress(updatedProgress)

        // 4. Update Missions
        var missionsAffected = 0
        when (result.gameId) {
            "RACING" -> {
                missionDao.incrementProgress("m_race_1", 1)
                missionsAffected++
            }
            "FOOTBALL" -> {
                if (result.extraStatKey == "GOALS") {
                    missionDao.incrementProgress("m_football_1", result.extraStatValue)
                } else if (result.isWin) {
                    missionDao.incrementProgress("m_football_1", 1)
                }
                missionsAffected++
            }
            "CRICKET" -> {
                missionDao.incrementProgress("m_cricket_1", result.score)
                missionsAffected++
            }
            "SHOP" -> {
                missionDao.incrementProgress("m_shop_1", maxOf(1, result.extraStatValue))
                missionsAffected++
            }
            "PUZZLE" -> {
                missionDao.incrementProgress("m_puzzle_1", 1)
                missionsAffected++
            }
            "FARM" -> {
                missionDao.incrementProgress("m_farm_1", maxOf(1, result.extraStatValue))
                missionsAffected++
            }
        }

        // 5. Check Achievements
        val unlockedTitles = mutableListOf<String>()
        if (result.gameId == "RACING") {
            achievementDao.updateProgress("ach_first_race", updatedPlayer.racingWins)
        }
        if (result.gameId == "FOOTBALL" && (result.extraStatKey == "GOALS" || result.score > 0)) {
            achievementDao.updateProgress("ach_goal", 1)
        }
        if (result.gameId == "CRICKET" && result.extraStatKey == "SIXES") {
            achievementDao.updateProgress("ach_cricket_six", result.extraStatValue)
        }
        if (result.gameId == "PUZZLE") {
            achievementDao.updateProgress("ach_puzzle_solver", updatedPlayer.puzzlesCompleted)
        }
        if (result.gameId == "SHOP") {
            achievementDao.updateProgress("ach_shop_master", updatedPlayer.coins)
        }

        RewardSummary(
            xpGained = xpGained,
            coinsGained = coinsGained,
            newLevel = newLevel,
            didLevelUp = didLevelUp,
            missionsUpdated = missionsAffected,
            achievementsUnlocked = unlockedTitles
        )
    }

    companion object {
        fun calculateLevel(xp: Int): Int {
            return 1 + (xp / 150)
        }

        fun xpToNextLevel(currentXp: Int): Int {
            val level = calculateLevel(currentXp)
            val nextLevelXp = level * 150
            return maxOf(0, nextLevelXp - currentXp)
        }

        fun currentLevelProgressFraction(currentXp: Int): Float {
            val level = calculateLevel(currentXp)
            val currentLevelBase = (level - 1) * 150
            val xpInCurrentLevel = currentXp - currentLevelBase
            return (xpInCurrentLevel.toFloat() / 150f).coerceIn(0f, 1f)
        }
    }
}
