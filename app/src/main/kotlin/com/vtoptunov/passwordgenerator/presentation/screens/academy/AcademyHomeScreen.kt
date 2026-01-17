package com.vtoptunov.passwordgenerator.presentation.screens.academy

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.R
import com.vtoptunov.passwordgenerator.domain.model.AcademyGame
import com.vtoptunov.passwordgenerator.domain.model.AcademyProgress
import com.vtoptunov.passwordgenerator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyHomeScreen(
    onNavigateBack: () -> Unit,
    onGameSelected: (AcademyGame) -> Unit,
    onLessonsClick: () -> Unit = {},
    viewModel: AcademyHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                    ) {
                        Text(
                            "🎮",
                            fontSize = 24.sp
                        )
                        Text(
                            "PassForge Academy",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(dimensions.spacingMedium),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            item {
                ProgressCard(state.progress)
            }
            
            item {
                // Lessons Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLessonsClick() },
                    colors = CardDefaults.cardColors(containerColor = ElectricPurple.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(dimensions.cardCornerRadius)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.spacingLarge),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📚",
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    stringResource(R.string.security_lessons),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    stringResource(R.string.learn_cybersecurity_basics),
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ElectricPurple,
                            modifier = Modifier.size(dimensions.iconMedium)
                        )
                    }
                }
            }
            
            item {
                Text(
                    "Choose Your Game",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = dimensions.spacingSmall)
                )
            }
            
            items(AcademyGame.values().toList()) { game ->
                GameCard(
                    game = game,
                    progress = state.progress,
                    onClick = { onGameSelected(game) }
                )
            }
            
            item {
                Spacer(Modifier.height(16.dp))
                InfoCard()
            }
        }
    }
}

@Composable
private fun ProgressCard(progress: AcademyProgress) {
    val dimensions = LocalDimensions.current
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(dimensions.cardCornerRadius)),
        color = SurfaceDark,
        shape = RoundedCornerShape(dimensions.cardCornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(dimensions.spacingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Level ${progress.level}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Text(
                        "${progress.totalXp} XP",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(ElectricPurple, ElectricPurpleDark)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "⭐",
                        fontSize = 40.sp
                    )
                }
            }
            
            Spacer(Modifier.height(dimensions.spacingMedium))
            
            LinearProgressIndicator(
                progress = (progress.totalXp % 100) / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensions.spacingSmall)
                    .clip(RoundedCornerShape(dimensions.spacingExtraSmall)),
                color = CyberBlue,
                trackColor = SurfaceMedium
            )
            
            Spacer(Modifier.height(dimensions.spacingSmall))
            
            Text(
                "${progress.totalXp % 100}/100 XP to next level",
                fontSize = 12.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun GameCard(
    game: AcademyGame,
    progress: AcademyProgress,
    onClick: () -> Unit
) {
    val isUnlocked = progress.gamesUnlocked.contains(game)
    val dimensions = LocalDimensions.current
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUnlocked) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .border(
                width = 2.dp,
                color = if (isUnlocked) CyberBlue.copy(alpha = 0.5f) else SurfaceMedium,
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            ),
        color = if (isUnlocked) SurfaceDark else SurfaceDark.copy(alpha = 0.5f),
        shape = RoundedCornerShape(dimensions.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(dimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensions.iconExtraLarge)
                    .background(
                        color = if (isUnlocked) ElectricPurple.copy(alpha = 0.2f) else SurfaceMedium,
                        shape = RoundedCornerShape(dimensions.cardCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) game.icon else "🔒",
                    fontSize = 32.sp
                )
            }
            
            Spacer(Modifier.width(dimensions.spacingMedium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    game.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) TextPrimary else TextTertiary
                )
                
                Spacer(Modifier.height(dimensions.spacingExtraSmall))
                
                Text(
                    game.description,
                    fontSize = 14.sp,
                    color = if (isUnlocked) TextSecondary else TextTertiary,
                    lineHeight = 18.sp
                )
                
                if (!isUnlocked) {
                    Spacer(Modifier.height(dimensions.spacingSmall))
                    Text(
                        "Unlocks at Level ${game.unlockLevel}",
                        fontSize = 12.sp,
                        color = WarningOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isUnlocked) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = CyberBlue,
                    modifier = Modifier.size(dimensions.iconMedium)
                )
            }
        }
    }
}

@Composable
private fun InfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ElectricPurple.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = ElectricPurple,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    "Learn While Playing",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricPurple
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    "Each game teaches you real cybersecurity concepts. " +
                    "Complete levels to earn XP and unlock new games!",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

