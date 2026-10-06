# WafaVerse: Bangladesh Ultimate Adventure 🇧🇩🎮

**Developed by:** Md. Mehedi Hasan  
**Brand:** Mehedi364  
**GitHub Repository:** [Mehedii364/WafaVerse](https://github.com/Mehedii364/WafaVerse)  
**Package:** `com.mehedi364.wafaverse`  

---

## 🌟 Overview

**WafaVerse** is a production-quality, offline-first native Android game universe and game hub built completely with **Kotlin** and **Jetpack Compose (Material 3)**.

Designed with an authentic Bangladeshi theme, it combines high-octane racing across iconic landmarks, tactical cricket batting, fast-paced 5v5 soccer, local grocery business simulation, agriculture farming, city management, and brain training puzzles into a unified championship ecosystem.

---

## 🎮 Playable Game Modes

| Game Mode | Highlights | Controls / Mechanics |
| :--- | :--- | :--- |
| **🏎️ Wafa Racing** | Dhaka Flyover, Padma Bridge, Village Road, Sylhet Highway, Cox's Marine Drive. | Interactive steering D-pad & drag gestures, speed gauge, nitro boost, oncoming traffic avoidance, collectibles, damage meter. |
| **🏏 Wafa Cricket** | Super Over chase (Bangladesh vs Opponent), 2 overs, 3 wickets. | Delivery timing meter, Loft (6), Drive (4), Defend (1), real scoreboard with overs & commentary. |
| **⚽ Wafa Football** | Dhaka Strikers vs Chattogram Kings 5v5 match. | Virtual D-Pad dribbling, Shoot button with power launch, Pass button, goalkeeper AI, countdown clock. |
| **🧩 Puzzle Center** | Word Jumble, Memory Match, Quick Math. | Scrambled Bangladesh landmark words (Sundarban, Padma, etc.), 4x4 card pair matching, 10s speed arithmetic. |
| **🏪 Wafa Mudi Shop** | "মেসার্স ওয়াফা স্টোর" Grocery Simulation. | Karwan Bazar wholesale buying, customer counter serving with speech bubbles, inventory tracking, profit calculations, shop tier upgrades. |
| **🌾 Wafa Farm** | Sonar Bangla Agricultural Farming. | 6 expandable plots, native crops (Aman Rice, Mustard, Jute, Potato, Tea, Mango), growth timers, harvesting & Krishi bazaar selling. |
| **🏙️ Wafa City Builder** | Mega metropolis strategy simulator. | 8 construction plots: Residential, Shops, Primary School, Hospital, Solar Power, Agro Farm. Population, happiness index & tax revenue loop. |
| **⚡ Mini Games** | Reflex & agility testing. | 10-second rapid tap sprint & millisecond reflex reaction test (Red to Green screen). |
| **📚 Knowledge Center** | Educational quiz. | Bangladesh History, Heritage & Programming (Kotlin, Jetpack Compose, Declarative UI) with explanations. |
| **🧭 Wafa Adventure** | Top-down story quest. | Sadarghat riverfront & tea garden tile exploration, NPC dialogues (Karim Majhi, Runa Apup), artifact gathering. |
| **🤖 Wafa AI Guide** | Intelligent Game Strategist. | Offline responsive game strategy advisor with tips on racing, shop pricing, farming rotations, and missions. |

---

## 🏗️ Architecture & Technology Stack

- **Language:** 100% Modern Kotlin
- **UI Framework:** Jetpack Compose (Material 3, Edge-to-Edge, dark gaming palette)
- **Local Database:** Room Database with KSP (`WafaVerseDatabase`)
  - Tables: `PlayerEntity`, `MissionEntity`, `AchievementEntity`, `GameProgressEntity`, `ShopInventoryEntity`, `FarmPlotEntity`, `CityBuildingEntity`
- **State Management:** Android ViewModel, Kotlin Coroutines, and reactive `StateFlow`
- **Settings:** Jetpack DataStore Preferences (Bangla / English, Sound, Haptics, 60fps Performance)
- **Centralized Rewards:** `RewardManager` handles all XP, Leveling ($Level = 1 + XP / 150$), Coins, and Mission triggers without duplicating logic.

---

## 🛠️ Build & Installation

### Local Build:
```bash
# Run unit tests
./gradlew test

# Assemble real debug APK
./gradlew assembleDebug
```

Output APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🚀 GitHub Actions Automated Pipeline

The automated workflow located at `.github/workflows/android-build.yml`:
1. Checks out code and configures JDK 17 & Android SDK.
2. Runs unit test suite (`./gradlew test`).
3. Executes real Gradle build (`./gradlew assembleDebug`).
4. Verifies the APK existence, validates with `unzip -t`, and confirms non-zero byte size (>1MB).
5. Copies genuine APK to `.build-outputs/app-debug.apk` and `APK_DOWNLOAD/app-debug.apk`.
6. Checks SHA-256 hash consistency between both copies.
7. Publishes GitHub Action Artifact `app-debug-apk`.

---

## 🛡️ License & Credits

Created and developed by **Md. Mehedi Hasan** ([Mehedi364](https://github.com/Mehedii364)).  
All assets and fictional game systems are original and offline-first.
