package com.vtoptunov.passwordgenerator.domain.usecase.game

import com.vtoptunov.passwordgenerator.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GeneratePasswordCrackerLevelUseCase @Inject constructor() {
    
    private val weakPasswords = mapOf(
        1 to listOf("password", "123456", "qwerty", "abc123", "letmein"),
        2 to listOf("password1", "12345678", "qwerty123", "welcome", "admin"),
        3 to listOf("Password1", "Welcome1", "Admin123", "User1234"),
        4 to listOf("P@ssword", "Passw0rd", "Admin@123", "Welcome!"),
        5 to listOf("Password123", "Welcome2024", "Admin@2024"),
        6 to listOf("P@ssw0rd1", "Summer2024", "Winter2023"),
        7 to listOf("MyPassword", "JohnDoe123", "Sarah1990"),
        8 to listOf("P@ssw0rd!", "Tr0ub4dor&3", "C0rrectH0rse")
    )
    
    operator fun invoke(levelNumber: Int): PasswordCrackerLevel {
        val tier = when {
            levelNumber <= 5 -> 1
            levelNumber <= 10 -> 2
            levelNumber <= 15 -> 3
            levelNumber <= 20 -> 4
            levelNumber <= 30 -> 5
            levelNumber <= 40 -> 6
            levelNumber <= 50 -> 7
            else -> 8
        }
        
        val passwords = weakPasswords[tier] ?: weakPasswords[8]!!
        val password = passwords.random()
        val weaknesses = analyzeWeaknesses(password)
        
        val hintsAvailable = when {
            levelNumber <= 10 -> 3
            levelNumber <= 25 -> 2
            levelNumber <= 50 -> 1
            else -> 0
        }
        
        val timeLimit = when {
            levelNumber <= 10 -> 60
            levelNumber <= 25 -> 45
            levelNumber <= 50 -> 30
            else -> 20
        }
        
        val xpReward = 50 + (tier * 10) + (weaknesses.size * 5)
        
        return PasswordCrackerLevel(
            levelNumber = levelNumber,
            weakPassword = password,
            weaknesses = weaknesses,
            hintsAvailable = hintsAvailable,
            timeLimit = timeLimit,
            xpReward = xpReward
        )
    }
    
    private fun analyzeWeaknesses(password: String): List<PasswordWeakness> {
        val weaknesses = mutableListOf<PasswordWeakness>()
        
        if (password.length < 12) {
            weaknesses.add(PasswordWeakness.TOO_SHORT)
        }
        
        if (containsDictionaryWord(password)) {
            weaknesses.add(PasswordWeakness.DICTIONARY_WORD)
        }
        
        if (containsCommonPattern(password)) {
            weaknesses.add(PasswordWeakness.COMMON_PATTERN)
        }
        
        if (containsKeyboardPattern(password)) {
            weaknesses.add(PasswordWeakness.KEYBOARD_PATTERN)
        }
        
        if (containsYear(password)) {
            weaknesses.add(PasswordWeakness.CONTAINS_YEAR)
        }
        
        if (hasRepeatingChars(password)) {
            weaknesses.add(PasswordWeakness.REPEATING_CHARS)
        }
        
        if (!password.any { it.isUpperCase() }) {
            weaknesses.add(PasswordWeakness.NO_UPPERCASE)
        }
        
        if (!password.any { it.isLowerCase() }) {
            weaknesses.add(PasswordWeakness.NO_LOWERCASE)
        }
        
        if (!password.any { it.isDigit() }) {
            weaknesses.add(PasswordWeakness.NO_NUMBERS)
        }
        
        if (!password.any { !it.isLetterOrDigit() }) {
            weaknesses.add(PasswordWeakness.NO_SYMBOLS)
        }
        
        if (hasSequentialChars(password)) {
            weaknesses.add(PasswordWeakness.SEQUENTIAL)
        }
        
        return weaknesses
    }
    
    private fun containsDictionaryWord(password: String): Boolean {
        val commonWords = listOf(
            "password", "welcome", "admin", "user", "login",
            "pass", "word", "test", "demo", "qwerty", "abc"
        )
        return commonWords.any { password.lowercase().contains(it) }
    }
    
    private fun containsCommonPattern(password: String): Boolean {
        val patterns = listOf("123", "abc", "111", "000", "aaa")
        return patterns.any { password.lowercase().contains(it) }
    }
    
    private fun containsKeyboardPattern(password: String): Boolean {
        val patterns = listOf("qwerty", "asdf", "zxcv", "qaz", "wsx")
        return patterns.any { password.lowercase().contains(it) }
    }
    
    private fun containsYear(password: String): Boolean {
        val yearRange = (1990..2025)
        return yearRange.any { password.contains(it.toString()) }
    }
    
    private fun hasRepeatingChars(password: String): Boolean {
        return password.zipWithNext().any { (a, b) -> a == b && password.count { it == a } >= 3 }
    }
    
    private fun hasSequentialChars(password: String): Boolean {
        return password.zipWithNext().any { (a, b) -> 
            b.code - a.code == 1 || b.code - a.code == -1
        }
    }
}

