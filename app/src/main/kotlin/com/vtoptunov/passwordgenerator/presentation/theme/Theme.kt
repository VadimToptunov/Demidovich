package com.vtoptunov.passwordgenerator.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CyberSafe Dark Theme
 * Professional cyberpunk-inspired dark theme
 * Safe for Google Play - no illegal/hacking connotations
 */
private val DarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    onPrimary = DeepSpace,
    primaryContainer = CyberBlueDark,
    onPrimaryContainer = CyberBlueLight,
    
    secondary = ElectricPurple,
    onSecondary = DeepSpace,
    secondaryContainer = ElectricPurpleDark,
    onSecondaryContainer = ElectricPurpleLight,
    
    tertiary = NeonGreen,
    onTertiary = DeepSpace,
    tertiaryContainer = NeonGreenDark,
    onTertiaryContainer = NeonGreen,
    
    background = DeepSpace,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMedium,
    onSurfaceVariant = TextSecondary,
    
    surfaceTint = CyberBlue,
    inverseSurface = TextPrimary,
    inverseOnSurface = DeepSpace,
    
    error = DangerRed,
    onError = TextPrimary,
    errorContainer = DangerRedDark,
    onErrorContainer = DangerRed,
    
    outline = TextTertiary,
    outlineVariant = TextDisabled,
    
    scrim = DeepSpace.copy(alpha = 0.8f)
)

@Composable
fun PasswordGeneratorTheme(
    darkTheme: Boolean = true, // Always dark for cyber aesthetic
    dynamicColor: Boolean = false,
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

