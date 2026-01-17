package com.vtoptunov.passwordgenerator.presentation.screens.backup

import com.vtoptunov.passwordgenerator.domain.usecase.backup.BackupFile

data class BackupState(
    val backups: List<BackupFile> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingBackup: Boolean = false,
    val isRestoring: Boolean = false,
    val totalBackupSize: Long = 0L,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class BackupEvent {
    object CreateBackup : BackupEvent()
    data class RestoreBackup(val backupFile: BackupFile) : BackupEvent()
    data class DeleteBackup(val backupFile: BackupFile) : BackupEvent()
    data class ExportBackup(val backupFile: BackupFile) : BackupEvent()
    object ImportBackup : BackupEvent()
    object DismissError : BackupEvent()
    object DismissSuccess : BackupEvent()
}
