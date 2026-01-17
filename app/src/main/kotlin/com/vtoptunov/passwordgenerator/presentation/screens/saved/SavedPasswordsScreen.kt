package com.vtoptunov.passwordgenerator.presentation.screens.saved
import com.vtoptunov.passwordgenerator.R

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.model.PasswordCategory
import com.vtoptunov.passwordgenerator.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPasswordsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SavedPasswordsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    
    val filteredPasswords = remember(state.passwords, state.searchQuery, state.selectedCategory) {
        state.passwords.filter { password ->
            val matchesSearch = if (state.searchQuery.isBlank()) {
                true
            } else {
                password.password.contains(state.searchQuery, ignoreCase = true) ||
                password.note?.contains(state.searchQuery, ignoreCase = true) == true
            }
            
            val matchesCategory = if (state.selectedCategory == null) {
                true
            } else {
                password.category.name == state.selectedCategory
            }
            
            matchesSearch && matchesCategory
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.saved_passwords_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpace,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = CyberBlue
                )
            )
        },
        containerColor = DeepSpace
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(SavedPasswordsEvent.SearchQueryChanged(it)) },
                modifier = Modifier.padding(LocalDimensions.current.spacingMedium)
            )
            
            CategoryFilter(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onEvent(SavedPasswordsEvent.CategorySelected(it)) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyberBlue)
                    }
                }
                
                filteredPasswords.isEmpty() && state.passwords.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                filteredPasswords.isEmpty() -> {
                    NoResultsState(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(LocalDimensions.current.spacingMedium),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredPasswords,
                            key = { it.id }
                        ) { password ->
                            PasswordItem(
                                password = password,
                                onCopy = { viewModel.onEvent(SavedPasswordsEvent.CopyPassword(it)) },
                                onDelete = { viewModel.onEvent(SavedPasswordsEvent.DeletePassword(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    if (state.showDeleteDialog) {
        DeleteConfirmationDialog(
            password = state.passwordToDelete,
            onConfirm = { viewModel.onEvent(SavedPasswordsEvent.ConfirmDelete) },
            onDismiss = { viewModel.onEvent(SavedPasswordsEvent.DismissDeleteDialog) }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.placeholder_search)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear))
                }
            }
        },
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            focusedBorderColor = CyberBlue,
            unfocusedBorderColor = SurfaceMedium,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = CyberBlue
        )
    )
}

@Composable
private fun CategoryFilter(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
    ) {
        item {
            FilterChip(
                label = stringResource(R.string.all),
                isSelected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }
        
        items(PasswordCategory.values()) { category ->
            FilterChip(
                label = category.displayName,
                isSelected = selectedCategory == category.name,
                onClick = { onCategorySelected(category.name) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) CyberBlue else SurfaceDark,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) DeepSpace else TextSecondary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PasswordItem(
    password: Password,
    onCopy: (String) -> Unit,
    onDelete: (Password) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceMedium, RoundedCornerShape(LocalDimensions.current.cardCornerRadius))
            .clip(RoundedCornerShape(LocalDimensions.current.cardCornerRadius))
            .clickable { isExpanded = !isExpanded },
        color = SurfaceDark,
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (showPassword) password.password else "•".repeat(12),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showPassword) TextPrimary else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                    ) {
                        CategoryBadge(password.category)
                        
                        Text(
                            text = formatDate(password.createdAt),
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { showPassword = !showPassword }
                    ) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(if (showPassword) R.string.hide else R.string.show),
                            tint = TextSecondary
                        )
                    }
                    
                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = stringResource(if (isExpanded) R.string.collapse else R.string.expand),
                            tint = TextSecondary
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    if (password.note != null) {
                        Text(
                            text = password.note,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                    ) {
                        Button(
                            onClick = { onCopy(password.password) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberBlue,
                                contentColor = DeepSpace
                            )
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.copy))
                        }
                        
                        OutlinedButton(
                            onClick = { onDelete(password) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DangerRed
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(category: PasswordCategory) {
    Surface(
        color = ElectricPurple.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ElectricPurple
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextTertiary
        )
        
        Spacer(Modifier.height(LocalDimensions.current.spacingMedium))
        
        Text(
            text = "No Saved Passwords",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(Modifier.height(LocalDimensions.current.spacingSmall))
        
        Text(
            text = stringResource(R.string.generate_and_save_passwords_description),
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun NoResultsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextTertiary
        )
        
        Spacer(Modifier.height(LocalDimensions.current.spacingMedium))
        
        Text(
            text = "No Results Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(Modifier.height(LocalDimensions.current.spacingSmall))
        
        Text(
            text = "Try adjusting your search or filter",
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    password: Password?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (password == null) return
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.delete_password_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = SurfaceDark,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

