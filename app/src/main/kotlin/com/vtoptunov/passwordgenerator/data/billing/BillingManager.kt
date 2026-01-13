package com.vtoptunov.passwordgenerator.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.vtoptunov.passwordgenerator.domain.model.Purchase
import com.vtoptunov.passwordgenerator.domain.model.PurchaseProduct
import com.vtoptunov.passwordgenerator.domain.model.PurchaseState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Play Billing operations
 * Handles subscriptions and in-app purchases
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()

    private val _connectionState = MutableStateFlow(BillingConnectionState.DISCONNECTED)
    val connectionState: StateFlow<BillingConnectionState> = _connectionState.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    private var productDetails: Map<String, ProductDetails> = emptyMap()

    init {
        startConnection()
    }

    /**
     * Start connection to Google Play Billing
     */
    private fun startConnection() {
        _connectionState.value = BillingConnectionState.CONNECTING
        
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _connectionState.value = BillingConnectionState.CONNECTED
                    Log.d(TAG, "Billing client connected")
                    
                    // Query purchases and products
                    coroutineScope.launch {
                        queryPurchases()
                        queryProductDetails()
                    }
                } else {
                    _connectionState.value = BillingConnectionState.ERROR
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                _connectionState.value = BillingConnectionState.DISCONNECTED
                Log.w(TAG, "Billing service disconnected")
                // Try to reconnect
                startConnection()
            }
        })
    }

    /**
     * Query available product details from Google Play
     */
    private suspend fun queryProductDetails() {
        // Query subscriptions
        val subscriptionProducts = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PurchaseProduct.PREMIUM_MONTHLY.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PurchaseProduct.PREMIUM_YEARLY.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val subscriptionParams = QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptionProducts)
            .build()

        billingClient.queryProductDetailsAsync(subscriptionParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { productDetails ->
                    this.productDetails = this.productDetails + (productDetails.productId to productDetails)
                }
                Log.d(TAG, "Queried ${productDetailsList.size} subscription products")
            }
        }

        // Query in-app products
        val inAppProducts = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PurchaseProduct.EXTRA_ATTEMPTS_PACK.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PurchaseProduct.XP_BOOSTER.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val inAppParams = QueryProductDetailsParams.newBuilder()
            .setProductList(inAppProducts)
            .build()

        billingClient.queryProductDetailsAsync(inAppParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetailsList.forEach { productDetails ->
                    this.productDetails = this.productDetails + (productDetails.productId to productDetails)
                }
                Log.d(TAG, "Queried ${productDetailsList.size} in-app products")
            }
        }
    }

    /**
     * Query existing purchases
     */
    suspend fun queryPurchases() {
        // Query subscriptions
        val subsResult = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        // Query in-app purchases
        val inAppResult = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val allPurchases = (subsResult.purchasesList + inAppResult.purchasesList)
            .map { it.toDomainModel() }

        _purchases.value = allPurchases
        Log.d(TAG, "Queried ${allPurchases.size} purchases")
    }

    /**
     * Launch purchase flow
     */
    fun launchPurchaseFlow(
        activity: Activity,
        product: PurchaseProduct
    ) {
        val productDetails = this.productDetails[product.productId]
        if (productDetails == null) {
            Log.e(TAG, "Product details not found for ${product.productId}")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .apply {
                    // For subscriptions, set offer token
                    if (productDetails.productType == BillingClient.ProductType.SUBS) {
                        productDetails.subscriptionOfferDetails?.firstOrNull()?.let { offer ->
                            setOfferToken(offer.offerToken)
                        }
                    }
                }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Failed to launch billing flow: ${billingResult.debugMessage}")
        }
    }

    /**
     * Acknowledge purchase (required for subscriptions and non-consumable products)
     */
    suspend fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        val result = billingClient.acknowledgePurchase(params)
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Purchase acknowledged")
        } else {
            Log.e(TAG, "Failed to acknowledge purchase: ${result.debugMessage}")
        }
    }

    /**
     * Consume purchase (for consumable items like extra attempts)
     */
    suspend fun consumePurchase(purchaseToken: String) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        val result = billingClient.consumePurchase(params)
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Purchase consumed")
        } else {
            Log.e(TAG, "Failed to consume purchase: ${result.billingResult.debugMessage}")
        }
    }

    /**
     * Get product price
     */
    fun getProductPrice(product: PurchaseProduct): String? {
        val details = productDetails[product.productId] ?: return null
        
        return when (details.productType) {
            BillingClient.ProductType.SUBS -> {
                details.subscriptionOfferDetails?.firstOrNull()
                    ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                    ?.formattedPrice
            }
            BillingClient.ProductType.INAPP -> {
                details.oneTimePurchaseOfferDetails?.formattedPrice
            }
            else -> null
        }
    }

    /**
     * Check if user has active premium subscription
     */
    fun hasPremiumSubscription(): Boolean {
        return _purchases.value.any { purchase ->
            (purchase.productId == PurchaseProduct.PREMIUM_MONTHLY.productId ||
             purchase.productId == PurchaseProduct.PREMIUM_YEARLY.productId) &&
            purchase.state == PurchaseState.PURCHASED
        }
    }

    /**
     * Called when purchases are updated
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<com.android.billingclient.api.Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "Purchases updated: ${purchases.size} items")
            
            coroutineScope.launch {
                // Process each purchase
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
                        // Acknowledge non-consumable purchases
                        if (!purchase.isAcknowledged) {
                            if (isConsumable(purchase)) {
                                consumePurchase(purchase.purchaseToken)
                            } else {
                                acknowledgePurchase(purchase.purchaseToken)
                            }
                        }
                    }
                }
                
                // Refresh purchases list
                queryPurchases()
            }
        } else {
            Log.e(TAG, "Purchase update failed: ${billingResult.debugMessage}")
        }
    }

    /**
     * Check if product is consumable
     */
    private fun isConsumable(purchase: com.android.billingclient.api.Purchase): Boolean {
        return purchase.products.any { productId ->
            productId == PurchaseProduct.EXTRA_ATTEMPTS_PACK.productId ||
            productId == PurchaseProduct.XP_BOOSTER.productId
        }
    }

    /**
     * Clean up resources
     */
    fun endConnection() {
        if (billingClient.isReady) {
            billingClient.endConnection()
            _connectionState.value = BillingConnectionState.DISCONNECTED
        }
    }

    companion object {
        private const val TAG = "BillingManager"
    }
}

/**
 * Convert Google Play Purchase to domain model
 */
private fun com.android.billingclient.api.Purchase.toDomainModel(): Purchase {
    return Purchase(
        productId = products.firstOrNull() ?: "",
        orderId = orderId ?: "",
        purchaseToken = purchaseToken,
        purchaseTime = purchaseTime,
        state = when (purchaseState) {
            com.android.billingclient.api.Purchase.PurchaseState.PURCHASED -> PurchaseState.PURCHASED
            com.android.billingclient.api.Purchase.PurchaseState.PENDING -> PurchaseState.PENDING
            else -> PurchaseState.CANCELED
        },
        isAcknowledged = isAcknowledged
    )
}

/**
 * Billing connection state
 */
enum class BillingConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

