package com.example.games.city

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

data class BuildingBlueprint(
    val type: String,
    val nameBn: String,
    val emoji: String,
    val cost: Int,
    val popBonus: Int,
    val happinessBonus: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaCityScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val buildings by viewModel.cityBuildings.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }

    val blueprints = remember {
        listOf(
            BuildingBlueprint("HOUSE", "আবাসিক ভবন", "🏘️", 200, popBonus = 25, happinessBonus = 5),
            BuildingBlueprint("SHOP", "বাণিজ্যিক সুপার মার্কেট", "🏪", 250, popBonus = 10, happinessBonus = 15),
            BuildingBlueprint("SCHOOL", "শহীদ স্মৃতি বিদ্যাপীঠ", "🏫", 300, popBonus = 5, happinessBonus = 20),
            BuildingBlueprint("HOSPITAL", "উপজেলা স্বাস্থ্য কমপ্লেক্স", "🏥", 350, popBonus = 0, happinessBonus = 25),
            BuildingBlueprint("FARM", "কৃষি ফার্ম ও শস্যভাণ্ডার", "🌾", 180, popBonus = 15, happinessBonus = 10),
            BuildingBlueprint("POWER", "সৌরবিদ্যুৎ কেন্দ্র", "⚡", 320, popBonus = 0, happinessBonus = 18)
        )
    }

    // Aggregate City Stats
    val totalPopulation = 100 + buildings.size * 25
    val totalHappiness = (75 + buildings.size * 4).coerceAtMost(100)
    val dailyRevenue = 50 + buildings.size * 20

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ওয়াফা মেগা সিটি বিল্ডার" else "Wafa City Builder",
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("city_back_btn")) {
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
            // City Metrics Dashboard Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.5.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "জনসংখ্যা (Pop)", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "$totalPopulation 👥", fontWeight = FontWeight.Black, color = ElectricBlue, fontSize = 15.sp)
                    }
                    Column {
                        Text(text = "সুখ সূচক (Happy)", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "$totalHappiness% 😊", fontWeight = FontWeight.Black, color = NeonEmerald, fontSize = 15.sp)
                    }
                    Column {
                        Text(text = "প্রতিদিনের রাজস্ব", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "+$dailyRevenue 🪙", fontWeight = FontWeight.Black, color = CyberGold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (language == AppLanguage.BANGLA) "নগরীর উন্নয়ন প্লট (৮টি জোন):" else "City Construction Zones (8 Slots):",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 8 City Grid Slots
            val slots = (0..7).toList()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(slots) { slotIdx ->
                    val building = buildings.find { it.slotIndex == slotIdx }
                    val blueprint = blueprints.find { it.type == building?.buildingType }

                    Surface(
                        onClick = {
                            selectedSlotIndex = slotIdx
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (building != null) DarkSurfaceVariant else DarkSurfaceCard,
                        border = BorderStroke(1.dp, if (building != null) NeonCyan else DarkBorder),
                        modifier = Modifier.aspectRatio(1.2f).testTag("city_slot_$slotIdx")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = blueprint?.emoji ?: "🏗️",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = blueprint?.nameBn ?: if (language == AppLanguage.BANGLA) "খালি প্লট #$slotIdx" else "Empty Plot #$slotIdx",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (building != null) TextPrimary else TextSecondary
                            )
                            if (building != null) {
                                Text(text = "Level ${building.level}", fontSize = 10.sp, color = NeonEmerald)
                            } else {
                                Text(text = "+ নির্মাণ করুন", fontSize = 10.sp, color = CyberGold)
                            }
                        }
                    }
                }
            }
        }

        // Build Dialog
        if (selectedSlotIndex != null) {
            AlertDialog(
                onDismissRequest = { selectedSlotIndex = null },
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ভবন বা অবকাঠামো নির্মাণ" else "Construct City Building",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        blueprints.forEach { bp ->
                            val canAfford = player.coins >= bp.cost
                            Surface(
                                onClick = {
                                    if (canAfford) {
                                        selectedSlotIndex?.let { viewModel.upgradeOrBuildCitySlot(it, bp.type) }
                                        selectedSlotIndex = null
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (canAfford) NeonCyan else DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = bp.emoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = bp.nameBn, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                            Text(text = "সুখ: +${bp.happinessBonus}% | মানুষ: +${bp.popBonus}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    Text(text = "${bp.cost} 🪙", fontWeight = FontWeight.Bold, color = if (canAfford) CyberGold else DangerRed, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { selectedSlotIndex = null }) {
                        Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurfaceCard
            )
        }
    }
}
