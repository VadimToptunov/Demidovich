package com.vtoptunov.passwordgenerator.domain.model

data class AppSettings(
    val biometricEnabled: Boolean = false,
    val autoLockEnabled: Boolean = false,
    val autoLockTimeoutMinutes: Int = 5,
    val clipboardClearSeconds: Int = 30,
    val showPasswordStrength: Boolean = true,
    val enableAnalytics: Boolean = false,
    val defaultPasswordLength: Int = 16,
    val defaultPasswordStyle: PasswordStyle = PasswordStyle.Random
)

