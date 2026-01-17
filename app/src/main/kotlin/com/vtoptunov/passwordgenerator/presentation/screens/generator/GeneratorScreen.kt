package com.vtoptunov.passwordgenerator.presentation.screens.generator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.R
import com.vtoptunov.passwordgenerator.domain.model.PasswordCategory
import com.vtoptunov.passwordgenerator.domain.model.PasswordStrength
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = hiltViewModel(),
    onNavigateToSaved: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGame: (String) -> Unit = {},
    onNavigateToAcademy: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    
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
                .padding(dimensions.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stringResource(R.string.password).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold
                    )
                Text(
                    stringResource(R.string.generator).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary
                )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                    IconButton(onClick = onNavigateToPremium) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Premium",
                            tint = WarningOrange,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                    IconButton(onClick = onNavigateToAcademy) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "PassForge Academy",
                            tint = ElectricPurple,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            tint = CyberBlue,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                    IconButton(onClick = onNavigateToSaved) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = "Saved Passwords",
                            tint = NeonGreen,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                }
            }
            
            // Password Display Card with Hacker Effect
            PasswordDisplayCard(
                password = state.generatedPassword?.password ?: "",
                onTrainMemory = {
                    state.generatedPassword?.password?.let { password ->
                        onNavigateToGame(password)
                    }
                },
                cracking = state.crackingSimulation,
                onCopy = { viewModel.onEvent(GeneratorEvent.CopyToClipboard) }
            )
            
            // Strength Indicator
            state.generatedPassword?.let { result ->
                StrengthIndicatorCard(
                    entropy = result.entropy,
                    strength = result.strength,
                    crackTime = result.crackTime
                )
            }
            
            // Style Selector
            StyleSelectorCard(
                selectedStyle = state.selectedStyle,
                expanded = state.styleExpanded,
                onToggle = { viewModel.onEvent(GeneratorEvent.ToggleStyle) },
                onStyleSelected = { viewModel.onEvent(GeneratorEvent.StyleSelected(it)) }
            )
            
            // Settings Dropdown
            SettingsDropdownCard(
                expanded = state.settingsExpanded,
                onToggle = { viewModel.onEvent(GeneratorEvent.ToggleSettings) },
                content = {
                    // Options (only for Random style)
                    if (state.selectedStyle == PasswordStyle.Random) {
                        OptionsCard(
                            length = state.passwordLength,
                            onLengthChanged = { viewModel.onEvent(GeneratorEvent.LengthChanged(it)) },
                            includeUppercase = state.includeUppercase,
                            includeLowercase = state.includeLowercase,
                            includeNumbers = state.includeNumbers,
                            includeSymbols = state.includeSymbols,
                            onOptionToggled = { viewModel.onEvent(GeneratorEvent.OptionToggled(it)) }
                        )
                    }
                }
            )
            
            // Category Selector
            CategorySelectorCard(
                selectedCategory = state.selectedCategory,
                expanded = state.categoryExpanded,
                onToggle = { viewModel.onEvent(GeneratorEvent.ToggleCategory) },
                onCategorySelected = { viewModel.onEvent(GeneratorEvent.CategorySelected(it)) }
            )
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(GeneratorEvent.GeneratePassword) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberBlue,
                        contentColor = DeepSpace
                    )
                ) {
                    if (state.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = DeepSpace
                        )
                    } else {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.generate))
                    }
                }
                
                Button(
                    onClick = { viewModel.onEvent(GeneratorEvent.SavePassword) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSaving && state.generatedPassword != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricPurple,
                        contentColor = TextPrimary
                    )
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
        
        // Success Snackbar
        AnimatedVisibility(
            visible = state.showSaveSuccess,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = DeepSpace)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.password_saved), color = DeepSpace, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PasswordDisplayCard(
    password: String,
    cracking: CrackingSimulationState?,
    onCopy: () -> Unit,
    onTrainMemory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.generated_password),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onTrainMemory) {
                        Icon(Icons.Default.Psychology, "Train Memory", tint = ElectricPurple)
                    }
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = CyberBlue)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Password with typewriter effect
            Text(
                text = password,
                style = PasswordTextStyle,
                color = CyberBlue,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Cracking Simulation
            cracking?.let {
                Spacer(Modifier.height(16.dp))
                CrackingSimulationView(it)
            }
        }
    }
}

