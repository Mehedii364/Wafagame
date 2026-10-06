package com.example.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.core.database.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE id = 1")
    fun getPlayer(): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = 1")
    suspend fun getPlayerSync(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("UPDATE players SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE players SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Int)

    @Query("UPDATE players SET level = :level WHERE id = 1")
    suspend fun updateLevel(level: Int)
}
