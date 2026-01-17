package com.vtoptunov.passwordgenerator.data.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Breach Checker using Bloom Filter for efficient password breach detection.
 * 
 * This service checks if a password exists in a database of known breached passwords
 * using a Bloom Filter for memory efficiency and speed.
 */
@Singleton
class BreachChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var bloomFilter: BloomFilter? = null
    private var isInitialized = false
    
    /**
     * Initialize the Bloom Filter with common breached passwords.
     * This should be called once at app startup.
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext
        
        try {
            // Create Bloom Filter optimized for password checking
            bloomFilter = BloomFilter.forPasswordChecking()
            
            // Load common breached passwords
            loadCommonBreachedPasswords()
            
            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: create empty filter
            bloomFilter = BloomFilter.forPasswordChecking()
            isInitialized = true
        }
    }
    
    /**
     * Check if a password is in the breached database.
     * 
     * @return true if password is likely breached (or false positive)
     *         false if password is definitely not in breach database
     */
    suspend fun isPasswordBreached(password: String): Boolean = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            initialize()
        }
        
        bloomFilter?.mightContain(password.lowercase()) ?: false
    }
    
    /**
     * Check password and return detailed result
     */
    suspend fun checkPassword(password: String): BreachCheckResult = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            initialize()
        }
        
        val isBreached = bloomFilter?.mightContain(password.lowercase()) ?: false
        
        BreachCheckResult(
            password = password,
            isBreached = isBreached,
            confidence = if (isBreached) 0.99 else 1.0, // 1% false positive rate
            source = "Local Bloom Filter",
            checkedAt = System.currentTimeMillis()
        )
    }
    
    /**
     * Load common breached passwords from embedded list.
     * In production, this would load from a larger dataset.
     */
    private fun loadCommonBreachedPasswords() {
        val commonBreachedPasswords = getTopBreachedPasswords()
        
        commonBreachedPasswords.forEach { password ->
            bloomFilter?.add(password.lowercase())
        }
    }
    
    /**
     * Get top breached passwords (sample list).
     * In production, this would be loaded from an asset file or downloaded.
     */
    private fun getTopBreachedPasswords(): List<String> {
        return listOf(
            // Top 100 most common passwords from breach databases
            "123456", "password", "123456789", "12345678", "12345",
            "1234567", "password1", "123123", "1234567890", "000000",
            "abc123", "qwerty", "111111", "letmein", "welcome",
            "monkey", "dragon", "master", "sunshine", "princess",
            "football", "shadow", "michael", "jennifer", "jordan",
            "ashley", "batman", "superman", "trustno1", "passw0rd",
            "admin", "root", "toor", "test", "guest",
            "login", "access", "secret", "oracle", "sql",
            "god", "love", "sex", "money", "star",
            "hello", "freedom", "whatever", "qazwsx", "zxcvbnm",
            "asdfgh", "123qwe", "1q2w3e4r", "1234qwer", "qwerty123",
            "qwertyuiop", "123321", "654321", "666666", "777777",
            "888888", "999999", "123abc", "password123", "passw0rd",
            "admin123", "root123", "letmein123", "welcome123", "monkey123",
            "dragon123", "master123", "sunshine123", "princess123", "football123",
            "shadow123", "michael123", "jennifer123", "jordan123", "ashley123",
            "iloveyou", "lovely", "fuckyou", "biteme", "starwars",
            "pokemon", "batman123", "superman123", "computer", "internet",
            "changeme", "default", "gateway", "unknown", "server",
            "database", "system", "user", "administrator", "password1234"
        )
    }
    
    /**
     * Get Bloom Filter statistics
     */
    fun getStatistics(): BreachCheckerStatistics {
        return BreachCheckerStatistics(
            isInitialized = isInitialized,
            memoryUsageBytes = bloomFilter?.getMemoryUsage() ?: 0,
            falsePositiveRate = 0.01,
            estimatedPasswordsLoaded = getTopBreachedPasswords().size
        )
    }
}

/**
 * Result of a breach check
 */
data class BreachCheckResult(
    val password: String,
    val isBreached: Boolean,
    val confidence: Double, // 0.0 to 1.0
    val source: String,
    val checkedAt: Long
)

/**
 * Breach Checker statistics
 */
data class BreachCheckerStatistics(
    val isInitialized: Boolean,
    val memoryUsageBytes: Long,
    val falsePositiveRate: Double,
    val estimatedPasswordsLoaded: Int
)

