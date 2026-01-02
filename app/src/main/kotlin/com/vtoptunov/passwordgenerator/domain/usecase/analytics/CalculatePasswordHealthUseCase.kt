package com.vtoptunov.passwordgenerator.domain.usecase.analytics

import com.vtoptunov.passwordgenerator.domain.model.*
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class CalculatePasswordHealthUseCase @Inject constructor(
    private val passwordRepository: PasswordRepository
) {
    
    operator fun invoke(): Flow<PasswordHealthStats> {
        return passwordRepository.getAllPasswords().map { passwords ->
            if (passwords.isEmpty()) {
                return@map PasswordHealthStats()
            }
            
            val now = System.currentTimeMillis()
            val entropyList = passwords.map { calculatePasswordEntropy(it.password) }
            
            // Calculate strength distribution
            val strengthCounts = passwords.groupingBy { 
                PasswordStrength.fromEntropy(calculatePasswordEntropy(it.password))
            }.eachCount()
            
            val strongCount = (strengthCounts[PasswordStrength.STRONG] ?: 0) + 
                             (strengthCounts[PasswordStrength.VERY_STRONG] ?: 0)
            val weakCount = (strengthCounts[PasswordStrength.VERY_WEAK] ?: 0) + 
                           (strengthCounts[PasswordStrength.WEAK] ?: 0)
            
            // Calculate ages
            val ages = passwords.map { 
                TimeUnit.MILLISECONDS.toDays(now - it.createdAt)
            }
            val oldestAge = ages.maxOrNull() ?: 0L
            val newestAge = ages.minOrNull() ?: 0L
            
            // Calculate average entropy
            val avgEntropy = entropyList.average()
            
            // Identify security issues
            val issues = mutableListOf<SecurityIssue>()
            
            passwords.forEach { password ->
                val entropy = calculatePasswordEntropy(password.password)
                val strength = PasswordStrength.fromEntropy(entropy)
                val age = TimeUnit.MILLISECONDS.toDays(now - password.createdAt)
                
                // Weak passwords
                if (strength == PasswordStrength.WEAK || strength == PasswordStrength.VERY_WEAK) {
                    issues.add(SecurityIssue.WeakPassword(password.id, strength))
                }
                
                // Old passwords (>90 days)
                if (age > 90) {
                    issues.add(SecurityIssue.OldPassword(password.id, age.toInt()))
                }
                
                // Low entropy (<60 bits)
                if (entropy < 60) {
                    issues.add(SecurityIssue.LowEntropyPassword(password.id, entropy))
                }
            }
            
            // Calculate health score (0-100)
            val healthScore = calculateHealthScore(
                totalCount = passwords.size,
                strongCount = strongCount,
                weakCount = weakCount,
                avgEntropy = avgEntropy,
                issueCount = issues.size
            )
            
            // Calculate achievements
            val achievements = calculateAchievements(passwords, avgEntropy)
            
            // Determine average strength
            val avgStrength = PasswordStrength.fromEntropy(avgEntropy)
            
            PasswordHealthStats(
                totalPasswords = passwords.size,
                strongPasswords = strongCount,
                weakPasswords = weakCount,
                averageStrength = avgStrength,
                averageEntropy = avgEntropy,
                oldestPasswordAge = oldestAge,
                newestPasswordAge = newestAge,
                healthScore = healthScore,
                securityIssues = issues,
                achievements = achievements
            )
        }
    }
    
    private fun calculateHealthScore(
        totalCount: Int,
        strongCount: Int,
        weakCount: Int,
        avgEntropy: Double,
        issueCount: Int
    ): Int {
        var score = 50 // Base score
        
        // Strong password ratio (+30 points max)
        val strongRatio = if (totalCount > 0) strongCount.toFloat() / totalCount else 0f
        score += (strongRatio * 30).roundToInt()
        
        // Weak password penalty (-20 points max)
        val weakRatio = if (totalCount > 0) weakCount.toFloat() / totalCount else 0f
        score -= (weakRatio * 20).roundToInt()
        
        // Average entropy bonus (+20 points max)
        val entropyScore = when {
            avgEntropy >= 128 -> 20
            avgEntropy >= 80 -> 15
            avgEntropy >= 60 -> 10
            avgEntropy >= 40 -> 5
            else -> 0
        }
        score += entropyScore
        
        // Issue penalty (-5 points per issue, max -30)
        score -= (issueCount * 5).coerceAtMost(30)
        
        return score.coerceIn(0, 100)
    }
    
    private fun calculateAchievements(
        passwords: List<Password>,
        avgEntropy: Double
    ): List<Achievement> {
        val all = Achievement.getAll()
        
        return all.map { achievement ->
            when (achievement.id) {
                "first_password" -> achievement.copy(
                    isUnlocked = passwords.isNotEmpty(),
                    currentValue = passwords.size.coerceAtMost(1),
                    progress = if (passwords.isNotEmpty()) 1f else 0f
                )
                "password_creator" -> achievement.copy(
                    isUnlocked = passwords.size >= 10,
                    currentValue = passwords.size,
                    progress = (passwords.size / 10f).coerceAtMost(1f)
                )
                "security_expert" -> achievement.copy(
                    isUnlocked = passwords.size >= 50,
                    currentValue = passwords.size,
                    progress = (passwords.size / 50f).coerceAtMost(1f)
                )
                "crypto_master" -> achievement.copy(
                    isUnlocked = passwords.size >= 100,
                    currentValue = passwords.size,
                    progress = (passwords.size / 100f).coerceAtMost(1f)
                )
                "all_strong" -> {
                    val allStrong = passwords.all { 
                        val entropy = calculatePasswordEntropy(it.password)
                        val strength = PasswordStrength.fromEntropy(entropy)
                        strength == PasswordStrength.STRONG || strength == PasswordStrength.VERY_STRONG
                    } && passwords.isNotEmpty()
                    achievement.copy(
                        isUnlocked = allStrong,
                        currentValue = if (allStrong) 1 else 0,
                        progress = if (allStrong) 1f else 0f
                    )
                }
                "entropy_champion" -> {
                    val maxEntropy = passwords.maxOfOrNull { calculatePasswordEntropy(it.password) } ?: 0.0
                    achievement.copy(
                        isUnlocked = maxEntropy >= 200,
                        currentValue = maxEntropy.toInt(),
                        progress = (maxEntropy / 200).toFloat().coerceAtMost(1f)
                    )
                }
                "perfect_health" -> {
                    // This would require calculating health score, simplified here
                    achievement.copy(isUnlocked = false, progress = 0f)
                }
                else -> achievement
            }
        }
    }
    
    private fun calculatePasswordEntropy(password: String): Double {
        var charsetSize = 0
        if (password.any { it.isLowerCase() }) charsetSize += 26
        if (password.any { it.isUpperCase() }) charsetSize += 26
        if (password.any { it.isDigit() }) charsetSize += 10
        if (password.any { !it.isLetterOrDigit() }) charsetSize += 32
        
        if (charsetSize == 0) return 0.0
        
        return password.length * kotlin.math.log2(charsetSize.toDouble())
    }
}

