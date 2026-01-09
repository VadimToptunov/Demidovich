package com.vtoptunov.passwordgenerator.data.security

import java.security.MessageDigest
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

/**
 * Bloom Filter implementation for efficient password breach checking.
 * 
 * A Bloom Filter is a space-efficient probabilistic data structure that can test
 * whether an element is a member of a set. False positives are possible, but false
 * negatives are not.
 * 
 * This implementation uses multiple hash functions to minimize false positive rate.
 */
class BloomFilter(
    private val expectedElements: Int = 1_000_000,
    private val falsePositiveRate: Double = 0.01
) {
    private val bitArraySize: Int
    private val hashFunctionCount: Int
    private val bitArray: BooleanArray
    
    init {
        // Calculate optimal bit array size
        bitArraySize = calculateOptimalBitArraySize(expectedElements, falsePositiveRate)
        
        // Calculate optimal number of hash functions
        hashFunctionCount = calculateOptimalHashFunctionCount(bitArraySize, expectedElements)
        
        // Initialize bit array
        bitArray = BooleanArray(bitArraySize)
    }
    
    /**
     * Add an element to the Bloom Filter
     */
    fun add(element: String) {
        val hashes = getHashes(element)
        hashes.forEach { hash ->
            bitArray[hash % bitArraySize] = true
        }
    }
    
    /**
     * Check if an element might be in the set
     * @return true if element might be in set (or false positive)
     *         false if element is definitely not in set
     */
    fun mightContain(element: String): Boolean {
        val hashes = getHashes(element)
        return hashes.all { hash ->
            bitArray[hash % bitArraySize]
        }
    }
    
    /**
     * Generate k hash values for an element using double hashing
     */
    private fun getHashes(element: String): IntArray {
        val hash1 = hash(element, 0)
        val hash2 = hash(element, 1)
        
        return IntArray(hashFunctionCount) { i ->
            val combinedHash = hash1 + i * hash2
            (combinedHash and Int.MAX_VALUE)
        }
    }
    
    /**
     * Hash function using SHA-256
     */
    private fun hash(element: String, seed: Int): Int {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("$element$seed".toByteArray())
        
        // Convert first 4 bytes to int
        return ((hash[0].toInt() and 0xFF) shl 24) or
               ((hash[1].toInt() and 0xFF) shl 16) or
               ((hash[2].toInt() and 0xFF) shl 8) or
               (hash[3].toInt() and 0xFF)
    }
    
    /**
     * Calculate optimal bit array size: m = -(n * ln(p)) / (ln(2)^2)
     */
    private fun calculateOptimalBitArraySize(n: Int, p: Double): Int {
        return ceil(-(n * ln(p)) / (ln(2.0).pow(2))).toInt()
    }
    
    /**
     * Calculate optimal number of hash functions: k = (m/n) * ln(2)
     */
    private fun calculateOptimalHashFunctionCount(m: Int, n: Int): Int {
        return ceil((m.toDouble() / n) * ln(2.0)).toInt().coerceAtLeast(1)
    }
    
    /**
     * Get current false positive probability
     */
    fun getFalsePositiveProbability(insertedElements: Int): Double {
        val exponent = -hashFunctionCount.toDouble() * insertedElements / bitArraySize
        return (1 - kotlin.math.exp(exponent)).pow(hashFunctionCount.toDouble())
    }
    
    /**
     * Get memory usage in bytes
     */
    fun getMemoryUsage(): Long {
        return bitArraySize / 8L
    }
    
    companion object {
        /**
         * Create a Bloom Filter with custom parameters
         */
        fun create(
            expectedElements: Int,
            falsePositiveRate: Double = 0.01
        ): BloomFilter {
            return BloomFilter(expectedElements, falsePositiveRate)
        }
        
        /**
         * Create a Bloom Filter for common password checking
         * Optimized for ~10 million passwords with 1% false positive rate
         */
        fun forPasswordChecking(): BloomFilter {
            return BloomFilter(
                expectedElements = 10_000_000,
                falsePositiveRate = 0.01
            )
        }
    }
}

