package com.example.games.minigames

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaMiniGamesScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Tap Sprint, 1 = Reaction Test

    // Tap Sprint State
    var tapCount by remember { mutableIntStateOf(0) }
    var tapTimer by remember { mutableIntStateOf(10) }
    var isTapGameActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTapGameActive) {
        while (isTapGameActive && tapTimer > 0) {
            delay(1000)
            tapTimer--
            if (tapTimer == 0) {
                isTapGameActive = false
                viewModel.submitGameResult(GameResult("MINI_GAME", score = tapCount * 3, isWin = tapCount > 30))
            }
        }
    }

    // Reaction Time State
    // States: IDLE, WAITING (Red), READY (Green), FINISHED
    var reactionState by remember { mutableStateOf("IDLE") }
    var reactionStartTime by remember { mutableLongStateOf(0L) }
    var reactionResultMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(reactionState) {
        if (reactionState == "WAITING") {
            val waitMs = Random.nextLong(1500, 4000)
            delay(waitMs)
            if (reactionState == "WAITING") {
                reactionState = "READY"
                reactionStartTime = System.currentTimeMillis()
                viewModel.effectsHelper.vibrateTap()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "মিনি গেম সেন্টার (দ্রুত চ্যালেঞ্জ)" else "Mini Game Center",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("minigames_back_btn")) {
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
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurfaceCard,
                contentColor = CyberGold
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text(if (language == AppLanguage.BANGLA) "দ্রুত ট্যাপ চ্যালেঞ্জ" else "Tap Sprint", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text(if (language == AppLanguage.BANGLA) "রিফ্লেক্স রিঅ্যাকশন" else "Reflex Test", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (activeTab == 0) {
                // TAP SPRINT
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "১০ সেকেন্ডে যত বেশি সম্ভব ট্যাপ করুন!" else "Tap as fast as possible in 10s!",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "⏱️ $tapTimer সেকেন্ড", fontSize = 28.sp, fontWeight = FontWeight.Black, color = CyberGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "মোট ট্যাপ: $tapCount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)

                    Spacer(modifier = Modifier.height(30.dp))

                    if (!isTapGameActive && tapTimer == 10) {
                        Button(
                            onClick = {
                                tapCount = 0
                                tapTimer = 10
                                isTapGameActive = true
                            },
                            modifier = Modifier.size(160.dp).testTag("start_tap_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground)
                        ) {
                            Text(text = if (language == AppLanguage.BANGLA) "শুরু করুন\n(START)" else "START\nSPRINT", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    } else if (isTapGameActive) {
                        Button(
                            onClick = {
                                tapCount++
                                viewModel.effectsHelper.vibrateTap()
                            },
                            modifier = Modifier.size(180.dp).testTag("rapid_tap_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun, contentColor = Color.White)
                        ) {
                            Text(text = "TAP! 🔥", fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        // Game Ended
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceCard,
                            border = BorderStroke(1.5.dp, CyberGold),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "ফলাফল: $tapCount ট্যাপ!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = CyberGold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        tapCount = 0
                                        tapTimer = 10
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground)
                                ) {
                                    Text(if (language == AppLanguage.BANGLA) "আবার চেষ্টা করুন" else "Try Again", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // REACTION TEST
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (reactionState) {
                        "IDLE" -> {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "পর্দা লাল থেকে সবুজ হওয়ার সাথে সাথেই ট্যাপ করুন!" else "Tap as soon as the screen turns GREEN!",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { reactionState = "WAITING" },
                                modifier = Modifier.height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DarkBackground),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (language == AppLanguage.BANGLA) "পরীক্ষা শুরু করুন" else "Start Reflex Test", fontWeight = FontWeight.Bold)
                            }
                        }
                        "WAITING" -> {
                            Surface(
                                onClick = {
                                    // Too early!
                                    reactionState = "IDLE"
                                    viewModel.effectsHelper.vibrateCrash()
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = DangerRed,
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "অপেক্ষা করুন...\n(সবুজ হলে ট্যাপ করবেন)" else "WAIT FOR GREEN...",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        "READY" -> {
                            Surface(
                                onClick = {
                                    reactionResultMs = System.currentTimeMillis() - reactionStartTime
                                    reactionState = "FINISHED"
                                    viewModel.submitGameResult(GameResult("MINI_GAME", score = (1000 - reactionResultMs).toInt().coerceAtLeast(30), isWin = reactionResultMs < 350))
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = NeonEmerald,
                                modifier = Modifier.fillMaxSize().padding(16.dp).testTag("reaction_green_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "TAP NOW! ⚡",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkBackground
                                    )
                                }
                            }
                        }
                        "FINISHED" -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurfaceCard,
                                border = BorderStroke(1.5.dp, CyberGold),
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "আপনার প্রতিক্রিয়ার সময়:", fontSize = 14.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "$reactionResultMs ms", fontSize = 38.sp, fontWeight = FontWeight.Black, color = CyberGold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (reactionResultMs < 250) "🏆 প্রো গেমার লেভেল!" else if (reactionResultMs < 350) "⚡ খুব ভালো রিফ্লেক্স!" else "👍 সাধারণ মানুষের গতি!",
                                        fontSize = 14.sp,
                                        color = NeonEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Button(
                                        onClick = { reactionState = "IDLE" },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground)
                                    ) {
                                        Text(if (language == AppLanguage.BANGLA) "পুনরায় পরীক্ষা দিন" else "Try Again", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
