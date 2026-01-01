package com.vtoptunov.passwordgenerator.domain.model

/**
 * Password generation styles
 */
sealed class PasswordStyle {
    object Random : PasswordStyle()
    object XKCD : PasswordStyle()
    object Phonetic : PasswordStyle()
    object Story : PasswordStyle()
    object Pronounceable : PasswordStyle()
    
    val displayName: String
        get() = when (this) {
            Random -> "Random"
            XKCD -> "XKCD (Memorable)"
            Phonetic -> "Phonetic"
            Story -> "Story"
            Pronounceable -> "Pronounceable"
        }
    
    val description: String
        get() = when (this) {
            Random -> "Strong but hard to remember"
            XKCD -> "Easy to remember, very secure"
            Phonetic -> "Easy to say over phone"
            Story -> "Memorable story-based"
            Pronounceable -> "Fake words, easy to say"
        }
    
    val example: String
        get() = when (this) {
            Random -> "X7\$kP9@mL2&"
            XKCD -> "correct-horse-battery-2024!"
            Phonetic -> "Alpha-Bravo-Charlie-742\$"
            Story -> "Elephant@Jumps#Telescope2024"
            Pronounceable -> "Trofanible-27\$"
        }
}

/**
 * Password strength levels
 */
enum class PasswordStrength(
    val displayName: String,
    val minEntropy: Double,
    val color: String // Hex color for UI
) {
    VERY_WEAK("Very Weak", 0.0, "#FF4757"),
    WEAK("Weak", 28.0, "#FF6B6B"),
    FAIR("Fair", 36.0, "#FFA500"),
    STRONG("Strong", 60.0, "#4CAF50"),
    VERY_STRONG("Very Strong", 128.0, "#2E7D32");
    
    companion object {
        fun fromEntropy(entropy: Double): PasswordStrength {
            return when {
                entropy < 28 -> VERY_WEAK
                entropy < 36 -> WEAK
                entropy < 60 -> FAIR
                entropy < 128 -> STRONG
                else -> VERY_STRONG
            }
        }
    }
}

/**
 * Result of password generation with metadata
 */
data class PasswordGenerationResult(
    val password: String,
    val style: PasswordStyle,
    val entropy: Double,
    val strength: PasswordStrength,
    val crackTime: String,
    val isMemorizable: Boolean,
    val isBreached: Boolean = false
)

