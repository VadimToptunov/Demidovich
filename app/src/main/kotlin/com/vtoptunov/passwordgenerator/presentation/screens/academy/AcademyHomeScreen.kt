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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "🎮",
                            fontSize = 24.sp
                        )
                        Text(
                            "CyberSafe Academy",
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "📚",
                                fontSize = 32.sp
                            )
                            Column {
                                Text(
                                    "Уроки безопасности",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Изучай основы кибербезопасности",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ElectricPurple,
                            modifier = Modifier.size(28.dp)
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
                    modifier = Modifier.padding(vertical = 8.dp)
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        color = SurfaceDark,
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
            
            Spacer(Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = (progress.totalXp % 100) / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CyberBlue,
                trackColor = SurfaceMedium
            )
            
            Spacer(Modifier.height(8.dp))
            
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
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (isUnlocked) SurfaceDark else SurfaceDark.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (isUnlocked) ElectricPurple.copy(alpha = 0.2f) else SurfaceMedium,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) game.icon else "🔒",
                    fontSize = 32.sp
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    game.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) TextPrimary else TextTertiary
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    game.description,
                    fontSize = 14.sp,
                    color = if (isUnlocked) TextSecondary else TextTertiary,
                    lineHeight = 18.sp
                )
                
                if (!isUnlocked) {
                    Spacer(Modifier.height(8.dp))
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
                    tint = CyberBlue
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

