package com.vtoptunov.passwordgenerator.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vtoptunov.passwordgenerator.domain.model.AppSettings
import com.vtoptunov.passwordgenerator.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private object PreferencesKeys {
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val AUTO_LOCK_ENABLED = booleanPreferencesKey("auto_lock_enabled")
        val AUTO_LOCK_TIMEOUT = intPreferencesKey("auto_lock_timeout")
        val CLIPBOARD_CLEAR_SECONDS = intPreferencesKey("clipboard_clear_seconds")
        val SHOW_PASSWORD_STRENGTH = booleanPreferencesKey("show_password_strength")
        val ENABLE_ANALYTICS = booleanPreferencesKey("enable_analytics")
        val DEFAULT_PASSWORD_LENGTH = intPreferencesKey("default_password_length")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
    
    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeString = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeString)
            } catch (e: IllegalArgumentException) {
                ThemeMode.DARK
            }
            
            AppSettings(
                biometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false,
                autoLockEnabled = preferences[PreferencesKeys.AUTO_LOCK_ENABLED] ?: false,
                autoLockTimeoutMinutes = preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] ?: 5,
                clipboardClearSeconds = preferences[PreferencesKeys.CLIPBOARD_CLEAR_SECONDS] ?: 30,
                showPasswordStrength = preferences[PreferencesKeys.SHOW_PASSWORD_STRENGTH] ?: true,
                enableAnalytics = preferences[PreferencesKeys.ENABLE_ANALYTICS] ?: false,
                defaultPasswordLength = preferences[PreferencesKeys.DEFAULT_PASSWORD_LENGTH] ?: 16,
                themeMode = themeMode
            )
        }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }
    
    suspend fun setAutoLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOCK_ENABLED] = enabled
        }
    }
    
    suspend fun setAutoLockTimeout(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] = minutes
        }
    }
    
    suspend fun setClipboardClearSeconds(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CLIPBOARD_CLEAR_SECONDS] = seconds
        }
    }
    
    suspend fun setShowPasswordStrength(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_PASSWORD_STRENGTH] = show
        }
    }
    
    suspend fun setEnableAnalytics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_ANALYTICS] = enabled
        }
    }
    
    suspend fun setDefaultPasswordLength(length: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PASSWORD_LENGTH] = length
        }
    }
    
    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }
}

