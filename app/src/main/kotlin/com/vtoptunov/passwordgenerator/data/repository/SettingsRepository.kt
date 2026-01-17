package com.vtoptunov.passwordgenerator.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.vtoptunov.passwordgenerator.domain.model.AppSettings
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import com.vtoptunov.passwordgenerator.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    private object PreferencesKeys {
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val AUTO_LOCK_ENABLED = booleanPreferencesKey("auto_lock_enabled")
        val AUTO_LOCK_TIMEOUT = intPreferencesKey("auto_lock_timeout")
        val USE_SYSTEM_LOCK_TIMEOUT = booleanPreferencesKey("use_system_lock_timeout")
        val CLIPBOARD_CLEAR_SECONDS = intPreferencesKey("clipboard_clear_seconds")
        val SHOW_PASSWORD_STRENGTH = booleanPreferencesKey("show_password_strength")
        val ENABLE_ANALYTICS = booleanPreferencesKey("enable_analytics")
        val DEFAULT_PASSWORD_LENGTH = intPreferencesKey("default_password_length")
        val DEFAULT_PASSWORD_STYLE = stringPreferencesKey("default_password_style")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
    
    val settings: Flow<AppSettings> = dataStore.data
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
            
            val passwordStyleString = preferences[PreferencesKeys.DEFAULT_PASSWORD_STYLE] ?: "Random"
            val passwordStyle = when (passwordStyleString) {
                "Random" -> PasswordStyle.Random
                "XKCD" -> PasswordStyle.XKCD
                "Phonetic" -> PasswordStyle.Phonetic
                "Story" -> PasswordStyle.Story
                "Pronounceable" -> PasswordStyle.Pronounceable
                else -> PasswordStyle.Random
            }
            
            AppSettings(
                biometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false,
                autoLockEnabled = preferences[PreferencesKeys.AUTO_LOCK_ENABLED] ?: false,
                autoLockTimeoutMinutes = preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] ?: 5,
                useSystemLockTimeout = preferences[PreferencesKeys.USE_SYSTEM_LOCK_TIMEOUT] ?: false,
                clipboardClearSeconds = preferences[PreferencesKeys.CLIPBOARD_CLEAR_SECONDS] ?: 30,
                showPasswordStrength = preferences[PreferencesKeys.SHOW_PASSWORD_STRENGTH] ?: true,
                enableAnalytics = preferences[PreferencesKeys.ENABLE_ANALYTICS] ?: false,
                defaultPasswordLength = preferences[PreferencesKeys.DEFAULT_PASSWORD_LENGTH] ?: 16,
                defaultPasswordStyle = passwordStyle,
                themeMode = themeMode
            )
        }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }
    
    suspend fun setAutoLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOCK_ENABLED] = enabled
        }
    }
    
    suspend fun setAutoLockTimeout(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] = minutes
        }
    }
    
    suspend fun setUseSystemLockTimeout(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_SYSTEM_LOCK_TIMEOUT] = enabled
        }
    }
    
    suspend fun setClipboardClearSeconds(seconds: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CLIPBOARD_CLEAR_SECONDS] = seconds
        }
    }
    
    suspend fun setShowPasswordStrength(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_PASSWORD_STRENGTH] = show
        }
    }
    
    suspend fun setEnableAnalytics(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_ANALYTICS] = enabled
        }
    }
    
    suspend fun setDefaultPasswordLength(length: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PASSWORD_LENGTH] = length
        }
    }
    
    suspend fun setDefaultPasswordStyle(style: PasswordStyle) {
        val styleName = when (style) {
            PasswordStyle.Random -> "Random"
            PasswordStyle.XKCD -> "XKCD"
            PasswordStyle.Phonetic -> "Phonetic"
            PasswordStyle.Story -> "Story"
            PasswordStyle.Pronounceable -> "Pronounceable"
        }
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PASSWORD_STYLE] = styleName
        }
    }
    
    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }
}

