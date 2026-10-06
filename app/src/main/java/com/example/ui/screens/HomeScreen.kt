package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.localization.WafaStrings
import com.example.core.navigation.Screen
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

data class FeaturedGameCard(
    val id: String,
    val title: String,
    val titleBn: String,
    val tag: String,
    val emoji: String,
    val color: Color,
    val route: String
)

@Composable
fun HomeScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onNavigate: (String) -> Unit
) {
    val player by viewModel.player.collectAsStateWithLifecycle()
    val missions by viewModel.missions.collectAsStateWithLifecycle()

    val featuredGames = listOf(
        FeaturedGameCard("racing", "Wafa Racing", "ওয়াফা রেসিং", "Speed & Nitro", "🏎️", NeonEmerald, Screen.Racing.route),
        FeaturedGameCard("cricket", "Wafa Cricket", "ওয়াফা ক্রিকেট", "Super Over", "🏏", CyberGold, Screen.Cricket.route),
        FeaturedGameCard("football", "Wafa Football", "ওয়াফা ফুটবল", "5v5 Showdown", "⚽", ElectricBlue, Screen.Football.route),
        FeaturedGameCard("shop", "Mudi Shop", "মুদি শপ সিমুলেটর", "Business Tycoon", "🏪", CyberAmber, Screen.Shop.route),
        FeaturedGameCard("puzzle", "Puzzle Center", "পাজল সেন্টার", "Brain Training", "🧩", NeonCyan, Screen.Puzzle.route),
        FeaturedGameCard("farm", "Wafa Farm", "ওয়াফা এগ্রো ফার্ম", "Crops & Harvest", "🌾", BengalGreen, Screen.Farm.route),
        FeaturedGameCard("city", "City Builder", "সিটি বিল্ডার", "Mega Metropolis", "🏙️", Color(0xFF9C27B0), Screen.City.route)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // CONTINUE PLAYING HERO BANNER
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("continue_playing_card"),
                shape = RoundedCornerShape(18.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.5.dp, NeonEmerald)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(DarkSurfaceVariant, Color(0xFF0F2B1D))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BengalGreen
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "চালিয়ে যান" else "CONTINUE PLAYING",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonEmerald
                                )
                            }
                            Text(text = "🏁 High Score: 1450", fontSize = 11.sp, color = CyberGold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "ওয়াফা রেসিং: পদ্মা সেতু এক্সপ্রেস" else "Wafa Racing: Padma Express",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "রকেট রিকশা নিয়ে হাইওয়েতে নতুন রেকর্ড গড়ুন!" else "Drive your turbo vehicle through river bridges & city routes!",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(text = "🏎️", fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onNavigate(Screen.Racing.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("launch_racing_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonEmerald,
                                contentColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "এখনই খেলুন (PLAY NOW)" else "PLAY NOW",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // FEATURED GAMES HORIZONTAL CAROUSEL
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সেরা গেমসমূহ" else "Featured Games",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    TextButton(onClick = { onNavigate(Screen.Games.route) }) {
                        Text(text = if (language == AppLanguage.BANGLA) "সব দেখুন" else "View All", color = CyberGold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "More", modifier = Modifier.size(16.dp), tint = CyberGold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(featuredGames) { game ->
                        Surface(
                            onClick = { onNavigate(game.route) },
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceCard,
                            border = BorderStroke(1.5.dp, game.color.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .size(width = 140.dp, height = 150.dp)
                                .testTag("featured_game_${game.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = game.emoji, fontSize = 36.sp)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) game.titleBn else game.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = game.tag,
                                        fontSize = 10.sp,
                                        color = game.color,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DarkSurfaceVariant
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "খেলুন" else "Play",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // DAILY CHALLENGE & AI STRATEGY HUB (2 Columns / Side by Side)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily Challenge Card
                Surface(
                    onClick = { onNavigate(Screen.Football.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, CyberGold)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = "Daily", tint = CyberGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Daily Cup", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ফুটবলে ২টি গোল করুন" else "Score 2 Goals",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "+300 🪙 +120 XP", color = NeonEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Wafa AI Card
                Surface(
                    onClick = { onNavigate(Screen.WafaAi.route) },
                    modifier = Modifier.weight(1f).testTag("home_ai_btn"),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, NeonCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = "AI", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Wafa AI", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "গেম কৌশল ও টিপস" else "Game Strategy & Tips",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = if (language == AppLanguage.BANGLA) "এআই পরামর্শ নিন" else "Ask AI Guide", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // QUICK MISSIONS LIST
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "চলমান মিশন" else "Active Missions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { onNavigate(Screen.Missions.route) }) {
                        Text(text = if (language == AppLanguage.BANGLA) "সব দেখুন" else "View All", color = NeonEmerald)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                missions.take(2).forEach { mission ->
                    val progressFraction = (mission.currentCount.toFloat() / mission.targetCount).coerceIn(0f, 1f)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) mission.titleBn else mission.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${mission.currentCount} / ${mission.targetCount}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mission.currentCount >= mission.targetCount) NeonEmerald else TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = NeonEmerald,
                                trackColor = DarkBorder
                            )
                        }
                    }
                }
            }
        }
    }
}
