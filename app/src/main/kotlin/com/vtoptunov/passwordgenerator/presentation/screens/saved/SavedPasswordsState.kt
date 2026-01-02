package com.vtoptunov.passwordgenerator.presentation.screens.saved

import com.vtoptunov.passwordgenerator.domain.model.Password

data class SavedPasswordsState(
    val passwords: List<Password> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val showDeleteDialog: Boolean = false,
    val passwordToDelete: Password? = null
)

sealed class SavedPasswordsEvent {
    data class SearchQueryChanged(val query: String) : SavedPasswordsEvent()
    data class CategorySelected(val category: String?) : SavedPasswordsEvent()
    data class DeletePassword(val password: Password) : SavedPasswordsEvent()
    data class CopyPassword(val password: String) : SavedPasswordsEvent()
    object ConfirmDelete : SavedPasswordsEvent()
    object DismissDeleteDialog : SavedPasswordsEvent()
    object Refresh : SavedPasswordsEvent()
}

