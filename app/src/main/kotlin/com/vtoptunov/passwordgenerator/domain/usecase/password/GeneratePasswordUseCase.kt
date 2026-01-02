package com.vtoptunov.passwordgenerator.domain.usecase.password

import com.vtoptunov.passwordgenerator.domain.model.PasswordGenerationResult
import com.vtoptunov.passwordgenerator.domain.model.PasswordStrength
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

@Singleton
class GeneratePasswordUseCase @Inject constructor() {
    
    // Sample wordlists (in production, load from assets with 10k+ words)
    private val dicewareWords = listOf(
        "ability", "able", "about", "above", "absent", "absorb", "abstract", "absurd",
        "abuse", "access", "accident", "account", "accuse", "achieve", "acid", "acoustic",
        "acquire", "across", "act", "action", "actor", "actress", "actual", "adapt",
        "battery", "beach", "bean", "beauty", "become", "beef", "before", "begin"
    )
    
    private val phonetic = listOf(
        "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot",
        "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima",
        "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo",
        "Sierra", "Tango", "Uniform", "Victor", "Whiskey",
        "Xray", "Yankee", "Zulu"
    )
    
    operator fun invoke(
        style: PasswordStyle,
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeLowercase: Boolean = true,
        includeNumbers: Boolean = true,
        includeSymbols: Boolean = true
    ): PasswordGenerationResult {
        val password = when (style) {
            is PasswordStyle.Random -> generateRandom(
                length, includeUppercase, includeLowercase, includeNumbers, includeSymbols
            )
            is PasswordStyle.XKCD -> generateXKCD()
            is PasswordStyle.Phonetic -> generatePhonetic()
            is PasswordStyle.Story -> generateStory()
            is PasswordStyle.Pronounceable -> generatePronounceable()
        }
        
        val entropy = calculateEntropy(password, style)
        val strength = PasswordStrength.fromEntropy(entropy)
        val crackTime = calculateCrackTime(entropy)
        val isMemorizable = style != PasswordStyle.Random
        
        return PasswordGenerationResult(
            password = password,
            style = style,
            entropy = entropy,
            strength = strength,
            crackTime = crackTime,
            isMemorizable = isMemorizable
        )
    }
    
    private fun generateRandom(
        length: Int,
        upper: Boolean,
        lower: Boolean,
        nums: Boolean,
        syms: Boolean
    ): String {
        val chars = buildString {
            if (upper) append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            if (lower) append("abcdefghijklmnopqrstuvwxyz")
            if (nums) append("0123456789")
            if (syms) append("!@#\$%^&*()_+-=[]{}|;:,.<>?")
        }
        if (chars.isEmpty()) return ""
        return (1..length).map { chars.random() }.joinToString("")
    }
    
    private fun generateXKCD(): String {
        val words = List(4) { dicewareWords.random().replaceFirstChar { it.uppercase() } }
        val number = Random.nextInt(1000, 9999)
        val symbol = "!@#\$%^&*".random()
        return "${words.joinToString("-")}-$number$symbol"
    }
    
    private fun generatePhonetic(): String {
        val words = List(5) { phonetic.random() }
        val number = Random.nextInt(100, 999)
        val symbol = "!@#\$%^&*".random()
        return "${words.joinToString("-")}-$number$symbol"
    }
    
    private fun generateStory(): String {
        val nouns = listOf("Elephant", "Tiger", "Ocean", "Mountain", "Castle")
        val verbs = listOf("Jumps", "Runs", "Swims", "Flies", "Dances")
        val places = listOf("Park", "Beach", "City", "Forest", "Island")
        val year = 2024
        return "${nouns.random()}@${verbs.random()}#${nouns.random()}\$${places.random()}$year"
    }
    
    private fun generatePronounceable(): String {
        val consonants = "bcdfghjklmnprstvwxyz"
        val vowels = "aeiou"
        val word = (1..3).joinToString("") {
            "${consonants.random()}${vowels.random()}${consonants.random()}"
        }.replaceFirstChar { it.uppercase() }
        val number = Random.nextInt(10, 99)
        val symbol = "!@#\$".random()
        return "$word-$number$symbol"
    }
    
    private fun calculateEntropy(password: String, style: PasswordStyle): Double {
        return when (style) {
            is PasswordStyle.Random -> {
                val size = estimateCharsetSize(password)
                password.length * log2(size.toDouble())
            }
            is PasswordStyle.XKCD -> log2(7776.0.pow(4)) + log2(9000.0) + log2(8.0)
            is PasswordStyle.Phonetic -> log2(26.0.pow(5)) + log2(900.0) + log2(8.0)
            is PasswordStyle.Story -> log2(10000.0) * 3 + log2(2000.0) + log2(100.0)
            is PasswordStyle.Pronounceable -> log2(2205.0.pow(3)) + log2(90.0) + log2(4.0)
        }
    }
    
    private fun estimateCharsetSize(password: String): Int {
        var size = 0
        if (password.any { it.isLowerCase() }) size += 26
        if (password.any { it.isUpperCase() }) size += 26
        if (password.any { it.isDigit() }) size += 10
        if (password.any { !it.isLetterOrDigit() }) size += 32
        return size
    }
    
    private fun calculateCrackTime(entropy: Double): String {
        val attemptsPerSecond = 100_000_000_000.0
        val combinations = 2.0.pow(entropy)
        val seconds = combinations / attemptsPerSecond
        
        return when {
            seconds < 1 -> "Instant"
            seconds < 60 -> "${seconds.roundToLong()} seconds"
            seconds < 3600 -> "${(seconds / 60).roundToLong()} minutes"
            seconds < 86400 -> "${(seconds / 3600).roundToLong()} hours"
            seconds < 31536000 -> "${(seconds / 86400).roundToLong()} days"
            seconds < 3153600000 -> "${(seconds / 31536000).roundToLong()} years"
            else -> "${(seconds / 31536000000).roundToLong()} centuries"
        }
    }
}

