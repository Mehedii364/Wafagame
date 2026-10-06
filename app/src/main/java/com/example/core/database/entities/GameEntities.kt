package com.example.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleBn: String,
    val description: String,
    val descriptionBn: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardXp: Int,
    val rewardCoins: Int,
    val category: String, // DAILY, WEEKLY, CAREER
    val isClaimed: Boolean = false,
    val isCompleted: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleBn: String,
    val description: String,
    val descriptionBn: String,
    val iconName: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val rewardCoins: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

@Entity(tableName = "game_progress")
data class GameProgressEntity(
    @PrimaryKey val gameId: String,
    val highScore: Int = 0,
    val stars: Int = 0,
    val totalPlays: Int = 0,
    val lastPlayedTime: Long = 0L,
    val unlockedLevel: Int = 1
)

@Entity(tableName = "shop_inventory")
data class ShopInventoryEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val nameBn: String,
    val stock: Int = 20,
    val costPrice: Int = 10,
    val salePrice: Int = 15,
    val unlocked: Boolean = true,
    val category: String = "GROCERY"
)

@Entity(tableName = "farm_plots")
data class FarmPlotEntity(
    @PrimaryKey val plotId: Int,
    val cropType: String = "NONE", // NONE, RICE, MUSTARD, JUTE, POTATO, TEA, MANGO
    val plantedTime: Long = 0L,
    val growthDurationSec: Int = 30,
    val isWatered: Boolean = false,
    val isReadyToHarvest: Boolean = false
)

@Entity(tableName = "city_buildings")
data class CityBuildingEntity(
    @PrimaryKey val buildingId: String,
    val buildingType: String, // HOUSE, ROAD, SHOP, SCHOOL, HOSPITAL, FARM, PARK, POWER
    val level: Int = 1,
    val slotIndex: Int,
    val lastCollectedTime: Long = 0L
)
