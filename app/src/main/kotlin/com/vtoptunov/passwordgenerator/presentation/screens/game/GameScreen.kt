package com.vtoptunov.passwordgenerator.presentation.screens.game
import com.vtoptunov.passwordgenerator.R

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import com.vtoptunov.passwordgenerator.domain.model.GamePhase
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun GameScreen(
    navController: NavController,
    customPassword: String? = null,
    viewModel: GameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Header
        GameHeader(
            onBackClick = {
                if (state.session != null) {
                    viewModel.onEvent(GameEvent.BackToDifficultySelector)
                } else {
                    navController.popBackStack()
                }
            },
            playerStats = state.playerStats
        )
        
        when {
            state.showDifficultySelector -> {
                DifficultySelector(
                    onDifficultySelected = { difficulty ->
                        viewModel.onEvent(GameEvent.StartGame(difficulty, customPassword))
                    }
                )
            }
            state.session != null -> {
                when (state.session!!.phase) {
                    GamePhase.MEMORIZING -> {
                        MemorizePhase(
                            password = state.session!!.game.correctPassword,
                            remainingTime = state.session!!.remainingMemorizeTime ?: 0
                        )
                    }
                    GamePhase.SELECTING -> {
                        SelectionPhase(
                            passwords = state.shuffledPasswords, // Use fixed list
                            selectedPassword = state.selectedPassword,
                            lastWrongPassword = state.lastWrongPassword,
                            attemptsRemaining = state.attemptsRemaining,
                            onPasswordSelected = { password ->
                                viewModel.onEvent(GameEvent.SelectPassword(password))
                            },
                            onConfirm = {
                                viewModel.onEvent(GameEvent.ConfirmSelection)
                            },
                            isChecking = state.isCheckingAnswer
                        )
                    }
                    GamePhase.RESULT -> {
                        state.session!!.result?.let { result ->
                            ResultPhase(
                                result = result,
                                showAdPrompt = state.showAdPrompt,
                                canWatchAd = state.canWatchAd,
                                onWatchAd = {
                                    viewModel.onEvent(GameEvent.WatchAdForExtraAttempt)
                                },
                                onPlayAgain = {
                                    viewModel.onEvent(GameEvent.PlayAgain)
                                },
                                onExit = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                    else -> {} // DIFFICULTY_SELECTION, SELECTION
                }
            }
        }
    }
}

@Composable
fun GameHeader(
    onBackClick: () -> Unit,
    playerStats: com.vtoptunov.passwordgenerator.domain.model.PlayerStats
) {
    val dimensions = LocalDimensions.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalDimensions.current.spacingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
            Text(
                text = stringResource(R.string.memory_training),
                style = MaterialTheme.typography.headlineSmall,
                color = CyberBlue
            )
        }
        
        // Player Level & XP
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Level ${playerStats.level}",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricPurple,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = playerStats.xpProgress,
                    modifier = Modifier
                        .width(80.dp)
                        .height(LocalDimensions.current.spacingExtraSmall)
                        .clip(RoundedCornerShape(2.dp)),
                    color = NeonGreen,
                    trackColor = SurfaceDark
                )
            }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Level",
                tint = NeonGreen,
                modifier = Modifier.size(LocalDimensions.current.iconMedium)
            )
        }
    }
}

