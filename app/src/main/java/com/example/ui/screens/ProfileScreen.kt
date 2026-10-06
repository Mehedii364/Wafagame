package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage
) {
    val player by viewModel.player.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember(player.name) { mutableStateOf(player.name) }

    val avatars = listOf(1 to "🐅", 2 to "⚡", 3 to "🏏", 4 to "🎮")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Player Bio Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.5.dp, CyberGold)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Showcase
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonEmerald, BengalGreen)
                                )
                            )
                            .border(2.dp, CyberGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatars.find { it.first == player.avatarId }?.second ?: "🐅",
                            fontSize = 36.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar Selector Row
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        avatars.forEach { (id, emoji) ->
                            val isSelected = player.avatarId == id
                            Surface(
                                onClick = { viewModel.setPlayerAvatar(id) },
                                shape = CircleShape,
                                color = if (isSelected) DarkSurfaceVariant else DarkSurface,
                                border = BorderStroke(1.5.dp, if (isSelected) NeonEmerald else DarkBorder),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Player Name & Edit
                    if (isEditingName) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonEmerald,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            IconButton(onClick = {
                                viewModel.setPlayerName(editedName)
                                isEditingName = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = NeonEmerald)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            IconButton(onClick = { isEditingName = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = "Level ${player.level} • ${player.xp} Total XP",
                        color = NeonEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // CAREER STATS GRID
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "ক্যারিয়ার পরিসংখ্যান" else "Career Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("মোট ম্যাচ", "${player.totalGamesPlayed}", NeonEmerald)
                        StatItem("রেসিং জয়", "${player.racingWins}", CyberGold)
                        StatItem("ফুটবল জয়", "${player.footballWins}", ElectricBlue)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("ক্রিকেট জয়", "${player.cricketWins}", CrimsonSun)
                        StatItem("ধাঁধা সম্পন্ন", "${player.puzzlesCompleted}", NeonCyan)
                        StatItem("চ্যাম্পিয়ন পয়েন্ট", "${player.championshipScore}", CyberGold)
                    }
                }
            }
        }

        // ACHIEVEMENTS SHOWCASE
        item {
            Text(
                text = if (language == AppLanguage.BANGLA) "অর্জন ও পদক (অ্যাচিভমেন্ট)" else "Achievements & Trophies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGold
            )
        }

        items(achievements) { ach ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (ach.isUnlocked) DarkSurfaceCard else DarkSurfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (ach.isUnlocked) CyberGold else DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (ach.isUnlocked) CyberGold.copy(alpha = 0.2f) else DarkBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = ach.title,
                            tint = if (ach.isUnlocked) CyberGold else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) ach.titleBn else ach.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (ach.isUnlocked) TextPrimary else TextMuted
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) ach.descriptionBn else ach.description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    if (ach.isUnlocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BengalGreen
                        ) {
                            Text(
                                text = "UNLOCKED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}
