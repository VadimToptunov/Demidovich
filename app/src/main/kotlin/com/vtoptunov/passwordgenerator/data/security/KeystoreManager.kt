package com.vtoptunov.passwordgenerator.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
    
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
    
    private val securePrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    fun getDatabasePassphrase(): ByteArray {
        val stored = securePrefs.getString(KEY_DB_PASSPHRASE, null)
        
        return if (stored != null) {
            try {
                android.util.Base64.decode(stored, android.util.Base64.DEFAULT)
            } catch (e: Exception) {
                val newPassphrase = generateSecurePassphrase()
                securePrefs.edit()
                    .putString(KEY_DB_PASSPHRASE, android.util.Base64.encodeToString(newPassphrase, android.util.Base64.DEFAULT))
                    .apply()
                newPassphrase
            }
        } else {
            val newPassphrase = generateSecurePassphrase()
            securePrefs.edit()
                .putString(KEY_DB_PASSPHRASE, android.util.Base64.encodeToString(newPassphrase, android.util.Base64.DEFAULT))
                .apply()
            newPassphrase
        }
    }
    
    private fun generateSecurePassphrase(): ByteArray {
        // Note: We cannot export keys from AndroidKeyStore (they return null).
        // Instead, we generate a random passphrase secured by EncryptedSharedPreferences,
        // which itself is backed by the AndroidKeyStore MasterKey.
        return try {
            val random = java.security.SecureRandom()
            ByteArray(32).apply { random.nextBytes(this) }
        } catch (e: Exception) {
            // Fallback to timestamp-based seed if SecureRandom fails
            val fallbackRandom = java.util.Random(System.currentTimeMillis())
            ByteArray(32).apply { fallbackRandom.nextBytes(this) }
        }
    }
}

