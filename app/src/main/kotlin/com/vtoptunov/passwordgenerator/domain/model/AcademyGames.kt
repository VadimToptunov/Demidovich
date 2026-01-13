package com.vtoptunov.passwordgenerator.domain.model

// Memory Match Game Models
data class MemoryGameLevel(
    val levelNumber: Int,
    val password: String,
    val shuffledOptions: List<String>,
    val memorizeTime: Int,
    val xpReward: Int
)

data class MemoryGame(
    val id: String,
    val correctPassword: String,
    val decoyPasswords: List<String>,
    val difficulty: GameDifficulty,
    val memorizeTimeSeconds: Int,
    val xpReward: Int,
    val maxAttempts: Int = 3
)

enum class GamePhase {
    DIFFICULTY_SELECTION,
    MEMORIZING,
    SELECTING,
    SELECTION,
    RESULT
}

enum class GameDifficulty(
    val displayName: String,
    val decoyCount: Int,
    val memorizeTime: Int,
    val minPasswordLength: Int,
    val xpMultiplier: Double,
    val xpReward: Int
) {
    BEGINNER("Beginner", 3, 10, 8, 1.0, 50),
    EASY("Easy", 5, 8, 10, 1.5, 75),
    MEDIUM("Medium", 7, 7, 12, 2.0, 100),
    HARD("Hard", 9, 6, 14, 2.5, 150),
    EXPERT("Expert", 11, 5, 16, 3.0, 200);
    
    companion object {
        fun fromLevel(level: Int): GameDifficulty = when {
            level <= 5 -> BEGINNER
            level <= 10 -> EASY
            level <= 20 -> MEDIUM
            level <= 35 -> HARD
            else -> EXPERT
        }
    }
}

data class GameResult(
    val isCorrect: Boolean,
    val isSuccess: Boolean,
    val xpEarned: Int,
    val newStreak: Int,
    val attemptsUsed: Int = 0,
    val timeSpentSeconds: Int = 0,
    val difficulty: GameDifficulty = GameDifficulty.BEGINNER,
    val password: String = ""
)

data class PlayerStats(
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val totalXP: Int = 0,
    val gamesWonByDifficulty: Map<GameDifficulty, Int> = emptyMap(),
    val xpProgress: Float = 0f // BUG FIX #20: Pass correct value from DataStore instead of computing
)

data class GameSession(
    val game: MemoryGame,
    val phase: GamePhase,
    val remainingMemorizeTime: Int? = null,
    val result: GameResult? = null,
    val startTime: Long = System.currentTimeMillis()
)

// Password Cracker Game Models
data class PasswordCrackerLevel(
    val levelNumber: Int,
    val weakPassword: String,
    val weaknesses: List<PasswordWeakness>,
    val hintsAvailable: Int,
    val timeLimit: Int,
    val xpReward: Int
)

enum class PasswordWeakness(val displayName: String, val description: String) {
    TOO_SHORT("Too Short", "Less than 12 characters"),
    DICTIONARY_WORD("Dictionary Word", "Contains common words"),
    COMMON_PATTERN("Common Pattern", "Uses predictable patterns like 123 or abc"),
    KEYBOARD_PATTERN("Keyboard Pattern", "Follows keyboard layout like qwerty"),
    CONTAINS_YEAR("Contains Year", "Includes year 1990-2025"),
    REPEATING_CHARS("Repeating Characters", "Has repeated characters like aaa or 111"),
    NO_UPPERCASE("No Uppercase", "Missing uppercase letters"),
    NO_LOWERCASE("No Lowercase", "Missing lowercase letters"),
    NO_NUMBERS("No Numbers", "Missing numbers"),
    NO_SYMBOLS("No Symbols", "Missing special characters"),
    PERSONAL_INFO("Personal Info", "Contains names or birthdays"),
    SEQUENTIAL("Sequential", "Uses sequential numbers or letters")
}

data class PasswordCrackerState(
    val currentLevel: PasswordCrackerLevel? = null,
    val userInput: String = "",
    val hintsUsed: Int = 0,
    val timeRemaining: Int = 0,
    val isComplete: Boolean = false,
    val isCorrect: Boolean = false,
    val revealedWeaknesses: List<PasswordWeakness> = emptyList(),
    val streak: Int = 0,
    val totalXp: Int = 0
)

data class PhishingScenario(
    val levelNumber: Int,
    val url: String,
    val emailFrom: String,
    val emailSubject: String,
    val emailBody: String,
    val isPhishing: Boolean,
    val redFlags: List<PhishingRedFlag>,
    val difficulty: PhishingDifficulty,
    val xpReward: Int
)

enum class PhishingDifficulty {
    OBVIOUS,    // Level 1-10
    SUBTLE,     // Level 11-25
    SOPHISTICATED  // Level 26+
}

