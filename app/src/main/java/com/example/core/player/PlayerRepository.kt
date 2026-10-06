package com.example.core.player

import com.example.core.database.WafaVerseDatabase
import com.example.core.database.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerRepository(private val database: WafaVerseDatabase) {
    private val playerDao = database.playerDao()

    val playerFlow: Flow<PlayerEntity> = playerDao.getPlayer().map { player ->
        player ?: PlayerEntity(
            id = 1,
            name = "বাংলার মুসাফির",
            coins = 1000,
            level = 1,
            xp = 50,
            championshipScore = 200
        )
    }

    suspend fun getPlayer(): PlayerEntity {
        return playerDao.getPlayerSync() ?: PlayerEntity().also {
            playerDao.insertPlayer(it)
        }
    }

    suspend fun updatePlayer(player: PlayerEntity) {
        playerDao.updatePlayer(player)
    }

    suspend fun addCoins(amount: Int) {
        playerDao.addCoins(amount)
    }

    suspend fun addXp(amount: Int) {
        playerDao.addXp(amount)
    }

    suspend fun setAvatar(avatarId: Int) {
        val current = getPlayer()
        playerDao.updatePlayer(current.copy(avatarId = avatarId))
    }

    suspend fun setName(name: String) {
        val current = getPlayer()
        playerDao.updatePlayer(current.copy(name = name))
    }
}
