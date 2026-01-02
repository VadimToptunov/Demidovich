package com.vtoptunov.passwordgenerator.domain.usecase.game

import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import com.vtoptunov.passwordgenerator.domain.model.MemoryGame
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Generates a memory training game with a password and similar decoys
 */
@Singleton
class GenerateMemoryGameUseCase @Inject constructor() {
    
    operator fun invoke(difficulty: GameDifficulty, basePassword: String? = null): MemoryGame {
        val password = basePassword ?: generateRandomPassword(difficulty.minPasswordLength)
        val decoys = generateDecoyPasswords(password, difficulty.decoyCount)
        
        return MemoryGame(
            id = UUID.randomUUID().toString(),
            correctPassword = password,
            decoyPasswords = decoys.shuffled(),
            difficulty = difficulty,
            memorizeTimeSeconds = difficulty.memorizeTime,
            xpReward = difficulty.xpReward
        )
    }
    
    private fun generateRandomPassword(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    
    /**
     * Generates decoy passwords that are similar to the original
     * but with subtle differences to make the game challenging
     */
    private fun generateDecoyPasswords(original: String, count: Int): List<String> {
        val decoys = mutableSetOf<String>()
        
        while (decoys.size < count) {
            val decoy = generateSimilarPassword(original)
            if (decoy != original && decoy !in decoys) {
                decoys.add(decoy)
            }
        }
        
        return decoys.toList()
    }
    
    private fun generateSimilarPassword(original: String): String {
        val modifications = listOf(
            { s: String -> swapRandomChars(s) },
            { s: String -> replaceRandomChar(s) },
            { s: String -> replaceSimilarChar(s) },
            { s: String -> changeRandomCase(s) },
            { s: String -> swapAdjacentChars(s) }
        )
        
        // Apply 1-2 random modifications
        val numModifications = Random.nextInt(1, 3)
        var result = original
        
        repeat(numModifications) {
            result = modifications.random()(result)
        }
        
        return result
    }
    
    private fun swapRandomChars(s: String): String {
        if (s.length < 2) return s
        val chars = s.toCharArray()
        val i = Random.nextInt(0, s.length - 1)
        val temp = chars[i]
        chars[i] = chars[i + 1]
        chars[i + 1] = temp
        return String(chars)
    }
    
    private fun replaceRandomChar(s: String): String {
        if (s.isEmpty()) return s
        val chars = s.toCharArray()
        val i = Random.nextInt(s.length)
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()"
        chars[i] = charset.random()
        return String(chars)
    }
    
    private fun replaceSimilarChar(s: String): String {
        if (s.isEmpty()) return s
        
        // Replace with visually similar characters
        val similarChars = mapOf(
            '0' to 'O', 'O' to '0', 'o' to '0',
            '1' to 'l', 'l' to '1', 'I' to '1',
            '5' to 'S', 'S' to '5',
            '8' to 'B', 'B' to '8',
            'Z' to '2', '2' to 'Z',
            'G' to '6', '6' to 'G'
        )
        
        val chars = s.toCharArray()
        val replaceableIndices = chars.indices.filter { chars[it] in similarChars.keys }
        
        if (replaceableIndices.isEmpty()) return replaceRandomChar(s)
        
        val i = replaceableIndices.random()
        chars[i] = similarChars[chars[i]] ?: chars[i]
        return String(chars)
    }
    
    private fun changeRandomCase(s: String): String {
        if (s.isEmpty()) return s
        val chars = s.toCharArray()
        val letterIndices = chars.indices.filter { chars[it].isLetter() }
        
        if (letterIndices.isEmpty()) return s
        
        val i = letterIndices.random()
        chars[i] = if (chars[i].isUpperCase()) {
            chars[i].lowercaseChar()
        } else {
            chars[i].uppercaseChar()
        }
        return String(chars)
    }
    
    private fun swapAdjacentChars(s: String): String {
        if (s.length < 2) return s
        val chars = s.toCharArray()
        val i = Random.nextInt(0, s.length - 1)
        val temp = chars[i]
        chars[i] = chars[i + 1]
        chars[i + 1] = temp
        return String(chars)
    }
}