enum class PhishingRedFlag(val displayName: String, val severity: RedFlagSeverity) {
    NO_HTTPS("No HTTPS", RedFlagSeverity.CRITICAL),
    SUSPICIOUS_DOMAIN("Suspicious Domain", RedFlagSeverity.CRITICAL),
    TYPO_IN_URL("Typo in URL", RedFlagSeverity.CRITICAL),
    NUMBERS_IN_DOMAIN("Numbers in Domain", RedFlagSeverity.HIGH),
    SUSPICIOUS_EMAIL("Suspicious Email Address", RedFlagSeverity.HIGH),
    FALSE_URGENCY("Creates False Urgency", RedFlagSeverity.MEDIUM),
    GENERIC_GREETING("Generic Greeting", RedFlagSeverity.LOW),
    SPELLING_ERRORS("Spelling/Grammar Errors", RedFlagSeverity.MEDIUM),
    SUSPICIOUS_ATTACHMENT("Suspicious Attachment", RedFlagSeverity.HIGH),
    REQUESTS_PASSWORD("Requests Password", RedFlagSeverity.CRITICAL),
    TOO_GOOD_TO_BE_TRUE("Too Good to Be True", RedFlagSeverity.HIGH),
    MISMATCHED_URL("Displayed URL ≠ Actual URL", RedFlagSeverity.CRITICAL)
}

enum class RedFlagSeverity {
    CRITICAL,  // 🚫 Red
    HIGH,      // 🔴 Orange-Red
    MEDIUM,    // ⚠️ Yellow
    LOW        // ℹ️ Blue
}

data class PhishingHunterState(
    val currentScenario: PhishingScenario? = null,
    val userAnswer: Boolean? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val revealedRedFlags: List<PhishingRedFlag> = emptyList(),
    val streak: Int = 0,
    val accuracy: Float = 0f,
    val totalXp: Int = 0
)

data class SocialEngineeringScenario(
    val levelNumber: Int,
    val scenarioType: SocialEngineeringType,
    val conversationMessages: List<ConversationMessage>,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val explanation: String,
    val tactics: List<EngineeringTactic>,
    val xpReward: Int
)

data class ConversationMessage(
    val sender: MessageSender,
    val text: String,
    val isSuspicious: Boolean = false
)

enum class MessageSender {
    ATTACKER,
    YOU
}

enum class SocialEngineeringType(val displayName: String) {
    PRETEXTING("Pretexting - False Scenario"),
    BAITING("Baiting - Tempting Offer"),
    QUID_PRO_QUO("Quid Pro Quo - Something for Something"),
    TAILGATING("Tailgating - Physical Access"),
    IMPERSONATION("Impersonation - Fake Identity")
}

enum class EngineeringTactic(val displayName: String, val description: String) {
    AUTHORITY("Authority", "Impersonates someone with power"),
    URGENCY("Urgency", "Creates false time pressure"),
    FEAR("Fear", "Threatens negative consequences"),
    CURIOSITY("Curiosity", "Exploits natural curiosity"),
    GREED("Greed", "Promises money or rewards"),
    HELPFULNESS("Helpfulness", "Exploits desire to help"),
    TRUST("Trust", "Builds false sense of trust"),
    SCARCITY("Scarcity", "Limited time offer")
}

data class SocialEngineeringState(
    val currentScenario: SocialEngineeringScenario? = null,
    val selectedAnswer: String? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val revealedTactics: List<EngineeringTactic> = emptyList(),
    val streak: Int = 0,
    val totalXp: Int = 0
)

data class AcademyProgress(
    val totalXp: Int = 0,
    val level: Int = 1,
    val memoryMatchBestStreak: Int = 0,
    val passwordCrackerBestStreak: Int = 0,
    val phishingHunterAccuracy: Float = 0f,
    val socialEngineeringCompleted: Int = 0,
    val gamesUnlocked: Set<AcademyGame> = setOf(AcademyGame.MEMORY_MATCH),
    val lessonsCompleted: Int = 0
)

enum class AcademyGame(
    val displayName: String,
    val icon: String,
    val unlockLevel: Int,
    val description: String
) {
    MEMORY_MATCH(
        "Memory Match",
        "🧠",
        1,
        "Remember passwords and find them among decoys"
    ),
    PASSWORD_CRACKER(
        "Password Cracker",
        "🔓",
        3,
        "Learn why weak passwords are dangerous"
    ),
    PHISHING_HUNTER(
        "Phishing Hunter",
        "🎣",
        5,
        "Spot fake websites and emails"
    ),
    SOCIAL_ENGINEERING(
        "Social Engineering",
        "🎭",
        7,
        "Defend against manipulation tactics"
    )
}

