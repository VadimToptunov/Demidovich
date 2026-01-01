package com.demidovich.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CyberSafe Dark Theme
 * A cyberpunk-inspired dark theme with neon accents
 * Safe for Google Play - professional but edgy
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors - Cyber Blue
    primary = CyberBlue,
    onPrimary = DeepSpace,
    primaryContainer = CyberBlueDark,
    onPrimaryContainer = CyberBlueLight,
    
    // Secondary colors - Electric Purple
    secondary = ElectricPurple,
    onSecondary = DeepSpace,
    secondaryContainer = ElectricPurpleDark,
    onSecondaryContainer = ElectricPurpleLight,
    
    // Tertiary colors - Neon Green
    tertiary = NeonGreen,
    onTertiary = DeepSpace,
    tertiaryContainer = NeonGreenDark,
    onTertiaryContainer = NeonGreen,
    
    // Background
    background = DeepSpace,
    onBackground = TextPrimary,
    
    // Surface
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMedium,
    onSurfaceVariant = TextSecondary,
    
    // Surface tints
    surfaceTint = CyberBlue,
    inverseSurface = TextPrimary,
    inverseOnSurface = DeepSpace,
    
    // Error colors
    error = DangerRed,
    onError = TextPrimary,
    errorContainer = DangerRedDark,
    onErrorContainer = DangerRed,
    
    // Outline
    outline = TextTertiary,
    outlineVariant = TextDisabled,
    
    // Scrim
    scrim = DeepSpace.copy(alpha = 0.8f)
)

@Composable
fun CyberSafeTheme(
    darkTheme: Boolean = true, // Always dark for cyber aesthetic
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CyberTypography,
        content = content
    )
}

