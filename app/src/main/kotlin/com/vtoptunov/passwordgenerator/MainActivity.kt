package com.vtoptunov.passwordgenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.data.datastore.OnboardingDataStore
import com.vtoptunov.passwordgenerator.data.repository.SettingsRepository
import com.vtoptunov.passwordgenerator.domain.model.ThemeMode
import com.vtoptunov.passwordgenerator.presentation.navigation.AppNavigation
import com.vtoptunov.passwordgenerator.presentation.screens.onboarding.OnboardingScreen
import com.vtoptunov.passwordgenerator.presentation.screens.splash.EnhancedMatrixSplashScreen
import com.vtoptunov.passwordgenerator.presentation.theme.PasswordGeneratorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var onboardingDataStore: OnboardingDataStore
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
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
                
                when {
                    showSplash -> {
                        EnhancedMatrixSplashScreen(
                            onTimeout = { 
                                showSplash = false
                                // Show onboarding only if not completed
                                showOnboarding = !isOnboardingCompleted
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

