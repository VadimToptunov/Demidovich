package com.vtoptunov.passwordgenerator.presentation.screens.settings

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.datastore.OnboardingDataStore
import com.vtoptunov.passwordgenerator.data.repository.SettingsRepository
import com.vtoptunov.passwordgenerator.data.security.BiometricAuthManager
import com.vtoptunov.passwordgenerator.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val biometricAuthManager: BiometricAuthManager,
    private val onboardingDataStore: OnboardingDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
        checkBiometricAvailability()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _state.update { it.copy(settings = settings, isLoading = false) }
            }
        }
    }
    
    private fun checkBiometricAvailability() {
        val availability = biometricAuthManager.isBiometricAvailable()
        _state.update { it.copy(biometricAvailability = availability) }
    }
    
    fun toggleBiometric(activity: FragmentActivity, enable: Boolean) {
        if (enable) {
            // Verify biometric before enabling
            biometricAuthManager.authenticate(
                activity = activity,
                title = "Enable Biometric Authentication",
                subtitle = "Verify your identity",
                description = "Confirm your fingerprint or face to enable biometric lock",
                onSuccess = {
                    viewModelScope.launch {
                        settingsRepository.setBiometricEnabled(true)
                    }
                },
                onError = { error ->
                    // Handle error, keep disabled
                },
                onFailed = {
                    // Authentication failed, keep disabled
                }
            )
        } else {
            viewModelScope.launch {
                settingsRepository.setBiometricEnabled(false)
            }
        }
    }
    
    fun setAutoLock(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoLockEnabled(enabled)
        }
    }
    
    fun setAutoLockTimeout(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setAutoLockTimeout(minutes)
        }
    }
    
    fun setUseSystemLockTimeout(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseSystemLockTimeout(enabled)
        }
    }
    
    fun setClipboardClear(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setClipboardClearSeconds(seconds)
        }
    }
    
    fun setShowPasswordStrength(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowPasswordStrength(show)
        }
    }
    
    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }
    
    fun resetOnboarding() {
        viewModelScope.launch {
            onboardingDataStore.setOnboardingCompleted(false)
            // User will need to restart app to see onboarding
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}

