package com.example.games.education

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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

data class QuizQuestion(
    val questionBn: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaQuizScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    val questions = remember {
        listOf(
            QuizQuestion(
                questionBn = "পদ্মা সেতুর মোট দৈর্ঘ্য কত কিলোমিটার?",
                options = listOf("৬.১৫ কিমি", "৫.৮০ কিমি", "৭.২০ কিমি", "৬.৫০ কিমি"),
                correctIndex = 0,
                explanation = "পদ্মা সেতুর মোট দৈর্ঘ্য ৬.১৫ কিলোমিটার (৪২টি পিলার ও ৪১টি স্প্যান)।"
            ),
            QuizQuestion(
                questionBn = "কোটলিন (Kotlin) প্রোগ্রামিং ভাষায় ডিফল্ট ভেরিয়েবল কি ধরনের?",
                options = listOf("Mutable (var)", "Immutable (val)", "Dynamic", "Static"),
                correctIndex = 1,
                explanation = "কোটলিনে 'val' দিয়ে তৈরি ভেরিয়েবল অপরিবর্তনীয় (immutable)।"
            ),
            QuizQuestion(
                questionBn = "বাংলাদেশের সংবিধান প্রণয়ন কমিটির প্রধান কে ছিলেন?",
                options = listOf("ড. কামাল হোসেন", "তাজউদ্দীন আহমদ", "সৈয়দ নজরুল ইসলাম", "মনসুর আলী"),
                correctIndex = 0,
                explanation = "১৯৭২ সালের গণপরিষদে সংবিধান খসড়া প্রণয়ন কমিটির সভাপতি ছিলেন ড. কামাল হোসেন।"
            ),
            QuizQuestion(
                questionBn = "অ্যান্ড্রয়েড জেটপ্যাক কম্পোজ (Jetpack Compose) কি ধরনের UI ফ্রেমওয়ার্ক?",
                options = listOf("Imperative XML", "Declarative UI", "Hybrid WebView", "ActionScript"),
                correctIndex = 1,
                explanation = "জেটপ্যাক কম্পোজ হলো আধুনিক ডিক্লেয়ারেটিভ (Declarative) ইউআই টুলকিট।"
            ),
            QuizQuestion(
                questionBn = "সুন্দরবনের কোন প্রাণীটি বিশ্বের কাছে বাংলাদেশের অন্যতম প্রধান প্রতীক?",
                options = listOf("চিত্রা হরিণ", "রয়্যাল বেঙ্গল টাইগার", "নোনা পানির কুমির", "গাঙ্গেয় ডলফিন"),
                correctIndex = 1,
                explanation = "সুন্দরবনের বিশ্বখ্যাত রয়েল বেঙ্গল টাইগার বাংলাদেশের প্রতীক।"
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentQ = questions[currentIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "জ্ঞান জিজ্ঞাসা (নলেজ সেন্টার)" else "Knowledge Center",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("quiz_back_btn")) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isQuizCompleted) {
                // Question Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "প্রশ্ন ${currentIndex + 1} / ${questions.size}", fontWeight = FontWeight.Bold, color = NeonEmerald, fontSize = 14.sp)
                    Text(text = "স্কোর: $score", fontWeight = FontWeight.Bold, color = CyberGold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.5.dp, DarkBorder)
                ) {
                    Text(
                        text = currentQ.questionBn,
                        modifier = Modifier.padding(18.dp),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentQ.options.forEachIndexed { idx, opt ->
                        val isChosen = selectedOption == idx
                        val isCorrect = idx == currentQ.correctIndex
                        val btnColor = if (isAnswerSubmitted) {
                            if (isCorrect) NeonEmerald else if (isChosen) DangerRed else DarkSurfaceVariant
                        } else {
                            if (isChosen) DarkSurfaceVariant else DarkSurfaceCard
                        }
                        val borderColor = if (isChosen) CyberGold else DarkBorder

                        Surface(
                            onClick = {
                                if (!isAnswerSubmitted) {
                                    selectedOption = idx
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = btnColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth().testTag("quiz_opt_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A'.code + idx).toChar()}.",
                                    fontWeight = FontWeight.Black,
                                    color = CyberGold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = opt, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explanation when submitted
                if (isAnswerSubmitted) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant
                    ) {
                        Text(
                            text = "ব্যাখ্যা: ${currentQ.explanation}",
                            modifier = Modifier.padding(12.dp),
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Next or Submit Button
                Button(
                    onClick = {
                        if (!isAnswerSubmitted && selectedOption != null) {
                            isAnswerSubmitted = true
                            if (selectedOption == currentQ.correctIndex) {
                                score++
                                viewModel.effectsHelper.vibrateSuccess()
                            } else {
                                viewModel.effectsHelper.vibrateCrash()
                            }
                        } else if (isAnswerSubmitted) {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                                selectedOption = null
                                isAnswerSubmitted = false
                            } else {
                                isQuizCompleted = true
                                viewModel.submitGameResult(GameResult("QUIZ", score = score * 20, isWin = score >= 3))
                            }
                        }
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("quiz_action_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (!isAnswerSubmitted) "উত্তর নিশ্চিত করুন" else if (currentIndex < questions.size - 1) "পরবর্তী প্রশ্ন" else "ফলাফল দেখুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            } else {
                // Completed Summary Card
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = DarkSurfaceCard,
                    border = BorderStroke(1.5.dp, CyberGold)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉 কুইজ সমাপ্ত!", fontSize = 24.sp, fontWeight = FontWeight.Black, color = CyberGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "আপনার স্কোর: $score / ${questions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                currentIndex = 0
                                selectedOption = null
                                score = 0
                                isAnswerSubmitted = false
                                isQuizCompleted = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = DarkBackground),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("আবার কুইজ দিন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
