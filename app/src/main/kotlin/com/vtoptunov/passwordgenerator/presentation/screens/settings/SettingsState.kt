package com.vtoptunov.passwordgenerator.presentation.screens.settings

import com.vtoptunov.passwordgenerator.data.security.BiometricAvailability
import com.vtoptunov.passwordgenerator.domain.model.AppSettings

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val biometricAvailability: BiometricAvailability = BiometricAvailability.Unknown,
    val isLoading: Boolean = true,
    val showBiometricPrompt: Boolean = false
)
