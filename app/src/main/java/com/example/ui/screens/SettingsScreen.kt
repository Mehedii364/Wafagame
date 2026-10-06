package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.settings.AppLanguage
import com.example.ui.WafaMainViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: WafaMainViewModel,
    language: AppLanguage
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (language == AppLanguage.BANGLA) "সেটিংস ও কনফিগারেশন" else "Settings & Preferences",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CyberGold
        )

        // LANGUAGE TOGGLE
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceCard,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = NeonEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ভাষা নির্বাচন (Language)" else "App Language",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.setLanguage(AppLanguage.BANGLA) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.language == AppLanguage.BANGLA) NeonEmerald else DarkSurfaceVariant,
                            contentColor = if (settings.language == AppLanguage.BANGLA) DarkBackground else TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("বাংলা (Bangla)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.language == AppLanguage.ENGLISH) NeonEmerald else DarkSurfaceVariant,
                            contentColor = if (settings.language == AppLanguage.ENGLISH) DarkBackground else TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("English", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AUDIO & FEEDBACK TOGGLES
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceCard,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (language == AppLanguage.BANGLA) "সাউন্ড ও হ্যাপটিক" else "Audio & Haptics",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingSwitchRow(
                    title = if (language == AppLanguage.BANGLA) "শব্দ প্রভাব (Sound Effects)" else "Sound Effects",
                    checked = settings.soundEnabled,
                    onCheckedChange = { viewModel.toggleSound(it) }
                )

                SettingSwitchRow(
                    title = if (language == AppLanguage.BANGLA) "হ্যাপটিক কম্পন (Haptics)" else "Haptic Vibration",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { viewModel.toggleHaptics(it) }
                )

                SettingSwitchRow(
                    title = if (language == AppLanguage.BANGLA) "হাই পারফরম্যান্স মোড (60fps)" else "High Performance (60fps)",
                    checked = settings.highPerformance,
                    onCheckedChange = { viewModel.togglePerformance(it) }
                )
            }
        }

        // RESET DATA BUTTON
        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = DangerRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset")
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (language == AppLanguage.BANGLA) "লোকাল ডাটা রিসেট করুন" else "Reset Local Game Data", fontWeight = FontWeight.Bold)
        }

        // DEVELOPER ABOUT CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceCard,
            border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "About", tint = CyberGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "WafaVerse Platform", fontWeight = FontWeight.Black, color = CyberGold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Subtitle: Bangladesh Ultimate Adventure", fontSize = 12.sp, color = TextPrimary)
                Text(text = "Developer: Md. Mehedi Hasan", fontSize = 12.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                Text(text = "Brand: Mehedi364", fontSize = 12.sp, color = TextSecondary)
                Text(text = "GitHub: Mehedii364/WafaVerse", fontSize = 12.sp, color = ElectricBlue)
                Text(text = "Package: com.mehedi364.wafaverse", fontSize = 11.sp, color = TextMuted)
                Text(text = "Offline-First Native Android Game Platform", fontSize = 11.sp, color = TextMuted)
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(if (language == AppLanguage.BANGLA) "ডাটা রিসেট নিশ্চিতকরণ" else "Confirm Reset", color = DangerRed) },
            text = {
                Text(
                    text = if (language == AppLanguage.BANGLA) "আপনি কি নিশ্চিত যে আপনার সমস্ত খেলার অগ্রগতি রিসেট করতে চান?"
                    else "Are you sure you want to reset your local player progress?",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(if (language == AppLanguage.BANGLA) "হ্যাঁ, রিসেট করুন" else "Yes, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(if (language == AppLanguage.BANGLA) "বাতিল" else "Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = TextPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonEmerald,
                checkedTrackColor = BengalGreen
            )
        )
    }
}
