package com.vtoptunov.passwordgenerator.presentation.screens.premium
import com.vtoptunov.passwordgenerator.R

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.domain.model.PurchaseProduct
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LocalDimensions.current.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = CyberBlue
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "PREMIUM",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarningOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "UPGRADE",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                }
            }

            // Premium Status Card
            if (state.premiumStatus.isPremium) {
                PremiumStatusCard(state.premiumStatus)
            }

            // Benefits Section
            BenefitsSection()

            // Subscription Plans
            Text(
                "Subscription Plans",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            state.availableProducts
                .filter {
                    it.product == PurchaseProduct.PREMIUM_MONTHLY ||
                    it.product == PurchaseProduct.PREMIUM_YEARLY
                }
                .forEach { productInfo ->
                    ProductCard(
                        productInfo = productInfo,
                        isPurchasing = state.isPurchasing,
                        onPurchase = {
                            activity?.let { act ->
                                viewModel.purchaseProduct(act, productInfo.product)
                            }
                        }
                    )
                }

            // In-App Purchases
            Text(
                "In-App Purchases",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            state.availableProducts
                .filter {
                    it.product == PurchaseProduct.EXTRA_ATTEMPTS_PACK ||
                    it.product == PurchaseProduct.XP_BOOSTER
                }
                .forEach { productInfo ->
                    ProductCard(
                        productInfo = productInfo,
                        isPurchasing = state.isPurchasing,
                        onPurchase = {
                            activity?.let { act ->
                                viewModel.purchaseProduct(act, productInfo.product)
                            }
                        }
                    )
                }

            // Restore Purchases Button
            OutlinedButton(
                onClick = { viewModel.onEvent(PremiumEvent.RestorePurchases) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyberBlue
                ),
                enabled = !state.isLoading
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                Text("Restore Purchases")
            }

            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        }

        // Loading Indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepSpace.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonGreen)
            }
        }

        // Error Snackbar
        AnimatedVisibility(
            visible = state.error != null,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Snackbar(
                modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                action = {
                    TextButton(onClick = { viewModel.onEvent(PremiumEvent.DismissError) }) {
                        Text("Dismiss", color = TextPrimary)
                    }
                },
                containerColor = DangerRed,
                contentColor = TextPrimary
            ) {
                Text(state.error ?: "")
            }
        }

        // Success Snackbar
        AnimatedVisibility(
            visible = state.showSuccessMessage,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Snackbar(
                modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                action = {
                    TextButton(onClick = { viewModel.onEvent(PremiumEvent.DismissSuccess) }) {
                        Text(stringResource(R.string.ok), color = DeepSpace)
                    }
                },
                containerColor = NeonGreen,
                contentColor = DeepSpace
            ) {
                Text("Purchase successful!")
            }
        }
    }
}

@Composable
fun PremiumStatusCard(premiumStatus: com.vtoptunov.passwordgenerator.domain.model.PremiumStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "PREMIUM ACTIVE",
                    style = MaterialTheme.typography.titleLarge,
                    color = WarningOrange,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(LocalDimensions.current.iconLarge)
                )
            }

            premiumStatus.subscriptionType?.let { type ->
                Text(
                    "Plan: ${type.displayName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            if (premiumStatus.extraAttempts > 0) {
                Text(
                    "Extra Attempts: ${premiumStatus.extraAttempts}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyberBlue
                )
            }

            if (premiumStatus.xpBoosterActive) {
                Text(
                    "2x XP Booster Active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricPurple
                )
            }
        }
    }
}

@Composable
fun BenefitsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            Text(
                "Premium Benefits",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            BenefitItem(
                icon = Icons.Default.Block,
                text = "Remove all ads",
                color = DangerRed
            )
            BenefitItem(
                icon = Icons.Default.AllInclusive,
                text = "Unlimited game attempts",
                color = NeonGreen
            )
            BenefitItem(
                icon = Icons.Default.Star,
                text = "Exclusive security tips",
                color = WarningOrange
            )
            BenefitItem(
                icon = Icons.Default.Speed,
                text = "Priority support",
                color = CyberBlue
            )
        }
    }
}

@Composable
fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(LocalDimensions.current.iconMedium)
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
    }
}

@Composable
fun ProductCard(
    productInfo: ProductInfo,
    isPurchasing: Boolean,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (productInfo.product == PurchaseProduct.PREMIUM_YEARLY) {
                ElectricPurple.copy(alpha = 0.2f)
            } else {
                CardBackground
            }
        ),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        productInfo.product.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        productInfo.product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                if (productInfo.product == PurchaseProduct.PREMIUM_YEARLY) {
                    Surface(
                        color = WarningOrange,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "BEST VALUE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepSpace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    productInfo.price ?: "Loading...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )

                if (productInfo.isOwned) {
                    Button(
                        onClick = { },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Owned")
                    }
                } else {
                    Button(
                        onClick = onPurchase,
                        enabled = !isPurchasing && productInfo.price != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberBlue
                        )
                    ) {
                        if (isPurchasing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(LocalDimensions.current.iconSmall),
                                color = TextPrimary
                            )
                        } else {
                            Text("Purchase", color = DeepSpace)
                        }
                    }
                }
            }
        }
    }
}

