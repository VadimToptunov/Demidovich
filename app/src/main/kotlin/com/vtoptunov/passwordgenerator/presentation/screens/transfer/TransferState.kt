package com.vtoptunov.passwordgenerator.presentation.screens.transfer

import android.graphics.Bitmap
import com.vtoptunov.passwordgenerator.domain.model.Password

data class TransferState(
    val mode: TransferMode = TransferMode.SELECT,
    val passwords: List<Password> = emptyList(),
    val selectedPasswords: Set<String> = emptySet(),
    val qrCodeBitmap: Bitmap? = null,
    val scannedData: String? = null,
    val importedPasswords: List<Password> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val showCamera: Boolean = false
)

enum class TransferMode {
    SELECT,      // Select export or import
    EXPORT_SELECT, // Select passwords to export
    EXPORT_QR,   // Show QR code for export
    IMPORT_SCAN, // Scan QR code for import
    IMPORT_REVIEW // Review imported passwords
}

sealed class TransferEvent {
    object SelectExport : TransferEvent()
    object SelectImport : TransferEvent()
    data class TogglePasswordSelection(val passwordId: String) : TransferEvent()
    object SelectAllPasswords : TransferEvent()
    object DeselectAllPasswords : TransferEvent()
    object GenerateQRCode : TransferEvent()
    object StartScanning : TransferEvent()
    data class OnQRScanned(val data: String) : TransferEvent()
    object ConfirmImport : TransferEvent()
    object Cancel : TransferEvent()
    object DismissError : TransferEvent()
}

