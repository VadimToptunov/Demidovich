package com.vtoptunov.passwordgenerator.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val BIOMETRIC_SETUP_SHOWN = booleanPreferencesKey("biometric_setup_shown")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setBiometricSetupShown(shown: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.BIOMETRIC_SETUP_SHOWN] = shown
        }
    }

    val isBiometricSetupShown: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.BIOMETRIC_SETUP_SHOWN] ?: false
    }
}

