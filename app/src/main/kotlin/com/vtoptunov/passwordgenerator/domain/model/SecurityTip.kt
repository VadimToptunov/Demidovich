package com.vtoptunov.passwordgenerator.domain.model

data class SecurityTip(
    val id: String,
    val title: String,
    val description: String,
    val category: TipCategory,
    val importance: TipImportance
)

enum class TipCategory {
    PASSWORD_CREATION,
    PASSWORD_STORAGE,
    PHISHING,
    SOCIAL_ENGINEERING,
    GENERAL_SECURITY
}

enum class TipImportance {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

object SecurityTips {
    val allTips = listOf(
        SecurityTip(
            id = "tip_001",
            title = "Never Reuse Passwords",
            description = "Using the same password across multiple sites means one breach compromises all accounts. Use unique passwords for each service.",
            category = TipCategory.PASSWORD_CREATION,
            importance = TipImportance.CRITICAL
        ),
        SecurityTip(
            id = "tip_002",
            title = "Enable Two-Factor Authentication",
            description = "2FA adds an extra layer of security beyond passwords. Even if your password is compromised, attackers can't access your account.",
            category = TipCategory.GENERAL_SECURITY,
            importance = TipImportance.CRITICAL
        ),
        SecurityTip(
            id = "tip_003",
            title = "Check URL Before Entering Passwords",
            description = "Phishing sites often use similar-looking URLs. Always verify you're on the legitimate website before entering credentials.",
            category = TipCategory.PHISHING,
            importance = TipImportance.HIGH
        ),
        SecurityTip(
            id = "tip_004",
            title = "Use Password Manager",
            description = "Password managers securely store and auto-fill passwords, making it easy to use unique, complex passwords everywhere.",
            category = TipCategory.PASSWORD_STORAGE,
            importance = TipImportance.HIGH
        ),
        SecurityTip(
            id = "tip_005",
            title = "Longer is Stronger",
            description = "Password length matters more than complexity. A 16-character password is exponentially harder to crack than an 8-character one.",
            category = TipCategory.PASSWORD_CREATION,
            importance = TipImportance.HIGH
        ),
        SecurityTip(
            id = "tip_006",
            title = "Beware of Social Engineering",
            description = "Attackers may impersonate IT support or trusted contacts to trick you into revealing passwords. Always verify identities through alternative channels.",
            category = TipCategory.SOCIAL_ENGINEERING,
            importance = TipImportance.HIGH
        ),
        SecurityTip(
            id = "tip_007",
            title = "Don't Share Passwords",
            description = "Even with trusted people, sharing passwords creates security risks. Use proper account sharing features instead.",
            category = TipCategory.PASSWORD_STORAGE,
            importance = TipImportance.MEDIUM
        ),
        SecurityTip(
            id = "tip_008",
            title = "Regular Security Checkups",
            description = "Periodically review your passwords, update weak ones, and check for data breaches affecting your accounts.",
            category = TipCategory.GENERAL_SECURITY,
            importance = TipImportance.MEDIUM
        ),
        SecurityTip(
            id = "tip_009",
            title = "Avoid Dictionary Words",
            description = "Passwords based on dictionary words are vulnerable to dictionary attacks. Use random characters or passphrases instead.",
            category = TipCategory.PASSWORD_CREATION,
            importance = TipImportance.MEDIUM
        ),
        SecurityTip(
            id = "tip_010",
            title = "Watch for Phishing Emails",
            description = "Legitimate companies won't ask for passwords via email. Be suspicious of urgent requests or threats.",
            category = TipCategory.PHISHING,
            importance = TipImportance.HIGH
        ),
        SecurityTip(
            id = "tip_011",
            title = "Use Biometric Authentication",
            description = "Fingerprint and face recognition add convenience and security. Enable them where available.",
            category = TipCategory.GENERAL_SECURITY,
            importance = TipImportance.MEDIUM
        ),
        SecurityTip(
            id = "tip_012",
            title = "Secure Your Recovery Options",
            description = "Recovery email and phone numbers should be secure. They're often targeted by attackers to reset your passwords.",
            category = TipCategory.PASSWORD_STORAGE,
            importance = TipImportance.HIGH
        )
    )
    
    fun getTipOfTheDay(daysSinceEpoch: Long): SecurityTip {
        val index = (daysSinceEpoch % allTips.size).toInt()
        return allTips[index]
    }
    
    fun getRandomTip(): SecurityTip {
        return allTips.random()
    }
    
    fun getTipsByCategory(category: TipCategory): List<SecurityTip> {
        return allTips.filter { it.category == category }
    }
    
    fun getCriticalTips(): List<SecurityTip> {
        return allTips.filter { it.importance == TipImportance.CRITICAL }
    }
}

