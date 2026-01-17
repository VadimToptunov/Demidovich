package com.vtoptunov.passwordgenerator.presentation.screens.transfer
import com.vtoptunov.passwordgenerator.R

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun TransferScreen(
    navController: NavController,
    viewModel: TransferViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (state.mode == TransferMode.SELECT) {
                        navController.popBackStack()
                    } else {
                        viewModel.onEvent(TransferEvent.Cancel)
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                Text(
                    text = "Transfer Passwords",
                    style = MaterialTheme.typography.headlineSmall,
                    color = CyberBlue
                )
            }
        }
        
        // Content based on mode
        AnimatedContent(
            targetState = state.mode,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            }, label = "transfer_mode"
        ) { mode ->
            when (mode) {
                TransferMode.SELECT -> SelectModeScreen(
                    onExport = { viewModel.onEvent(TransferEvent.SelectExport) },
                    onImport = { viewModel.onEvent(TransferEvent.SelectImport) }
                )
                TransferMode.EXPORT_SELECT -> ExportSelectScreen(
                    passwords = state.passwords,
                    selectedPasswords = state.selectedPasswords,
                    onToggleSelection = { id ->
                        viewModel.onEvent(TransferEvent.TogglePasswordSelection(id))
                    },
                    onSelectAll = { viewModel.onEvent(TransferEvent.SelectAllPasswords) },
                    onDeselectAll = { viewModel.onEvent(TransferEvent.DeselectAllPasswords) },
                    onGenerate = { viewModel.onEvent(TransferEvent.GenerateQRCode) },
                    isProcessing = state.isProcessing
                )
                TransferMode.EXPORT_QR -> ExportQRScreen(
                    qrBitmap = state.qrCodeBitmap,
                    onDone = { viewModel.onEvent(TransferEvent.Cancel) }
                )
                TransferMode.IMPORT_SCAN -> ImportScanScreen(
                    showCamera = state.showCamera,
                    onStartScan = { viewModel.onEvent(TransferEvent.StartScanning) },
                    onQRScanned = { data ->
                        viewModel.onEvent(TransferEvent.OnQRScanned(data))
                    },
                    isProcessing = state.isProcessing
                )
                TransferMode.IMPORT_REVIEW -> ImportReviewScreen(
                    passwords = state.importedPasswords,
                    onConfirm = { viewModel.onEvent(TransferEvent.ConfirmImport) },
                    onCancel = { viewModel.onEvent(TransferEvent.Cancel) },
                    isProcessing = state.isProcessing
                )
            }
        }
        
        // Error Snackbar
        state.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                action = {
                    TextButton(onClick = { viewModel.onEvent(TransferEvent.DismissError) }) {
                        Text(stringResource(R.string.dismiss), color = TextPrimary)
                    }
                },
                containerColor = DangerRed
            ) {
                Text(error, color = TextPrimary)
            }
        }
        
        // Success Message
        if (state.success) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                if (state.mode == TransferMode.SELECT) {
                    navController.popBackStack()
                }
            }
        }
    }
}

@Composable
fun SelectModeScreen(
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.choose_transfer_mode),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        TransferModeCard(
            title = "Export Passwords",
            description = "Generate QR code to transfer passwords to another device",
            icon = Icons.Default.QrCode2,
            iconTint = NeonGreen,
            onClick = onExport
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        TransferModeCard(
            title = "Import Passwords",
            description = "Scan QR code to import passwords from another device",
            icon = Icons.Default.QrCodeScanner,
            iconTint = CyberBlue,
            onClick = onImport
        )
    }
}

