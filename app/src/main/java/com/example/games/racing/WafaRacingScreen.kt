package com.example.games.racing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.rewards.GameResult
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class RacingVehicle(
    val id: String,
    val name: String,
    val nameBn: String,
    val emoji: String,
    val topSpeed: Int,
    val handling: Int,
    val color: Color
)

data class RacingTrack(
    val id: String,
    val name: String,
    val nameBn: String,
    val bgGradient: List<Color>,
    val roadColor: Color,
    val obstacleType: String
)

data class Obstacle(
    val id: Int,
    val lane: Int, // 0 = Left, 1 = Center, 2 = Right
    var y: Float, // 0f (top) to 1f (bottom)
    val isCoin: Boolean = false,
    val isNitro: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaRacingScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val vehicles = remember {
        listOf(
            RacingVehicle("v_rickshaw", "Rocket Rickshaw", "রকেট রিকশা", "🛺", 120, 95, NeonEmerald),
            RacingVehicle("v_cng", "Turbo Green CNG", "টার্বো সিএনজি", "🛺", 145, 85, CyberGold),
            RacingVehicle("v_chander", "Chander Gari 4x4", "চান্দের গাড়ি ৪x৪", "🚙", 155, 75, ElectricBlue),
            RacingVehicle("v_bus", "Royal Shamoli Bus", "রয়্যাল বাস এক্সপ্রেস", "🚌", 135, 60, CrimsonSun),
            RacingVehicle("v_sports", "Cyber Padma Racer", "সাইবার পদ্মা রেসার", "🏎️", 185, 90, NeonCyan)
        )
    }

    val tracks = remember {
        listOf(
            RacingTrack("t_dhaka", "Dhaka Flyover", "ঢাকা ফ্লাইওভার এক্সপ্রেস", listOf(Color(0xFF0F2027), Color(0xFF203A43)), Color(0xFF2C3E50), "TRAFFIC"),
            RacingTrack("t_padma", "Padma Bridge Route", "পদ্মা সেতু রুট", listOf(Color(0xFF141E30), Color(0xFF243B55)), Color(0xFF34495E), "POTHOLE"),
            RacingTrack("t_village", "Village Green Road", "সোনার বাংলা গ্রাম সড়ক", listOf(Color(0xFF0B3B24), Color(0xFF134E5E)), Color(0xFF4A4E51), "COW"),
            RacingTrack("t_sylhet", "Sylhet Tea Highway", "সিলেট চা বাগান হাইওয়ে", listOf(Color(0xFF0A2E1D), Color(0xFF165B33)), Color(0xFF2E4053), "ROCKS"),
            RacingTrack("t_marine", "Cox's Marine Drive", "কক্সবাজার মেরিন ড্রাইভ", listOf(Color(0xFF003973), Color(0xFFE5E5BE)), Color(0xFF2C3E50), "WATER")
        )
    }

    var selectedVehicle by remember { mutableStateOf(vehicles[0]) }
    var selectedTrack by remember { mutableStateOf(tracks[0]) }

    // Game Loop States
    var isPlaying by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var isCrashed by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(0) }

    // Player position: 0f (left), 1f (center), 2f (right)
    var playerLanePosition by remember { mutableFloatStateOf(1f) }
    var currentSpeed by remember { mutableIntStateOf(0) }
    var distanceTravelled by remember { mutableFloatStateOf(0f) }
    val maxDistance = 1000f

    var playerHealth by remember { mutableIntStateOf(100) }
    var coinsCollected by remember { mutableIntStateOf(0) }
    var nitroFuel by remember { mutableIntStateOf(50) }
    var isNitroActive by remember { mutableStateOf(false) }

    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }
    var roadOffset by remember { mutableFloatStateOf(0f) }

    // Start Game Trigger
    fun startRace() {
        isFinished = false
        isCrashed = false
        playerHealth = 100
        coinsCollected = 0
        nitroFuel = 60
        distanceTravelled = 0f
        playerLanePosition = 1f
        obstacles = emptyList()
        currentSpeed = 0
        countdown = 3
    }

    // Countdown Timer
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
            if (countdown == 0) {
                isPlaying = true
                currentSpeed = selectedVehicle.topSpeed / 2
            }
        }
    }

    // 60fps Game Loop
    LaunchedEffect(isPlaying, isNitroActive) {
        var obstacleIdCounter = 0
        while (isPlaying) {
            delay(32) // ~30-35 FPS smooth mobile game tick

            // Speed calculation
            val targetSpeed = if (isNitroActive && nitroFuel > 0) {
                selectedVehicle.topSpeed + 40
            } else {
                selectedVehicle.topSpeed
            }
            if (currentSpeed < targetSpeed) currentSpeed = minOf(targetSpeed, currentSpeed + 3)
            if (currentSpeed > targetSpeed) currentSpeed = maxOf(targetSpeed, currentSpeed - 4)

            if (isNitroActive && nitroFuel > 0) {
                nitroFuel = maxOf(0, nitroFuel - 1)
                if (nitroFuel == 0) isNitroActive = false
            }

            // Road scroll animation
            roadOffset = (roadOffset + (currentSpeed / 20f)) % 100f
            distanceTravelled += (currentSpeed / 60f)

            // Spawn obstacles
            val updatedObstacles = obstacles.map { it.copy(y = it.y + (currentSpeed / 2800f)) }
                .filter { it.y < 1.1f }
                .toMutableList()

            if (Random.nextFloat() < 0.05f && updatedObstacles.size < 5) {
                val lane = Random.nextInt(0, 3)
                val typeRoll = Random.nextFloat()
                val isCoin = typeRoll < 0.35f
                val isNitro = typeRoll in 0.35f..0.50f
                updatedObstacles.add(Obstacle(obstacleIdCounter++, lane, -0.1f, isCoin, isNitro))
            }

            // Collision check (Player is around y = 0.82f)
            val currentLaneInt = playerLanePosition.toInt().coerceIn(0, 2)
            val collided = updatedObstacles.firstOrNull {
                it.lane == currentLaneInt && it.y in 0.74f..0.88f
            }

            if (collided != null) {
                updatedObstacles.remove(collided)
                if (collided.isCoin) {
                    coinsCollected += 10
                    viewModel.effectsHelper.vibrateTap()
                } else if (collided.isNitro) {
                    nitroFuel = minOf(100, nitroFuel + 35)
                    viewModel.effectsHelper.vibrateTap()
                } else {
                    // Crash obstacle
                    playerHealth = maxOf(0, playerHealth - 25)
                    currentSpeed = maxOf(30, currentSpeed - 40)
                    viewModel.effectsHelper.vibrateCrash()

                    if (playerHealth <= 0) {
                        isPlaying = false
                        isCrashed = true
                        val finalScore = (distanceTravelled * 1.5f + coinsCollected * 5).toInt()
                        viewModel.submitGameResult(GameResult("RACING", score = finalScore, isWin = false))
                    }
                }
            }

            obstacles = updatedObstacles

            // Check Finish
            if (distanceTravelled >= maxDistance) {
                isPlaying = false
                isFinished = true
                val finalScore = (1000 + coinsCollected * 10 + playerHealth * 5)
                viewModel.submitGameResult(GameResult("RACING", score = finalScore, isWin = true))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ওয়াফা রেসিং" else "Wafa Racing",
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                        Text(
                            text = selectedTrack.nameBn,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("racing_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isPlaying && countdown == 0 && !isFinished && !isCrashed) {
                // Pre-Race Garage Selection Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "যানবাহন ও ট্র্যাক নির্বাচন করুন" else "Select Vehicle & Track",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vehicle Showcase Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceCard,
                        border = BorderStroke(2.dp, selectedVehicle.color)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = selectedVehicle.emoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) selectedVehicle.nameBn else selectedVehicle.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Max Speed: ${selectedVehicle.topSpeed} km/h",
                                    color = NeonEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Handling: ${selectedVehicle.handling}%",
                                    color = CyberGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Selector Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(vehicles) { vehicle ->
                            val isSelected = vehicle.id == selectedVehicle.id
                            Surface(
                                onClick = { selectedVehicle = vehicle },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) DarkSurfaceVariant else DarkSurface,
                                border = BorderStroke(1.5.dp, if (isSelected) vehicle.color else DarkBorder),
                                modifier = Modifier.size(width = 110.dp, height = 75.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = vehicle.emoji, fontSize = 22.sp)
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) vehicle.nameBn else vehicle.name,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Track Selector Row
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ট্র্যাক রুট:" else "Racing Route:",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(tracks) { track ->
                            val isSelected = track.id == selectedTrack.id
                            Surface(
                                onClick = { selectedTrack = track },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) DarkSurfaceVariant else DarkSurface,
                                border = BorderStroke(1.5.dp, if (isSelected) NeonCyan else DarkBorder),
                                modifier = Modifier.size(width = 130.dp, height = 65.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) track.nameBn else track.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = if (isSelected) NeonCyan else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Start Race Button
                    Button(
                        onClick = { startRace() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_race_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonEmerald,
                            contentColor = DarkBackground
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "রেস শুরু করুন (START RACE)" else "START RACE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            } else if (countdown > 0) {
                // 3-2-1 Countdown Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$countdown",
                            fontSize = 84.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberGold
                        )
                        Text(
                            text = if (language == AppLanguage.BANGLA) "প্রস্তুত হোন!" else "GET READY!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                // ACTIVE RACE SCREEN
                Column(modifier = Modifier.fillMaxSize()) {
                    // HUD Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceCard,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Speed
                            Column {
                                Text(text = "$currentSpeed KM/H", fontWeight = FontWeight.Black, color = if (isNitroActive) NeonCyan else NeonEmerald, fontSize = 16.sp)
                                Text(text = "Speed", fontSize = 10.sp, color = TextMuted)
                            }

                            // Health
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = "HP", tint = DangerRed, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "$playerHealth%", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                }
                                LinearProgressIndicator(
                                    progress = { playerHealth / 100f },
                                    modifier = Modifier.width(60.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = if (playerHealth > 40) NeonEmerald else DangerRed,
                                    trackColor = DarkBorder
                                )
                            }

                            // Coins
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = CyberGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "$coinsCollected", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 13.sp)
                            }

                            // Progress
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "${(distanceTravelled / maxDistance * 100).toInt()}%", fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 12.sp)
                                Text(text = "Finish", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    // Interactive Race Canvas
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount > 15) {
                                        playerLanePosition = minOf(2f, playerLanePosition + 1f)
                                    } else if (dragAmount < -15) {
                                        playerLanePosition = maxOf(0f, playerLanePosition - 1f)
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw Track Background
                            drawRect(
                                brush = Brush.verticalGradient(selectedTrack.bgGradient),
                                size = size
                            )

                            // Draw Road Surface (Centered, taking 75% of width)
                            val roadLeft = w * 0.12f
                            val roadWidth = w * 0.76f
                            val roadRight = roadLeft + roadWidth

                            drawRect(
                                color = selectedTrack.roadColor,
                                topLeft = Offset(roadLeft, 0f),
                                size = Size(roadWidth, h)
                            )

                            // Road Borders
                            drawRect(color = Color.White, topLeft = Offset(roadLeft - 4f, 0f), size = Size(4f, h))
                            drawRect(color = Color.White, topLeft = Offset(roadRight, 0f), size = Size(4f, h))

                            // Lane Markers (2 dashed lines)
                            val laneWidth = roadWidth / 3f
                            val lane1X = roadLeft + laneWidth
                            val lane2X = roadLeft + laneWidth * 2

                            val dashLength = 40f
                            val gapLength = 30f
                            val totalSegment = dashLength + gapLength
                            var yPos = (roadOffset % totalSegment) - totalSegment

                            while (yPos < h) {
                                drawRect(
                                    color = Color(0xFFF1C40F),
                                    topLeft = Offset(lane1X - 2f, yPos),
                                    size = Size(4f, dashLength)
                                )
                                drawRect(
                                    color = Color(0xFFF1C40F),
                                    topLeft = Offset(lane2X - 2f, yPos),
                                    size = Size(4f, dashLength)
                                )
                                yPos += totalSegment
                            }

                            // Draw Obstacles / Pickups
                            obstacles.forEach { obs ->
                                val obsLaneX = roadLeft + (obs.lane * laneWidth) + (laneWidth / 2f)
                                val obsY = obs.y * h

                                if (obs.isCoin) {
                                    // Golden Coin Pickup
                                    drawCircle(
                                        color = CyberGold,
                                        radius = 16f,
                                        center = Offset(obsLaneX, obsY)
                                    )
                                    drawCircle(
                                        color = CyberAmber,
                                        radius = 10f,
                                        center = Offset(obsLaneX, obsY)
                                    )
                                } else if (obs.isNitro) {
                                    // Cyan Nitro Canister
                                    drawRoundRect(
                                        color = NeonCyan,
                                        topLeft = Offset(obsLaneX - 12f, obsY - 18f),
                                        size = Size(24f, 36f),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                } else {
                                    // Traffic / Obstacle Vehicle (Red/Orange box)
                                    drawRoundRect(
                                        color = CrimsonSun,
                                        topLeft = Offset(obsLaneX - 20f, obsY - 32f),
                                        size = Size(40f, 64f),
                                        cornerRadius = CornerRadius(8f, 8f)
                                    )
                                    // Windshield
                                    drawRoundRect(
                                        color = Color(0xFF2C3E50),
                                        topLeft = Offset(obsLaneX - 16f, obsY - 12f),
                                        size = Size(32f, 20f),
                                        cornerRadius = CornerRadius(4f, 4f)
                                    )
                                }
                            }

                            // Draw Player Vehicle
                            val playerLaneX = roadLeft + (playerLanePosition * laneWidth) + (laneWidth / 2f)
                            val playerY = h * 0.82f

                            // Vehicle Shadow
                            drawOval(
                                color = Color.Black.copy(alpha = 0.4f),
                                topLeft = Offset(playerLaneX - 24f, playerY + 24f),
                                size = Size(48f, 18f)
                            )

                            // Vehicle Body
                            drawRoundRect(
                                color = selectedVehicle.color,
                                topLeft = Offset(playerLaneX - 22f, playerY - 36f),
                                size = Size(44f, 72f),
                                cornerRadius = CornerRadius(10f, 10f)
                            )

                            // Vehicle Roof / Cockpit
                            drawRoundRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(playerLaneX - 16f, playerY - 16f),
                                size = Size(32f, 32f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            // Headlights
                            drawCircle(color = Color(0xFFFFF9C4), radius = 6f, center = Offset(playerLaneX - 14f, playerY - 34f))
                            drawCircle(color = Color(0xFFFFF9C4), radius = 6f, center = Offset(playerLaneX + 14f, playerY - 34f))

                            // Nitro Flame
                            if (isNitroActive) {
                                drawCircle(color = NeonCyan, radius = 10f, center = Offset(playerLaneX, playerY + 40f))
                                drawCircle(color = Color.White, radius = 5f, center = Offset(playerLaneX, playerY + 38f))
                            }
                        }

                        // On-Screen Steering & Boost Touch Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left & Right Steer
                            Row {
                                FilledTonalIconButton(
                                    onClick = {
                                        playerLanePosition = maxOf(0f, playerLanePosition - 1f)
                                        viewModel.effectsHelper.vibrateTap()
                                    },
                                    modifier = Modifier.size(60.dp).testTag("steer_left_btn"),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = DarkSurfaceCard.copy(alpha = 0.85f))
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Steer Left", tint = TextPrimary, modifier = Modifier.size(32.dp))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                FilledTonalIconButton(
                                    onClick = {
                                        playerLanePosition = minOf(2f, playerLanePosition + 1f)
                                        viewModel.effectsHelper.vibrateTap()
                                    },
                                    modifier = Modifier.size(60.dp).testTag("steer_right_btn"),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = DarkSurfaceCard.copy(alpha = 0.85f))
                                ) {
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Steer Right", tint = TextPrimary, modifier = Modifier.size(32.dp))
                                }
                            }

                            // Nitro Boost Button
                            Button(
                                onClick = {
                                    if (nitroFuel > 10) {
                                        isNitroActive = !isNitroActive
                                        viewModel.effectsHelper.vibrateTap()
                                    }
                                },
                                modifier = Modifier.size(72.dp).testTag("nitro_btn"),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNitroActive) NeonCyan else CyberGold,
                                    contentColor = DarkBackground
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Bolt, contentDescription = "Nitro", modifier = Modifier.size(24.dp))
                                    Text(text = "$nitroFuel%", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Finish / Crash Dialog
                if (isFinished || isCrashed) {
                    AlertDialog(
                        onDismissRequest = { isFinished = false; isCrashed = false },
                        title = {
                            Text(
                                text = if (isFinished) {
                                    if (language == AppLanguage.BANGLA) "রেস ফিনিশ! বিজয়ী!" else "RACE FINISHED! VICTORY!"
                                } else {
                                    if (language == AppLanguage.BANGLA) "যানবাহন দুর্ঘটনা!" else "CRASHED!"
                                },
                                fontWeight = FontWeight.Black,
                                color = if (isFinished) CyberGold else DangerRed
                            )
                        },
                        text = {
                            Column {
                                Text(
                                    text = if (isFinished) {
                                        if (language == AppLanguage.BANGLA) "অসাধারণ রেসিং দক্ষতা! আপনি সফলভাবে ফিনিশ লাইনে পৌঁছেছেন।"
                                        else "Magnificent driving! You crossed the finish line with pride."
                                    } else {
                                        if (language == AppLanguage.BANGLA) "রাস্তার বাধা এড়াতে পারেননি। আবার চেষ্টা করুন!"
                                        else "Too much obstacle damage. Try again and steer carefully!"
                                    },
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Distance: ${distanceTravelled.toInt()}m", color = TextSecondary)
                                Text(text = "Coins Collected: $coinsCollected", color = CyberGold, fontWeight = FontWeight.Bold)
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { startRace() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground)
                            ) {
                                Text(text = if (language == AppLanguage.BANGLA) "পুনরায় খেলুন" else "Play Again", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { isFinished = false; isCrashed = false }) {
                                Text(text = if (language == AppLanguage.BANGLA) "গ্যারেজে ফিরুন" else "Back to Garage", color = TextSecondary)
                            }
                        },
                        containerColor = DarkSurfaceCard
                    )
                }
            }
        }
    }
}