@Composable
fun CrackingSimulationView(state: CrackingSimulationState) {
    Column {
        Text(
            "🔓 Hacker's View:",
            style = MaterialTheme.typography.labelSmall,
            color = DangerRed,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(4.dp))
        
        Text(
            state.crackedChars,
            style = CodeTextStyle,
            color = DangerRed
        )
        
        Spacer(Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = state.progress,
            modifier = Modifier.fillMaxWidth(),
            color = DangerRed,
            trackColor = SurfaceMedium
        )
        
        Spacer(Modifier.height(4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Attempts: ${formatNumber(state.attempts)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            Text(
                "${state.timeElapsedMs}ms",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun StrengthIndicatorCard(
    entropy: Double,
    strength: PasswordStrength,
    crackTime: String
) {
    val strengthColor = when (strength) {
        PasswordStrength.VERY_WEAK, PasswordStrength.WEAK -> DangerRed
        PasswordStrength.FAIR -> WarningOrange
        PasswordStrength.STRONG, PasswordStrength.VERY_STRONG -> NeonGreen
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = strengthColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    strength.displayName.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = strengthColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Entropy: ${entropy.toInt()} bits",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Crack Time",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    crackTime,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StyleSelectorCard(
    selectedStyle: PasswordStyle,
    expanded: Boolean,
    onToggle: () -> Unit,
    onStyleSelected: (PasswordStyle) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = ElectricPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        stringResource(R.string.generation_style),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = ElectricPurple
                    )
                }
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    listOf(
                        PasswordStyle.Random,
                        PasswordStyle.XKCD,
                        PasswordStyle.Phonetic,
                        PasswordStyle.Story,
                        PasswordStyle.Pronounceable
                    ).forEach { style ->
                        StyleChip(
                            style = style,
                            isSelected = selectedStyle == style,
                            onClick = { onStyleSelected(style) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StyleChip(
    style: PasswordStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) CyberBlue.copy(alpha = 0.2f) else SurfaceMedium,
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyberBlue) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = CyberBlue,
                    unselectedColor = TextTertiary
                )
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    style.displayName,
                    color = if (isSelected) CyberBlue else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    style.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun OptionsCard(
    length: Int,
    onLengthChanged: (Int) -> Unit,
    includeUppercase: Boolean,
    includeLowercase: Boolean,
    includeNumbers: Boolean,
    includeSymbols: Boolean,
    onOptionToggled: (PasswordOption) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Length: $length",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            
            Slider(
                value = length.toFloat(),
                onValueChange = { onLengthChanged(it.toInt()) },
                valueRange = 8f..32f,
                steps = 24,
                colors = SliderDefaults.colors(
                    thumbColor = CyberBlue,
                    activeTrackColor = CyberBlue,
                    inactiveTrackColor = SurfaceMedium
                )
            )
            
            Spacer(Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = includeUppercase,
                    onClick = { onOptionToggled(PasswordOption.UPPERCASE) },
                    label = { Text("A-Z") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = includeLowercase,
                    onClick = { onOptionToggled(PasswordOption.LOWERCASE) },
                    label = { Text("a-z") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = includeNumbers,
                    onClick = { onOptionToggled(PasswordOption.NUMBERS) },
                    label = { Text("0-9") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = includeSymbols,
                    onClick = { onOptionToggled(PasswordOption.SYMBOLS) },
                    label = { Text("!@#") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun formatNumber(number: Long): String {
    return when {
        number < 1000 -> number.toString()
        number < 1000000 -> "${number / 1000}K"
        number < 1000000000 -> "${number / 1000000}M"
        else -> "${number / 1000000000}B"
    }
}

@Composable
fun SettingsDropdownCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = CyberBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        stringResource(R.string.settings_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = CyberBlue
                    )
                }
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun CategorySelectorCard(
    selectedCategory: PasswordCategory,
    expanded: Boolean,
    onToggle: () -> Unit,
    onCategorySelected: (PasswordCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        stringResource(R.string.category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = NeonGreen
                    )
                }
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PasswordCategory.UNCATEGORIZED,
                            selected = selectedCategory == PasswordCategory.UNCATEGORIZED,
                            onClick = { onCategorySelected(PasswordCategory.UNCATEGORIZED) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryChip(
                            category = PasswordCategory.WORK,
                            selected = selectedCategory == PasswordCategory.WORK,
                            onClick = { onCategorySelected(PasswordCategory.WORK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PasswordCategory.PERSONAL,
                            selected = selectedCategory == PasswordCategory.PERSONAL,
                            onClick = { onCategorySelected(PasswordCategory.PERSONAL) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryChip(
                            category = PasswordCategory.BANKING,
                            selected = selectedCategory == PasswordCategory.BANKING,
                            onClick = { onCategorySelected(PasswordCategory.BANKING) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PasswordCategory.SOCIAL,
                            selected = selectedCategory == PasswordCategory.SOCIAL,
                            onClick = { onCategorySelected(PasswordCategory.SOCIAL) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryChip(
                            category = PasswordCategory.EMAIL,
                            selected = selectedCategory == PasswordCategory.EMAIL,
                            onClick = { onCategorySelected(PasswordCategory.EMAIL) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            category = PasswordCategory.SHOPPING,
                            selected = selectedCategory == PasswordCategory.SHOPPING,
                            onClick = { onCategorySelected(PasswordCategory.SHOPPING) },
                            modifier = Modifier.weight(1f)
                        )
                        CategoryChip(
                            category = PasswordCategory.OTHER,
                            selected = selectedCategory == PasswordCategory.OTHER,
                            onClick = { onCategorySelected(PasswordCategory.OTHER) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: PasswordCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryIcon = when (category) {
        PasswordCategory.WORK -> "💼"
        PasswordCategory.PERSONAL -> "👤"
        PasswordCategory.BANKING -> "🏦"
        PasswordCategory.SOCIAL -> "📱"
        PasswordCategory.EMAIL -> "✉️"
        PasswordCategory.SHOPPING -> "🛒"
        PasswordCategory.OTHER -> "📁"
        PasswordCategory.UNCATEGORIZED -> "🔐"
    }
    
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(categoryIcon, style = MaterialTheme.typography.bodySmall)
                Text(
                    category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ElectricPurple.copy(alpha = 0.3f),
            selectedLabelColor = ElectricPurple,
            containerColor = SurfaceMedium,
            labelColor = TextSecondary
        )
    )
}
