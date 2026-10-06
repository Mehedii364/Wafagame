package com.example.games.adventure

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.rewards.GameResult
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*
import kotlin.math.hypot

data class AdventureItem(
    val id: String,
    val nameBn: String,
    val emoji: String,
    val x: Float,
    val y: Float,
    var isCollected: Boolean = false
)

data class AdventureNpc(
    val nameBn: String,
    val emoji: String,
    val x: Float,
    val y: Float,
    val dialogueBn: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaAdventureScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var playerX by remember { mutableFloatStateOf(0.5f) }
    var playerY by remember { mutableFloatStateOf(0.7f) }

    val npcs = remember {
        listOf(
            AdventureNpc("করিম মাঝি", "👴", 0.3f, 0.3f, "নৌকার হারিয়ে যাওয়া সোনালী কম্পাসটি খুঁজে দিন ভাই! নদীর জোয়ার আসছে!"),
            AdventureNpc("রুপা আপু", "👩‍🎓", 0.75f, 0.4f, "সিলেটের চা বাগান অনেক সুন্দর! বাগানের বুনো ফুল সংগ্রহ করুন।")
        )
    }

    var items by remember {
        mutableStateOf(
            listOf(
                AdventureItem("i_compass", "সোনালী কম্পাস", "🧭", 0.25f, 0.45f),
                AdventureItem("i_flower", "চা বাগানের বুনো ফুল", "🌺", 0.8f, 0.6f),
                AdventureItem("i_sweets", "পোড়াবাড়ির চমচম", "🍬", 0.5f, 0.2f)
            )
        )
    }

    var activeDialogue by remember { mutableStateOf<String?>(null) }
    var questComplete by remember { mutableStateOf(false) }

    fun checkInteractions() {
        // Check items pickup
        items = items.map { item ->
            if (!item.isCollected && hypot(playerX - item.x, playerY - item.y) < 0.12f) {
                viewModel.effectsHelper.vibrateSuccess()
                item.copy(isCollected = true)
            } else item
        }

        // Check Quest completion
        if (items.all { it.isCollected } && !questComplete) {
            questComplete = true
            viewModel.submitGameResult(GameResult("ADVENTURE", score = 150, isWin = true))
        }

        // Check NPC dialogue
        val nearbyNpc = npcs.firstOrNull { hypot(playerX - it.x, playerY - it.y) < 0.15f }
        if (nearbyNpc != null) {
            activeDialogue = "${nearbyNpc.nameBn}: \"${nearbyNpc.dialogueBn}\""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ওয়াফা অ্যাডভেঞ্চার (সদরঘাট ও নদীমাতৃক বাংলা)" else "Wafa Adventure",
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("adventure_back_btn")) {
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
                .padding(12.dp)
        ) {
            // Quest Objective Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, CyberGold)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📜", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "অভিযান লক্ষ্য: সদরঘাটের ৩টি বস্তু সংগ্রহ" else "Quest: Collect all 3 artifacts",
                            fontWeight = FontWeight.Bold,
                            color = CyberGold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "সংগৃহীত: ${items.count { it.isCollected }} / ${items.size}",
                            fontSize = 11.sp,
                            color = NeonEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2D Tile Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // River on Top
                    drawRect(color = Color(0xFF1B4965), topLeft = Offset(0f, 0f), size = Size(w, h * 0.28f))
                    // Shore Sand & Grass
                    drawRect(color = Color(0xFF2C6E49), topLeft = Offset(0f, h * 0.28f), size = Size(w, h * 0.72f))

                    // River Ghat Wooden Pier
                    drawRect(color = Color(0xFF8B5A2B), topLeft = Offset(w * 0.4f, h * 0.15f), size = Size(w * 0.2f, h * 0.25f))

                    // Boat on River
                    drawOval(color = Color(0xFF5C4033), topLeft = Offset(w * 0.2f, h * 0.08f), size = Size(64f, 28f))

                    // Draw Items
                    items.forEach { item ->
                        if (!item.isCollected) {
                            drawCircle(color = CyberGold, radius = 14f, center = Offset(item.x * w, item.y * h))
                        }
                    }

                    // Draw NPCs
                    npcs.forEach { npc ->
                        drawCircle(color = ElectricBlue, radius = 18f, center = Offset(npc.x * w, npc.y * h))
                    }

                    // Draw Player Avatar
                    drawCircle(color = NeonEmerald, radius = 22f, center = Offset(playerX * w, playerY * h))
                    drawCircle(color = BengalGreen, radius = 16f, center = Offset(playerX * w, playerY * h))
                }

                // Dialogue Bubble
                if (activeDialogue != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(10.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, NeonEmerald)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = activeDialogue ?: "", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { activeDialogue = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Adventure Navigation D-Pad Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        playerY = maxOf(0.18f, playerY - 0.08f)
                        checkInteractions()
                    },
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = TextPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                    IconButton(
                        onClick = {
                            playerX = maxOf(0.1f, playerX - 0.08f)
                            checkInteractions()
                        },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = TextPrimary)
                    }

                    IconButton(
                        onClick = {
                            playerX = minOf(0.9f, playerX + 0.08f)
                            checkInteractions()
                        },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = TextPrimary)
                    }
                }

                IconButton(
                    onClick = {
                        playerY = minOf(0.9f, playerY + 0.08f)
                        checkInteractions()
                    },
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = TextPrimary)
                }
            }
        }
    }
}
