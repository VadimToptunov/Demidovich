package com.vtoptunov.passwordgenerator.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
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

/**
 * CyberSafe Light Theme
 * Professional clean light theme for daytime use
 */
private val LightColorScheme = lightColorScheme(
    primary = CyberBlue,
    onPrimary = Color.White,
    primaryContainer = CyberBlueLight,
    onPrimaryContainer = CyberBlueDark,
    
    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = ElectricPurpleLight,
    onSecondaryContainer = ElectricPurpleDark,
    
    tertiary = NeonGreen,
    onTertiary = Color.White,
    tertiaryContainer = NeonGreenLight,
    onTertiaryContainer = NeonGreenDark,
    
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),
    
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF424242),
    
    surfaceTint = CyberBlue,
    inverseSurface = Color(0xFF2D2D2D),
    inverseOnSurface = Color(0xFFF0F0F0),
    
    error = DangerRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFCACACA),
    
    scrim = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun PasswordGeneratorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CyberTypography,
        content = content
    )
}