@Singleton
class GeneratePhishingScenarioUseCase @Inject constructor() {
    
    operator fun invoke(levelNumber: Int): PhishingScenario {
        val difficulty = when {
            levelNumber <= 10 -> PhishingDifficulty.OBVIOUS
            levelNumber <= 25 -> PhishingDifficulty.SUBTLE
            else -> PhishingDifficulty.SOPHISTICATED
        }
        
        val (url, emailFrom, subject, body, isPhishing, redFlags) = when (difficulty) {
            PhishingDifficulty.OBVIOUS -> generateObviousPhishing(levelNumber)
            PhishingDifficulty.SUBTLE -> generateSubtlePhishing(levelNumber)
            PhishingDifficulty.SOPHISTICATED -> generateSophisticatedPhishing(levelNumber)
        }
        
        val xpReward = when (difficulty) {
            PhishingDifficulty.OBVIOUS -> 30
            PhishingDifficulty.SUBTLE -> 60
            PhishingDifficulty.SOPHISTICATED -> 100
        }
        
        return PhishingScenario(
            levelNumber = levelNumber,
            url = url,
            emailFrom = emailFrom,
            emailSubject = subject,
            emailBody = body,
            isPhishing = isPhishing,
            redFlags = redFlags,
            difficulty = difficulty,
            xpReward = xpReward
        )
    }
    
    private fun generateObviousPhishing(level: Int): PhishingData {
        val scenarios = listOf(
            PhishingData(
                url = "http://g00gle.com/verify",
                emailFrom = "security@gmail-verify.net",
                subject = "URGENT! VERIFY YOUR ACCOUNT NOW!!!",
                body = "Your account will be DELETED in 24 hours! CLICK HERE to verify NOW!!!",
                isPhishing = true,
                redFlags = listOf(
                    PhishingRedFlag.NO_HTTPS,
                    PhishingRedFlag.TYPO_IN_URL,
                    PhishingRedFlag.SUSPICIOUS_EMAIL,
                    PhishingRedFlag.FALSE_URGENCY
                )
            ),
            PhishingData(
                url = "http://paypa1.com/login",
                emailFrom = "service@paypal-security.com",
                subject = "Your PayPal Account Has Been Locked",
                body = "We detected unusual activity. Click here immediately to unlock your account!",
                isPhishing = true,
                redFlags = listOf(
                    PhishingRedFlag.NO_HTTPS,
                    PhishingRedFlag.NUMBERS_IN_DOMAIN,
                    PhishingRedFlag.SUSPICIOUS_EMAIL,
                    PhishingRedFlag.FALSE_URGENCY
                )
            )
        )
        return scenarios.random()
    }
    
    private fun generateSubtlePhishing(level: Int): PhishingData {
        val scenarios = listOf(
            PhishingData(
                url = "https://accounts-google.com/signin",
                emailFrom = "no-reply@google-accounts.com",
                subject = "Unusual sign-in activity detected",
                body = "We noticed a new sign-in to your Google Account. If this wasn't you, please secure your account.",
                isPhishing = true,
                redFlags = listOf(
                    PhishingRedFlag.SUSPICIOUS_DOMAIN,
                    PhishingRedFlag.SUSPICIOUS_EMAIL,
                    PhishingRedFlag.FALSE_URGENCY
                )
            )
        )
        return scenarios.random()
    }
    
    private fun generateSophisticatedPhishing(level: Int): PhishingData {
        val scenarios = listOf(
            PhishingData(
                url = "https://accounts.g00gle.com/security",
                emailFrom = "no-reply@google.com",
                subject = "Review your recent account activity",
                body = "Hi there,\n\nWe noticed you signed in from a new device. Review your recent activity to ensure your account is secure.\n\nBest regards,\nThe Google Security Team",
                isPhishing = true,
                redFlags = listOf(
                    PhishingRedFlag.TYPO_IN_URL,
                    PhishingRedFlag.NUMBERS_IN_DOMAIN
                )
            )
        )
        return scenarios.random()
    }
}

private data class PhishingData(
    val url: String,
    val emailFrom: String,
    val subject: String,
    val body: String,
    val isPhishing: Boolean,
    val redFlags: List<PhishingRedFlag>
)

@Singleton
class GenerateSocialEngineeringScenarioUseCase @Inject constructor() {
    
