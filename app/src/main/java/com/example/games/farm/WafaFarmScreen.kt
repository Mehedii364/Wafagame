package com.example.games.farm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.database.entities.FarmPlotEntity
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class CropDefinition(
    val type: String,
    val nameBn: String,
    val emoji: String,
    val growthSec: Int,
    val harvestReward: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaFarmScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val farmPlots by viewModel.farmPlots.collectAsStateWithLifecycle()
    var selectedPlotForPlanting by remember { mutableStateOf<FarmPlotEntity?>(null) }

    // Live clock ticker to trigger recomposition for crop growth timers
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val cropDefinitions = remember {
        listOf(
            CropDefinition("RICE", "আমন ধান", "🌾", 15, 45),
            CropDefinition("MUSTARD", "হলুদ সরিষা", "🌼", 20, 60),
            CropDefinition("JUTE", "সোনালী পাট", "🌿", 25, 75),
            CropDefinition("POTATO", "গোল আলু", "🥔", 30, 95),
            CropDefinition("TEA", "সিলেটি চা", "🍃", 35, 120),
            CropDefinition("MANGO", "রাজশাহীর আম", "🥭", 45, 160)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সোনার বাংলা এগ্রো ফার্ম" else "Wafa Agro Farm",
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("farm_back_btn")) {
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
            // Farm Overview Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, BengalGreen)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🚜", fontSize = 36.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "উর্বর ফসলি জমি" else "Fertile Bengal Farmland",
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "বীজ রোপণ করুন ও সময়মতো ফসল ঘরে তুলুন।" else "Plant native crops and harvest for coins & XP.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6 Farm Plots (2 columns x 3 rows)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(farmPlots) { plot ->
                    val cropDef = cropDefinitions.find { it.type == plot.cropType }
                    val isGrowing = plot.cropType != "NONE" && plot.plantedTime > 0
                    val elapsedSec = if (isGrowing) ((currentTimeMillis - plot.plantedTime) / 1000).toInt() else 0
                    val isReady = isGrowing && elapsedSec >= plot.growthDurationSec
                    val progressFraction = if (isGrowing) (elapsedSec.toFloat() / plot.growthDurationSec).coerceIn(0f, 1f) else 0f

                    Surface(
                        onClick = {
                            if (plot.cropType == "NONE") {
                                selectedPlotForPlanting = plot
                            } else if (isReady) {
                                viewModel.harvestPlot(plot.plotId, plot.cropType)
                                viewModel.effectsHelper.vibrateSuccess()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isReady) Color(0xFF1E382B) else DarkSurfaceCard,
                        border = BorderStroke(1.5.dp, if (isReady) NeonEmerald else DarkBorder),
                        modifier = Modifier.aspectRatio(1.1f).testTag("farm_plot_${plot.plotId}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isReady) "✨ ${cropDef?.emoji ?: "🌾"}" else if (isGrowing) "🌱" else "🟫",
                                fontSize = 34.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isGrowing) (cropDef?.nameBn ?: "ফসল") else if (language == AppLanguage.BANGLA) "অনাবাদী জমি (প্লট ${plot.plotId})" else "Empty Plot ${plot.plotId}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isReady) CyberGold else TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isGrowing) {
                                if (isReady) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonEmerald
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.BANGLA) "ফসল তুলুন" else "Harvest",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            color = DarkBackground,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        )
                                    }
                                } else {
                                    val remaining = maxOf(0, plot.growthDurationSec - elapsedSec)
                                    Text(text = "বাকি ${remaining}s", fontSize = 11.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier.fillMaxWidth(0.8f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                                        color = NeonEmerald,
                                        trackColor = DarkBorder
                                    )
                                }
                            } else {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "+ বীজ বপন" else "+ Plant Seed",
                                    fontSize = 11.sp,
                                    color = CyberGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sheet / Dialog for Seed Selection
        if (selectedPlotForPlanting != null) {
            AlertDialog(
                onDismissRequest = { selectedPlotForPlanting = null },
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "বীজ নির্বাচন করুন" else "Select Crop Seed",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cropDefinitions.forEach { crop ->
                            Surface(
                                onClick = {
                                    selectedPlotForPlanting?.let {
                                        viewModel.plantCrop(it.plotId, crop.type)
                                    }
                                    selectedPlotForPlanting = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceVariant,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = crop.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = crop.nameBn, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = "সময়: ${crop.growthSec}s | পুরস্কার: ${crop.harvestReward} 🪙", fontSize = 11.sp, color = NeonEmerald)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { selectedPlotForPlanting = null }) {
                        Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel", color = TextSecondary)
                    }
                },
                containerColor = DarkSurfaceCard
            )
        }
    }
}
