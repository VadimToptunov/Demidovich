package com.vtoptunov.passwordgenerator.presentation.screens.premium

import com.vtoptunov.passwordgenerator.domain.model.PremiumStatus
import com.vtoptunov.passwordgenerator.domain.model.PurchaseProduct

data class PremiumState(
    val premiumStatus: PremiumStatus = PremiumStatus(),
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val availableProducts: List<ProductInfo> = emptyList(),
    val error: String? = null,
    val showSuccessMessage: Boolean = false
)

data class ProductInfo(
    val product: PurchaseProduct,
    val price: String?,
    val isOwned: Boolean
)

sealed class PremiumEvent {
    data class PurchaseProduct(val product: PurchaseProduct) : PremiumEvent()
    object RestorePurchases : PremiumEvent()
    object DismissError : PremiumEvent()
    object DismissSuccess : PremiumEvent()
}

