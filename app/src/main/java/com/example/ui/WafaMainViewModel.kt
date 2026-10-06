package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.audio.GameEffectsHelper
import com.example.core.database.WafaVerseDatabase
import com.example.core.database.entities.*
import com.example.core.player.PlayerRepository
import com.example.core.rewards.GameResult
import com.example.core.rewards.RewardManager
import com.example.core.rewards.RewardSummary
import com.example.core.settings.AppLanguage
import com.example.core.settings.GameSettingsManager
import com.example.core.settings.UserSettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WafaMainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = WafaVerseDatabase.getInstance(application)
    val playerRepository = PlayerRepository(db)
    val rewardManager = RewardManager(db)
    val settingsManager = GameSettingsManager(application)
    val effectsHelper = GameEffectsHelper(application)

    // Observable States
    val player: StateFlow<PlayerEntity> = playerRepository.playerFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerEntity()
        )

    val missions: StateFlow<List<MissionEntity>> = db.missionDao().getAllMissions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val achievements: StateFlow<List<AchievementEntity>> = db.achievementDao().getAllAchievements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val shopInventory: StateFlow<List<ShopInventoryEntity>> = db.shopDao().getInventory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val farmPlots: StateFlow<List<FarmPlotEntity>> = db.farmDao().getAllPlots()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cityBuildings: StateFlow<List<CityBuildingEntity>> = db.cityDao().getAllBuildings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val settings: StateFlow<UserSettings> = settingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    // Last Reward Banner State
    private val _lastReward = MutableStateFlow<RewardSummary?>(null)
    val lastReward: StateFlow<RewardSummary?> = _lastReward.asStateFlow()

    fun clearLastReward() {
        _lastReward.value = null
    }

    fun submitGameResult(result: GameResult, onProcessed: (RewardSummary) -> Unit = {}) {
        viewModelScope.launch {
            val summary = rewardManager.processGameResult(result)
            _lastReward.value = summary
            if (settings.value.hapticsEnabled) {
                effectsHelper.vibrateSuccess()
            }
            onProcessed(summary)
        }
    }

    fun claimMission(mission: MissionEntity) {
        if (mission.isClaimed || mission.currentCount < mission.targetCount) return
        viewModelScope.launch {
            db.missionDao().markClaimed(mission.id)
            playerRepository.addCoins(mission.rewardCoins)
            playerRepository.addXp(mission.rewardXp)
            if (settings.value.hapticsEnabled) {
                effectsHelper.vibrateSuccess()
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            settingsManager.setLanguage(lang)
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setSound(enabled)
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setMusic(enabled)
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setHaptics(enabled)
        }
    }

    fun togglePerformance(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setHighPerformance(enabled)
        }
    }

    fun setPlayerAvatar(avatarId: Int) {
        viewModelScope.launch {
            playerRepository.setAvatar(avatarId)
        }
    }

    fun setPlayerName(name: String) {
        viewModelScope.launch {
            playerRepository.setName(name)
        }
    }

    // Shop Operations
    fun restockProduct(productId: String, qty: Int, costTotal: Int) {
        viewModelScope.launch {
            val currentPlayer = player.value
            if (currentPlayer.coins >= costTotal) {
                playerRepository.addCoins(-costTotal)
                db.shopDao().restock(productId, qty)
            }
        }
    }

    fun sellProductToCustomer(productId: String, qty: Int, saleTotal: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val affected = db.shopDao().sellStock(productId, qty)
            if (affected > 0) {
                playerRepository.addCoins(saleTotal)
                submitGameResult(GameResult("SHOP", score = saleTotal, extraStatKey = "ITEMS", extraStatValue = qty))
                onSuccess()
            }
        }
    }

    // Farm Operations
    fun plantCrop(plotId: Int, cropType: String) {
        viewModelScope.launch {
            val durationSec = when (cropType) {
                "RICE" -> 15
                "MUSTARD" -> 20
                "JUTE" -> 25
                "POTATO" -> 30
                "TEA" -> 35
                "MANGO" -> 45
                else -> 20
            }
            db.farmDao().updatePlot(
                FarmPlotEntity(
                    plotId = plotId,
                    cropType = cropType,
                    plantedTime = System.currentTimeMillis(),
                    growthDurationSec = durationSec,
                    isWatered = true,
                    isReadyToHarvest = false
                )
            )
        }
    }

    fun harvestPlot(plotId: Int, cropType: String) {
        viewModelScope.launch {
            val cropValue = when (cropType) {
                "RICE" -> 40
                "MUSTARD" -> 55
                "JUTE" -> 70
                "POTATO" -> 90
                "TEA" -> 110
                "MANGO" -> 150
                else -> 30
            }
            db.farmDao().updatePlot(
                FarmPlotEntity(
                    plotId = plotId,
                    cropType = "NONE",
                    plantedTime = 0L,
                    growthDurationSec = 0,
                    isWatered = false,
                    isReadyToHarvest = false
                )
            )
            submitGameResult(GameResult("FARM", score = cropValue, extraStatKey = "HARVEST", extraStatValue = 1))
        }
    }

    // City Builder Operations
    fun upgradeOrBuildCitySlot(slotIndex: Int, buildingType: String) {
        viewModelScope.launch {
            val cost = 200
            if (player.value.coins >= cost) {
                playerRepository.addCoins(-cost)
                val id = "b_slot_$slotIndex"
                db.cityDao().insertBuilding(
                    CityBuildingEntity(
                        buildingId = id,
                        buildingType = buildingType,
                        level = 1,
                        slotIndex = slotIndex,
                        lastCollectedTime = System.currentTimeMillis()
                    )
                )
                submitGameResult(GameResult("CITY", score = 25))
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            val resetPlayer = PlayerEntity(
                id = 1,
                name = "বাংলার মুসাফির",
                coins = 500,
                level = 1,
                xp = 0,
                championshipScore = 100
            )
            db.playerDao().insertPlayer(resetPlayer)
        }
    }
}
