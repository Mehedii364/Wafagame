package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.Screen
import com.example.core.settings.AppLanguage
import com.example.ui.theme.*

data class LibraryGame(
    val id: String,
    val title: String,
    val titleBn: String,
    val category: String,
    val emoji: String,
    val stars: Int,
    val route: String,
    val descriptionBn: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLibraryScreen(
    language: AppLanguage,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val games = remember {
        listOf(
            LibraryGame("racing", "Wafa Racing", "ওয়াফা রেসিং", "RACING", "🏎️", 3, Screen.Racing.route, "হাইস্পিড রেসিং, পদ্মা সেতু ও গ্রামের পথ"),
            LibraryGame("cricket", "Wafa Cricket", "ওয়াফা ক্রিকেট", "SPORTS", "🏏", 3, Screen.Cricket.route, "টাইগারদের সুপার ওভার ব্যাটিং শোডাউন"),
            LibraryGame("football", "Wafa Football", "ওয়াফা ফুটবল", "SPORTS", "⚽", 3, Screen.Football.route, "৫ বনাম ৫ ফুটবল চ্যাম্পিয়নশিপ"),
            LibraryGame("puzzle", "Puzzle Center", "পাজল সেন্টার", "PUZZLE", "🧩", 3, Screen.Puzzle.route, "শব্দ সাজাও, মেমোরি কার্ড ও কুইক ম্যাথ"),
            LibraryGame("shop", "Mudi Shop", "মুদি শপ সিমুলেটর", "STRATEGY", "🏪", 3, Screen.Shop.route, "পণ্য কেনাবেচা ও দোকানের উন্নতি"),
            LibraryGame("farm", "Wafa Farm", "ওয়াফা এগ্রো ফার্ম", "STRATEGY", "🌾", 3, Screen.Farm.route, "বীজ বপন, ফসল পরিচর্যা ও বাজারজাতকরণ"),
            LibraryGame("city", "City Builder", "সিটি বিল্ডার", "STRATEGY", "🏙️", 3, Screen.City.route, "আবাসিক, স্কুল ও স্বাস্থ্য কমপ্লেক্স নির্মাণ"),
            LibraryGame("minigames", "Mini Games", "মিনি গেম সেন্টার", "MINI", "⚡", 3, Screen.MiniGames.route, "ট্যাপ স্প্রিন্ট ও রিফ্লেক্স টেস্ট"),
            LibraryGame("quiz", "Knowledge Quiz", "জ্ঞান জিজ্ঞাসা", "EDUCATION", "📚", 3, Screen.Quiz.route, "বাংলাদেশ ও প্রোগ্রামিং বিষয়ক কুইজ"),
            LibraryGame("adventure", "Wafa Adventure", "ওয়াফা অ্যাডভেঞ্চার", "ADVENTURE", "🧭", 3, Screen.Adventure.route, "সদরঘাট ও নদীমাতৃক বাংলার রোমাঞ্চ")
        )
    }

    val categories = listOf("ALL", "RACING", "SPORTS", "STRATEGY", "PUZZLE", "MINI", "EDUCATION", "ADVENTURE")

    val filteredGames = games.filter { game ->
        val matchesCategory = selectedCategory == "ALL" || game.category == selectedCategory
        val matchesQuery = searchQuery.isEmpty() ||
                game.title.contains(searchQuery, ignoreCase = true) ||
                game.titleBn.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("game_library_screen")
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (language == AppLanguage.BANGLA) "গেম খুঁজুন..." else "Search games...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonEmerald) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("library_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceCard,
                unfocusedContainerColor = DarkSurfaceCard,
                focusedBorderColor = NeonEmerald,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) NeonEmerald else DarkSurfaceCard,
                    border = BorderStroke(1.dp, if (isSelected) NeonEmerald else DarkBorder)
                ) {
                    Text(
                        text = cat,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DarkBackground else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Games Grid (2 Columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredGames) { game ->
                Surface(
                    onClick = { onNavigate(game.route) },
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.testTag("game_card_${game.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = game.emoji, fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) game.titleBn else game.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = game.category,
                            fontSize = 10.sp,
                            color = CyberGold,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigate(game.route) },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = NeonEmerald),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (language == AppLanguage.BANGLA) "খেলুন" else "Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
