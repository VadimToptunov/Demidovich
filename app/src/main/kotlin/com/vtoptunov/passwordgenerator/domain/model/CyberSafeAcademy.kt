package com.vtoptunov.passwordgenerator.domain.model

/**
 * CyberSafe Academy - Educational mini-games system
 * Multiple games teaching cybersecurity through gameplay
 */

sealed class MiniGame(
    val id: String,
    val displayName: String,
    val description: String,
    val icon: String,
    val unlockLevel: Int = 1
) {
    object MemoryMatch : MiniGame(
        id = "memory_match",
        displayName = "Memory Match",
        description = "Remember and identify the correct password from similar options",
        icon = "🧠",
        unlockLevel = 1
    )
    
    object PasswordCracker : MiniGame(
        id = "password_cracker",
        displayName = "Password Cracker",
        description = "Learn what makes passwords weak by trying to crack them",
        icon = "🔓",
        unlockLevel = 3
    )
    
    object PhishingHunter : MiniGame(
        id = "phishing_hunter",
        displayName = "Phishing Hunter",
        description = "Spot fake login pages and suspicious password requests",
        icon = "🎣",
        unlockLevel = 5
    )
    
    object SecurityQuiz : MiniGame(
        id = "security_quiz",
        displayName = "Security Quiz",
        description = "Test your cybersecurity knowledge with quick questions",
        icon = "❓",
        unlockLevel = 8
    )
    
    companion object {
        fun all() = listOf(MemoryMatch, PasswordCracker, PhishingHunter, SecurityQuiz)
        fun fromId(id: String) = all().find { it.id == id }
    }
}

/**
 * Progressive difficulty system with infinite levels
 */
data class GameLevel(
    val level: Int,
    val difficulty: InfiniteDifficulty,
    val xpReward: Int,
    val securityTip: SecurityTip? = null
) {
    companion object {
        fun forLevel(level: Int, gameType: MiniGame): GameLevel {
            val difficulty = InfiniteDifficulty.fromLevel(level)
            val xpReward = calculateXPReward(level, difficulty)
            val tip = SecurityTip.forLevel(level, gameType)
            
            return GameLevel(level, difficulty, xpReward, tip)
        }
        
        private fun calculateXPReward(level: Int, difficulty: InfiniteDifficulty): Int {
            // Base XP increases with level, multiplied by difficulty
            val baseXP = 10 + (level * 2)
            return (baseXP * difficulty.multiplier).toInt()
        }
    }
}

/**
 * Infinite difficulty progression
 */
data class InfiniteDifficulty(
    val tier: DifficultyTier,
    val modifier: Int, // Additional difficulty within tier
    val multiplier: Double
) {
    enum class DifficultyTier(val displayName: String, val baseMultiplier: Double) {
        BEGINNER("Beginner", 1.0),
        EASY("Easy", 1.2),
        MEDIUM("Medium", 1.5),
        HARD("Hard", 2.0),
        EXPERT("Expert", 2.5),
        MASTER("Master", 3.0),
        INSANE("Insane", 4.0),
        LEGENDARY("Legendary", 5.0)
    }
    
    val displayName: String
        get() = if (modifier > 0) {
            "${tier.displayName} +$modifier"
        } else {
            tier.displayName
        }
    
    companion object {
        fun fromLevel(level: Int): InfiniteDifficulty {
            return when {
                level <= 5 -> InfiniteDifficulty(DifficultyTier.BEGINNER, level - 1, 1.0 + (level - 1) * 0.05)
                level <= 10 -> InfiniteDifficulty(DifficultyTier.EASY, level - 6, 1.2 + (level - 6) * 0.06)
                level <= 20 -> InfiniteDifficulty(DifficultyTier.MEDIUM, level - 11, 1.5 + (level - 11) * 0.05)
                level <= 35 -> InfiniteDifficulty(DifficultyTier.HARD, level - 21, 2.0 + (level - 21) * 0.03)
                level <= 50 -> InfiniteDifficulty(DifficultyTier.EXPERT, level - 36, 2.5 + (level - 36) * 0.03)
                level <= 75 -> InfiniteDifficulty(DifficultyTier.MASTER, level - 51, 3.0 + (level - 51) * 0.02)
                level <= 100 -> InfiniteDifficulty(DifficultyTier.INSANE, level - 76, 4.0 + (level - 76) * 0.02)
                else -> InfiniteDifficulty(DifficultyTier.LEGENDARY, level - 101, 5.0 + (level - 101) * 0.01)
            }
        }
    }
}

/**
 * Educational security tips shown during gameplay
 */
