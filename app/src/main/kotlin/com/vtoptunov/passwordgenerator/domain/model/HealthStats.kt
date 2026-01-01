package com.vtoptunov.passwordgenerator.domain.model

/**
 * Crack simulation state for real-time visualization
 */
data class CrackSimulation(
    val password: String,
    val crackedChars: String,
    val progress: Float,
    val attempts: Long,
    val timeElapsedMs: Long,
    val isComplete: Boolean
)

/**
 * Password health statistics
 */
data class HealthStats(
    val totalPasswords: Int,
    val strongPasswords: Int,
    val weakPasswords: Int,
    val reusedPasswords: Int,
    val breachedPasswords: Int,
    val averageStrength: PasswordStrength,
    val healthScore: Int, // 0-100
    val issues: List<SecurityIssue>,
    val unlockedAchievements: List<Achievement>
)

/**
 * Security issues detected
 */
sealed class SecurityIssue {
    data class WeakPassword(val passwordId: String, val password: String) : SecurityIssue()
    data class ReusedPassword(val passwordIds: List<String>, val count: Int) : SecurityIssue()
    data class BreachedPassword(val passwordId: String) : SecurityIssue()
    data class OldPassword(val passwordId: String, val ageInDays: Int) : SecurityIssue()
}

/**
 * Gamification achievements
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long? = null
) {
    companion object {
        fun getDefaultAchievements(): List<Achievement> = listOf(
            Achievement(
                id = "first_password",
                title = "Password Newbie",
                description = "Save your first password",
                iconName = "emoji_events",
                isUnlocked = false
            ),
            Achievement(
                id = "all_strong",
                title = "Security Conscious",
                description = "All passwords are strong",
                iconName = "security",
                isUnlocked = false
            ),
            Achievement(
                id = "no_reuse",
                title = "Unique Master",
                description = "No reused passwords",
                iconName = "star",
                isUnlocked = false
            ),
            Achievement(
                id = "fifty_passwords",
                title = "Password Hoarder",
                description = "Save 50+ passwords",
                iconName = "inventory",
                isUnlocked = false
            ),
            Achievement(
                id = "unbreakable",
                title = "Unbreakable",
                description = "Average crack time > 100 years",
                iconName = "shield",
                isUnlocked = false
            ),
            Achievement(
                id = "no_breaches",
                title = "Fortress",
                description = "No breached passwords",
                iconName = "verified_user",
                isUnlocked = false
            )
        )
    }
}

