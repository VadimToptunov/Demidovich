package com.vtoptunov.passwordgenerator.domain.usecase.game

import com.vtoptunov.passwordgenerator.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Generates challenges for Password Cracker mini-game
 * Players try to "crack" passwords to learn what makes them weak
 */
@Singleton
class GeneratePasswordCrackerChallengeUseCase @Inject constructor() {
    
    data class CrackerChallenge(
        val password: String,
        val hints: List<String>,
        val weaknesses: List<Weakness>,
        val estimatedTime: String,
        val difficulty: InfiniteDifficulty
    )
    
    enum class Weakness(val description: String, val severity: Int) {
        TOO_SHORT("Too short (< 12 characters)", 3),
        DICTIONARY_WORD("Contains dictionary word", 2),
        COMMON_PATTERN("Common pattern (123, abc)", 3),
        NO_NUMBERS("No numbers", 1),
        NO_SYMBOLS("No special characters", 1),
        NO_UPPERCASE("No uppercase letters", 1),
        REPEATED_CHARS("Repeated characters", 2),
        KEYBOARD_PATTERN("Keyboard pattern (qwerty)", 3),
        PERSONAL_INFO("Contains personal info", 3),
        YEAR_PATTERN("Contains year (1990-2025)", 2)
    }
    
    operator fun invoke(level: Int): CrackerChallenge {
        val gameLevel = GameLevel.forLevel(level, MiniGame.PasswordCracker)
        
        // Generate weak password based on level
        val password = generateWeakPassword(level)
        val weaknesses = analyzeWeaknesses(password, level)
        val hints = generateHints(weaknesses, level)
        val time = estimateCrackTime(weaknesses)
        
        return CrackerChallenge(
            password = password,
            hints = hints,
            weaknesses = weaknesses,
            estimatedTime = time,
            difficulty = gameLevel.difficulty
        )
    }
    
    private fun generateWeakPassword(level: Int): String {
        // Early levels: Very obvious weak passwords
        // Later levels: Subtly weak passwords
        return when {
            level <= 5 -> generateVeryWeakPassword()
            level <= 15 -> generateModeratelyWeakPassword()
            level <= 30 -> generateSubtlyWeakPassword()
            else -> generateDeceptivelyWeakPassword()
        }
    }
    
    private fun generateVeryWeakPassword(): String {
        val patterns = listOf(
            "password",
            "123456",
            "qwerty",
            "abc123",
            "password123",
            "letmein",
            "welcome",
            "admin",
            "12345678"
        )
        return patterns.random()
    }
    
    private fun generateModeratelyWeakPassword(): String {
        val patterns = listOf(
            "Password1",
            "Welcome123",
            "Summer2024",
            "IloveyouIloveyou123",
            "Football!",
            "Qwerty123",
            "Sunshine1"
        )
        return patterns.random()
    }
    
    private fun generateSubtlyWeakPassword(): String {
        val patterns = listOf(
            "P@ssw0rd",
            "Summer2024!",
            "J0hn1990",
            "Monkey123!",
            "Dragon2024",
            "Baseball1!",
            "Michael1985"
        )
        return patterns.random()
    }
    
    private fun generateDeceptivelyWeakPassword(): String {
        // Looks strong but has subtle weaknesses
        val patterns = listOf(
            "Tr0ub4dor&3", // XKCD famous example
            "P@ssw0rd!2024",
            "MyN4m3isJ0hn!",
            "!QAZ2wsx#EDC",
            "Abc123!@#Def",
            "November2024!"
        )
        return patterns.random()
    }
    
