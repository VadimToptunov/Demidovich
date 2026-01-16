package com.vtoptunov.passwordgenerator.presentation.screens.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as? FragmentActivity
    
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
                .padding(16.dp)
        ) {
            // Header
            SettingsHeader(onNavigateBack = onNavigateBack)
            
            Spacer(Modifier.height(24.dp))
            
            // Security Section
            SectionHeader("🔒 Security")
            
            SettingsCard {
                SettingsSwitch(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Authentication",
                    description = "Use fingerprint or face to unlock",
                    checked = state.settings.biometricEnabled,
                    enabled = state.biometricAvailability.isAvailable,
                    onCheckedChange = { enabled ->
                        context?.let {
                            viewModel.toggleBiometric(it, enabled)
                        }
                    }
                )
                
                if (!state.biometricAvailability.isAvailable) {
                    Text(
                        text = state.biometricAvailability.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        modifier = Modifier.padding(start = 56.dp, top = 4.dp, bottom = 8.dp)
                    )
                }
                
                Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsSwitch(
                    icon = Icons.Default.Lock,
                    title = "Auto-Lock",
                    description = "Lock app after inactivity",
                    checked = state.settings.autoLockEnabled,
                    onCheckedChange = { viewModel.setAutoLock(it) }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Privacy Section
            SectionHeader("🛡️ Privacy")
            
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.ContentCopy,
                    title = "Clipboard Timeout",
                    description = "Clear clipboard after ${state.settings.clipboardClearSeconds}s"
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Display Section
            SectionHeader("🎨 Display")
            
            SettingsCard {
                ThemeModePicker(
                    currentTheme = state.settings.themeMode,
                    onThemeChanged = { viewModel.setThemeMode(it) }
                )
                
                Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsSwitch(
                    icon = Icons.Default.Visibility,
                    title = "Password Strength Indicator",
                    description = "Show strength when generating",
                    checked = state.settings.showPasswordStrength,
                    onCheckedChange = { viewModel.setShowPasswordStrength(it) }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            // About Section
            SectionHeader("ℹ️ About")
            
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    description = "3.0.0 (Generator v2)"
                )
                
                Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "Open Source",
                    description = "Built with Kotlin + Jetpack Compose"
                )
            }
        }
    }
}

@Composable
fun SettingsHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, "Back", tint = CyberBlue)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "SETTINGS",
                style = MaterialTheme.typography.headlineSmall,
                color = CyberBlue,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(Modifier.width(48.dp)) // Balance layout
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) CyberBlue else TextDisabled,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = if (enabled) TextPrimary else TextDisabled,
                fontWeight = FontWeight.Medium
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberBlue,
                checkedTrackColor = CyberBlue.copy(alpha = 0.5f),
                uncheckedThumbColor = TextDisabled,
                uncheckedTrackColor = SurfaceMedium
            )
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = CyberBlue,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ThemeModePicker(
    currentTheme: com.vtoptunov.passwordgenerator.domain.model.ThemeMode,
    onThemeChanged: (com.vtoptunov.passwordgenerator.domain.model.ThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Palette,
                contentDescription = null,
                tint = CyberBlue,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Theme",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Choose your preferred theme",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeChip(
                label = "☀️ Light",
                selected = currentTheme == com.vtoptunov.passwordgenerator.domain.model.ThemeMode.LIGHT,
                onClick = { onThemeChanged(com.vtoptunov.passwordgenerator.domain.model.ThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                label = "🌙 Dark",
                selected = currentTheme == com.vtoptunov.passwordgenerator.domain.model.ThemeMode.DARK,
                onClick = { onThemeChanged(com.vtoptunov.passwordgenerator.domain.model.ThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                label = "📱 System",
                selected = currentTheme == com.vtoptunov.passwordgenerator.domain.model.ThemeMode.SYSTEM,
                onClick = { onThemeChanged(com.vtoptunov.passwordgenerator.domain.model.ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) CyberBlue.copy(alpha = 0.2f) else SurfaceMedium,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, CyberBlue) else null,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) CyberBlue else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
