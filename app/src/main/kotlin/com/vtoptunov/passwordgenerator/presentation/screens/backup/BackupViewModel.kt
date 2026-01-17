package com.vtoptunov.passwordgenerator.presentation.screens.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.usecase.backup.BackupFile
import com.vtoptunov.passwordgenerator.domain.usecase.backup.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(BackupState())
    val state: StateFlow<BackupState> = _state.asStateFlow()
    
    init {
        loadBackups()
    }
    
    fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.CreateBackup -> createBackup()
            is BackupEvent.RestoreBackup -> restoreBackup(event.backupFile)
            is BackupEvent.DeleteBackup -> deleteBackup(event.backupFile)
            is BackupEvent.ExportBackup -> {} // Handled by UI with file picker
            is BackupEvent.ImportBackup -> {} // Handled by UI with file picker
            BackupEvent.DismissError -> _state.update { it.copy(error = null) }
            BackupEvent.DismissSuccess -> _state.update { it.copy(successMessage = null) }
        }
    }
    
    fun loadBackups() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val backups = backupManager.getBackups()
                val totalSize = backupManager.getTotalBackupSize()
                _state.update {
                    it.copy(
                        backups = backups,
                        totalBackupSize = totalSize,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load backups: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun createBackup() {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingBackup = true) }
            try {
                val result = backupManager.createAutoBackup()
                if (result.isSuccess) {
                    loadBackups()
                    _state.update {
                        it.copy(
                            successMessage = "Backup created successfully! 💾",
                            isCreatingBackup = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            error = "Failed to create backup: ${result.exceptionOrNull()?.message}",
                            isCreatingBackup = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error creating backup: ${e.message}",
                        isCreatingBackup = false
                    )
                }
            }
        }
    }
    
    private fun restoreBackup(backupFile: BackupFile) {
        viewModelScope.launch {
            _state.update { it.copy(isRestoring = true) }
            try {
                val result = backupManager.restoreFromBackup(backupFile.file)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    _state.update {
                        it.copy(
                            successMessage = "Restored $count passwords! ✅",
                            isRestoring = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            error = "Failed to restore: ${result.exceptionOrNull()?.message}",
                            isRestoring = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error restoring backup: ${e.message}",
                        isRestoring = false
                    )
                }
            }
        }
    }
    
    private fun deleteBackup(backupFile: BackupFile) {
        viewModelScope.launch {
            try {
                val success = backupManager.deleteBackup(backupFile)
                if (success) {
                    loadBackups()
                    _state.update {
                        it.copy(successMessage = "Backup deleted")
                    }
                } else {
                    _state.update {
                        it.copy(error = "Failed to delete backup")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error deleting backup: ${e.message}")
                }
            }
        }
    }
    
    fun exportBackup(backupFile: BackupFile, uri: Uri) {
        viewModelScope.launch {
            try {
                val result = backupManager.exportBackupToExternal(backupFile.file, uri)
                if (result.isSuccess) {
                    _state.update {
                        it.copy(successMessage = "Backup exported successfully! 📤")
                    }
                } else {
                    _state.update {
                        it.copy(error = "Failed to export: ${result.exceptionOrNull()?.message}")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error exporting backup: ${e.message}")
                }
            }
        }
    }
    
    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isRestoring = true) }
            try {
                val result = backupManager.importBackupFromExternal(uri)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    loadBackups() // Refresh backups list
                    _state.update {
                        it.copy(
                            successMessage = "Imported $count passwords! 📥",
                            isRestoring = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            error = "Failed to import: ${result.exceptionOrNull()?.message}",
                            isRestoring = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error importing backup: ${e.message}",
                        isRestoring = false
                    )
                }
            }
        }
    }
}