    private fun analyzeWeaknesses(password: String, level: Int): List<Weakness> {
        val weaknesses = mutableListOf<Weakness>()
        
        if (password.length < 12) weaknesses.add(Weakness.TOO_SHORT)
        if (containsDictionaryWord(password)) weaknesses.add(Weakness.DICTIONARY_WORD)
        if (hasCommonPattern(password)) weaknesses.add(Weakness.COMMON_PATTERN)
        if (!password.any { it.isDigit() }) weaknesses.add(Weakness.NO_NUMBERS)
        if (!password.any { !it.isLetterOrDigit() }) weaknesses.add(Weakness.NO_SYMBOLS)
        if (!password.any { it.isUpperCase() }) weaknesses.add(Weakness.NO_UPPERCASE)
        if (hasRepeatedChars(password)) weaknesses.add(Weakness.REPEATED_CHARS)
        if (hasKeyboardPattern(password)) weaknesses.add(Weakness.KEYBOARD_PATTERN)
        if (hasYearPattern(password)) weaknesses.add(Weakness.YEAR_PATTERN)
        
        return weaknesses
    }
    
    private fun containsDictionaryWord(password: String): Boolean {
        val commonWords = listOf(
            "password", "welcome", "admin", "letmein", "monkey",
            "dragon", "master", "sunshine", "princess", "football",
            "shadow", "michael", "jennifer", "summer", "winter"
        )
        val lower = password.lowercase()
        return commonWords.any { lower.contains(it) }
    }
    
    private fun hasCommonPattern(password: String): Boolean {
        val patterns = listOf("123", "abc", "xyz", "000", "111", "aaa")
        val lower = password.lowercase()
        return patterns.any { lower.contains(it) }
    }
    
    private fun hasRepeatedChars(password: String): Boolean {
        return password.zipWithNext().any { (a, b) -> a == b }
    }
    
    private fun hasKeyboardPattern(password: String): Boolean {
        val patterns = listOf("qwerty", "asdf", "zxcv", "!qaz", "2wsx")
        val lower = password.lowercase()
        return patterns.any { lower.contains(it) }
    }
    
    private fun hasYearPattern(password: String): Boolean {
        return Regex("(19|20)\\d{2}").containsMatchIn(password)
    }
    
    private fun generateHints(weaknesses: List<Weakness>, level: Int): List<String> {
        // Early levels: Give more hints
        // Later levels: Fewer hints
        val numHints = when {
            level <= 10 -> weaknesses.size
            level <= 25 -> (weaknesses.size * 0.7).toInt().coerceAtLeast(1)
            else -> (weaknesses.size * 0.5).toInt().coerceAtLeast(1)
        }
        
        return weaknesses
            .sortedByDescending { it.severity }
            .take(numHints)
            .map { "⚠️ ${it.description}" }
    }
    
    private fun estimateCrackTime(weaknesses: List<Weakness>): String {
        val totalSeverity = weaknesses.sumOf { it.severity }
        
        return when {
            totalSeverity >= 10 -> "< 1 second"
            totalSeverity >= 7 -> "< 1 minute"
            totalSeverity >= 5 -> "< 1 hour"
            totalSeverity >= 3 -> "< 1 day"
            else -> "< 1 week"
        }
    }
}

/**
 * Generates challenges for Phishing Hunter mini-game
 * Players identify fake/suspicious login scenarios
 */
@Singleton
class GeneratePhishingChallengeUseCase @Inject constructor() {
    
    data class PhishingChallenge(
        val scenario: PhishingScenario,
        val isLegitimate: Boolean,
        val redFlags: List<String>,
        val difficulty: InfiniteDifficulty
    )
    
    data class PhishingScenario(
        val url: String,
        val emailSender: String,
        val message: String,
        val urgencyLevel: String
    )
    
    operator fun invoke(level: Int): PhishingChallenge {
        val gameLevel = GameLevel.forLevel(level, MiniGame.PhishingHunter)
        val isLegit = Random.nextBoolean()
        
        val scenario = if (isLegit) {
            generateLegitimateScenario(level)
        } else {
            generatePhishingScenario(level)
        }
        
        val redFlags = if (!isLegit) identifyRedFlags(scenario, level) else emptyList()
        
        return PhishingChallenge(
            scenario = scenario,
            isLegitimate = isLegit,
            redFlags = redFlags,
            difficulty = gameLevel.difficulty
        )
    }
    
