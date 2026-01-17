package com.vtoptunov.passwordgenerator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.data.datastore.OnboardingDataStore
import com.vtoptunov.passwordgenerator.data.repository.SettingsRepository
import com.vtoptunov.passwordgenerator.data.security.BiometricAuthManager
import com.vtoptunov.passwordgenerator.domain.model.ThemeMode
import com.vtoptunov.passwordgenerator.presentation.navigation.AppNavigation
import com.vtoptunov.passwordgenerator.presentation.screens.lock.BiometricLockScreen
import com.vtoptunov.passwordgenerator.presentation.screens.onboarding.OnboardingScreen
import com.vtoptunov.passwordgenerator.presentation.screens.splash.EnhancedMatrixSplashScreen
import com.vtoptunov.passwordgenerator.presentation.theme.PasswordGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    @Inject
    lateinit var onboardingDataStore: OnboardingDataStore
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var biometricAuthManager: BiometricAuthManager
    
    private var appInBackground by mutableStateOf(false)
    private var lastPauseTime = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val settings by settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = com.vtoptunov.passwordgenerator.domain.model.AppSettings()
            )
            
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme
            }
            
            PasswordGeneratorTheme(darkTheme = darkTheme) {
                var showSplash by remember { mutableStateOf(true) }
                val isOnboardingCompleted by onboardingDataStore.isOnboardingCompleted
                    .collectAsStateWithLifecycle(initialValue = false)
                var showOnboarding by remember { mutableStateOf(false) }
                
                // Check if app should be locked
                val shouldLock = remember(appInBackground, settings.biometricEnabled, settings.autoLockEnabled) {
                    if (!settings.biometricEnabled) return@remember false
                    if (!appInBackground) return@remember false
                    
                    // If auto-lock is disabled, lock immediately on background
                    if (!settings.autoLockEnabled) return@remember true
                    
                    // If auto-lock is enabled, check timeout
                    val inactiveTimeMs = System.currentTimeMillis() - lastPauseTime
                    val timeoutMs = settings.autoLockTimeoutMinutes * 60 * 1000L
                    inactiveTimeMs >= timeoutMs
                }
                
                var isLocked by remember { mutableStateOf(shouldLock) }
                
                // Update lock state when shouldLock changes
                LaunchedEffect(shouldLock) {
                    if (shouldLock) {
                        isLocked = true
                    }
                }
                
                when {
                    showSplash -> {
                        EnhancedMatrixSplashScreen(
                            onTimeout = { 
                                showSplash = false
                                // Show onboarding only if not completed
                                showOnboarding = !isOnboardingCompleted
                                // Lock if biometric is enabled (first launch)
                                if (settings.biometricEnabled) {
                                    isLocked = true
                                }
                            }
                        )
                    }
                    showOnboarding -> {
                        OnboardingScreen(
                            onOnboardingComplete = { 
                                showOnboarding = false 
                            }
                        )
                    }
                    isLocked -> {
                        BiometricLockScreen(
                            activity = this@MainActivity,
                            biometricAuthManager = biometricAuthManager,
                            onAuthenticated = {
                                isLocked = false
                                appInBackground = false
                            }
                        )
                    }
                    else -> {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Mark app as going to background and record time
        lastPauseTime = System.currentTimeMillis()
        appInBackground = true
    }
    
    override fun onResume() {
        super.onResume()
        // Will trigger recomposition and lock screen if needed
    }
}

