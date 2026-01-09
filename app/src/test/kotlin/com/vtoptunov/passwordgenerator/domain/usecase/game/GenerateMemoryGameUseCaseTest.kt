package com.vtoptunov.passwordgenerator.domain.usecase.game

import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for memory game generation logic
 */
class GenerateMemoryGameUseCaseTest {

    private lateinit var useCase: GenerateMemoryGameUseCase

    @Before
    fun setup() {
        useCase = GenerateMemoryGameUseCase()
    }

    @Test
    fun `generates game with correct password length`() {
        val game = useCase(GameDifficulty.MEDIUM)
        
        assertTrue("Password should match minimum length",
            game.correctPassword.length >= GameDifficulty.MEDIUM.minPasswordLength)
    }

    @Test
    fun `generates correct number of decoys`() {
        val game = useCase(GameDifficulty.HARD)
        
        assertEquals("Should have correct number of decoys",
            GameDifficulty.HARD.decoyCount, 
            game.decoyPasswords.size)
    }

    @Test
    fun `decoys are different from correct password`() {
        val game = useCase(GameDifficulty.EASY)
        
        assertFalse("Decoys should not contain correct password",
            game.decoyPasswords.contains(game.correctPassword))
    }

    @Test
    fun `decoys are unique`() {
        val game = useCase(GameDifficulty.EXPERT)
        
        val uniqueDecoys = game.decoyPasswords.toSet()
        assertEquals("All decoys should be unique",
            game.decoyPasswords.size,
            uniqueDecoys.size)
    }

    @Test
    fun `decoys are similar to original password`() {
        val originalPassword = "Test123!Password"
        val game = useCase(GameDifficulty.MEDIUM, originalPassword)
        
        game.decoyPasswords.forEach { decoy ->
            val similarity = calculateSimilarity(originalPassword, decoy)
            assertTrue("Decoy should be similar to original (similarity: $similarity)",
                similarity > 0.6) // At least 60% similar
        }
    }

    @Test
    fun `memorize time matches difficulty`() {
        val game = useCase(GameDifficulty.BEGINNER)
        
        assertEquals("Memorize time should match difficulty",
            GameDifficulty.BEGINNER.memorizeTime,
            game.memorizeTimeSeconds)
    }

    @Test
    fun `XP reward matches difficulty`() {
        val easy = useCase(GameDifficulty.EASY)
        val expert = useCase(GameDifficulty.EXPERT)
        
        assertTrue("Expert should have more XP than Easy",
            expert.xpReward > easy.xpReward)
    }

    @Test
    fun `custom password is used when provided`() {
        val customPassword = "MyCustomP@ssw0rd123"
        val game = useCase(GameDifficulty.MEDIUM, customPassword)
        
        assertEquals("Should use custom password",
            customPassword,
            game.correctPassword)
    }

    @Test
    fun `game has unique ID`() {
        val game1 = useCase(GameDifficulty.EASY)
        val game2 = useCase(GameDifficulty.EASY)
        
        assertNotEquals("Games should have unique IDs",
            game1.id,
            game2.id)
    }

    @Test
    fun `difficulty progression works correctly`() {
        val difficulties = listOf(
            GameDifficulty.BEGINNER,
            GameDifficulty.EASY,
            GameDifficulty.MEDIUM,
            GameDifficulty.HARD,
            GameDifficulty.EXPERT
        )
        
        difficulties.zipWithNext().forEach { (easier, harder) ->
            assertTrue("Harder difficulty should have more decoys",
                harder.decoyCount > easier.decoyCount)
            assertTrue("Harder difficulty should have less time",
                harder.memorizeTime <= easier.memorizeTime)
        }
    }

    @Test
    fun `decoy generation handles short passwords`() {
        val shortPassword = "A1!"
        val game = useCase(GameDifficulty.EASY, shortPassword)
        
        assertEquals("Should still generate correct password",
            shortPassword,
            game.correctPassword)
        assertTrue("Should generate some decoys",
            game.decoyPasswords.isNotEmpty())
    }

    @Test
    fun `decoy generation handles long passwords`() {
        val longPassword = "ThisIsAVeryLongPasswordWithManyCharacters123!@#"
        val game = useCase(GameDifficulty.HARD, longPassword)
        
        assertEquals("Should use long password",
            longPassword,
            game.correctPassword)
        assertEquals("Should generate correct number of decoys",
            GameDifficulty.HARD.decoyCount,
            game.decoyPasswords.size)
    }

    @Test
    fun `max attempts is set correctly`() {
        val game = useCase(GameDifficulty.MEDIUM)
        
        assertEquals("Should have 3 attempts by default",
            3,
            game.maxAttempts)
    }

    @Test
    fun `difficulty from level works correctly`() {
        assertEquals(GameDifficulty.BEGINNER, GameDifficulty.fromLevel(1))
        assertEquals(GameDifficulty.BEGINNER, GameDifficulty.fromLevel(5))
        assertEquals(GameDifficulty.EASY, GameDifficulty.fromLevel(6))
        assertEquals(GameDifficulty.EASY, GameDifficulty.fromLevel(10))
        assertEquals(GameDifficulty.MEDIUM, GameDifficulty.fromLevel(15))
        assertEquals(GameDifficulty.HARD, GameDifficulty.fromLevel(25))
        assertEquals(GameDifficulty.EXPERT, GameDifficulty.fromLevel(40))
    }

    // Helper function to calculate similarity between two strings
    private fun calculateSimilarity(str1: String, str2: String): Double {
        if (str1 == str2) return 1.0
        if (str1.length != str2.length) return 0.0
        
        val matches = str1.zip(str2).count { it.first == it.second }
        return matches.toDouble() / str1.length
    }
}

