package com.example

import com.example.core.rewards.RewardManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WafaVerseLogicTest {

    @Test
    fun testLevelCalculation() {
        assertEquals(1, RewardManager.calculateLevel(0))
        assertEquals(1, RewardManager.calculateLevel(149))
        assertEquals(2, RewardManager.calculateLevel(150))
        assertEquals(3, RewardManager.calculateLevel(300))
        assertEquals(5, RewardManager.calculateLevel(600))
    }

    @Test
    fun testXpToNextLevel() {
        assertEquals(150, RewardManager.xpToNextLevel(0))
        assertEquals(50, RewardManager.xpToNextLevel(100))
        assertEquals(150, RewardManager.xpToNextLevel(150))
    }

    @Test
    fun testCurrentLevelProgressFraction() {
        val fraction0 = RewardManager.currentLevelProgressFraction(0)
        assertEquals(0f, fraction0, 0.01f)

        val fractionHalf = RewardManager.currentLevelProgressFraction(75)
        assertEquals(0.5f, fractionHalf, 0.01f)

        val fractionLvl2 = RewardManager.currentLevelProgressFraction(225) // 75 xp into level 2
        assertEquals(0.5f, fractionLvl2, 0.01f)
    }

    @Test
    fun testShopProfitLogic() {
        val wholesaleCost = 280
        val retailPrice = 340
        val qtySold = 5
        val grossRevenue = retailPrice * qtySold
        val grossCost = wholesaleCost * qtySold
        val netProfit = grossRevenue - grossCost

        assertEquals(1700, grossRevenue)
        assertEquals(1400, grossCost)
        assertEquals(300, netProfit)
        assertTrue(netProfit > 0)
    }
}