@Composable
fun TransferModeCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(2.dp, iconTint.copy(alpha = 0.5f), RoundedCornerShape(LocalDimensions.current.cardCornerRadius)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(LocalDimensions.current.spacingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(LocalDimensions.current.iconExtraLarge)
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.spacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spacingExtraSmall))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ExportSelectScreen(
    passwords: List<Password>,
    selectedPasswords: Set<String>,
    onToggleSelection: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onGenerate: () -> Unit,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Selection Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedPasswords.size} selected",
                style = MaterialTheme.typography.titleMedium,
                color = ElectricPurple,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)) {
                TextButton(onClick = onSelectAll) {
                    Text("Select All", color = CyberBlue)
                }
                TextButton(onClick = onDeselectAll) {
                    Text("Deselect All", color = TextSecondary)
                }
            }
        }
        
        // Password List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            items(passwords) { password ->
                PasswordSelectItem(
                    password = password,
                    isSelected = password.id in selectedPasswords,
                    onToggle = { onToggleSelection(password.id) }
                )
            }
        }
        
        // Generate Button
        Button(
            onClick = onGenerate,
            enabled = selectedPasswords.isNotEmpty() && !isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = DeepSpace, modifier = Modifier.size(LocalDimensions.current.iconMedium))
            } else {
                Icon(Icons.Default.QrCode2, null, tint = DeepSpace)
                Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                Text("Generate QR Code", color = DeepSpace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PasswordSelectItem(
    password: Password,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonGreen else TextSecondary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(LocalDimensions.current.spacingSmall)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CardBackground.copy(alpha = 0.8f) else CardBackground
        ),
        shape = RoundedCornerShape(LocalDimensions.current.spacingSmall)
    ) {
        Row(
            modifier = Modifier.padding(LocalDimensions.current.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonGreen,
                    uncheckedColor = TextSecondary
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = password.password,
                    style = PasswordTextStyle.copy(fontSize = 14.sp),
                    color = if (isSelected) NeonGreen else TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spacingExtraSmall))
                Text(
                    text = password.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ExportQRScreen(
    qrBitmap: Bitmap?,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Scan this QR Code",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        qrBitmap?.let { bitmap ->
            Card(
                modifier = Modifier
                    .size(300.dp)
                    .padding(LocalDimensions.current.spacingMedium),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize().padding(LocalDimensions.current.spacingSmall)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingLarge))
        
        Text(
            text = "Use another device to scan this code and import the passwords",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
        ) {
            Text("Done", color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ImportScanScreen(
    showCamera: Boolean,
    onStartScan: () -> Unit,
    onQRScanned: (String) -> Unit,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showCamera) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan",
                tint = CyberBlue,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingLarge))
            
            Text(
                text = stringResource(R.string.scan_qr_code),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
            
            Text(
                text = "Position the QR code within the camera frame to import passwords",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onStartScan,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = TextPrimary, modifier = Modifier.size(LocalDimensions.current.iconMedium))
                } else {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                    Text("Start Scanning", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Camera preview for QR scanning
            // Note: Full CameraX implementation deferred to keep codebase focused
            // Users can use manual JSON import as alternative
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.camera),
                            modifier = Modifier.size(64.dp),
                            tint = TextSecondary
                        )
                        Text(
                            "Camera Scanner",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            "Point camera at QR code",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImportReviewScreen(
    passwords: List<Password>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Review Imported Passwords",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium)
        )
        
        Text(
            text = "${passwords.size} passwords found",
            style = MaterialTheme.typography.titleMedium,
            color = ElectricPurple,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            items(passwords) { password ->
                PasswordReviewItem(password = password)
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            OutlinedButton(
                onClick = onCancel,
                enabled = !isProcessing,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Text(stringResource(R.string.cancel))
            }
            
            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = DeepSpace, modifier = Modifier.size(LocalDimensions.current.iconMedium))
                } else {
                    Icon(Icons.Default.Check, null, tint = DeepSpace)
                    Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                    Text(stringResource(R.string.import_action), color = DeepSpace, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PasswordReviewItem(password: Password) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(LocalDimensions.current.spacingSmall)
    ) {
        Column(modifier = Modifier.padding(LocalDimensions.current.spacingSmall)) {
            Text(
                text = password.password,
                style = PasswordTextStyle.copy(fontSize = 14.sp),
                color = CyberBlue,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingExtraSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = password.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                if (password.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = DangerRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

