package com.demidovich.domain.usecase.password

import com.demidovich.domain.model.PasswordGenerationResult
import com.demidovich.domain.model.PasswordStrength
import com.demidovich.domain.model.PasswordStyle
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

@Singleton
class GeneratePasswordUseCase @Inject constructor() {
    
    // Simplified wordlists - in production, load from assets
    // These are just examples, real implementation will have 10k+ words
    private val commonNouns = listOf(
        "ability", "access", "account", "address", "agency", "agreement", "airport", "amount",
        "analysis", "animal", "answer", "apple", "area", "argument", "article", "audience",
        // ... add 10,000 more
        "banana", "battery", "beach", "bedroom", "bicycle", "bottle", "building", "business"
    )
    
    private val commonVerbs = listOf(
        "accept", "achieve", "acquire", "adapt", "add", "adjust", "admire", "admit",
        "advance", "affect", "afford", "agree", "allow", "amaze", "analyze", "announce",
        // ... add 10,000 more
        "bring", "build", "calculate", "capture", "carry", "catch", "celebrate", "change"
    )
    
    private val places = listOf(
        "airport", "bakery", "bank", "beach", "bridge", "building", "cafe", "castle",
        "church", "cinema", "city", "college", "desert", "factory", "farm", "forest",
        // ... add 10,000 more
        "garden", "gym", "harbor", "hospital", "hotel", "house", "island", "kitchen"
    )
    
    private val dicewareWords = listOf(
        "ability", "able", "about", "above", "absent", "absorb", "abstract", "absurd",
        "abuse", "access", "accident", "account", "accuse", "achieve", "acid", "acoustic",
        // ... EFF Diceware 7776 words
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
            isMemorizable = isMemorizable,
            isBreached = false
        )
    }
    
    private fun generateRandom(
        length: Int,
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean
    ): String {
        val chars = buildString {
            if (includeUppercase) append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            if (includeLowercase) append("abcdefghijklmnopqrstuvwxyz")
            if (includeNumbers) append("0123456789")
            if (includeSymbols) append("!@#\$%^&*()_+-=[]{}|;:,.<>?")
        }
        
        if (chars.isEmpty()) return ""
        
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
    
    private fun generateXKCD(): String {
        // 4 random words + number + symbol
        val words = List(4) { dicewareWords.random().capitalize() }
        val number = Random.nextInt(1000, 9999)
        val symbol = "!@#\$%^&*".random()
        
        return "${words.joinToString("-")}-$number$symbol"
    }
    
    private fun generatePhonetic(): String {
        // 5 phonetic words + number + symbol
        val words = List(5) { phonetic.random() }
        val number = Random.nextInt(100, 999)
        val symbol = "!@#\$%^&*".random()
        
        return "${words.joinToString("-")}-$number$symbol"
    }
    
    private fun generateStory(): String {
        // Subject + Verb + Object + Place + Year
        val subject = commonNouns.random().capitalize()
        val verb = commonVerbs.random().capitalize()
        val obj = commonNouns.random().capitalize()
        val place = places.random().capitalize()
        val year = 2024
        val symbols = listOf("@", "#", "\$", "!")
        
        return "$subject${symbols.random()}$verb${symbols.random()}$obj#$place$year"
    }
    
    private fun generatePronounceable(): String {
        // Generate fake but pronounceable words
        val consonants = "bcdfghjklmnprstvwxyz"
        val vowels = "aeiou"
        val syllables = mutableListOf<String>()
        
        // Create 3 syllables (consonant-vowel-consonant)
        repeat(3) {
            syllables.add(
                "${consonants.random()}" +
                "${vowels.random()}" +
                "${consonants.random()}"
            )
        }
        
        val word = syllables.joinToString("").replaceFirstChar { it.uppercase() }
        val number = Random.nextInt(10, 99)
        val symbol = "!@#\$".random()
        
        return "$word-$number$symbol"
    }
    
    private fun calculateEntropy(password: String, style: PasswordStyle): Double {
        return when (style) {
            is PasswordStyle.Random -> {
                val charsetSize = estimateCharsetSize(password)
                password.length * log2(charsetSize.toDouble())
            }
            is PasswordStyle.XKCD -> {
                // 4 words from 7776 + 4 digits + 8 symbols
                log2(7776.0.pow(4)) + log2(9000.0) + log2(8.0)
            }
            is PasswordStyle.Phonetic -> {
                // 5 words from 26 + 3 digits + 8 symbols
                log2(26.0.pow(5)) + log2(900.0) + log2(8.0)
            }
            is PasswordStyle.Story -> {
                // Assuming 10k words for each category
                log2(10000.0) * 3 + log2(2000.0) + log2(100.0) + log2(4.0)
            }
            is PasswordStyle.Pronounceable -> {
                // 21 consonants * 5 vowels * 21 consonants = 2205 per syllable
                log2(2205.0.pow(3)) + log2(90.0) + log2(4.0)
            }
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
        // Modern GPU: ~100 billion attempts/second
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
    
    private fun String.capitalize() = replaceFirstChar { it.uppercase() }
}