    private fun generateLegitimateScenario(level: Int): PhishingScenario {
        val scenarios = listOf(
            PhishingScenario(
                url = "https://accounts.google.com/signin",
                emailSender = "no-reply@accounts.google.com",
                message = "Security alert: New sign-in from Chrome on Windows",
                urgencyLevel = "Info"
            ),
            PhishingScenario(
                url = "https://www.paypal.com/signin",
                emailSender = "service@paypal.com",
                message = "Your payment was successful. Order #12345",
                urgencyLevel = "Normal"
            )
        )
        return scenarios.random()
    }
    
    private fun generatePhishingScenario(level: Int): PhishingScenario {
        // Difficulty increases with level
        return when {
            level <= 10 -> generateObviousPhishing()
            level <= 25 -> generateSubtlePhishing()
            else -> generateSophisticatedPhishing()
        }
    }
    
    private fun generateObviousPhishing(): PhishingScenario {
        val scenarios = listOf(
            PhishingScenario(
                url = "http://g00gle.com/signin", // No HTTPS + typo
                emailSender = "security@gmail-verify.net",
                message = "URGENT! Your account will be CLOSED in 24 hours! Click here NOW!",
                urgencyLevel = "🚨 URGENT"
            ),
            PhishingScenario(
                url = "http://paypa1.com/verify",
                emailSender = "noreply@paypal-security.com",
                message = "Verify your account immediately or it will be suspended!",
                urgencyLevel = "⚠️ ACTION REQUIRED"
            )
        )
        return scenarios.random()
    }
    
    private fun generateSubtlePhishing(): PhishingScenario {
        val scenarios = listOf(
            PhishingScenario(
                url = "https://accounts-google.com/signin",
                emailSender = "no-reply@google-accounts.com",
                message = "Unusual activity detected. Please verify your identity.",
                urgencyLevel = "Security Alert"
            )
        )
        return scenarios.random()
    }
    
    private fun generateSophisticatedPhishing(): PhishingScenario {
        val scenarios = listOf(
            PhishingScenario(
                url = "https://accounts.g00gle.com/signin", // HTTPS but wrong domain
                emailSender = "no-reply@accounts.google.com", // Spoofed
                message = "We noticed a new sign-in to your account. If this wasn't you, please review your activity.",
                urgencyLevel = "Info"
            )
        )
        return scenarios.random()
    }
    
    private fun identifyRedFlags(scenario: PhishingScenario, level: Int): List<String> {
        val flags = mutableListOf<String>()
        
        // URL checks
        if (!scenario.url.startsWith("https://")) flags.add("🚫 No HTTPS encryption")
        if (scenario.url.contains("0") || scenario.url.contains("1")) flags.add("🚫 Suspicious domain (number substitution)")
        if (scenario.url.contains("-")) flags.add("⚠️ Hyphen in domain (often used in phishing)")
        
        // Email checks
        if (!scenario.emailSender.contains("@")) flags.add("🚫 Invalid email format")
        val domain = scenario.emailSender.substringAfter("@")
        if (domain.contains("-")) flags.add("⚠️ Suspicious email domain")
        
        // Message checks
        if (scenario.message.contains("URGENT", ignoreCase = true)) flags.add("⚠️ Creates false urgency")
        if (scenario.message.contains("click here", ignoreCase = true)) flags.add("⚠️ Suspicious call-to-action")
        if (scenario.message.contains("verify", ignoreCase = true) && 
            scenario.message.contains("immediately", ignoreCase = true)) {
            flags.add("⚠️ Urgency + verification request")
        }
        
        // Show fewer flags on higher levels
        val numFlags = when {
            level <= 15 -> flags.size
            level <= 35 -> (flags.size * 0.6).toInt().coerceAtLeast(1)
            else -> (flags.size * 0.3).toInt().coerceAtLeast(1)
        }
        
        return flags.take(numFlags)
    }
}

