package com.vtoptunov.passwordgenerator.presentation.screens.transfer

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import com.vtoptunov.passwordgenerator.domain.usecase.transfer.ExportPasswordsUseCase
import com.vtoptunov.passwordgenerator.domain.usecase.transfer.ImportPasswordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    private val exportPasswordsUseCase: ExportPasswordsUseCase,
    private val importPasswordsUseCase: ImportPasswordsUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(TransferState())
    val state: StateFlow<TransferState> = _state.asStateFlow()
    
    init {
        loadPasswords()
    }
    
    fun onEvent(event: TransferEvent) {
        when (event) {
            TransferEvent.SelectExport -> selectExport()
            TransferEvent.SelectImport -> selectImport()
            is TransferEvent.TogglePasswordSelection -> togglePasswordSelection(event.passwordId)
            TransferEvent.SelectAllPasswords -> selectAllPasswords()
            TransferEvent.DeselectAllPasswords -> deselectAllPasswords()
            TransferEvent.GenerateQRCode -> generateQRCode()
            TransferEvent.StartScanning -> startScanning()
            is TransferEvent.OnQRScanned -> onQRScanned(event.data)
            TransferEvent.ConfirmImport -> confirmImport()
            TransferEvent.Cancel -> cancel()
            TransferEvent.DismissError -> dismissError()
        }
    }
    
    private fun loadPasswords() {
        viewModelScope.launch {
            val passwords = passwordRepository.getAllPasswords().first()
            _state.update { it.copy(passwords = passwords) }
        }
    }
    
    private fun selectExport() {
        _state.update {
            it.copy(
                mode = TransferMode.EXPORT_SELECT,
                error = null
            )
        }
    }
    
    private fun selectImport() {
        _state.update {
            it.copy(
                mode = TransferMode.IMPORT_SCAN,
                error = null
            )
        }
    }
    
    private fun togglePasswordSelection(passwordId: String) {
        _state.update { currentState ->
            val newSelection = if (passwordId in currentState.selectedPasswords) {
                currentState.selectedPasswords - passwordId
            } else {
                currentState.selectedPasswords + passwordId
            }
            currentState.copy(selectedPasswords = newSelection)
        }
    }
    
    private fun selectAllPasswords() {
        _state.update { currentState ->
            currentState.copy(
                selectedPasswords = currentState.passwords.map { it.id }.toSet()
            )
        }
    }
    
    private fun deselectAllPasswords() {
        _state.update { it.copy(selectedPasswords = emptySet()) }
    }
    
    private fun generateQRCode() {
        _state.update { it.copy(isProcessing = true, error = null) }
        
        viewModelScope.launch {
            try {
                val selectedIds = _state.value.selectedPasswords
                if (selectedIds.isEmpty()) {
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            error = "Please select at least one password to export"
                        )
                    }
                    return@launch
                }
                
                val passwordsToExport = _state.value.passwords.filter { it.id in selectedIds }
                val export = exportPasswordsUseCase(passwordsToExport)
                val qrBitmap = exportPasswordsUseCase.generateQRCode(export)
                
                if (qrBitmap != null) {
                    _state.update {
                        it.copy(
                            mode = TransferMode.EXPORT_QR,
                            qrCodeBitmap = qrBitmap,
                            isProcessing = false,
                            success = true
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isProcessing = false,
                            error = "Failed to generate QR code. Try selecting fewer passwords."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isProcessing = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
    
    private fun startScanning() {
        _state.update {
            it.copy(
                showCamera = true,
                error = null
            )
        }
    }
    
    private fun onQRScanned(data: String) {
        _state.update { it.copy(isProcessing = true, showCamera = false, error = null) }
        
        viewModelScope.launch {
            try {
                val result = importPasswordsUseCase.decodeFromBase64(data)
                
                result.fold(
                    onSuccess = { passwords ->
                        _state.update {
                            it.copy(
                                mode = TransferMode.IMPORT_REVIEW,
                                importedPasswords = passwords,
                                isProcessing = false
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isProcessing = false,
                                error = error.message ?: "Failed to import passwords"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isProcessing = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
    
    private fun confirmImport() {
        _state.update { it.copy(isProcessing = true, error = null) }
        
        viewModelScope.launch {
            try {
                val passwordsToImport = _state.value.importedPasswords
                
                passwordsToImport.forEach { password ->
                    passwordRepository.insertPassword(password)
                }
                
                _state.update {
                    it.copy(
                        mode = TransferMode.SELECT,
                        importedPasswords = emptyList(),
                        isProcessing = false,
                        success = true
                    )
                }
                
                // Reload passwords
                loadPasswords()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isProcessing = false,
                        error = e.message ?: "Failed to save passwords"
                    )
                }
            }
        }
    }
    
    private fun cancel() {
        _state.update {
            TransferState(passwords = it.passwords)
        }
    }
    
    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
}

