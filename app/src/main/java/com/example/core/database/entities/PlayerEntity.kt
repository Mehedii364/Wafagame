package com.example.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "মুসাফির যোদ্ধা", // Traveler Warrior
    val avatarId: Int = 1,
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 500,
    val totalGamesPlayed: Int = 0,
    val racingWins: Int = 0,
    val footballWins: Int = 0,
    val cricketWins: Int = 0,
    val puzzlesCompleted: Int = 0,
    val shopLevel: Int = 1,
    val farmLevel: Int = 1,
    val cityLevel: Int = 1,
    val adventureProgress: Int = 0,
    val achievementsUnlocked: Int = 0,
    val championshipScore: Int = 150,
    val favoriteGame: String = "Wafa Racing"
)
