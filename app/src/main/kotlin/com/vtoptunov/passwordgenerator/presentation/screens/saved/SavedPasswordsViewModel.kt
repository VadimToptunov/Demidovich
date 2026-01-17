package com.vtoptunov.passwordgenerator.presentation.screens.saved

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPasswordsViewModel @Inject constructor(
    private val passwordRepository: PasswordRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _state = MutableStateFlow(SavedPasswordsState())
    val state: StateFlow<SavedPasswordsState> = _state.asStateFlow()
    
    init {
        loadPasswords()
    }
    
    fun onEvent(event: SavedPasswordsEvent) {
        when (event) {
            is SavedPasswordsEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            
            is SavedPasswordsEvent.CategorySelected -> {
                _state.update { it.copy(selectedCategory = event.category) }
            }
            
            is SavedPasswordsEvent.DeletePassword -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = true,
                        passwordToDelete = event.password
                    )
                }
            }
            
            is SavedPasswordsEvent.CopyPassword -> {
                copyToClipboard(event.password)
            }
            
            SavedPasswordsEvent.ConfirmDelete -> {
                _state.value.passwordToDelete?.let { password ->
                    viewModelScope.launch {
                        passwordRepository.deletePassword(password)
                        _state.update {
                            it.copy(
                                showDeleteDialog = false,
                                passwordToDelete = null
                            )
                        }
                    }
                }
            }
            
            SavedPasswordsEvent.DismissDeleteDialog -> {
                _state.update {
                    it.copy(
                        showDeleteDialog = false,
                        passwordToDelete = null
                    )
                }
            }
            
            SavedPasswordsEvent.Refresh -> {
                loadPasswords()
            }
        }
    }
    
    private fun loadPasswords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            passwordRepository.getAllPasswords().collectLatest { passwords ->
                _state.update {
                    it.copy(
                        passwords = passwords,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("password", text)
        clipboard.setPrimaryClip(clip)
    }
}

