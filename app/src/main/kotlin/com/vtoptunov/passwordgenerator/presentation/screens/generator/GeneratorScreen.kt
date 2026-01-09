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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onNavigateToAcademy: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "PASSWORD",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "GENERATOR",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSecondary
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onNavigateToAcademy) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "CyberSafe Academy",
                            tint = WarningOrange
                        )
                    }
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            tint = ElectricPurple
                        )
                    }
                    IconButton(onClick = onNavigateToSaved) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = "Saved Passwords",
                            tint = CyberBlue
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = NeonGreen
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
                onStyleSelected = { viewModel.onEvent(GeneratorEvent.StyleSelected(it)) }
            )
            
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
                        Text("Generate")
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
                        Text("Save")
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
                    Text("Password saved!", color = DeepSpace, fontWeight = FontWeight.Bold)
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
                    "Generated Password",
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
    onStyleSelected: (PasswordStyle) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Generation Style",
                style = MaterialTheme.typography.titleSmall,
                color = TextSecondary
            )
            
            Spacer(Modifier.height(12.dp))
            
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
                // BUG FIX #5: Correct steps calculation for slider
                // Range 8-32 has 25 values (32-8+1), so we need 24 steps (25-1)
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

