package com.example.games.football

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
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaFootballScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var matchSecondsRemaining by remember { mutableIntStateOf(60) }
    var isMatchActive by remember { mutableStateOf(false) }
    var matchFinished by remember { mutableStateOf(false) }

    var userGoals by remember { mutableIntStateOf(0) }
    var opponentGoals by remember { mutableIntStateOf(0) }

    // Positions normalized (0f..1f):
    // Opponent Goal at top (y = 0.05f), User Goal at bottom (y = 0.95f)
    var playerX by remember { mutableFloatStateOf(0.5f) }
    var playerY by remember { mutableFloatStateOf(0.70f) }

    var ballX by remember { mutableFloatStateOf(0.5f) }
    var ballY by remember { mutableFloatStateOf(0.65f) }
    var ballVelX by remember { mutableFloatStateOf(0f) }
    var ballVelY by remember { mutableFloatStateOf(0f) }

    // Opponent Defender & Goalie
    var opponentGoalieX by remember { mutableFloatStateOf(0.5f) }
    var goalBannerText by remember { mutableStateOf("") }

    fun startMatch() {
        matchSecondsRemaining = 60
        userGoals = 0
        opponentGoals = 0
        isMatchActive = true
        matchFinished = false
        playerX = 0.5f
        playerY = 0.70f
        ballX = 0.5f
        ballY = 0.65f
        ballVelX = 0f
        ballVelY = 0f
        goalBannerText = ""
    }

    // Timer countdown
    LaunchedEffect(isMatchActive) {
        while (isMatchActive && matchSecondsRemaining > 0) {
            delay(1000)
            matchSecondsRemaining--
            if (matchSecondsRemaining == 0) {
                isMatchActive = false
                matchFinished = true
                val isWin = userGoals > opponentGoals
                val finalScore = (userGoals * 100 + 50)
                viewModel.submitGameResult(GameResult("FOOTBALL", score = finalScore, isWin = isWin, extraStatKey = "GOALS", extraStatValue = userGoals))
            }
        }
    }

    // Match Physics Loop (30 FPS)
    LaunchedEffect(isMatchActive) {
        while (isMatchActive) {
            delay(33)

            // Ball movement & friction
            ballX = (ballX + ballVelX).coerceIn(0.08f, 0.92f)
            ballY = (ballY + ballVelY).coerceIn(0.04f, 0.96f)
            ballVelX *= 0.92f
            ballVelY *= 0.92f

            // Player Dribble Magnetic Radius
            val distToBall = hypot(playerX - ballX, playerY - ballY)
            if (distToBall < 0.08f && hypot(ballVelX, ballVelY) < 0.01f) {
                ballX = playerX
                ballY = playerY - 0.05f
            }

            // AI Goalie movement tracking ball X
            if (ballX > opponentGoalieX) opponentGoalieX = minOf(0.68f, opponentGoalieX + 0.015f)
            if (ballX < opponentGoalieX) opponentGoalieX = maxOf(0.32f, opponentGoalieX - 0.015f)

            // Goal Check at Opponent Top Goal (y < 0.08f, x in 0.35f..0.65f)
            if (ballY < 0.08f && ballX in 0.35f..0.65f) {
                // Check Goalie save
                val distToGoalie = hypot(ballX - opponentGoalieX, ballY - 0.08f)
                if (distToGoalie < 0.07f) {
                    // Blocked! Bounce back
                    ballVelY = 0.04f
                    viewModel.effectsHelper.vibrateCrash()
                } else {
                    // GOAL!
                    userGoals++
                    goalBannerText = "GOAAAL! ⚽🔥"
                    viewModel.effectsHelper.vibrateSuccess()
                    // Reset to center
                    ballX = 0.5f
                    ballY = 0.5f
                    ballVelX = 0f
                    ballVelY = 0f
                    playerX = 0.5f
                    playerY = 0.70f
                }
            }

            // Opponent rare counter-attack goal
            if (Random.nextFloat() < 0.003f && ballY > 0.85f && userGoals > 0) {
                opponentGoals++
                goalBannerText = "OPPONENT GOAL! ⚽"
                viewModel.effectsHelper.vibrateCrash()
                ballX = 0.5f
                ballY = 0.5f
                ballVelX = 0f
                ballVelY = 0f
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ওয়াফা ফুটবল ২০২৬" else "Wafa Football 2026",
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("football_back_btn")) {
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
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Match Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "DHAKA: $userGoals", fontWeight = FontWeight.Black, color = NeonEmerald, fontSize = 16.sp)
                    Text(text = "⏱️ ${matchSecondsRemaining}s", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 14.sp)
                    Text(text = "CHATTOGRAM: $opponentGoals", fontWeight = FontWeight.Black, color = ElectricBlue, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Soccer Pitch Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Field Grass (Emerald lawn with alternating stripes)
                    drawRect(color = Color(0xFF1E6B38), size = size)

                    val stripeHeight = h / 6f
                    for (i in 0..5 step 2) {
                        drawRect(
                            color = Color(0xFF195E30),
                            topLeft = Offset(0f, i * stripeHeight),
                            size = Size(w, stripeHeight)
                        )
                    }

                    // Field Boundary
                    val margin = 20f
                    drawRect(
                        color = Color.White.copy(alpha = 0.8f),
                        topLeft = Offset(margin, margin),
                        size = Size(w - margin * 2, h - margin * 2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )

                    // Halfway Line & Center Circle
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(margin, h / 2f),
                        end = Offset(w - margin, h / 2f),
                        strokeWidth = 3f
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = 45f,
                        center = Offset(w / 2f, h / 2f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                    )

                    // Top Opponent Goal
                    val goalWidth = w * 0.34f
                    val goalLeft = (w - goalWidth) / 2f
                    drawRect(
                        color = Color.White.copy(alpha = 0.9f),
                        topLeft = Offset(goalLeft, margin),
                        size = Size(goalWidth, 30f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )

                    // Bottom User Goal
                    drawRect(
                        color = Color.White.copy(alpha = 0.9f),
                        topLeft = Offset(goalLeft, h - margin - 30f),
                        size = Size(goalWidth, 30f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )

                    // Draw Opponent Goalie (Blue Circle)
                    drawCircle(
                        color = ElectricBlue,
                        radius = 18f,
                        center = Offset(opponentGoalieX * w, 0.08f * h)
                    )

                    // Draw User Striker (Bengal Green Circle with Gold ring)
                    drawCircle(
                        color = CyberGold,
                        radius = 20f,
                        center = Offset(playerX * w, playerY * h)
                    )
                    drawCircle(
                        color = NeonEmerald,
                        radius = 16f,
                        center = Offset(playerX * w, playerY * h)
                    )

                    // Draw Soccer Ball
                    drawCircle(
                        color = Color.White,
                        radius = 12f,
                        center = Offset(ballX * w, ballY * h)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 5f,
                        center = Offset(ballX * w, ballY * h)
                    )
                }

                // Goal Banner Flash
                if (goalBannerText.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceCard.copy(alpha = 0.95f),
                        border = BorderStroke(2.dp, CyberGold)
                    ) {
                        Text(
                            text = goalBannerText,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Football Controls (Directional movement & Shoot/Pass)
            if (isMatchActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // D-PAD Controller
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = { playerY = maxOf(0.20f, playerY - 0.06f) },
                            modifier = Modifier.size(46.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = TextPrimary)
                        }

                        Row {
                            IconButton(
                                onClick = { playerX = maxOf(0.12f, playerX - 0.06f) },
                                modifier = Modifier.size(46.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                            ) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(32.dp))
                            IconButton(
                                onClick = { playerX = minOf(0.88f, playerX + 0.06f) },
                                modifier = Modifier.size(46.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                            ) {
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = TextPrimary)
                            }
                        }

                        IconButton(
                            onClick = { playerY = minOf(0.88f, playerY + 0.06f) },
                            modifier = Modifier.size(46.dp).clip(CircleShape).background(DarkSurfaceCard).border(1.dp, DarkBorder, CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = TextPrimary)
                        }
                    }

                    // Action Buttons (SHOOT & SPRINT)
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                // SHOOT Power Shot towards opponent goal
                                ballVelY = -0.065f
                                ballVelX = ((0.5f - ballX) * 0.08f) + Random.nextFloat() * 0.02f - 0.01f
                                goalBannerText = ""
                                viewModel.effectsHelper.vibrateTap()
                            },
                            modifier = Modifier.size(72.dp).testTag("football_shoot_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun, contentColor = Color.White),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "SHOOT", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                // PASS to forward area
                                ballVelY = -0.04f
                                goalBannerText = ""
                                viewModel.effectsHelper.vibrateTap()
                            },
                            modifier = Modifier.size(62.dp).testTag("football_pass_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "PASS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Pre-match or Match-Finished Button
                Button(
                    onClick = { startMatch() },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("football_start_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (matchFinished) {
                            if (language == AppLanguage.BANGLA) "পুনরায় খেলুন (PLAY AGAIN)" else "PLAY AGAIN"
                        } else {
                            if (language == AppLanguage.BANGLA) "ম্যাচ শুরু করুন (KICK OFF)" else "KICK OFF MATCH"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
