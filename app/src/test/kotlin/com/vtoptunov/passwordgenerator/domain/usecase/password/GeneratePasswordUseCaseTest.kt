package com.vtoptunov.passwordgenerator.domain.usecase.password

import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for password generation logic
 */
class GeneratePasswordUseCaseTest {

    private lateinit var useCase: GeneratePasswordUseCase

    @Before
    fun setup() {
        useCase = GeneratePasswordUseCase()
    }

    @Test
    fun `random password has correct length`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 16
        )
        
        assertEquals(16, result.password.length)
    }

    @Test
    fun `random password includes all character types when enabled`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 20,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true
        )
        
        val password = result.password
        assertTrue("Should contain uppercase", password.any { it.isUpperCase() })
        assertTrue("Should contain lowercase", password.any { it.isLowerCase() })
        assertTrue("Should contain numbers", password.any { it.isDigit() })
        assertTrue("Should contain symbols", password.any { !it.isLetterOrDigit() })
    }

    @Test
    fun `random password respects character type restrictions`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 16,
            includeUppercase = false,
            includeLowercase = true,
            includeNumbers = false,
            includeSymbols = false
        )
        
        val password = result.password
        assertFalse("Should not contain uppercase", password.any { it.isUpperCase() })
        assertTrue("Should only contain lowercase", password.all { it.isLowerCase() })
    }

    @Test
    fun `random password handles all options disabled`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 16,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = false
        )
        
        // Should default to alphanumeric
        assertNotNull(result.password)
        assertTrue("Should have fallback charset", result.password.isNotEmpty())
    }

    @Test
    fun `XKCD password contains multiple words`() {
        val result = useCase(style = PasswordStyle.XKCD)
        
        val password = result.password
        assertTrue("Should contain hyphens", password.contains("-"))
        assertTrue("Should be memorable", result.isMemorizable)
        
        val wordCount = password.split("-").size
        assertTrue("Should have multiple words", wordCount >= 4)
    }

    @Test
    fun `phonetic password uses NATO alphabet`() {
        val result = useCase(style = PasswordStyle.Phonetic)
        
        val password = result.password
        assertTrue("Should contain hyphens", password.contains("-"))
        assertTrue("Should be memorable", result.isMemorizable)
        
        // Check for NATO alphabet words
        val natoWords = listOf("Alpha", "Bravo", "Charlie", "Delta", "Echo")
        val containsNato = natoWords.any { password.contains(it) }
        assertTrue("Should use NATO alphabet", containsNato)
    }

    @Test
    fun `story password has narrative structure`() {
        val result = useCase(style = PasswordStyle.Story)
        
        val password = result.password
        assertTrue("Should contain symbols", password.any { !it.isLetterOrDigit() })
        assertTrue("Should be memorable", result.isMemorizable)
    }

    @Test
    fun `pronounceable password is readable`() {
        val result = useCase(style = PasswordStyle.Pronounceable)
        
        val password = result.password
        assertNotNull(password)
        assertTrue("Should contain hyphens", password.contains("-"))
        assertTrue("Should be memorable", result.isMemorizable)
    }

    @Test
    fun `entropy increases with password length`() {
        val short = useCase(
            style = PasswordStyle.Random,
            length = 8
        )
        
        val long = useCase(
            style = PasswordStyle.Random,
            length = 16
        )
        
        assertTrue("Longer password should have more entropy", 
            long.entropy > short.entropy)
    }

    @Test
    fun `entropy increases with character set size`() {
        val simple = useCase(
            style = PasswordStyle.Random,
            length = 16,
            includeUppercase = false,
            includeLowercase = true,
            includeNumbers = false,
            includeSymbols = false
        )
        
        val complex = useCase(
            style = PasswordStyle.Random,
            length = 16,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true
        )
        
        assertTrue("Complex password should have more entropy",
            complex.entropy > simple.entropy)
    }

    @Test
    fun `crack time correlates with entropy`() {
        val weak = useCase(
            style = PasswordStyle.Random,
            length = 6
        )
        
        val strong = useCase(
            style = PasswordStyle.Random,
            length = 20
        )
        
        assertNotEquals("Weak and strong should have different crack times",
            weak.crackTime, strong.crackTime)
    }

    @Test
    fun `password strength classification is correct`() {
        val veryWeak = useCase(
            style = PasswordStyle.Random,
            length = 4
        )
        
        val veryStrong = useCase(
            style = PasswordStyle.Random,
            length = 32
        )
        
        assertTrue("Short password should be weak",
            veryWeak.strength.ordinal < veryStrong.strength.ordinal)
    }

    @Test
    fun `XKCD style is marked as memorizable`() {
        val result = useCase(style = PasswordStyle.XKCD)
        assertTrue("XKCD should be memorable", result.isMemorizable)
    }

    @Test
    fun `random style is not marked as memorizable`() {
        val result = useCase(style = PasswordStyle.Random, length = 16)
        assertFalse("Random should not be memorable", result.isMemorizable)
    }

    @Test
    fun `generated passwords are unique`() {
        val passwords = (1..100).map {
            useCase(style = PasswordStyle.Random, length = 16).password
        }.toSet()
        
        assertEquals("Should generate 100 unique passwords", 100, passwords.size)
    }

    @Test
    fun `entropy calculation handles edge cases`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 1
        )
        
        assertTrue("Entropy should be positive", result.entropy > 0)
    }

    @Test
    fun `crack time never shows negative values`() {
        val result = useCase(
            style = PasswordStyle.Random,
            length = 128
        )
        
        assertNotNull("Crack time should not be null", result.crackTime)
        assertFalse("Crack time should not contain negative",
            result.crackTime.contains("-"))
    }
}

