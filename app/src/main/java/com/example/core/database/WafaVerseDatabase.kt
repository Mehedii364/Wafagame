package com.example.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.core.database.dao.*
import com.example.core.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerEntity::class,
        MissionEntity::class,
        AchievementEntity::class,
        GameProgressEntity::class,
        ShopInventoryEntity::class,
        FarmPlotEntity::class,
        CityBuildingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WafaVerseDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun missionDao(): MissionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun gameProgressDao(): GameProgressDao
    abstract fun shopDao(): ShopDao
    abstract fun farmDao(): FarmDao
    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var INSTANCE: WafaVerseDatabase? = null

        fun getInstance(context: Context): WafaVerseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WafaVerseDatabase::class.java,
                    "wafaverse_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: WafaVerseDatabase) {
            // Player
            db.playerDao().insertPlayer(
                PlayerEntity(
                    id = 1,
                    name = "বাংলার মুসাফির",
                    coins = 1000,
                    level = 1,
                    xp = 50,
                    championshipScore = 200
                )
            )

            // Initial Missions
            val initialMissions = listOf(
                MissionEntity(
                    id = "m_race_1",
                    title = "Dhaka Speedster",
                    titleBn = "ঢাকার গতির রাজা",
                    description = "Complete 2 races on any track",
                    descriptionBn = "যেকোনো ট্র্যাকে ২টি রেস সম্পন্ন করুন",
                    targetCount = 2,
                    currentCount = 0,
                    rewardXp = 100,
                    rewardCoins = 250,
                    category = "DAILY"
                ),
                MissionEntity(
                    id = "m_football_1",
                    title = "Bangla Striker",
                    titleBn = "বাংলার সেরা স্ট্রাইকার",
                    description = "Score 2 goals in Wafa Football",
                    descriptionBn = "ওয়াফা ফুটবলে ২টি গোল করুন",
                    targetCount = 2,
                    currentCount = 0,
                    rewardXp = 120,
                    rewardCoins = 300,
                    category = "DAILY"
                ),
                MissionEntity(
                    id = "m_cricket_1",
                    title = "Master Blaster",
                    titleBn = "মাস্টার ব্লাস্টার",
                    description = "Score 20 runs in a Cricket match",
                    descriptionBn = "ক্রিকেট ম্যাচে ২০ রান সংগ্রহ করুন",
                    targetCount = 20,
                    currentCount = 0,
                    rewardXp = 150,
                    rewardCoins = 350,
                    category = "DAILY"
                ),
                MissionEntity(
                    id = "m_shop_1",
                    title = "Dokan Master",
                    titleBn = "দোকানের বেচাকেনা",
                    description = "Serve 5 customers in Wafa Mudi Shop",
                    descriptionBn = "মুদি দোকানে ৫ জন ক্রেতাকে পণ্য দিন",
                    targetCount = 5,
                    currentCount = 0,
                    rewardXp = 80,
                    rewardCoins = 200,
                    category = "DAILY"
                ),
                MissionEntity(
                    id = "m_puzzle_1",
                    title = "Mind Explorer",
                    titleBn = "বুদ্ধির লড়াই",
                    description = "Solve 2 puzzles in Puzzle Center",
                    descriptionBn = "পাজল সেন্টারে ২টি ধাঁধা সমাধান করুন",
                    targetCount = 2,
                    currentCount = 0,
                    rewardXp = 100,
                    rewardCoins = 250,
                    category = "DAILY"
                ),
                MissionEntity(
                    id = "m_farm_1",
                    title = "Sonar Bangla Farmer",
                    titleBn = "সোনার বাংলার কৃষক",
                    description = "Harvest 3 crops in Wafa Farm",
                    descriptionBn = "ওয়াফা খামারে ৩টি ফসল ঘরে তুলুন",
                    targetCount = 3,
                    currentCount = 0,
                    rewardXp = 100,
                    rewardCoins = 300,
                    category = "WEEKLY"
                )
            )
            db.missionDao().insertMissions(initialMissions)

            // Initial Achievements
            val initialAchievements = listOf(
                AchievementEntity(
                    id = "ach_first_race",
                    title = "Padma Express",
                    titleBn = "পদ্মা এক্সপ্রেস",
                    description = "Finish your very first race",
                    descriptionBn = "প্রথম রেস সফলভাবে শেষ করুন",
                    iconName = "sports_motorsports",
                    targetValue = 1,
                    rewardCoins = 500
                ),
                AchievementEntity(
                    id = "ach_goal",
                    title = "Goal Machine",
                    titleBn = "গোল মেশিন",
                    description = "Score your first goal in football",
                    descriptionBn = "ফুটবলে আপনার প্রথম গোল করুন",
                    iconName = "sports_soccer",
                    targetValue = 1,
                    rewardCoins = 400
                ),
                AchievementEntity(
                    id = "ach_cricket_six",
                    title = "Over the Padma",
                    titleBn = "নদীর ওপারে ছক্কা",
                    description = "Hit 5 sixes in cricket matches",
                    descriptionBn = "ক্রিকেটে মোট ৫টি ছক্কা হাঁকান",
                    iconName = "sports_cricket",
                    targetValue = 5,
                    rewardCoins = 600
                ),
                AchievementEntity(
                    id = "ach_shop_master",
                    title = "Bap Bap Dokandar",
                    titleBn = "তুখোড় ব্যবসায়ী",
                    description = "Earn 1000 virtual coins from grocery sales",
                    descriptionBn = "মুদি দোকান থেকে ১০০০ কয়েন আয় করুন",
                    iconName = "storefront",
                    targetValue = 1000,
                    rewardCoins = 800
                ),
                AchievementEntity(
                    id = "ach_farm_harvest",
                    title = "Golden Grain",
                    titleBn = "সোনার ফসল",
                    description = "Harvest 10 crops in Wafa Farm",
                    descriptionBn = "খামার থেকে ১০টি ফসল সংগ্রহ করুন",
                    iconName = "agriculture",
                    targetValue = 10,
                    rewardCoins = 500
                ),
                AchievementEntity(
                    id = "ach_puzzle_solver",
                    title = "Brain Champion",
                    titleBn = "মেধার শিরোমণি",
                    description = "Solve 5 Bangla or Memory puzzles",
                    descriptionBn = "৫টি পাজল সফলভাবে সম্পূর্ণ করুন",
                    iconName = "psychology",
                    targetValue = 5,
                    rewardCoins = 700
                )
            )
            db.achievementDao().insertAchievements(initialAchievements)

            // Initial Grocery Products
            val initialShop = listOf(
                ShopInventoryEntity("p_rice", "Miniket Rice (5kg)", "মিনিকেট চাল (৫কেজি)", stock = 15, costPrice = 280, salePrice = 340),
                ShopInventoryEntity("p_dal", "Masoor Dal (1kg)", "মসুর ডাল (১কেজি)", stock = 20, costPrice = 110, salePrice = 135),
                ShopInventoryEntity("p_oil", "Soyabean Oil (2L)", "সয়াবিন তেল (২লিটার)", stock = 12, costPrice = 310, salePrice = 360),
                ShopInventoryEntity("p_salt", "Iodized Salt (1kg)", "আয়োডিনযুক্ত লবণ", stock = 30, costPrice = 35, salePrice = 45),
                ShopInventoryEntity("p_sugar", "Deshi Sugar (1kg)", "দেশি চিনি (১কেজি)", stock = 25, costPrice = 120, salePrice = 140),
                ShopInventoryEntity("p_tea", "Sylheti Black Tea (400g)", "সিলেটি চা পাতা", stock = 18, costPrice = 180, salePrice = 220),
                ShopInventoryEntity("p_biscuit", "Toast Biscuits Pack", "টোস্ট বিস্কুট প্যাকেট", stock = 35, costPrice = 40, salePrice = 55),
                ShopInventoryEntity("p_drink", "Mojo Cola (500ml)", "মোজো কোলা (৫০০মি.লি.)", stock = 24, costPrice = 25, salePrice = 35),
                ShopInventoryEntity("p_soap", "Neem Bath Soap", "নিম সাবান", stock = 20, costPrice = 50, salePrice = 65)
            )
            db.shopDao().initInventory(initialShop)

            // Initial Farm Plots
            val plots = (1..6).map { id ->
                FarmPlotEntity(
                    plotId = id,
                    cropType = if (id == 1) "RICE" else if (id == 2) "MUSTARD" else "NONE",
                    plantedTime = if (id <= 2) System.currentTimeMillis() - 15000L else 0L,
                    growthDurationSec = 20,
                    isWatered = id <= 2,
                    isReadyToHarvest = false
                )
            }
            db.farmDao().initPlots(plots)

            // Initial City Buildings
            val buildings = listOf(
                CityBuildingEntity("b_townhall", "TOWNHALL", level = 1, slotIndex = 0),
                CityBuildingEntity("b_house1", "HOUSE", level = 1, slotIndex = 1),
                CityBuildingEntity("b_farm1", "FARM", level = 1, slotIndex = 2),
                CityBuildingEntity("b_shop1", "SHOP", level = 1, slotIndex = 3)
            )
            buildings.forEach { db.cityDao().insertBuilding(it) }
        }
    }
}
