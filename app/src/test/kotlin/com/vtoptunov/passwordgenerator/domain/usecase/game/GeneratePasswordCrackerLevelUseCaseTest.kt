package com.vtoptunov.passwordgenerator.domain.usecase.game

import com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Password Cracker game level generation
 */
class GeneratePasswordCrackerLevelUseCaseTest {

    private lateinit var useCase: GeneratePasswordCrackerLevelUseCase

    @Before
    fun setup() {
        useCase = GeneratePasswordCrackerLevelUseCase()
    }

    @Test
    fun `generates appropriate password for level 1`() {
        val level = useCase(1)
        
        assertEquals(1, level.levelNumber)
        assertTrue("Level 1 should have weak password",
            level.weaknesses.isNotEmpty())
    }

    @Test
    fun `difficulty increases with level`() {
        val level1 = useCase(1)
        val level50 = useCase(50)
        
        assertTrue("Higher levels should have less hints",
            level50.hintsAvailable <= level1.hintsAvailable)
        assertTrue("Higher levels should have less time",
            level50.timeLimit <= level1.timeLimit)
    }

    @Test
    fun `detects TOO_SHORT weakness`() {
        val level = useCase(1) // Level 1 typically has short passwords
        
        val hasShortPassword = level.password.length < 12
        val hasShortWeakness = level.weaknesses.contains(PasswordWeakness.TOO_SHORT)
        
        if (hasShortPassword) {
            assertTrue("Should detect TOO_SHORT weakness", hasShortWeakness)
        }
    }

    @Test
    fun `detects DICTIONARY_WORD weakness`() {
        // Test multiple levels to find one with dictionary word
        val levels = (1..10).map { useCase(it) }
        
        val hasDictionaryWeakness = levels.any { level ->
            level.weaknesses.contains(PasswordWeakness.DICTIONARY_WORD) &&
            listOf("password", "qwerty", "admin", "welcome").any { 
                level.password.contains(it, ignoreCase = true) 
            }
        }
        
        assertTrue("Should detect DICTIONARY_WORD weakness in early levels",
            hasDictionaryWeakness)
    }

    @Test
    fun `detects COMMON_PATTERN weakness`() {
        val levels = (1..5).map { useCase(it) }
        
        val hasCommonPattern = levels.any { level ->
            level.password.contains("123") || 
            level.password.contains("abc", ignoreCase = true) ||
            level.password.contains("qwe", ignoreCase = true)
        }
        
        assertTrue("Early levels should have common patterns", hasCommonPattern)
    }

    @Test
    fun `detects CONTAINS_YEAR weakness`() {
        val levels = (1..20).map { useCase(it) }
        
        val hasYearWeakness = levels.any { level ->
            level.weaknesses.contains(PasswordWeakness.CONTAINS_YEAR) &&
            (2020..2025).any { year -> level.password.contains(year.toString()) }
        }
        
        assertTrue("Some levels should contain years", hasYearWeakness)
    }

    @Test
    fun `XP reward scales with difficulty`() {
        val level1 = useCase(1)
        val level10 = useCase(10)
        val level50 = useCase(50)
        
        assertTrue("XP should increase with level",
            level10.xpReward >= level1.xpReward)
        assertTrue("XP should increase with level",
            level50.xpReward >= level10.xpReward)
    }

    @Test
    fun `hints decrease as level increases`() {
        val level1 = useCase(1)
        val level30 = useCase(30)
        val level60 = useCase(60)
        
        assertTrue("Hints should decrease",
            level30.hintsAvailable <= level1.hintsAvailable)
        assertTrue("Hints can reach zero",
            level60.hintsAvailable <= level30.hintsAvailable)
    }

    @Test
    fun `time limit decreases with level`() {
        val level5 = useCase(5)
        val level25 = useCase(25)
        val level55 = useCase(55)
        
        assertTrue("Time should decrease",
            level25.timeLimit <= level5.timeLimit)
        assertTrue("Time should continue decreasing",
            level55.timeLimit <= level25.timeLimit)
    }

    @Test
    fun `password has at least one weakness`() {
        for (level in 1..50) {
            val game = useCase(level)
            assertTrue("Level $level should have at least one weakness",
                game.weaknesses.isNotEmpty())
        }
    }

    @Test
    fun `passwords are generated consistently for same level`() {
        val level1a = useCase(10)
        val level1b = useCase(10)
        
        // Different passwords but same difficulty characteristics
        assertEquals("Should have same hints available",
            level1a.hintsAvailable,
            level1b.hintsAvailable)
        assertEquals("Should have same time limit",
            level1a.timeLimit,
            level1b.timeLimit)
    }

    @Test
    fun `detects NO_UPPERCASE weakness`() {
        val levels = (1..10).map { useCase(it) }
        
        val hasNoUppercaseWeakness = levels.any { level ->
            !level.password.any { it.isUpperCase() } &&
            level.weaknesses.contains(PasswordWeakness.NO_UPPERCASE)
        }
        
        assertTrue("Should detect NO_UPPERCASE in some passwords",
            hasNoUppercaseWeakness)
    }

    @Test
    fun `detects NO_SYMBOLS weakness`() {
        val levels = (1..10).map { useCase(it) }
        
        val hasNoSymbolsWeakness = levels.any { level ->
            !level.password.any { !it.isLetterOrDigit() } &&
            level.weaknesses.contains(PasswordWeakness.NO_SYMBOLS)
        }
        
        assertTrue("Should detect NO_SYMBOLS in some passwords",
            hasNoSymbolsWeakness)
    }

    @Test
    fun `weakness descriptions are helpful`() {
        PasswordWeakness.values().forEach { weakness ->
            assertFalse("Display name should not be empty",
                weakness.displayName.isBlank())
            assertFalse("Description should not be empty",
                weakness.description.isBlank())
        }
    }

    @Test
    fun `level 100+ has maximum difficulty`() {
        val level150 = useCase(150)
        
        assertTrue("Should have minimal or no hints",
            level150.hintsAvailable <= 1)
        assertTrue("Should have tight time limit",
            level150.timeLimit <= 20)
    }
}

