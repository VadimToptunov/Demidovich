package com.vtoptunov.passwordgenerator.domain.model

/**
 * Represents a purchasable product in the app
 */
enum class PurchaseProduct(
    val productId: String,
    val displayName: String,
    val description: String
) {
    PREMIUM_MONTHLY(
        productId = "premium_monthly",
        displayName = "Premium Monthly",
        description = "Remove ads, unlimited game attempts, exclusive tips"
    ),
    PREMIUM_YEARLY(
        productId = "premium_yearly",
        displayName = "Premium Yearly",
        description = "Remove ads, unlimited game attempts, exclusive tips (Best Value!)"
    ),
    EXTRA_ATTEMPTS_PACK(
        productId = "extra_attempts_5",
        displayName = "5 Extra Attempts",
        description = "Get 5 extra attempts for Academy games"
    ),
    XP_BOOSTER(
        productId = "xp_booster_2x",
        displayName = "2x XP Booster (24h)",
        description = "Double XP gain for 24 hours"
    )
}

/**
 * Purchase status
 */
enum class PurchaseState {
    PENDING,
    PURCHASED,
    CANCELED,
    REFUNDED,
    EXPIRED
}

/**
 * Represents a user purchase
 */
data class Purchase(
    val productId: String,
    val orderId: String,
    val purchaseToken: String,
    val purchaseTime: Long,
    val state: PurchaseState,
    val isAcknowledged: Boolean
)

/**
 * Premium subscription status
 */
data class PremiumStatus(
    val isPremium: Boolean = false,
    val subscriptionType: PurchaseProduct? = null,
    val expiryDate: Long? = null,
    val extraAttempts: Int = 0,
    val xpBoosterActive: Boolean = false,
    val xpBoosterExpiry: Long? = null
)

/**
 * Billing events
 */
sealed class BillingEvent {
    object LoadProducts : BillingEvent()
    data class PurchaseProduct(val product: PurchaseProduct) : BillingEvent()
    object RestorePurchases : BillingEvent()
    object ConsumeExtraAttempt : BillingEvent()
}

/**
 * Billing state
 */
data class BillingState(
    val isLoading: Boolean = false,
    val premiumStatus: PremiumStatus = PremiumStatus(),
    val availableProducts: List<PurchaseProduct> = emptyList(),
    val error: String? = null,
    val showPurchaseDialog: Boolean = false,
    val selectedProduct: PurchaseProduct? = null
)