@Composable
fun DifficultySelector(
    onDifficultySelected: (GameDifficulty) -> Unit
) {
    val dimensions = LocalDimensions.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Choose Difficulty",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        
        items(GameDifficulty.entries.toTypedArray()) { difficulty ->
            DifficultyCard(
                difficulty = difficulty,
                onClick = { onDifficultySelected(difficulty) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Column(
                    modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info),
                        tint = CyberBlue,
                        modifier = Modifier.size(LocalDimensions.current.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        text = "How to Play",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        text = """
                            1. Memorize the password shown
                            2. Select the correct password from similar options
                            3. You have 3 attempts per game
                            4. Watch an ad for an extra attempt
                            5. Earn XP and level up!
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyCard(
    difficulty: GameDifficulty,
    onClick: () -> Unit
) {
    val color = when (difficulty) {
        GameDifficulty.BEGINNER -> Color(0xFF4CAF50)
        GameDifficulty.EASY -> NeonGreen
        GameDifficulty.MEDIUM -> CyberBlue
        GameDifficulty.HARD -> WarningOrange
        GameDifficulty.EXPERT -> DangerRed
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(2.dp, color.copy(alpha = 0.5f), RoundedCornerShape(LocalDimensions.current.cardCornerRadius)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = difficulty.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                Text(
                    text = "Options: ${difficulty.decoyCount + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Time: ${difficulty.memorizeTime}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Length: ${difficulty.minPasswordLength}+ chars",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "XP",
                    tint = color,
                    modifier = Modifier.size(LocalDimensions.current.iconLarge)
                )
                Text(
                    text = "+${difficulty.xpReward} XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MemorizePhase(
    password: String,
    remainingTime: Int
) {
    val dimensions = LocalDimensions.current
    val infiniteTransition = rememberInfiniteTransition(label = "memorize_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Timer Circle
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = remainingTime / 10f,
                modifier = Modifier.fillMaxSize(),
                color = CyberBlue,
                strokeWidth = 8.dp
            )
            Text(
                text = "$remainingTime",
                style = MaterialTheme.typography.displayLarge,
                color = CyberBlue,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = stringResource(R.string.memorize_this_password),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingLarge))
        
        // Password Display with Glow Effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            GlowBlue.copy(alpha = glowAlpha),
                            GlowPurple.copy(alpha = glowAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
                ),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
        ) {
            Text(
                text = password,
                style = PasswordTextStyle.copy(fontSize = 24.sp),
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalDimensions.current.spacingLarge),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "Focus",
                tint = NeonGreen,
                modifier = Modifier.size(LocalDimensions.current.iconSmall)
            )
            Text(
                text = stringResource(R.string.focus_on_details),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SelectionPhase(
    passwords: List<String>, // Fixed password list
    selectedPassword: String?,
    lastWrongPassword: String?, // Last wrong answer
    attemptsRemaining: Int,
    onPasswordSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    isChecking: Boolean
) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium)
    ) {
        // Attempts Display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val isFilled = index < attemptsRemaining
                Icon(
                    imageVector = if (isFilled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Attempt",
                    tint = if (isFilled) DangerRed else TextSecondary,
                    modifier = Modifier
                        .size(LocalDimensions.current.iconLarge)
                        .padding(LocalDimensions.current.spacingExtraSmall)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        Text(
            text = stringResource(R.string.select_correct_password),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        // Confirm Button - ABOVE password list
        Button(
            onClick = onConfirm,
            enabled = selectedPassword != null && !isChecking,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                disabledContainerColor = SurfaceDark
            ),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
        ) {
            if (isChecking) {
                CircularProgressIndicator(
                    color = TextPrimary,
                    modifier = Modifier.size(LocalDimensions.current.iconMedium)
                )
            } else {
                Text(
                    text = stringResource(R.string.confirm_selection),
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepSpace
                )
            }
        }
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        // Password Options Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (passwords.size <= 4) 1 else 2),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall),
            modifier = Modifier.weight(1f)
        ) {
            items(passwords.size) { index ->
                val password = passwords[index]
                PasswordOptionCard(
                    password = password,
                    isSelected = password == selectedPassword,
                    isWrong = password == lastWrongPassword, // Display in red
                    onClick = { onPasswordSelected(password) },
                    enabled = !isChecking
                )
            }
        }
    }
}

@Composable
fun PasswordOptionCard(
    password: String,
    isSelected: Boolean,
    isWrong: Boolean, // New parameter
    onClick: () -> Unit,
    enabled: Boolean
) {
    val dimensions = LocalDimensions.current
    val borderColor = when {
        isWrong -> DangerRed // Wrong answer - red
        isSelected -> NeonGreen // Selected - green
        else -> TextSecondary.copy(alpha = 0.3f) // Default
    }
    
    val textColor = when {
        isWrong -> DangerRed
        isSelected -> NeonGreen
        else -> TextPrimary
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .border(
                width = if (isSelected || isWrong) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isWrong -> DangerRed.copy(alpha = 0.1f) // Red background for error
                isSelected -> CardBackground.copy(alpha = 0.8f)
                else -> CardBackground
            }
        ),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Text(
            text = password,
            style = PasswordTextStyle.copy(fontSize = 16.sp),
            color = textColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingMedium),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultPhase(
    result: com.vtoptunov.passwordgenerator.domain.model.GameResult,
    showAdPrompt: Boolean,
    canWatchAd: Boolean,
    onWatchAd: () -> Unit,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Result Icon
        Icon(
            imageVector = if (result.isSuccess) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = if (result.isSuccess) "Success" else "Failed",
            tint = if (result.isSuccess) NeonGreen else DangerRed,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        
        Text(
            text = if (result.isSuccess) "Excellent!" else stringResource(R.string.try_again),
            style = MaterialTheme.typography.headlineLarge,
            color = if (result.isSuccess) NeonGreen else DangerRed,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingLarge))
        
        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
        ) {
            Column(
                modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResultStat("Attempts Used", "${result.attemptsUsed}")
                ResultStat("Time", "${result.timeSpentSeconds}s")
                ResultStat(stringResource(R.string.difficulty_label), result.difficulty.displayName)
                if (result.isSuccess) {
                    ResultStat("XP Earned", "+${result.xpEarned}", NeonGreen)
                }
                
                Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
                
                Text(
                    text = stringResource(R.string.correct_password),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = result.password,
                    style = PasswordTextStyle,
                    color = CyberBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingLarge))
        
        // Ad Prompt for Extra Attempt
        if (showAdPrompt && canWatchAd) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ElectricPurple.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Column(
                    modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "Watch Ad",
                        tint = ElectricPurple,
                        modifier = Modifier.size(LocalDimensions.current.iconLarge)
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        text = "Get an Extra Attempt",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Watch a short ad to try again",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onWatchAd,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        shape = RoundedCornerShape(LocalDimensions.current.spacingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(R.string.watch_lowercase),
                            modifier = Modifier.size(LocalDimensions.current.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                        Text(stringResource(R.string.watch_ad))
                    }
                }
            }
            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))
        }
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
        ) {
            OutlinedButton(
                onClick = onExit,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Text(stringResource(R.string.exit))
            }
            
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Play Again",
                    modifier = Modifier.size(LocalDimensions.current.iconSmall)
                )
                Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
                Text(stringResource(R.string.play_again))
            }
        }
    }
}

@Composable
fun ResultStat(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

