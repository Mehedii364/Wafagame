package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

@Composable
fun MissionsScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage
) {
    val missions by viewModel.missions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("missions_screen")
    ) {
        Text(
            text = if (language == AppLanguage.BANGLA) "অভিযান ও মিশন বোর্ড" else "Mission Headquarters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CyberGold
        )
        Text(
            text = if (language == AppLanguage.BANGLA) "মিশন সম্পন্ন করে কয়েন ও এক্সপি সংগ্রহ করুন।" else "Complete missions to claim valuable coins & XP.",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(missions) { mission ->
                val isTargetReached = mission.currentCount >= mission.targetCount
                val progressFraction = (mission.currentCount.toFloat() / mission.targetCount).coerceIn(0f, 1f)

                Surface(
                    modifier = Modifier.fillMaxWidth().testTag("mission_card_${mission.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.dp, if (mission.isClaimed) DarkBorder else if (isTargetReached) NeonEmerald else DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) mission.titleBn else mission.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) mission.descriptionBn else mission.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurfaceVariant
                            ) {
                                Text(
                                    text = mission.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar and numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isTargetReached) NeonEmerald else CyberGold,
                                trackColor = DarkBorder
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${minOf(mission.currentCount, mission.targetCount)} / ${mission.targetCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTargetReached) NeonEmerald else TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Rewards Row + Claim Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = CyberGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "+${mission.rewardCoins}", fontSize = 12.sp, color = CyberGold, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.width(10.dp))

                                Icon(Icons.Default.Star, contentDescription = "XP", tint = NeonEmerald, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "+${mission.rewardXp} XP", fontSize = 12.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                            }

                            if (mission.isClaimed) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Claimed", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = if (language == AppLanguage.BANGLA) "সংগৃহীত" else "Claimed", fontSize = 11.sp, color = TextMuted)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.claimMission(mission) },
                                    enabled = isTargetReached,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonEmerald,
                                        contentColor = DarkBackground,
                                        disabledContainerColor = DarkSurfaceVariant,
                                        disabledContentColor = TextMuted
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp).testTag("claim_btn_${mission.id}"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "দাবি করুন" else "Claim",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
