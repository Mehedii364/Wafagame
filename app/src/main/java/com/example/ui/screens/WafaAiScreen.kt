package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

data class ChatMessage(
    val sender: String, // "AI" or "USER"
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WafaAiScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage,
    onBack: () -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "AI",
                    if (language == AppLanguage.BANGLA)
                        "আসসালামু আলাইকুম! আমি ওয়াফা এআই গুরু। ওয়াফাভার্সের যে কোনো গেম স্ট্র্যাটেজি, শপ ম্যানেজমেন্ট বা রেসিং টিপস জানতে আমাকে প্রশ্ন করুন!"
                    else
                        "Welcome to Wafa AI Game Assistant! Ask me any strategy for Wafa Racing, Mudi Shop, Farming, or Puzzles!"
                )
            )
        )
    }

    val suggestedQuestions = listOf(
        "What should I upgrade?",
        "How do I improve my Racing score?",
        "Mudi Shop pricing strategy",
        "Wafa Farm best crops",
        "How to hit sixes in Cricket?"
    )

    fun answerQuery(query: String) {
        val reply = when {
            query.contains("racing", ignoreCase = true) || query.contains("রেস", ignoreCase = true) ->
                "ওয়াফা রেসিং টিপস: টার্বো সিএনজি অথবা সাইবার কার বেছে নিন। ফাঁকা হাইওয়েতে নাইট্রো বুস্ট অন করুন এবং ট্রাফিকের সংঘর্ষ এড়াতে লেইন পরিবর্তনের জন্য দ্রুত সোয়াইপ করুন।"

            query.contains("shop", ignoreCase = true) || query.contains("দোকান", ignoreCase = true) || query.contains("pricing", ignoreCase = true) ->
                "মুদি শপ স্ট্র্যাটেজি: পাইকারি বাজার থেকে চাল, ডাল ও তেল বেশি পরিমাণে কিনুন। প্রতি আইটেমে ২০-৩০% প্রফিট মার্জিন রাখুন যাতে ক্রেতারা দ্রুত কেনাকাটা করে এবং আপনার ক্যাপিটাল বৃদ্ধি পায়।"

            query.contains("farm", ignoreCase = true) || query.contains("খামার", ignoreCase = true) || query.contains("crop", ignoreCase = true) ->
                "ফার্মিং গাইড: শুরুতে আমন ধান ও সরিষা দিয়ে দ্রুত কয়েন সংগ্রহ করুন। পরবর্তীতে সিলেটের চা পাতা ও রাজশাহীর আম রোপণ করলে প্রতি হার্ভেস্টে সর্বোচ্চ ১৫০ কয়েন পর্যন্ত আয় হবে!"

            query.contains("cricket", ignoreCase = true) || query.contains("ক্রিকেট", ignoreCase = true) || query.contains("six", ignoreCase = true) ->
                "ক্রিকেট টাইমিং টিপস: বল যখন পিচে ড্রপ খেয়ে ব্যাটারের ঠিক কাছাকাছি আসবে (৭০-৮৫% দুরত্ব), তখন 'LOFT' বা 'DRIVE' বাটন চাপুন। পারফেক্ট টাইমিংয়ে সরাসরি ওভার বাউন্ডারি (ছক্কা) হবে!"

            query.contains("upgrade", ignoreCase = true) || query.contains("উন্নতি", ignoreCase = true) ->
                "আপগ্রেড পরামর্শ: প্রথমে মুদি দোকানের ক্যাপাসিটি আপগ্রেড করুন যাতে পাইকারি স্টক বেশি রাখা যায়, এরপর সিটি বিল্ডারে স্কুল ও স্বাস্থ্য কমপ্লেক্স নির্মাণ করে শহরের জনসংখ্যা ও সুখ বৃদ্ধি করুন।"

            else ->
                "ওয়াফাভার্সে সর্বোচ্চ স্কোর অর্জনের জন্য প্রতিদিনের ৩টি ডেইলি মিশন সম্পূর্ণ করুন। এতে দ্রুত লেভেল আপ হবে এবং চ্যাম্পিয়নশিপ লিগে সিলভার থেকে ডায়মন্ডে প্রমোশন পাবেন!"
        }

        chatMessages = chatMessages + ChatMessage("USER", query) + ChatMessage("AI", reply)
        viewModel.effectsHelper.vibrateTap()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = "AI", tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.BANGLA) "ওয়াফা এআই গুরু" else "Wafa AI Guide",
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ai_back_btn")) {
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
            // Chat message list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages) { msg ->
                    val isUser = msg.sender == "USER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isUser) BengalGreen else DarkSurfaceCard,
                            border = BorderStroke(1.dp, if (isUser) NeonEmerald else DarkBorder),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggested Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestedQuestions) { q ->
                    Surface(
                        onClick = { answerQuery(q) },
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text(
                            text = q,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            color = CyberGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prompt Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { Text(if (language == AppLanguage.BANGLA) "এআই-কে জিজ্ঞাসা করুন..." else "Ask Wafa AI...", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("ai_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            val q = promptInput
                            promptInput = ""
                            answerQuery(q)
                        }
                    },
                    modifier = Modifier.size(48.dp).testTag("ai_send_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NeonCyan)
                }
            }
        }
    }
}
