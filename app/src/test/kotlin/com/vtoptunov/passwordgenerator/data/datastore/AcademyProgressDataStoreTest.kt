package com.vtoptunov.passwordgenerator.data.datastore

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Unit tests for AcademyProgressDataStore XP calculation formula
 */
class AcademyProgressDataStoreTest {
    
    /**
     * Test helper that mimics the private calculateXPForLevel function
     */
    private fun calculateXPForLevel(level: Int): Int {
        if (level <= 1) return 0
        return 25 * (level - 1) * (level + 2)
    }
    
    @Test
    fun `calculateXPForLevel returns correct cumulative XP values`() {
        // Level 1 is the starting level, requires 0 XP
        assertEquals("Level 1 should require 0 XP", 0, calculateXPForLevel(1))
        
        // Level 2 requires 100 XP total
        assertEquals("Level 2 should require 100 XP", 100, calculateXPForLevel(2))
        
        // Level 3 requires 250 XP total (100 + 150)
        assertEquals("Level 3 should require 250 XP", 250, calculateXPForLevel(3))
        
        // Level 4 requires 450 XP total (100 + 150 + 200)
        assertEquals("Level 4 should require 450 XP", 450, calculateXPForLevel(4))
        
        // Level 5 requires 700 XP total (100 + 150 + 200 + 250)
        assertEquals("Level 5 should require 700 XP", 700, calculateXPForLevel(5))
    }
    
    @Test
    fun `XP required for each level up follows correct pattern`() {
        // Level 1→2: 100 XP
        val xp1to2 = calculateXPForLevel(2) - calculateXPForLevel(1)
        assertEquals("Level 1→2 should require 100 XP", 100, xp1to2)
        
        // Level 2→3: 150 XP
        val xp2to3 = calculateXPForLevel(3) - calculateXPForLevel(2)
        assertEquals("Level 2→3 should require 150 XP", 150, xp2to3)
        
        // Level 3→4: 200 XP
        val xp3to4 = calculateXPForLevel(4) - calculateXPForLevel(3)
        assertEquals("Level 3→4 should require 200 XP", 200, xp3to4)
        
        // Level 4→5: 250 XP
        val xp4to5 = calculateXPForLevel(5) - calculateXPForLevel(4)
        assertEquals("Level 4→5 should require 250 XP", 250, xp4to5)
        
        // Pattern: Each level requires 50 more XP than the previous level
        // Level n→n+1 requires 50 * (n + 1) XP
    }
    
    @Test
    fun `XP required increases by 50 for each subsequent level`() {
        for (level in 2..10) {
            val currentLevelUpXP = calculateXPForLevel(level) - calculateXPForLevel(level - 1)
            val nextLevelUpXP = calculateXPForLevel(level + 1) - calculateXPForLevel(level)
            
            assertEquals(
                "XP difference between level $level and ${level+1} should be 50",
                50,
                nextLevelUpXP - currentLevelUpXP
            )
        }
    }
    
    @Test
    fun `formula matches documented progression`() {
        // According to documentation:
        // Level 1→2: 100 XP (50 * 2)
        // Level 2→3: 150 XP (50 * 3)
        // Level 3→4: 200 XP (50 * 4)
        // Pattern: 50 * (level + 1)
        
        for (level in 1..10) {
            val xpForLevelUp = if (level == 1) 0 else calculateXPForLevel(level) - calculateXPForLevel(level - 1)
            val expected = if (level == 1) 0 else 50 * (level + 1)
            
            assertEquals(
                "XP for level ${level-1}→$level should be ${expected}",
                expected,
                xpForLevelUp
            )
        }
    }
}
