package com.vtoptunov.passwordgenerator.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.R
import com.vtoptunov.passwordgenerator.presentation.theme.*
import com.vtoptunov.passwordgenerator.util.SystemLockTimeoutUtil

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToTransfer: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val dimensions = LocalDimensions.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showRestartDialog by remember { mutableStateOf(false) }
    
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
                .padding(LocalDimensions.current.spacingMedium)
        ) {
            // Header
            SettingsHeader(onNavigateBack = onNavigateBack)
            
            Spacer(Modifier.height(24.dp))
            
            // Security Section
            SectionHeader(stringResource(R.string.security_section))
            
            SettingsCard {
                SettingsSwitch(
                    icon = Icons.Default.Fingerprint,
                    title = stringResource(R.string.biometric_authentication),
                    description = stringResource(R.string.biometric_description),
                    checked = state.settings.biometricEnabled,
                    enabled = state.biometricAvailability.isAvailable && activity != null,
                    onCheckedChange = { enabled ->
                        if (activity != null) {
                            viewModel.toggleBiometric(activity, enabled)
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
                    title = stringResource(R.string.auto_lock),
                    description = stringResource(R.string.auto_lock_description),
                    checked = state.settings.autoLockEnabled,
                    onCheckedChange = { viewModel.setAutoLock(it) }
                )
                
                if (state.settings.autoLockEnabled) {
                    Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Use System Timeout toggle
                    SettingsSwitch(
                        icon = Icons.Default.PhoneAndroid,
                        title = stringResource(R.string.use_system_timeout),
                        description = stringResource(
                            R.string.use_system_timeout_description,
                            SystemLockTimeoutUtil.getSystemScreenTimeoutFormatted(context)
                        ),
                        checked = state.settings.useSystemLockTimeout,
                        onCheckedChange = { viewModel.setUseSystemLockTimeout(it) }
                    )
                    
                    if (!state.settings.useSystemLockTimeout) {
                        Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                        
                        Column(modifier = Modifier.padding(horizontal = LocalDimensions.current.spacingMedium)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CyberBlue,
                                    modifier = Modifier.size(LocalDimensions.current.iconMedium)
                                )
                                Spacer(Modifier.width(LocalDimensions.current.spacingMedium))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.lock_timeout),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = stringResource(R.string.lock_after_minutes, state.settings.autoLockTimeoutMinutes),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(LocalDimensions.current.spacingSmall))
                            
                            // Timeout selector buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 40.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 3, 5, 10, 15).forEach { minutes ->
                                    OutlinedButton(
                                        onClick = { viewModel.setAutoLockTimeout(minutes) },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (state.settings.autoLockTimeoutMinutes == minutes) 
                                                CyberBlue.copy(alpha = 0.2f) 
                                            else 
                                                SurfaceDark,
                                            contentColor = if (state.settings.autoLockTimeoutMinutes == minutes) 
                                                CyberBlue 
                                            else 
                                                TextSecondary
                                        ),
                                        border = if (state.settings.autoLockTimeoutMinutes == minutes)
                                            androidx.compose.foundation.BorderStroke(1.dp, CyberBlue)
                                        else
                                            null,
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "${minutes}m",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(LocalDimensions.current.spacingMedium))
            
            // Privacy Section
            SectionHeader(stringResource(R.string.privacy_section))
            
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.ContentCopy,
                    title = stringResource(R.string.clipboard_timeout),
                    description = stringResource(R.string.clipboard_timeout_description, state.settings.clipboardClearSeconds)
                )
            }
            
            Spacer(Modifier.height(LocalDimensions.current.spacingMedium))
            
            // Data Section
            SectionHeader(stringResource(R.string.data_section))
            
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.QrCode2,
                    title = stringResource(R.string.transfer_passwords),
                    description = stringResource(R.string.transfer_passwords_description),
                    onClick = onNavigateToTransfer
                )
            }
            
            Spacer(Modifier.height(LocalDimensions.current.spacingMedium))
            
            // About Section
            SectionHeader(stringResource(R.string.about_section))
            
            SettingsCard {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.version),
                    description = stringResource(R.string.app_version)
                )
                
                Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsItem(
                    icon = Icons.Default.Code,
                    title = stringResource(R.string.open_source),
                    description = stringResource(R.string.open_source_description)
                )
                
                Divider(color = SurfaceMedium, modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsItem(
                    icon = Icons.Default.School,
                    title = stringResource(R.string.show_onboarding),
                    description = stringResource(R.string.show_onboarding_description),
                    onClick = { 
                        viewModel.resetOnboarding()
                        showRestartDialog = true
                    }
                )
            }
        }
        
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Restart dialog for onboarding reset
        if (showRestartDialog) {
            AlertDialog(
                onDismissRequest = { showRestartDialog = false },
                title = {
                    Text(
                        stringResource(R.string.restart_required),
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberBlue
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.restart_required_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            activity?.let {
                                it.finishAffinity()
                                it.startActivity(it.intent)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.restart_now), color = CyberBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartDialog = false }) {
                        Text(stringResource(R.string.later), color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark,
                iconContentColor = CyberBlue,
                titleContentColor = CyberBlue,
                textContentColor = TextPrimary
            )
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
            Icon(Icons.Default.ArrowBack, stringResource(R.string.back), tint = CyberBlue)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.settings).uppercase(),
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
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium),
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
            modifier = Modifier.size(LocalDimensions.current.iconMedium)
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
    description: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = CyberBlue,
            modifier = Modifier.size(LocalDimensions.current.iconMedium)
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
