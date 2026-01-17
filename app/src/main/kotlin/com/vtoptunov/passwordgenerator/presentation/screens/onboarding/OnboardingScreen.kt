package com.vtoptunov.passwordgenerator.presentation.screens.onboarding
import com.vtoptunov.passwordgenerator.R
import androidx.compose.ui.res.stringResource

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.domain.model.OnboardingIcon
import com.vtoptunov.passwordgenerator.presentation.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val dimensions = LocalDimensions.current

    LaunchedEffect(Unit) {
        // Listen for onboarding completion
        // In a real implementation, we'd observe the DataStore flow
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                )
            )
    ) {
        if (state.pages.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LocalDimensions.current.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Skip button - only show on non-last pages
                if (pagerState.currentPage < state.pages.size - 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { 
                            viewModel.onEvent(OnboardingEvent.Skip)
                            onOnboardingComplete()
                        }) {
                            Text("Skip", color = TextSecondary)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Horizontal Pager with swipe support
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    state.pages.getOrNull(page)?.let { pageData ->
                        OnboardingPageContent(page = pageData)
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Page indicators
                PageIndicators(
                    totalPages = state.pages.size,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Only show Get Started button on last page
                if (pagerState.currentPage == state.pages.size - 1) {
                    Button(
                        onClick = {
                            viewModel.onEvent(OnboardingEvent.Complete)
                            onOnboardingComplete()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen
                        ),
                        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Get Started",
                            color = DeepSpace,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(LocalDimensions.current.iconMedium),
                            tint = DeepSpace
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(56.dp))
                }
            }
        }

        // Biometric setup dialog
        if (state.showBiometricPrompt) {
            BiometricSetupDialog(
                onEnable = { 
                    viewModel.onEvent(OnboardingEvent.EnableBiometric)
                    onOnboardingComplete()
                },
                onSkip = { 
                    viewModel.onEvent(OnboardingEvent.SkipBiometric)
                    onOnboardingComplete()
                }
            )
        }
    }
}

@Composable
fun OnboardingPageContent(page: com.vtoptunov.passwordgenerator.domain.model.OnboardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Icon
        val icon = getIconForPage(page.icon)
        val infiniteTransition = rememberInfiniteTransition(label = "iconAnimation")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "iconScale"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricPurple.copy(alpha = 0.3f), DeepSpace)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                tint = when (page.icon) {
                    OnboardingIcon.WELCOME -> CyberBlue
                    OnboardingIcon.SECURITY -> NeonGreen
                    OnboardingIcon.ACADEMY -> WarningOrange
                    OnboardingIcon.BIOMETRIC -> ElectricPurple
                    OnboardingIcon.READY -> CyberBlue
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5
        )
    }
}

@Composable
fun PageIndicators(totalPages: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isSelected) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) NeonGreen else TextTertiary
                    )
                    .animateContentSize()
            )
        }
    }
}

@Composable
fun BiometricSetupDialog(
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSkip,
        icon = {
            Icon(
                Icons.Default.Fingerprint,
                contentDescription = null,
                tint = ElectricPurple,
                modifier = Modifier.size(LocalDimensions.current.iconExtraLarge)
            )
        },
        title = {
            Text(stringResource(R.string.enable_biometric_lock), fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Protect your passwords with your fingerprint or face. You can change this later in Settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onEnable,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Text("Enable", color = DeepSpace, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text("Skip", color = TextSecondary)
            }
        },
        containerColor = CardBackground,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary
    )
}

@Composable
private fun getIconForPage(icon: OnboardingIcon): ImageVector {
    return when (icon) {
        OnboardingIcon.WELCOME -> Icons.Default.EmojiEmotions
        OnboardingIcon.SECURITY -> Icons.Default.Shield
        OnboardingIcon.ACADEMY -> Icons.Default.School
        OnboardingIcon.BIOMETRIC -> Icons.Default.Fingerprint
        OnboardingIcon.READY -> Icons.Default.CheckCircle
    }
}

