package com.vtoptunov.passwordgenerator.presentation.screens.passwordcracker

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vtoptunov.passwordgenerator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordCrackerScreen(
    navController: NavController,
    viewModel: PasswordCrackerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Password Cracker 🔓", color = TextPrimary)
                        Text(
                            "Level ${state.currentLevelNumber}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceMedium
                ),
                actions = {
                    // XP Display
                    Surface(
                        shape = RoundedCornerShape(dimensions.cardCornerRadius),
                        color = CyberBlue.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${state.totalXp} XP",
                                color = CyberBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        },
        containerColor = DeepSpace
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensions.spacingMedium)
        ) {
            // Stats Bar
            StatsBar(
                timeRemaining = state.timeRemaining,
                streak = state.streak,
                hintsUsed = state.hintsUsed,
                hintsAvailable = state.currentLevel?.hintsAvailable ?: 0
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            if (!state.showExplanation) {
                // Game Phase
                GameContent(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            } else {
                // Explanation Phase
                ExplanationContent(
                    state = state,
                    onNextLevel = { viewModel.onEvent(PasswordCrackerEvent.NextLevel) }
                )
            }
        }

        // Hint Confirmation Dialog
        if (state.showHintConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(PasswordCrackerEvent.CancelHint) },
                title = { Text("Use Hint?", color = TextPrimary) },
                text = { 
                    Text(
                        "Using a hint will reveal one weakness. You have ${state.currentLevel!!.hintsAvailable - state.hintsUsed} hints remaining.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(PasswordCrackerEvent.ConfirmUseHint) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                    ) {
                        Text("Use Hint", color = DeepSpace)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.onEvent(PasswordCrackerEvent.CancelHint) }
                    ) {
                        Text("Cancel", color = CyberBlue)
                    }
                },
                containerColor = DeepSpaceMedium
            )
        }
    }
}

@Composable
fun StatsBar(
    timeRemaining: Int,
    streak: Int,
    hintsUsed: Int,
    hintsAvailable: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Timer
        StatCard(
            icon = Icons.Default.Timer,
            label = "Time",
            value = "${timeRemaining}s",
            color = if (timeRemaining <= 5) DangerRed else NeonGreen
        )
        
        // Streak
        StatCard(
            icon = Icons.Default.Whatshot,
            label = "Streak",
            value = "$streak",
            color = ElectricPurple
        )
        
        // Hints
        StatCard(
            icon = Icons.Default.Lightbulb,
            label = "Hints",
            value = "${hintsAvailable - hintsUsed}",
            color = CyberBlue
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
        shape = RoundedCornerShape(dimensions.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.spacingSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(dimensions.iconSmall))
            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun GameContent(
    state: PasswordCrackerState,
    onEvent: (PasswordCrackerEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
            shape = RoundedCornerShape(dimensions.cardCornerRadius)
        ) {
            Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                Text(
                    "🎯 Your Mission",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberBlue
                )
                Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                Text(
                    "Identify the weak password. Use hints to reveal its weaknesses. Type the exact password to crack it!",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

        // Revealed Weaknesses
        if (state.revealedWeaknesses.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Text(
                        "🚨 Known Weaknesses:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DangerRed
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    state.revealedWeaknesses.forEach { weakness ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("• ", color = DangerRed, fontWeight = FontWeight.Bold)
                            Column {
                                Text(
                                    weakness.displayName,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    weakness.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
        }

        // Password Input
        OutlinedTextField(
            value = state.userInput,
            onValueChange = { onEvent(PasswordCrackerEvent.InputChanged(it)) },
            label = { Text("Enter the weak password", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberBlue,
                unfocusedBorderColor = TextTertiary,
                focusedLabelColor = CyberBlue,
                cursorColor = CyberBlue,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true,
            enabled = !state.isComplete
        )

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            // Hint Button
            OutlinedButton(
                onClick = { onEvent(PasswordCrackerEvent.UseHint) },
                modifier = Modifier.weight(1f),
                enabled = state.hintsUsed < (state.currentLevel?.hintsAvailable ?: 0) && !state.isComplete,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyberBlue
                )
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = "Hint", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hint")
            }

            // Submit Button
            Button(
                onClick = { onEvent(PasswordCrackerEvent.SubmitPassword) },
                modifier = Modifier.weight(2f),
                enabled = state.userInput.isNotBlank() && !state.isComplete,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Submit", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crack Password", color = DeepSpace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExplanationContent(
    state: PasswordCrackerState,
    onNextLevel: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        item {
            // Result Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isCorrect) NeonGreen.copy(alpha = 0.1f) else DangerRed.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.spacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (state.isCorrect) "✅ Password Cracked!" else "❌ Failed",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isCorrect) NeonGreen else DangerRed
                    )
                    if (state.isCorrect) {
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                        Text(
                            "+${state.currentLevel?.xpReward ?: 0} XP",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberBlue
                        )
                    }
                }
            }
        }

        item {
            // Explanation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Text(
                        state.explanation,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            // Next Level Button
            Button(
                onClick = onNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text(
                    "Next Level →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepSpace
                )
            }
        }
    }
}

