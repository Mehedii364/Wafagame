package com.example.games.cricket

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsCricket
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.rewards.GameResult
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaCricketScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    // Match State
    var targetRuns by remember { mutableIntStateOf(24) }
    var currentRuns by remember { mutableIntStateOf(0) }
    var wickets by remember { mutableIntStateOf(0) }
    val maxWickets = 3
    var totalBallsBowled by remember { mutableIntStateOf(0) }
    val maxBalls = 12 // 2 Overs Super Over match

    var commentary by remember {
        mutableStateOf(if (language == AppLanguage.BANGLA) "ম্যাচ শুরু হতে যাচ্ছে! ২ ওভারে ২৫ রান দরকার।" else "Super Over! Target: 25 runs in 2 overs.")
    }

    // Ball Animation State: 0f (bowler hand) to 1f (crease)
    var ballProgress by remember { mutableFloatStateOf(0f) }
    var isBowlerDelivering by remember { mutableStateOf(false) }
    var lastShotResult by remember { mutableStateOf("") }
    var matchOver by remember { mutableStateOf(false) }
    var wonMatch by remember { mutableStateOf(false) }

    fun bowlNextBall() {
        if (matchOver || isBowlerDelivering) return
        isBowlerDelivering = true
        ballProgress = 0f
        lastShotResult = ""
    }

    // Ball delivery loop
    LaunchedEffect(isBowlerDelivering) {
        if (isBowlerDelivering) {
            val deliverySpeed = Random.nextInt(15, 25) // variation in ball speed
            for (step in 1..40) {
                delay(deliverySpeed.toLong())
                ballProgress = step / 40f
            }
            // If user didn't swing: Missed / Dot or bowled
            if (isBowlerDelivering) {
                isBowlerDelivering = false
                totalBallsBowled++
                val isBowled = Random.nextFloat() < 0.20f
                if (isBowled) {
                    wickets++
                    lastShotResult = "BOWLED! ❌"
                    commentary = if (language == AppLanguage.BANGLA) "বোল্ড আউট! স্ট্যাম্প ভেঙে গেছে!" else "Clean bowled! The stumps are shattered!"
                    viewModel.effectsHelper.vibrateCrash()
                } else {
                    lastShotResult = "DOT BALL (0)"
                    commentary = if (language == AppLanguage.BANGLA) "বল ব্যাটে লাগেনি। ডট বল।" else "Beaten outside off stump! Dot ball."
                }

                // Check match outcome
                if (wickets >= maxWickets || totalBallsBowled >= maxBalls) {
                    matchOver = true
                    wonMatch = currentRuns >= targetRuns
                    val finalScore = currentRuns
                    viewModel.submitGameResult(GameResult("CRICKET", score = finalScore, isWin = wonMatch))
                }
            }
        }
    }

    // User Bat Swing Action
    fun swingBat(shotType: String) {
        if (!isBowlerDelivering || matchOver) return

        // Check timing window around ballProgress
        // Sweet spot: 0.70f to 0.88f
        val timing = ballProgress
        isBowlerDelivering = false
        totalBallsBowled++

        when {
            timing in 0.74f..0.84f -> {
                // PERFECT TIMING -> SIX!
                currentRuns += 6
                lastShotResult = "MASSIVE SIX! 🔥"
                commentary = if (language == AppLanguage.BANGLA) "অসাধারণ শট! বল স্টেডিয়ামের বাইরে, ছক্কা!" else "MAGNIFICENT STRIKE! High into the stands for SIX!"
                viewModel.effectsHelper.vibrateSuccess()
            }
            timing in 0.65f..0.92f -> {
                // GOOD TIMING -> FOUR!
                currentRuns += 4
                lastShotResult = "CRACKING FOUR! 🏏"
                commentary = if (language == AppLanguage.BANGLA) "দারুণ টাইমিং! চার রান!" else "Glorious cover drive racing to the fence for FOUR!"
                viewModel.effectsHelper.vibrateSuccess()
            }
            timing in 0.50f..0.96f -> {
                // FAIR TIMING -> 1 or 2 runs
                val runs = if (shotType == "LOFT") 2 else 1
                currentRuns += runs
                lastShotResult = "$runs RUNS"
                commentary = if (language == AppLanguage.BANGLA) "গ্যাপে ঠেলে দিয়ে $runs রান নিলেন।" else "Pushed into the gap for $runs."
                viewModel.effectsHelper.vibrateTap()
            }
            else -> {
                // MISTIMED -> Catch Out!
                wickets++
                lastShotResult = "CAUGHT OUT! ❌"
                commentary = if (language == AppLanguage.BANGLA) "মিশ শট! ক্যাচ তুলে দিলেন ফিল্ডারের হাতে!" else "Mistimed completely! Easy catch for mid-on!"
                viewModel.effectsHelper.vibrateCrash()
            }
        }

        // Check Match Status
        if (currentRuns >= targetRuns) {
            matchOver = true
            wonMatch = true
            viewModel.submitGameResult(GameResult("CRICKET", score = currentRuns, isWin = true, extraStatKey = "SIXES", extraStatValue = 2))
        } else if (wickets >= maxWickets || totalBallsBowled >= maxBalls) {
            matchOver = true
            wonMatch = false
            viewModel.submitGameResult(GameResult("CRICKET", score = currentRuns, isWin = false))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ওয়াফা ক্রিকেট শোডাউন" else "Wafa Cricket Showdown",
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("cricket_back_btn")) {
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
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Scoreboard Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = BorderStroke(1.5.dp, CyberGold)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "BANGLADESH TIGERS", fontWeight = FontWeight.Bold, color = NeonEmerald, fontSize = 13.sp)
                            Text(
                                text = "$currentRuns / $wickets",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = CyberGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val overs = "${totalBallsBowled / 6}.${totalBallsBowled % 6}"
                            Text(text = "Overs: $overs / 2.0", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            val needed = maxOf(0, targetRuns - currentRuns)
                            val remainingBalls = maxOf(0, maxBalls - totalBallsBowled)
                            Text(text = "Need $needed from $remainingBalls balls", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = commentary,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cricket Pitch Visual Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Field Grass
                    drawRect(color = Color(0xFF1B4D2E), size = size)

                    // Cricket 22-Yard Pitch
                    val pitchWidth = w * 0.44f
                    val pitchLeft = (w - pitchWidth) / 2f
                    drawRect(
                        color = Color(0xFFD4A373),
                        topLeft = Offset(pitchLeft, h * 0.10f),
                        size = Size(pitchWidth, h * 0.80f)
                    )

                    // Crease Lines
                    drawLine(
                        color = Color.White,
                        start = Offset(pitchLeft, h * 0.22f),
                        end = Offset(pitchLeft + pitchWidth, h * 0.22f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(pitchLeft, h * 0.78f),
                        end = Offset(pitchLeft + pitchWidth, h * 0.78f),
                        strokeWidth = 4f
                    )

                    // Wickets at bowler end
                    drawRect(color = Color(0xFF8B4513), topLeft = Offset(w / 2f - 12f, h * 0.18f), size = Size(24f, 10f))
                    // Wickets at batter end
                    drawRect(color = Color(0xFF8B4513), topLeft = Offset(w / 2f - 14f, h * 0.82f), size = Size(28f, 12f))

                    // Batter Representation (Green circle helmet)
                    drawCircle(color = BengalGreen, radius = 20f, center = Offset(w / 2f, h * 0.75f))
                    // Bat
                    drawRect(color = Color(0xFFE0A96D), topLeft = Offset(w / 2f + 16f, h * 0.73f), size = Size(10f, 34f))

                    // Ball (Crimson Red) moving down
                    if (isBowlerDelivering) {
                        val ballY = (h * 0.22f) + (ballProgress * (h * 0.56f))
                        // Ball Shadow
                        drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = 10f, center = Offset(w / 2f, ballY + 6f))
                        // Red Leather Cricket Ball
                        drawCircle(color = CrimsonSun, radius = 12f, center = Offset(w / 2f, ballY))
                        drawCircle(color = Color.White, radius = 3f, center = Offset(w / 2f - 3f, ballY - 3f))
                    }
                }

                // Shot Result Popup in Pitch
                if (lastShotResult.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceCard.copy(alpha = 0.9f),
                        border = BorderStroke(1.5.dp, CyberGold)
                    ) {
                        Text(
                            text = lastShotResult,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = CyberGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action / Batting Controls
            if (!matchOver) {
                if (!isBowlerDelivering) {
                    Button(
                        onClick = { bowlNextBall() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("cricket_bowl_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold, contentColor = DarkBackground),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.SportsCricket, contentDescription = "Bowl")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (language == AppLanguage.BANGLA) "পরবর্তী বল করুন (BOWL BALL)" else "NEXT BALL", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                } else {
                    // Batting Shot Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { swingBat("DRIVE") },
                            modifier = Modifier.weight(1f).height(52.dp).testTag("shot_drive_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "DRIVE (4)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = { swingBat("LOFT") },
                            modifier = Modifier.weight(1f).height(52.dp).testTag("shot_loft_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "LOFT (SIX!)", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }

                        Button(
                            onClick = { swingBat("DEFEND") },
                            modifier = Modifier.weight(1f).height(52.dp).testTag("shot_defend_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "DEFEND (1)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                // Match Ended Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.5.dp, if (wonMatch) NeonEmerald else DangerRed)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (wonMatch) {
                                if (language == AppLanguage.BANGLA) "ম্যাচ জয়ী! বাংলাদেশ জয়যুক্ত!" else "MATCH WON! TIGERS ROAR!"
                            } else {
                                if (language == AppLanguage.BANGLA) "ম্যাচ শেষ! লক্ষ্য অর্জন হয়নি।" else "MATCH OVER! Target Not Reached."
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (wonMatch) CyberGold else DangerRed
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                matchOver = false
                                currentRuns = 0
                                wickets = 0
                                totalBallsBowled = 0
                                lastShotResult = ""
                                commentary = if (language == AppLanguage.BANGLA) "নতুন ম্যাচ শুরু!" else "New match underway!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = if (language == AppLanguage.BANGLA) "আবার খেলুন" else "Play Super Over Again", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
