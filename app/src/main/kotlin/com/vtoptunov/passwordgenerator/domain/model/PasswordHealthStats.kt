package com.vtoptunov.passwordgenerator.domain.model

data class PasswordHealthStats(
    val totalPasswords: Int = 0,
    val strongPasswords: Int = 0,
    val weakPasswords: Int = 0,
    val averageStrength: PasswordStrength = PasswordStrength.WEAK,
    val averageEntropy: Double = 0.0,
    val oldestPasswordAge: Long = 0, // days
    val newestPasswordAge: Long = 0, // days
    val healthScore: Int = 0, // 0-100
    val securityIssues: List<SecurityIssue> = emptyList(),
    val achievements: List<Achievement> = emptyList()
)

sealed class SecurityIssue {
    data class WeakPassword(
        val passwordId: String,
        val strength: PasswordStrength,
        val recommendation: String = "Generate a stronger password with more entropy"
    ) : SecurityIssue()
    
    data class OldPassword(
        val passwordId: String,
        val ageInDays: Int,
        val recommendation: String = "Consider rotating this password"
    ) : SecurityIssue()
    
    data class LowEntropyPassword(
        val passwordId: String,
        val entropy: Double,
        val recommendation: String = "Use a longer or more complex password"
    ) : SecurityIssue()
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Float = 0f, // 0.0 to 1.0
    val requirement: Int = 0,
    val currentValue: Int = 0
) {
    companion object {
        fun getAll(): List<Achievement> = listOf(
            Achievement(
                id = "first_password",
                title = "First Steps",
                description = "Generate your first password",
                iconName = "🎯",
                requirement = 1
            ),
            Achievement(
                id = "password_creator",
                title = "Password Creator",
                description = "Generate 10 passwords",
                iconName = "🔐",
                requirement = 10
            ),
            Achievement(
                id = "security_expert",
                title = "Security Expert",
                description = "Generate 50 passwords",
                iconName = "🛡️",
                requirement = 50
            ),
            Achievement(
                id = "crypto_master",
                title = "Crypto Master",
                description = "Generate 100 passwords",
                iconName = "👑",
                requirement = 100
            ),
            Achievement(
                id = "all_strong",
                title = "Fortress Mode",
                description = "All saved passwords are STRONG or better",
                iconName = "💪",
                requirement = 1
            ),
            Achievement(
                id = "xkcd_lover",
                title = "XKCD Fan",
                description = "Generate 20 XKCD-style passwords",
                iconName = "🤓",
                requirement = 20
            ),
            Achievement(
                id = "variety_master",
                title = "Style Master",
                description = "Try all 5 password generation styles",
                iconName = "🎨",
                requirement = 5
            ),
            Achievement(
                id = "perfect_health",
                title = "Perfect Security",
                description = "Achieve 100% health score",
                iconName = "⭐",
                requirement = 100
            ),
            Achievement(
                id = "entropy_champion",
                title = "Entropy Champion",
                description = "Generate a password with 200+ bits entropy",
                iconName = "🔥",
                requirement = 200
            ),
            Achievement(
                id = "weekend_warrior",
                title = "Weekend Warrior",
                description = "Generate passwords 7 days in a row",
                iconName = "📅",
                requirement = 7
            )
        )
    }
}

