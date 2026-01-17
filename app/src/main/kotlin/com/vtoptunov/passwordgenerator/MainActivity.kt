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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
import com.vtoptunov.passwordgenerator.util.SystemLockTimeoutUtil
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
    
    // Synchronized state for lifecycle callbacks
    @Volatile
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
                
                // Manage app background state within Compose (fixes race condition)
                var appInBackground by remember { mutableStateOf(false) }
                var lastPauseTimeState by remember { mutableStateOf(0L) }
                
                // Sync with lifecycle
                DisposableEffect(Unit) {
                    val lifecycleObserver = object : DefaultLifecycleObserver {
                        override fun onPause(owner: LifecycleOwner) {
                            lastPauseTime = System.currentTimeMillis()
                            lastPauseTimeState = lastPauseTime
                            appInBackground = true
                        }
                        
                        override fun onResume(owner: LifecycleOwner) {
                            // Will trigger recomposition
                        }
                    }
                    lifecycle.addObserver(lifecycleObserver)
                    onDispose {
                        lifecycle.removeObserver(lifecycleObserver)
                    }
                }
                
                // Check if app should be locked
                val shouldLock = remember(appInBackground, settings.biometricEnabled, settings.autoLockEnabled, settings.useSystemLockTimeout, lastPauseTimeState) {
                    if (!settings.biometricEnabled) return@remember false
                    if (!appInBackground) return@remember false
                    
                    // If auto-lock is disabled, lock immediately on background
                    if (!settings.autoLockEnabled) return@remember true
                    
                    // If auto-lock is enabled, check timeout
                    val inactiveTimeMs = System.currentTimeMillis() - lastPauseTimeState
                    val timeoutMinutes = if (settings.useSystemLockTimeout) {
                        SystemLockTimeoutUtil.getSystemScreenTimeoutMinutes(this@MainActivity)
                    } else {
                        settings.autoLockTimeoutMinutes
                    }
                    val timeoutMs = timeoutMinutes * 60 * 1000L
                    inactiveTimeMs >= timeoutMs
                }
                
                var isLocked by remember { mutableStateOf(false) }
                
                // Sync isLocked with shouldLock
                LaunchedEffect(shouldLock) {
                    isLocked = shouldLock
                }
                
                // Security-first: Check lock before any other UI
                when {
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
}
