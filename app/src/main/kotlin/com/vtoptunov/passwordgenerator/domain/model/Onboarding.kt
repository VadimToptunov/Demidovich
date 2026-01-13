package com.vtoptunov.passwordgenerator.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: OnboardingIcon,
    val pageNumber: Int,
    val totalPages: Int
)

enum class OnboardingIcon {
    WELCOME,
    SECURITY,
    ACADEMY,
    BIOMETRIC,
    READY
}

data class OnboardingState(
    val currentPage: Int = 0,
    val isCompleted: Boolean = false,
    val shouldShowBiometricSetup: Boolean = false
)