    operator fun invoke(levelNumber: Int): SocialEngineeringScenario {
        val types = SocialEngineeringType.values()
        val type = types[levelNumber % types.size]
        
        val (messages, correct, wrong, tactics) = when (type) {
            SocialEngineeringType.PRETEXTING -> generatePretextingScenario()
            SocialEngineeringType.BAITING -> generateBaitingScenario()
            SocialEngineeringType.QUID_PRO_QUO -> generateQuidProQuoScenario()
            SocialEngineeringType.TAILGATING -> generateTailgatingScenario()
            SocialEngineeringType.IMPERSONATION -> generateImpersonationScenario()
        }
        
        return SocialEngineeringScenario(
            levelNumber = levelNumber,
            scenarioType = type,
            conversationMessages = messages,
            correctAnswer = correct,
            wrongAnswers = wrong,
            explanation = "This is a ${type.displayName} attack. ${getTacticsExplanation(tactics)}",
            tactics = tactics,
            xpReward = 75 + (tactics.size * 10)
        )
    }
    
    private fun generatePretextingScenario(): ScenarioData {
        return ScenarioData(
            messages = listOf(
                ConversationMessage(MessageSender.ATTACKER, "Hi, this is John from IT Support. We're doing a security audit."),
                ConversationMessage(MessageSender.ATTACKER, "Can you verify your email and last password reset date?", true)
            ),
            correctAnswer = "Ask for their employee ID and verify through official IT channels",
            wrongAnswers = listOf(
                "Provide the information requested",
                "Give them your password reset date only",
                "Ignore the message"
            ),
            tactics = listOf(EngineeringTactic.AUTHORITY, EngineeringTactic.TRUST)
        )
    }
    
    private fun generateBaitingScenario(): ScenarioData {
        return ScenarioData(
            messages = listOf(
                ConversationMessage(MessageSender.ATTACKER, "Congratulations! You've won a $500 Amazon gift card!"),
                ConversationMessage(MessageSender.ATTACKER, "Click here and enter your email to claim your prize!", true)
            ),
            correctAnswer = "Delete the message - it's too good to be true",
            wrongAnswers = listOf(
                "Click and enter email",
                "Forward to friends",
                "Reply asking for more details"
            ),
            tactics = listOf(EngineeringTactic.GREED, EngineeringTactic.CURIOSITY)
        )
    }
    
    private fun generateQuidProQuoScenario(): ScenarioData {
        return ScenarioData(
            messages = listOf(
                ConversationMessage(MessageSender.ATTACKER, "Hi, we're offering free security software upgrades."),
                ConversationMessage(MessageSender.ATTACKER, "Just provide your system credentials so we can install it remotely.", true)
            ),
            correctAnswer = "Decline and contact official IT support",
            wrongAnswers = listOf(
                "Provide credentials for the free upgrade",
                "Ask for more information about the software",
                "Accept but use fake credentials"
            ),
            tactics = listOf(EngineeringTactic.HELPFULNESS, EngineeringTactic.AUTHORITY)
        )
    }
    
    private fun generateTailgatingScenario(): ScenarioData {
        return ScenarioData(
            messages = listOf(
                ConversationMessage(MessageSender.ATTACKER, "Hey, can you hold the door? I forgot my badge and I'm late for a meeting."),
                ConversationMessage(MessageSender.ATTACKER, "I work on the 5th floor with Sarah from Marketing.", true)
            ),
            correctAnswer = "Politely refuse and direct them to reception for a temporary badge",
            wrongAnswers = listOf(
                "Hold the door - they seem legitimate",
                "Let them in but escort them",
                "Ignore them completely"
            ),
            tactics = listOf(EngineeringTactic.URGENCY, EngineeringTactic.HELPFULNESS)
        )
    }
    
    private fun generateImpersonationScenario(): ScenarioData {
        return ScenarioData(
            messages = listOf(
                ConversationMessage(MessageSender.ATTACKER, "This is the CEO. I'm in a meeting and need you to process an urgent wire transfer."),
                ConversationMessage(MessageSender.ATTACKER, "Send $50,000 to this account immediately. I'll explain later.", true)
            ),
            correctAnswer = "Verify through official channels - call the CEO's known number",
            wrongAnswers = listOf(
                "Process the transfer immediately",
                "Ask for more details via email",
                "Transfer a smaller amount first"
            ),
            tactics = listOf(EngineeringTactic.AUTHORITY, EngineeringTactic.URGENCY, EngineeringTactic.FEAR)
        )
    }
    
    private fun getTacticsExplanation(tactics: List<EngineeringTactic>): String {
        return "The attacker used: " + tactics.joinToString(", ") { it.displayName }
    }
}

private data class ScenarioData(
    val messages: List<ConversationMessage>,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val tactics: List<EngineeringTactic>
)

