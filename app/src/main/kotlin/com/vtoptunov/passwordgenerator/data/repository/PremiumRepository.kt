package com.vtoptunov.passwordgenerator.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.vtoptunov.passwordgenerator.domain.model.PremiumStatus
import com.vtoptunov.passwordgenerator.domain.model.PurchaseProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing premium status and consumable purchases
 */
@Singleton
class PremiumRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val SUBSCRIPTION_TYPE = stringPreferencesKey("subscription_type")
        val EXPIRY_DATE = longPreferencesKey("expiry_date")
        val EXTRA_ATTEMPTS = intPreferencesKey("extra_attempts")
        val XP_BOOSTER_ACTIVE = booleanPreferencesKey("xp_booster_active")
        val XP_BOOSTER_EXPIRY = longPreferencesKey("xp_booster_expiry")
    }

    val premiumStatus: Flow<PremiumStatus> = dataStore.data.map { prefs ->
        val xpBoosterExpiry = prefs[Keys.XP_BOOSTER_EXPIRY]
        val isXpBoosterActive = prefs[Keys.XP_BOOSTER_ACTIVE] == true &&
                xpBoosterExpiry != null &&
                xpBoosterExpiry > System.currentTimeMillis()

        PremiumStatus(
            isPremium = prefs[Keys.IS_PREMIUM] ?: false,
            subscriptionType = prefs[Keys.SUBSCRIPTION_TYPE]?.let { type ->
                try {
                    PurchaseProduct.valueOf(type)
                } catch (e: Exception) {
                    null
                }
            },
            expiryDate = prefs[Keys.EXPIRY_DATE],
            extraAttempts = prefs[Keys.EXTRA_ATTEMPTS] ?: 0,
            xpBoosterActive = isXpBoosterActive,
            xpBoosterExpiry = if (isXpBoosterActive) xpBoosterExpiry else null
        )
    }

    suspend fun setPremiumStatus(
        isPremium: Boolean,
        subscriptionType: PurchaseProduct? = null,
        expiryDate: Long? = null
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_PREMIUM] = isPremium
            if (subscriptionType != null) {
                prefs[Keys.SUBSCRIPTION_TYPE] = subscriptionType.name
            } else {
                prefs.remove(Keys.SUBSCRIPTION_TYPE)
            }
            if (expiryDate != null) {
                prefs[Keys.EXPIRY_DATE] = expiryDate
            } else {
                prefs.remove(Keys.EXPIRY_DATE)
            }
        }
    }

    suspend fun addExtraAttempts(count: Int) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.EXTRA_ATTEMPTS] ?: 0
            prefs[Keys.EXTRA_ATTEMPTS] = current + count
        }
    }

    suspend fun consumeExtraAttempt() {
        dataStore.edit { prefs ->
            val current = prefs[Keys.EXTRA_ATTEMPTS] ?: 0
            if (current > 0) {
                prefs[Keys.EXTRA_ATTEMPTS] = current - 1
            }
        }
    }

    suspend fun activateXpBooster(durationMillis: Long = 24 * 60 * 60 * 1000) {
        dataStore.edit { prefs ->
            prefs[Keys.XP_BOOSTER_ACTIVE] = true
            prefs[Keys.XP_BOOSTER_EXPIRY] = System.currentTimeMillis() + durationMillis
        }
    }

    suspend fun deactivateXpBooster() {
        dataStore.edit { prefs ->
            prefs[Keys.XP_BOOSTER_ACTIVE] = false
            prefs.remove(Keys.XP_BOOSTER_EXPIRY)
        }
    }

    suspend fun clearPremiumStatus() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.IS_PREMIUM)
            prefs.remove(Keys.SUBSCRIPTION_TYPE)
            prefs.remove(Keys.EXPIRY_DATE)
        }
    }
}

