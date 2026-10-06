package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.entities.PlayerEntity
import com.example.core.rewards.RewardManager
import com.example.core.settings.AppLanguage
import com.example.ui.theme.*

@Composable
fun WafaTopBar(
    player: PlayerEntity,
    language: AppLanguage,
    onProfileClick: () -> Unit,
    onChampionshipClick: () -> Unit
) {
    val levelProgress = RewardManager.currentLevelProgressFraction(player.xp)

    val tierName = when {
        player.championshipScore >= 1000 -> if (language == AppLanguage.BANGLA) "লেজেন্ড" else "Legend"
        player.championshipScore >= 600 -> if (language == AppLanguage.BANGLA) "ডায়মন্ড" else "Diamond"
        player.championshipScore >= 350 -> if (language == AppLanguage.BANGLA) "গোল্ড" else "Gold"
        player.championshipScore >= 150 -> if (language == AppLanguage.BANGLA) "সিলভার" else "Silver"
        else -> if (language == AppLanguage.BANGLA) "ব্রোঞ্জ" else "Bronze"
    }

    val tierColor = when {
        player.championshipScore >= 1000 -> NeonCyan
        player.championshipScore >= 600 -> ElectricBlue
        player.championshipScore >= 350 -> CyberGold
        player.championshipScore >= 150 -> Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wafa_top_bar"),
        color = DarkSurface,
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar + Name + Level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onProfileClick() }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonEmerald, BengalGreen)
                                )
                            )
                            .border(1.5.dp, CyberGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (player.avatarId == 1) "🐅" else if (player.avatarId == 2) "⚡" else if (player.avatarId == 3) "🏏" else "🎮",
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurfaceCard,
                                border = BorderStroke(1.dp, NeonEmerald)
                            ) {
                                Text(
                                    text = "Lv.${player.level}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            }
                        }

                        // Small XP info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "XP",
                                tint = CyberGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${player.xp} XP",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Coins + Rank
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Championship Rank Pill
                    Surface(
                        modifier = Modifier
                            .clickable { onChampionshipClick() }
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, tierColor)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Rank",
                                tint = tierColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tierName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor
                            )
                        }
                    }

                    // Coins Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, CyberGold.copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = CyberGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${player.coins}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Thin XP Progress Bar
            LinearProgressIndicator(
                progress = { levelProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = NeonEmerald,
                trackColor = DarkBorder,
            )
        }
    }
}
