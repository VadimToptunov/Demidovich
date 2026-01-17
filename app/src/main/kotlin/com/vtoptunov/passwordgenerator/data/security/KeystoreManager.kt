package com.vtoptunov.passwordgenerator.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeystoreManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val KEYSTORE_ALIAS = "password_db_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
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
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            
            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
                
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
                
                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }
            
            val secretKey = keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            secretKey.encoded
            
        } catch (e: Exception) {
            val random = java.security.SecureRandom()
            ByteArray(32).apply { random.nextBytes(this) }
        }
    }
}