data class SecurityTip(
    val id: String,
    val title: String,
    val message: String,
    val category: TipCategory,
    val learnMoreUrl: String? = null
) {
    enum class TipCategory {
        PASSWORD_STRENGTH,
        COMMON_ATTACKS,
        BEST_PRACTICES,
        PHISHING,
        ENCRYPTION,
        TWO_FACTOR,
        DATA_BREACH,
        SOCIAL_ENGINEERING
    }
    
    companion object {
        private val tips = listOf(
            // Password Strength Tips
            SecurityTip(
                "tip_length_matters",
                "Length Matters Most",
                "A 16-character password is exponentially harder to crack than an 8-character one, even with simple characters.",
                TipCategory.PASSWORD_STRENGTH
            ),
            SecurityTip(
                "tip_no_dictionary",
                "Avoid Dictionary Words",
                "Hackers use dictionaries with millions of common words and phrases. Random or made-up words are much safer.",
                TipCategory.PASSWORD_STRENGTH
            ),
            SecurityTip(
                "tip_unique_passwords",
                "Unique Passwords for Each Account",
                "If one password is breached, all your accounts with that password are compromised. Always use unique passwords.",
                TipCategory.BEST_PRACTICES
            ),
            
            // Attack Methods
            SecurityTip(
                "tip_brute_force",
                "Brute Force Attacks",
                "Attackers try every possible combination. Longer passwords exponentially increase the time needed.",
                TipCategory.COMMON_ATTACKS
            ),
            SecurityTip(
                "tip_dictionary_attack",
                "Dictionary Attacks",
                "Hackers use lists of common passwords, words, and phrases. That's why 'password123' is terrible!",
                TipCategory.COMMON_ATTACKS
            ),
            SecurityTip(
                "tip_credential_stuffing",
                "Credential Stuffing",
                "Hackers use leaked passwords from data breaches to try logging into other sites. Unique passwords prevent this.",
                TipCategory.COMMON_ATTACKS
            ),
            
            // Phishing
            SecurityTip(
                "tip_check_url",
                "Always Check the URL",
                "Phishing sites often use similar-looking domains like 'g00gle.com' instead of 'google.com'. Always verify!",
                TipCategory.PHISHING
            ),
            SecurityTip(
                "tip_no_email_passwords",
                "Never Share Passwords via Email",
                "Legitimate companies NEVER ask for your password via email. This is always a phishing attempt.",
                TipCategory.PHISHING
            ),
            
            // 2FA
            SecurityTip(
                "tip_enable_2fa",
                "Enable Two-Factor Authentication",
                "Even if your password is compromised, 2FA adds a second layer of protection that's much harder to bypass.",
                TipCategory.TWO_FACTOR
            ),
            SecurityTip(
                "tip_authenticator_app",
                "Use Authenticator Apps",
                "Authenticator apps are more secure than SMS codes, which can be intercepted via SIM swapping attacks.",
                TipCategory.TWO_FACTOR
            ),
            
            // Data Breaches
            SecurityTip(
                "tip_breach_check",
                "Check for Breaches Regularly",
                "Use services like HaveIBeenPwned to see if your email/password appeared in known data breaches.",
                TipCategory.DATA_BREACH
            ),
            SecurityTip(
                "tip_change_breached",
                "Change Breached Passwords Immediately",
                "If a site you use is breached, change that password everywhere you used it. Don't wait!",
                TipCategory.DATA_BREACH
            ),
            
            // Best Practices
            SecurityTip(
                "tip_password_manager",
                "Use a Password Manager",
                "You can't remember 100 unique complex passwords. A password manager does it for you securely.",
                TipCategory.BEST_PRACTICES
            ),
            SecurityTip(
                "tip_regular_updates",
                "Update Important Passwords Regularly",
                "For critical accounts (banking, email), change passwords every 6-12 months as a precaution.",
                TipCategory.BEST_PRACTICES
            ),
            
            // Encryption
            SecurityTip(
                "tip_https_only",
                "Only Enter Passwords on HTTPS Sites",
                "The lock icon (🔒) in your browser means the connection is encrypted. Never enter passwords without it!",
                TipCategory.ENCRYPTION
            ),
            SecurityTip(
                "tip_public_wifi",
                "Avoid Passwords on Public WiFi",
                "Public networks can be monitored. Use a VPN or avoid sensitive logins when on public WiFi.",
                TipCategory.ENCRYPTION
            )
        )
        
        fun forLevel(level: Int, gameType: MiniGame): SecurityTip? {
            // Show tip every 3 levels
            if (level % 3 != 0) return null
            
            // Filter tips by game type
            val relevantTips = when (gameType) {
                MiniGame.MemoryMatch -> tips.filter { 
                    it.category == TipCategory.PASSWORD_STRENGTH || 
                    it.category == TipCategory.BEST_PRACTICES 
                }
                MiniGame.PasswordCracker -> tips.filter { 
                    it.category == TipCategory.COMMON_ATTACKS ||
                    it.category == TipCategory.PASSWORD_STRENGTH
                }
                MiniGame.PhishingHunter -> tips.filter { 
                    it.category == TipCategory.PHISHING ||
                    it.category == TipCategory.SOCIAL_ENGINEERING
                }
                MiniGame.SecurityQuiz -> tips // All tips
            }
            
            // Return random tip from relevant ones
            return relevantTips.randomOrNull()
        }
        
        fun random() = tips.random()
        fun byCategory(category: TipCategory) = tips.filter { it.category == category }
    }
}

/**
 * Game session with infinite progression
 */
data class GameSession(
    val gameType: MiniGame,
    val currentLevel: Int,
    val highestLevel: Int,
    val totalXP: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val lastPlayedAt: Long = System.currentTimeMillis()
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) (gamesWon.toFloat() / gamesPlayed) * 100 else 0f
}

/**
 * Player progress across all games
 */
data class AcademyProgress(
    val totalLevel: Int,
    val totalXP: Int,
    val gamesUnlocked: List<String>,
    val sessions: Map<String, GameSession>,
    val achievements: List<Achievement>,
    val securityScore: Int // 0-100 based on game performance
) {
    fun getSession(gameType: MiniGame): GameSession? {
        return sessions[gameType.id]
    }
    
    fun isGameUnlocked(gameType: MiniGame): Boolean {
        return totalLevel >= gameType.unlockLevel
    }
    
    companion object {
        fun initial() = AcademyProgress(
            totalLevel = 1,
            totalXP = 0,
            gamesUnlocked = listOf(MiniGame.MemoryMatch.id),
            sessions = emptyMap(),
            achievements = emptyList(),
            securityScore = 0
        )
    }
}

