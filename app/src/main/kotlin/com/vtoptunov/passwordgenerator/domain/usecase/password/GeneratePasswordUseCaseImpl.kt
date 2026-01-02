package com.vtoptunov.passwordgenerator.domain.usecase.password

import com.vtoptunov.passwordgenerator.domain.model.PasswordGenerationResult
import com.vtoptunov.passwordgenerator.domain.model.PasswordStrength
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GeneratePasswordUseCaseImpl @Inject constructor() : GeneratePasswordUseCase {
    
    override suspend fun invoke(
        style: PasswordStyle,
        length: Int,
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean
    ): PasswordGenerationResult {
        val password = when (style) {
            PasswordStyle.Random -> generateRandom(length, includeUppercase, includeLowercase, includeNumbers, includeSymbols)
            PasswordStyle.XKCD -> generateXKCD(length)
            PasswordStyle.Phonetic -> generatePhonetic(length)
            PasswordStyle.Story -> generateStory(length)
            PasswordStyle.Pronounceable -> generatePronoun ceable(length)
        }
        
        val entropy = when (style) {
            PasswordStyle.Random -> calculateRandomEntropy(password, includeUppercase, includeLowercase, includeNumbers, includeSymbols)
            PasswordStyle.XKCD -> calculateXKCDEntropy(password)
            PasswordStyle.Phonetic -> calculatePhoneticEntropy(password)
            PasswordStyle.Story -> calculateStoryEntropy(password)
            PasswordStyle.Pronounceable -> calculatePronounceableEntropy(password)
        }
        
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
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean
    ): String {
        val charset = buildString {
            if (includeUppercase) append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            if (includeLowercase) append("abcdefghijklmnopqrstuvwxyz")
            if (includeNumbers) append("0123456789")
            if (includeSymbols) append("!@#$%^&*()_+-=[]{}|;:,.<>?")
        }
        
        // BUG FIX #7: If all options are disabled, default to alphanumeric
        // to prevent empty password generation with zero entropy
        if (charset.isEmpty()) {
            return generateRandom(
                length = length,
                includeUppercase = true,
                includeLowercase = true,
                includeNumbers = true,
                includeSymbols = false
            )
        }
        
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
    
    private fun generateXKCD(length: Int): String {
        // XKCD-style: correct-horse-battery-staple
        // Using most common English words for memorability
        val words = listOf(
            "correct", "horse", "battery", "staple", "dragon", "wizard", "castle", "mountain",
            "ocean", "forest", "thunder", "crystal", "phoenix", "shadow", "ancient", "mystic",
            "golden", "silver", "cosmic", "stellar", "nebula", "quantum", "infinity", "eternal",
            "frozen", "desert", "volcano", "river", "sunset", "sunrise", "eclipse", "meteor",
            "comet", "planet", "galaxy", "universe", "dimension", "portal", "legend", "destiny",
            "freedom", "journey", "adventure", "treasure", "empire", "kingdom", "realm", "sanctuary"
        )
        
        val numWords = (length / 8).coerceAtLeast(4)
        val selectedWords = (1..numWords).map { words.random() }
        val separator = listOf("-", "_", ".", "").random()
        val addNumber = Random.nextBoolean()
        val addSymbol = Random.nextBoolean()
        
        var result = selectedWords.joinToString(separator)
        
        // Capitalize random words for extra entropy
        if (Random.nextBoolean()) {
            result = result.split(separator).joinToString(separator) { word ->
                if (Random.nextBoolean()) word.replaceFirstChar { it.uppercase() } else word
            }
        }
        
        if (addNumber) {
            result += Random.nextInt(0, 100)
        }
        
        if (addSymbol) {
            result += listOf("!", "@", "#", "$", "%", "&").random()
        }
        
        return result.take(length.coerceAtLeast(20))
    }
    
    private fun generatePhonetic(length: Int): String {
        // NATO phonetic alphabet style: Alpha-Bravo-Charlie
        val phoneticWords = listOf(
            "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel",
            "India", "Juliett", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa",
            "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "Xray",
            "Yankee", "Zulu"
        )
        
        val numWords = (length / 6).coerceAtLeast(3)
        val selectedWords = (1..numWords).map { phoneticWords.random() }
        val number = Random.nextInt(0, 100)
        
        return "${selectedWords.joinToString("-")}$number"
    }
    
    private fun generateStory(length: Int): String {
        // Story-based: short memorable phrases
        val subjects = listOf("cat", "dog", "bird", "fish", "lion", "tiger", "bear", "wolf")
        val verbs = listOf("runs", "jumps", "flies", "swims", "hunts", "sleeps", "plays", "eats")
        val objects = listOf("tree", "house", "car", "boat", "plane", "ball", "food", "toy")
        val adjectives = listOf("quick", "lazy", "happy", "sad", "big", "small", "red", "blue")
        
        val subject = subjects.random()
        val verb = verbs.random()
        val obj = objects.random()
        val adj = adjectives.random()
        val number = Random.nextInt(0, 100)
        
        val story = "${adj.replaceFirstChar { it.uppercase() }}${subject.replaceFirstChar { it.uppercase() }}$verb${obj.replaceFirstChar { it.uppercase() }}$number"
        
        return story.take(length.coerceAtLeast(15))
    }
    
    private fun generatePronounceable(length: Int): String {
        // Generate pronounceable "words" using consonant-vowel patterns
        val consonants = "bcdfghjklmnpqrstvwxyz"
        val vowels = "aeiou"
        
        val result = StringBuilder()
        var useConsonant = Random.nextBoolean()
        
        while (result.length < length) {
            if (useConsonant) {
                result.append(consonants.random())
                // Sometimes add consonant clusters
                if (Random.nextInt(100) < 20 && result.length < length - 1) {
                    result.append(listOf("h", "r", "l").random())
                }
            } else {
                result.append(vowels.random())
                // Sometimes add double vowels
                if (Random.nextInt(100) < 15 && result.length < length - 1) {
                    result.append(vowels.random())
                }
            }
            useConsonant = !useConsonant
        }
        
        // Add some capitals and numbers for entropy
        var final = result.toString()
        final = final.replaceFirstChar { it.uppercase() }
        
        // Add number at the end
        if (final.length < length) {
            final += Random.nextInt(0, 100)
        }
        
        return final.take(length)
    }
    
    private fun calculateRandomEntropy(
        password: String,
        includeUppercase: Boolean,
        includeLowercase: Boolean,
        includeNumbers: Boolean,
        includeSymbols: Boolean
    ): Double {
        var charsetSize = 0
        if (includeUppercase) charsetSize += 26
        if (includeLowercase) charsetSize += 26
        if (includeNumbers) charsetSize += 10
        if (includeSymbols) charsetSize += 25
        
        return calculateEntropy(Math.pow(charsetSize.toDouble(), password.length.toDouble()))
    }
    
    private fun calculateXKCDEntropy(password: String): Double {
        // XKCD style has high entropy due to word combinations
        // Assuming ~2000 common words, each word adds ~11 bits
        val wordCount = password.count { it == '-' || it == '_' || it == '.' } + 1
        val wordEntropy = wordCount * 11.0
        
        // Add entropy for numbers and symbols
        val numberCount = password.count { it.isDigit() }
        val symbolCount = password.count { !it.isLetterOrDigit() && it != '-' && it != '_' && it != '.' }
        
        return wordEntropy + (numberCount * 3.3) + (symbolCount * 4.7)
    }
    
    private fun calculatePhoneticEntropy(password: String): Double {
        // 26 phonetic words + numbers
        val wordCount = password.count { it == '-' } + 1
        return wordCount * 4.7 + password.count { it.isDigit() } * 3.3
    }
    
    private fun calculateStoryEntropy(password: String): Double {
        // Story has moderate entropy from word combinations
        return password.length * 4.5
    }
    
    private fun calculatePronounceableEntropy(password: String): Double {
        // Pronounceable has lower entropy than random but better than predictable
        return password.length * 4.0
    }
}

