package com.example.core.localization

import com.example.core.settings.AppLanguage

object WafaStrings {
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফাভার্স" else "WafaVerse"
    fun appSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "বাংলাদেশ আল্টিমেট অ্যাডভেঞ্চার" else "Bangladesh Ultimate Adventure"

    fun home(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "হোম" else "Home"
    fun games(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "গেমস" else "Games"
    fun missions(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "মিশন" else "Missions"
    fun profile(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "প্রোফাইল" else "Profile"
    fun settings(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "সেটিংস" else "Settings"

    fun level(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "লেভেল" else "Level"
    fun xp(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "এক্সপি" else "XP"
    fun coins(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "কয়েন" else "Coins"
    fun championship(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "চ্যাম্পিয়নশিপ" else "Championship"

    fun continuePlaying(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "খেলা চালিয়ে যান" else "Continue Playing"
    fun featuredGames(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "সেরা গেমসমূহ" else "Featured Games"
    fun dailyChallenge(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "দৈনিক চ্যালেঞ্জ" else "Daily Challenge"
    fun gameCategories(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ক্যাটেগরি" else "Categories"
    fun wafaAi(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা এআই গুরু" else "Wafa AI Guide"

    fun playNow(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "খেলুন" else "Play Now"
    fun claimReward(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পুরস্কার নিন" else "Claim Reward"
    fun completed(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "সম্পন্ন" else "Completed"
    fun inProgress(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "চলমান" else "In Progress"

    fun racingTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা রেসিং" else "Wafa Racing"
    fun footballTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা ফুটবল" else "Wafa Football"
    fun cricketTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা ক্রিকেট" else "Wafa Cricket"
    fun puzzleTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পাজল সেন্টার" else "Puzzle Center"
    fun shopTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "মুদি শপ সিমুলেটর" else "Wafa Mudi Shop"
    fun farmTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা ফার্ম" else "Wafa Farm"
    fun cityTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা সিটি বিল্ডার" else "Wafa City"
    fun adventureTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওয়াফা অ্যাডভেঞ্চার" else "Wafa Adventure"
    fun miniGamesTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "মিনি গেম সেন্টার" else "Mini Games"
    fun quizTitle(lang: AppLanguage) = if (lang.isBn()) "জ্ঞান জিজ্ঞাসা (কুইজ)" else "Knowledge Center"

    private fun AppLanguage.isBn() = this == AppLanguage.BANGLA
}
