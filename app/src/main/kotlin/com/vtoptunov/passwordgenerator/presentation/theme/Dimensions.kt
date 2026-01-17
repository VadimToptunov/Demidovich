package com.vtoptunov.passwordgenerator.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window size classes based on Material Design 3 guidelines
 * https://m3.material.io/foundations/layout/applying-layout/window-size-classes
 */
enum class WindowSize {
    COMPACT,    // Phone portrait: < 600dp
    MEDIUM,     // Phone landscape / Small tablet: 600dp - 840dp
    EXPANDED    // Tablet / Desktop: > 840dp
}

/**
 * Responsive dimensions that adapt to screen size
 */
data class Dimensions(
    val windowSize: WindowSize,
    
    // Spacing
    val spacingExtraSmall: Dp,
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,
    val spacingExtraLarge: Dp,
    
    // Icon sizes
    val iconSmall: Dp,
    val iconMedium: Dp,
    val iconLarge: Dp,
    val iconExtraLarge: Dp,
    
    // Card/Component sizes
    val cardElevation: Dp,
    val cardCornerRadius: Dp,
    val buttonHeight: Dp,
    
    // Content width constraints
    val maxContentWidth: Dp,
    
    // Grid columns
    val gridColumns: Int
)

/**
 * Compact dimensions for phones in portrait mode
 */
private val CompactDimensions = Dimensions(
    windowSize = WindowSize.COMPACT,
    
    // Spacing
    spacingExtraSmall = 4.dp,
    spacingSmall = 8.dp,
    spacingMedium = 16.dp,
    spacingLarge = 24.dp,
    spacingExtraLarge = 32.dp,
    
    // Icon sizes
    iconSmall = 16.dp,
    iconMedium = 24.dp,
    iconLarge = 32.dp,
    iconExtraLarge = 48.dp,
    
    // Card/Component sizes
    cardElevation = 4.dp,
    cardCornerRadius = 12.dp,
    buttonHeight = 56.dp,
    
    // Content constraints
    maxContentWidth = 600.dp,
    
    // Grid
    gridColumns = 1
)

/**
 * Medium dimensions for tablets/landscape phones
 */
private val MediumDimensions = Dimensions(
    windowSize = WindowSize.MEDIUM,
    
    // Spacing - slightly larger
    spacingExtraSmall = 6.dp,
    spacingSmall = 12.dp,
    spacingMedium = 20.dp,
    spacingLarge = 32.dp,
    spacingExtraLarge = 48.dp,
    
    // Icon sizes - larger
    iconSmall = 20.dp,
    iconMedium = 28.dp,
    iconLarge = 40.dp,
    iconExtraLarge = 64.dp,
    
    // Card/Component sizes
    cardElevation = 6.dp,
    cardCornerRadius = 16.dp,
    buttonHeight = 64.dp,
    
    // Content constraints
    maxContentWidth = 840.dp,
    
    // Grid - 2 columns
    gridColumns = 2
)

/**
 * Expanded dimensions for large tablets/desktops
 */
private val ExpandedDimensions = Dimensions(
    windowSize = WindowSize.EXPANDED,
    
    // Spacing - much larger
    spacingExtraSmall = 8.dp,
    spacingSmall = 16.dp,
    spacingMedium = 24.dp,
    spacingLarge = 40.dp,
    spacingExtraLarge = 64.dp,
    
    // Icon sizes - largest
    iconSmall = 24.dp,
    iconMedium = 32.dp,
    iconLarge = 48.dp,
    iconExtraLarge = 80.dp,
    
    // Card/Component sizes
    cardElevation = 8.dp,
    cardCornerRadius = 20.dp,
    buttonHeight = 72.dp,
    
    // Content constraints
    maxContentWidth = 1200.dp,
    
    // Grid - 3 columns
    gridColumns = 3
)

/**
 * CompositionLocal for providing dimensions throughout the app
 */
val LocalDimensions = compositionLocalOf { CompactDimensions }

/**
 * Determine window size based on screen width
 */
@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    
    return remember(screenWidthDp) {
        when {
            screenWidthDp < 600.dp -> WindowSize.COMPACT
            screenWidthDp < 840.dp -> WindowSize.MEDIUM
            else -> WindowSize.EXPANDED
        }
    }
}

/**
 * Get appropriate dimensions for current window size
 */
@Composable
fun rememberDimensions(): Dimensions {
    val windowSize = rememberWindowSize()
    
    return remember(windowSize) {
        when (windowSize) {
            WindowSize.COMPACT -> CompactDimensions
            WindowSize.MEDIUM -> MediumDimensions
            WindowSize.EXPANDED -> ExpandedDimensions
        }
    }
}

/**
 * Provider for responsive dimensions
 */
@Composable
fun ProvideDimensions(content: @Composable () -> Unit) {
    val dimensions = rememberDimensions()
    
    CompositionLocalProvider(
        LocalDimensions provides dimensions,
        content = content
    )
}

/**
 * Extension property for easy access to dimensions
 */
val Dp.Companion.current: Dimensions
    @Composable
    get() = LocalDimensions.current