/**
 * Generates questions for Security Quiz mini-game
 */
@Singleton
class GenerateSecurityQuizQuestionUseCase @Inject constructor() {
    
    data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
        val category: SecurityTip.TipCategory,
        val difficulty: InfiniteDifficulty
    )
    
    operator fun invoke(level: Int): QuizQuestion {
        val gameLevel = GameLevel.forLevel(level, MiniGame.SecurityQuiz)
        val questions = getAllQuestions()
        
        // Filter by difficulty
        val appropriateQuestions = questions.filter { question ->
            getDifficultyScore(question) <= level
        }
        
        return appropriateQuestions.randomOrNull() ?: questions.random()
    }
    
    private fun getDifficultyScore(question: QuizQuestion): Int {
        return when (question.difficulty.tier) {
            InfiniteDifficulty.DifficultyTier.BEGINNER -> 1
            InfiniteDifficulty.DifficultyTier.EASY -> 5
            InfiniteDifficulty.DifficultyTier.MEDIUM -> 15
            InfiniteDifficulty.DifficultyTier.HARD -> 30
            else -> 50
        }
    }
    
    private fun getAllQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                question = "What makes a password strong?",
                options = listOf("Length and complexity", "Using your birthday", "Writing it down", "Sharing with friends"),
                correctIndex = 0,
                explanation = "Length matters most! A 16-character password with mixed characters is exponentially harder to crack.",
                category = SecurityTip.TipCategory.PASSWORD_STRENGTH,
                difficulty = InfiniteDifficulty.fromLevel(1)
            ),
            QuizQuestion(
                question = "Which is the most secure two-factor method?",
                options = listOf("SMS codes", "Email codes", "Authenticator app", "Security questions"),
                correctIndex = 2,
                explanation = "Authenticator apps are most secure because SMS can be intercepted via SIM swapping attacks.",
                category = SecurityTip.TipCategory.TWO_FACTOR,
                difficulty = InfiniteDifficulty.fromLevel(8)
            ),
            QuizQuestion(
                question = "What is phishing?",
                options = listOf("A type of malware", "Fake login pages", "Strong encryption", "Password strength"),
                correctIndex = 1,
                explanation = "Phishing uses fake login pages or emails to trick you into revealing passwords or personal info.",
                category = SecurityTip.TipCategory.PHISHING,
                difficulty = InfiniteDifficulty.fromLevel(3)
            ),
            QuizQuestion(
                question = "How often should you change critical passwords?",
                options = listOf("Never", "Daily", "Every 6-12 months", "Only when breached"),
                correctIndex = 2,
                explanation = "For important accounts, changing passwords every 6-12 months adds an extra layer of security.",
                category = SecurityTip.TipCategory.BEST_PRACTICES,
                difficulty = InfiniteDifficulty.fromLevel(10)
            ),
            QuizQuestion(
                question = "What does HTTPS mean?",
                options = listOf("High Transfer Protocol", "Encrypted connection", "Faster website", "Government site"),
                correctIndex = 1,
                explanation = "HTTPS means the connection is encrypted, protecting your data from eavesdroppers.",
                category = SecurityTip.TipCategory.ENCRYPTION,
                difficulty = InfiniteDifficulty.fromLevel(5)
            ),
            QuizQuestion(
                question = "What is a brute force attack?",
                options = listOf("Physical theft", "Trying all combinations", "Sending spam", "Virus infection"),
                correctIndex = 1,
                explanation = "Brute force attacks systematically try every possible password combination until finding the right one.",
                category = SecurityTip.TipCategory.COMMON_ATTACKS,
                difficulty = InfiniteDifficulty.fromLevel(12)
            )
            // Add 20+ more questions for variety
        )
    }
}

