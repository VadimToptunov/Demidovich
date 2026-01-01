package com.demidovich.presentation.screens.generator

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demidovich.domain.model.PasswordStrength
import com.demidovich.domain.model.PasswordStyle
import com.demidovich.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = hiltViewModel(),
    onNavigateToSaved: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CyberSafe Generator",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            tint = CyberBlue
                        )
                    }
                    IconButton(onClick = onNavigateToSaved) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Saved Passwords",
                            tint = CyberBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpace,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DeepSpace
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientStart,
                            GradientMiddle,
                            GradientEnd
                        )
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
                // Generated Password Card
                PasswordDisplayCard(
                    password = state.generatedPassword?.password ?: "",
                    strength = state.generatedPassword?.strength,
                    onCopy = { viewModel.onEvent(GeneratorEvent.CopyToClipboard) }
                )
                
                // Cracking Simulator
                state.crackingSimulation?.let { simulation ->
                    CrackingSimulatorCard(simulation)
                }
                
                // Password Stats
                state.generatedPassword?.let { result ->
                    PasswordStatsCard(
                        entropy = result.entropy,
                        crackTime = result.crackTime,
                        strength = result.strength
                    )
                }
                
                // Style Selector
                StyleSelectorCard(
                    selectedStyle = state.selectedStyle,
                    onStyleSelected = { style ->
                        viewModel.onEvent(GeneratorEvent.StyleSelected(style))
                    }
                )
                
                // Options Card (for Random style)
                if (state.selectedStyle == PasswordStyle.Random) {
                    PasswordOptionsCard(
                        length = state.passwordLength,
                        includeUppercase = state.includeUppercase,
                        includeLowercase = state.includeLowercase,
                        includeNumbers = state.includeNumbers,
                        includeSymbols = state.includeSymbols,
                        onLengthChanged = { length ->
                            viewModel.onEvent(GeneratorEvent.LengthChanged(length))
                        },
                        onOptionToggled = { option ->
                            viewModel.onEvent(GeneratorEvent.OptionToggled(option))
                        }
                    )
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Generate Button
                    Button(
                        onClick = { viewModel.onEvent(GeneratorEvent.GeneratePassword) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberBlue,
                            contentColor = DeepSpace
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isGenerating
                    ) {
                        if (state.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = DeepSpace,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Generate", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Save Button
                    Button(
                        onClick = { viewModel.onEvent(GeneratorEvent.SavePassword) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricPurple,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !state.isSaving && state.generatedPassword != null
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = TextPrimary,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                // Add some bottom spacing
                Spacer(Modifier.height(16.dp))
            }
            
            // Success Snackbar
            AnimatedVisibility(
                visible = state.showSaveSuccess,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                SuccessSnackbar("Password saved successfully! ✓")
            }
            
            // Copied Snackbar
            AnimatedVisibility(
                visible = state.copiedToClipboard,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                SuccessSnackbar("Copied to clipboard! (Auto-clear in 30s)")
            }
        }
    }
}

@Composable
fun PasswordDisplayCard(
    password: String,
    strength: PasswordStrength?,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
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
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = CyberBlue
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Password Text with cyber glow effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .padding(16.dp)
            ) {
                Text(
                    text = password.ifEmpty { "Generate a password..." },
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (password.isEmpty()) TextTertiary else CyberBlue,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }
            
            // Strength Indicator
            strength?.let {
                Spacer(Modifier.height(12.dp))
                StrengthIndicator(strength)
            }
        }
    }
}

@Composable
fun StrengthIndicator(strength: PasswordStrength) {
    val color = when (strength) {
        PasswordStrength.VERY_WEAK -> DangerRed
        PasswordStrength.WEAK -> Color(0xFFFF6B6B)
        PasswordStrength.FAIR -> WarningOrange
        PasswordStrength.STRONG -> NeonGreen
        PasswordStrength.VERY_STRONG -> Color(0xFF00CC6A)
    }
    
    val progress by animateFloatAsState(
        targetValue = when (strength) {
            PasswordStrength.VERY_WEAK -> 0.2f
            PasswordStrength.WEAK -> 0.4f
            PasswordStrength.FAIR -> 0.6f
            PasswordStrength.STRONG -> 0.8f
            PasswordStrength.VERY_STRONG -> 1f
        },
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Strength:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                strength.displayName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Spacer(Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SurfaceDark
        )
    }
}

@Composable
fun SuccessSnackbar(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = NeonGreen.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = DeepSpace
            )
            Spacer(Modifier.width(12.dp))
            Text(
                message,
                color = DeepSpace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

