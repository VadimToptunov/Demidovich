package com.vtoptunov.passwordgenerator.presentation.screens.premium

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.billing.BillingManager
import com.vtoptunov.passwordgenerator.data.repository.PremiumRepository
import com.vtoptunov.passwordgenerator.domain.model.PurchaseProduct
import com.vtoptunov.passwordgenerator.domain.model.PurchaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val premiumRepository: PremiumRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state.asStateFlow()

    init {
        loadPremiumStatus()
        loadAvailableProducts()
        observePurchases()
    }

    fun onEvent(event: PremiumEvent) {
        when (event) {
            is PremiumEvent.PurchaseProduct -> {
                // Purchase will be initiated from UI with Activity context
            }
            PremiumEvent.RestorePurchases -> restorePurchases()
            PremiumEvent.DismissError -> _state.update { it.copy(error = null) }
            PremiumEvent.DismissSuccess -> _state.update { it.copy(showSuccessMessage = false) }
        }
    }

    fun purchaseProduct(activity: Activity, product: PurchaseProduct) {
        _state.update { it.copy(isPurchasing = true, error = null) }
        try {
            billingManager.launchPurchaseFlow(activity, product)
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isPurchasing = false,
                    error = "Failed to start purchase: ${e.message}"
                )
            }
        }
    }

    private fun loadPremiumStatus() {
        viewModelScope.launch {
            premiumRepository.premiumStatus.collect { status ->
                _state.update { it.copy(premiumStatus = status) }
            }
        }
    }

    private fun loadAvailableProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val ownedPurchases = billingManager.purchases.value
                .filter { it.state == PurchaseState.PURCHASED }
                .map { it.productId }

            val products = PurchaseProduct.entries.map { product ->
                ProductInfo(
                    product = product,
                    price = billingManager.getProductPrice(product),
                    isOwned = ownedPurchases.contains(product.productId)
                )
            }

            _state.update {
                it.copy(
                    availableProducts = products,
                    isLoading = false
                )
            }
        }
    }

    private fun observePurchases() {
        viewModelScope.launch {
            billingManager.purchases.collect { purchases ->
                _state.update { it.copy(isPurchasing = false) }

                // Update premium status based on purchases
                val premiumPurchase = purchases.firstOrNull { purchase ->
                    (purchase.productId == PurchaseProduct.PREMIUM_MONTHLY.productId ||
                     purchase.productId == PurchaseProduct.PREMIUM_YEARLY.productId) &&
                    purchase.state == PurchaseState.PURCHASED
                }

                if (premiumPurchase != null) {
                    val subscriptionType = when (premiumPurchase.productId) {
                        PurchaseProduct.PREMIUM_MONTHLY.productId -> PurchaseProduct.PREMIUM_MONTHLY
                        PurchaseProduct.PREMIUM_YEARLY.productId -> PurchaseProduct.PREMIUM_YEARLY
                        else -> null
                    }

                    premiumRepository.setPremiumStatus(
                        isPremium = true,
                        subscriptionType = subscriptionType,
                        expiryDate = null // Will be set by backend validation
                    )

                    _state.update { it.copy(showSuccessMessage = true) }
                }

                // Process consumable purchases
                purchases.forEach { purchase ->
                    if (purchase.state == PurchaseState.PURCHASED) {
                        when (purchase.productId) {
                            PurchaseProduct.EXTRA_ATTEMPTS_PACK.productId -> {
                                premiumRepository.addExtraAttempts(5)
                                viewModelScope.launch {
                                    billingManager.consumePurchase(purchase.purchaseToken)
                                }
                            }
                            PurchaseProduct.XP_BOOSTER.productId -> {
                                premiumRepository.activateXpBooster()
                                viewModelScope.launch {
                                    billingManager.consumePurchase(purchase.purchaseToken)
                                }
                            }
                        }
                    }
                }

                // Refresh available products
                loadAvailableProducts()
            }
        }
    }

    private fun restorePurchases() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                billingManager.queryPurchases()
                _state.update {
                    it.copy(
                        isLoading = false,
                        showSuccessMessage = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to restore purchases: ${e.message}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Don't end connection here, let it stay active for app lifetime
    }
}

