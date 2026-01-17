package com.vtoptunov.passwordgenerator.domain.model

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

data class AppSettings(
    val biometricEnabled: Boolean = false,
    val autoLockEnabled: Boolean = false,
    val autoLockTimeoutMinutes: Int = 5,
    val useSystemLockTimeout: Boolean = false,
    val clipboardClearSeconds: Int = 30,
    val showPasswordStrength: Boolean = true,
    val enableAnalytics: Boolean = false,
    val defaultPasswordLength: Int = 16,
    val defaultPasswordStyle: PasswordStyle = PasswordStyle.Random,
    val themeMode: ThemeMode = ThemeMode.DARK
)

