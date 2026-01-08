package com.vtoptunov.passwordgenerator.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PasswordExport(
    val passwords: List<PasswordExportEntry>,
    val exportDate: Long = System.currentTimeMillis(),
    val version: String = "1.0",
    val checksum: String = ""
)

@Serializable
data class PasswordExportEntry(
    val id: String,
    val password: String,
    val category: String,
    val createdAt: Long,
    val lastModified: Long,
    val isFavorite: Boolean,
    val note: String?
)

enum class TransferType {
    QR_CODE,
    FILE,
    CLOUD
}

data class TransferState(
    val type: TransferType,
    val data: String? = null,
    val isProcessing: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

