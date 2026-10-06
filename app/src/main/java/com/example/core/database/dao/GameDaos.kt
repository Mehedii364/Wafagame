package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY isCompleted ASC, isClaimed ASC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("UPDATE missions SET currentCount = currentCount + :increment WHERE id = :missionId")
    suspend fun incrementProgress(missionId: String, increment: Int)

    @Query("UPDATE missions SET isCompleted = 1 WHERE id = :missionId")
    suspend fun markCompleted(missionId: String)

    @Query("UPDATE missions SET isClaimed = 1 WHERE id = :missionId")
    suspend fun markClaimed(missionId: String)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET currentValue = :value, isUnlocked = CASE WHEN :value >= targetValue THEN 1 ELSE isUnlocked END, unlockedAt = CASE WHEN :value >= targetValue AND isUnlocked = 0 THEN :now ELSE unlockedAt END WHERE id = :id")
    suspend fun updateProgress(id: String, value: Int, now: Long = System.currentTimeMillis())
}

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    fun getProgress(gameId: String): Flow<GameProgressEntity?>

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId")
    suspend fun getProgressSync(gameId: String): GameProgressEntity?

    @Query("SELECT * FROM game_progress")
    fun getAllProgress(): Flow<List<GameProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: GameProgressEntity)
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_inventory")
    fun getInventory(): Flow<List<ShopInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initInventory(items: List<ShopInventoryEntity>)

    @Update
    suspend fun updateItem(item: ShopInventoryEntity)

    @Query("UPDATE shop_inventory SET stock = stock + :qty WHERE productId = :productId")
    suspend fun restock(productId: String, qty: Int)

    @Query("UPDATE shop_inventory SET stock = stock - :qty WHERE productId = :productId AND stock >= :qty")
    suspend fun sellStock(productId: String, qty: Int): Int
}

@Dao
interface FarmDao {
    @Query("SELECT * FROM farm_plots ORDER BY plotId ASC")
    fun getAllPlots(): Flow<List<FarmPlotEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initPlots(plots: List<FarmPlotEntity>)

    @Update
    suspend fun updatePlot(plot: FarmPlotEntity)
}

@Dao
interface CityDao {
    @Query("SELECT * FROM city_buildings ORDER BY slotIndex ASC")
    fun getAllBuildings(): Flow<List<CityBuildingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: CityBuildingEntity)

    @Query("DELETE FROM city_buildings WHERE buildingId = :buildingId")
    suspend fun deleteBuilding(buildingId: String)
}
