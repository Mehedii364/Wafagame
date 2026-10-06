package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

data class LeaderboardEntry(
    val name: String,
    val rankTier: String,
    val points: Int,
    val avatar: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionshipScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val player by viewModel.player.collectAsStateWithLifecycle()

    val leaderboard = listOf(
        LeaderboardEntry("তানভীর (Dhaka)", "Legend", 1450, "👑"),
        LeaderboardEntry("সাইফুর (Sylhet)", "Diamond", 890, "⚡"),
        LeaderboardEntry(player.name + " (You)", if (player.championshipScore >= 600) "Diamond" else if (player.championshipScore >= 350) "Gold" else "Silver", player.championshipScore, "🐅"),
        LeaderboardEntry("রাকিব (Chittagong)", "Gold", 420, "🏏"),
        LeaderboardEntry("মাহিন (Rajshahi)", "Silver", 280, "🏎️"),
        LeaderboardEntry("আরিফ (Barishal)", "Bronze", 140, "🌾")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ওয়াফা চ্যাম্পিয়নশিপ লীগ" else "Wafa Championship",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("championship_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp)
        ) {
            // Trophy Standing Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.5.dp, CyberGold)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(CyberGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Trophy", tint = CyberGold, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(text = "আপনার বর্তমান স্কোর:", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${player.championshipScore} CHAMPION POINTS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberGold
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "রেসিং, ক্রিকেট ও ফুটবল জিতে পয়েন্ট বাড়ান!" else "Win matches to climb the tier ranks!",
                            fontSize = 11.sp,
                            color = NeonEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (language == AppLanguage.BANGLA) "চ্যাম্পিয়ন লিডারবোর্ড:" else "Championship Leaderboard:",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(leaderboard) { index, entry ->
                    val isUser = entry.name.contains("(You)")
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isUser) DarkSurfaceVariant else DarkSurfaceCard,
                        border = BorderStroke(1.dp, if (isUser) NeonEmerald else DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${index + 1}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (index == 0) CyberGold else TextSecondary,
                                    modifier = Modifier.width(32.dp)
                                )
                                Text(text = entry.avatar, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = entry.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                    Text(text = entry.rankTier, fontSize = 11.sp, color = if (entry.rankTier == "Legend") NeonCyan else CyberGold)
                                }
                            }

                            Text(
                                text = "${entry.points} pts",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isUser) NeonEmerald else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
