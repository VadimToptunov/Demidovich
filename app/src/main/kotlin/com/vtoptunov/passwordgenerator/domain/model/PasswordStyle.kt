package com.vtoptunov.passwordgenerator.domain.model

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
}

enum class PasswordStrength(val displayName: String, val minEntropy: Double) {
    VERY_WEAK("Very Weak", 0.0),
    WEAK("Weak", 28.0),
    FAIR("Fair", 36.0),
    STRONG("Strong", 60.0),
    VERY_STRONG("Very Strong", 128.0);
    
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

data class PasswordGenerationResult(
    val password: String,
    val style: PasswordStyle,
    val entropy: Double,
    val strength: PasswordStrength,
    val crackTime: String,
    val isMemorizable: Boolean
)
