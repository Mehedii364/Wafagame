package com.example.games.puzzle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class WordPuzzleItem(
    val word: String,
    val hintBn: String,
    val hintEn: String
)

data class MemoryCard(
    val id: Int,
    val symbol: String,
    var isRevealed: Boolean = false,
    var isMatched: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaPuzzleScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Word Puzzle, 1 = Memory Match, 2 = Quick Math

    val wordPuzzles = remember {
        listOf(
            WordPuzzleItem("SUNDARBAN", "বিশ্বের বৃহত্তম ম্যানগ্রোভ বন", "Largest mangrove forest in the world"),
            WordPuzzleItem("PADMA", "বাংলাদেশের গর্ব ও দীর্ঘতম নদী সেতু", "Longest river & pride bridge of BD"),
            WordPuzzleItem("SHAPLA", "বাংলাদেশের জাতীয় ফুল", "National flower of Bangladesh"),
            WordPuzzleItem("SAJEK", "মেঘের রাজ্য পাহাড়ি উপত্যকা", "Valley above clouds in Chattogram"),
            WordPuzzleItem("ILISH", "বাংলাদেশের জাতীয় মাছ", "National fish of Bangladesh"),
            WordPuzzleItem("COXBAZAR", "বিশ্বের দীর্ঘতম প্রাকৃতিক সমুদ্র সৈকত", "Longest natural sea beach")
        )
    }

    var currentWordIndex by remember { mutableIntStateOf(0) }
    val currentWordItem = wordPuzzles[currentWordIndex]

    // Scrambled letters & User Input
    var scrambledLetters by remember(currentWordIndex) {
        mutableStateOf(currentWordItem.word.toList().shuffled())
    }
    var userLetters by remember(currentWordIndex) {
        mutableStateOf(mutableListOf<Char>())
    }
    var wordSolved by remember(currentWordIndex) {
        mutableStateOf(false)
    }

    // Memory Match Game State
    val baseEmojis = remember { listOf("🐅", "🪷", "🏏", "🛺", "☕", "🐟", "🥭", "⛵") }
    var memoryCards by remember {
        val pairs = (baseEmojis + baseEmojis).shuffled()
        mutableStateOf(pairs.mapIndexed { index, emoji -> MemoryCard(index, emoji) })
    }
    var revealedIndices by remember { mutableStateOf(listOf<Int>()) }
    var matchedPairsCount by remember { mutableIntStateOf(0) }

    // Quick Math State
    var num1 by remember { mutableIntStateOf(12) }
    var num2 by remember { mutableIntStateOf(8) }
    var mathScore by remember { mutableIntStateOf(0) }
    var mathTimer by remember { mutableIntStateOf(10) }
    var isMathActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "বুদ্ধির আড্ডা (পাজল সেন্টার)" else "Puzzle Center",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("puzzle_back_btn")) {
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
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceCard,
                contentColor = NeonEmerald
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (language == AppLanguage.BANGLA) "শব্দ সাজাও" else "Word Jumble", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (language == AppLanguage.BANGLA) "মেমোরি কার্ড" else "Memory Cards", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (language == AppLanguage.BANGLA) "দ্রুত গণিত" else "Quick Math", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // TAB 1: BANGLA / BD WORD PUZZLE
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurfaceCard,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = "Hint", tint = CyberGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "HINT / সূত্র:", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.BANGLA) currentWordItem.hintBn else currentWordItem.hintEn,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Selected Letters Slots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            currentWordItem.word.indices.forEach { idx ->
                                val char = userLetters.getOrNull(idx)
                                Surface(
                                    modifier = Modifier.size(42.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (char != null) DarkSurfaceVariant else DarkSurface,
                                    border = BorderStroke(1.5.dp, if (wordSolved) NeonEmerald else CyberGold)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = char?.toString() ?: "_",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (wordSolved) NeonEmerald else TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(text = if (language == AppLanguage.BANGLA) "অক্ষর নির্বাচন করুন:" else "Tap Letters to Form Word:", color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Letter Tiles Bank
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            scrambledLetters.forEachIndexed { i, ch ->
                                Button(
                                    onClick = {
                                        if (userLetters.size < currentWordItem.word.length && !wordSolved) {
                                            userLetters = (userLetters + ch).toMutableList()
                                            viewModel.effectsHelper.vibrateTap()
                                            if (userLetters.joinToString("") == currentWordItem.word) {
                                                wordSolved = true
                                                viewModel.submitGameResult(GameResult("PUZZLE", score = 100, isWin = true))
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(44.dp).testTag("letter_tile_$i"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceCard, contentColor = CyberGold),
                                    contentPadding = PaddingValues(0.dp),
                                    border = BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
                                ) {
                                    Text(text = "$ch", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Controls: Clear / Next Word
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { userLetters = mutableListOf() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Clear")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (language == AppLanguage.BANGLA) "মুছুন" else "Clear")
                            }

                            if (wordSolved) {
                                Button(
                                    onClick = {
                                        currentWordIndex = (currentWordIndex + 1) % wordPuzzles.size
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Next")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (language == AppLanguage.BANGLA) "পরবর্তী শব্দ" else "Next Word", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 2: MEMORY MATCH CARDS (4x4)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) "মিলে যাওয়া জোড়া খুঁজে বের করুন (${matchedPairsCount}/8)" else "Find All Pairs (${matchedPairsCount}/8)",
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(memoryCards) { index, card ->
                                val isVisible = card.isRevealed || card.isMatched
                                Surface(
                                    onClick = {
                                        if (!card.isMatched && !card.isRevealed && revealedIndices.size < 2) {
                                            card.isRevealed = true
                                            revealedIndices = revealedIndices + index
                                            viewModel.effectsHelper.vibrateTap()

                                            if (revealedIndices.size == 2) {
                                                val first = memoryCards[revealedIndices[0]]
                                                val second = memoryCards[revealedIndices[1]]
                                                if (first.symbol == second.symbol) {
                                                    first.isMatched = true
                                                    second.isMatched = true
                                                    matchedPairsCount++
                                                    revealedIndices = emptyList()
                                                    viewModel.effectsHelper.vibrateSuccess()

                                                    if (matchedPairsCount == 8) {
                                                        viewModel.submitGameResult(GameResult("PUZZLE", score = 150, isWin = true))
                                                    }
                                                } else {
                                                    // mismatch: hide cards after delay
                                                }
                                            }
                                        } else if (revealedIndices.size == 2) {
                                            // reset revealed
                                            memoryCards.forEach { if (!it.isMatched) it.isRevealed = false }
                                            revealedIndices = emptyList()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isVisible) DarkSurfaceVariant else DarkSurfaceCard,
                                    border = BorderStroke(1.5.dp, if (card.isMatched) NeonEmerald else DarkBorder),
                                    modifier = Modifier.aspectRatio(1f).testTag("mem_card_$index")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (isVisible) card.symbol else "❓",
                                            fontSize = 26.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (matchedPairsCount == 8) {
                            Button(
                                onClick = {
                                    val pairs = (baseEmojis + baseEmojis).shuffled()
                                    memoryCards = pairs.mapIndexed { idx, emo -> MemoryCard(idx, emo) }
                                    revealedIndices = emptyList()
                                    matchedPairsCount = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGold, contentColor = DarkBackground)
                            ) {
                                Text(if (language == AppLanguage.BANGLA) "পুনরায় শুরু করুন" else "Play Again", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 3: QUICK MATH SPEED RUN
                    LaunchedEffect(isMathActive) {
                        while (isMathActive && mathTimer > 0) {
                            delay(1000)
                            mathTimer--
                            if (mathTimer == 0) {
                                isMathActive = false
                                viewModel.submitGameResult(GameResult("PUZZLE", score = mathScore * 20, isWin = mathScore > 3))
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!isMathActive) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "১০ সেকেন্ডে কতটি সঠিক উত্তর দিতে পারেন?" else "How many equations can you solve in 10s?",
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    mathScore = 0
                                    mathTimer = 10
                                    num1 = (5..25).random()
                                    num2 = (3..20).random()
                                    isMathActive = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (language == AppLanguage.BANGLA) "শুরু করুন (START)" else "START SPRINT", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(text = "⏱️ $mathTimer s", fontSize = 28.sp, fontWeight = FontWeight.Black, color = CyberGold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Score: $mathScore", fontSize = 18.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(20.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurfaceCard,
                                border = BorderStroke(1.5.dp, CyberGold)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "$num1 + $num2 = ?", fontSize = 34.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val correctAnswer = num1 + num2
                            val wrongAnswer1 = correctAnswer + listOf(-2, 3, 5, -4).random()
                            val wrongAnswer2 = correctAnswer + listOf(1, -3, 2, -1).random()
                            val options = remember(num1, num2) {
                                listOf(correctAnswer, wrongAnswer1, wrongAnswer2).shuffled()
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                options.forEach { opt ->
                                    Button(
                                        onClick = {
                                            if (opt == correctAnswer) {
                                                mathScore++
                                                viewModel.effectsHelper.vibrateSuccess()
                                            } else {
                                                viewModel.effectsHelper.vibrateCrash()
                                            }
                                            num1 = (5..30).random()
                                            num2 = (3..25).random()
                                        },
                                        modifier = Modifier.weight(1f).height(54.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = CyberGold),
                                        border = BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
                                    ) {
                                        Text(text = "$opt", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
