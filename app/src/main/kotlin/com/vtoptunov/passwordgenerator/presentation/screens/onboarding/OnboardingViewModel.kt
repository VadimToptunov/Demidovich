package com.vtoptunov.passwordgenerator.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.datastore.OnboardingDataStore
import com.vtoptunov.passwordgenerator.data.security.BiometricAuthManager
import com.vtoptunov.passwordgenerator.data.security.BiometricAvailability
import com.vtoptunov.passwordgenerator.domain.model.OnboardingIcon
import com.vtoptunov.passwordgenerator.domain.model.OnboardingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingScreenState(
    val pages: List<OnboardingPage> = emptyList(),
    val currentPage: Int = 0,
    val canSkip: Boolean = true,
    val biometricAvailable: Boolean = false,
    val showBiometricPrompt: Boolean = false
)

sealed class OnboardingEvent {
    object NextPage : OnboardingEvent()
    object PreviousPage : OnboardingEvent()
    object Skip : OnboardingEvent()
    object Complete : OnboardingEvent()
    object EnableBiometric : OnboardingEvent()
    object SkipBiometric : OnboardingEvent()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore,
    private val biometricAuthManager: BiometricAuthManager
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingScreenState())
    val state: StateFlow<OnboardingScreenState> = _state.asStateFlow()

    init {
        initializeOnboarding()
    }

    private fun initializeOnboarding() {
        viewModelScope.launch {
            val biometricAvailability = biometricAuthManager.isBiometricAvailable()
            val biometricAvailable = biometricAvailability == BiometricAvailability.Available

            val pages = buildOnboardingPages(biometricAvailable)

            _state.update {
                it.copy(
                    pages = pages,
                    biometricAvailable = biometricAvailable
                )
            }
        }
    }

    private fun buildOnboardingPages(includeBiometric: Boolean): List<OnboardingPage> {
        val pages = mutableListOf(
            OnboardingPage(
                title = "Welcome to PassForge",
                description = "Generate ultra-secure passwords with military-grade encryption. Your digital fortress starts here.",
                icon = OnboardingIcon.WELCOME,
                pageNumber = 0,
                totalPages = if (includeBiometric) 5 else 4
            ),
            OnboardingPage(
                title = "Fort Knox Security",
                description = "SQLCipher encryption, Android Keystore, and biometric locks keep your passwords safer than Fort Knox.",
                icon = OnboardingIcon.SECURITY,
                pageNumber = 1,
                totalPages = if (includeBiometric) 5 else 4
            ),
            OnboardingPage(
                title = "PassForge Academy",
                description = "Level up your security skills! Play games, earn XP, and become a cybersecurity expert.",
                icon = OnboardingIcon.ACADEMY,
                pageNumber = 2,
                totalPages = if (includeBiometric) 5 else 4
            )
        )

        if (includeBiometric) {
            pages.add(
                OnboardingPage(
                    title = "Biometric Protection",
                    description = "Use your fingerprint or face to unlock PassForge. Maximum security, minimum effort.",
                    icon = OnboardingIcon.BIOMETRIC,
                    pageNumber = 3,
                    totalPages = 5
                )
            )
        }

        pages.add(
            OnboardingPage(
                title = "You're All Set!",
                description = "Start generating unbreakable passwords and master cybersecurity. Welcome to the cyber elite! 🎯",
                icon = OnboardingIcon.READY,
                pageNumber = pages.size,
                totalPages = if (includeBiometric) 5 else 4
            )
        )

        return pages
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NextPage -> nextPage()
            OnboardingEvent.PreviousPage -> previousPage()
            OnboardingEvent.Skip -> skipOnboarding()
            OnboardingEvent.Complete -> completeOnboarding()
            OnboardingEvent.EnableBiometric -> enableBiometric()
            OnboardingEvent.SkipBiometric -> skipBiometric()
        }
    }

    private fun nextPage() {
        val currentPage = _state.value.currentPage
        val totalPages = _state.value.pages.size

        if (currentPage < totalPages - 1) {
            _state.update { it.copy(currentPage = currentPage + 1) }
        } else {
            completeOnboarding()
        }
    }

    private fun previousPage() {
        val currentPage = _state.value.currentPage
        if (currentPage > 0) {
            _state.update { it.copy(currentPage = currentPage - 1) }
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            onboardingDataStore.setOnboardingCompleted(true)
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            val currentPageData = _state.value.pages.getOrNull(_state.value.currentPage)
            
            // Show biometric prompt if on biometric page
            if (currentPageData?.icon == OnboardingIcon.BIOMETRIC && _state.value.biometricAvailable) {
                _state.update { it.copy(showBiometricPrompt = true) }
            } else {
                onboardingDataStore.setOnboardingCompleted(true)
            }
        }
    }

    private fun enableBiometric() {
        viewModelScope.launch {
            // Biometric will be enabled through SettingsRepository
            // For now, just mark as shown and complete onboarding
            onboardingDataStore.setBiometricSetupShown(true)
            onboardingDataStore.setOnboardingCompleted(true)
        }
    }

    private fun skipBiometric() {
        viewModelScope.launch {
            onboardingDataStore.setBiometricSetupShown(true)
            onboardingDataStore.setOnboardingCompleted(true)
        }
    }
}

