package com.vtoptunov.passwordgenerator.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vtoptunov.passwordgenerator.domain.model.*
import com.vtoptunov.passwordgenerator.presentation.theme.*
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
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
                .padding(16.dp)
        ) {
            // Header
            DashboardHeader(onNavigateBack = onNavigateBack)
            
            Spacer(Modifier.height(24.dp))
            
            if (state.isLoading) {
                LoadingState()
            } else {
                // Health Score Hero Card
                HealthScoreCard(
                    score = state.stats.healthScore,
                    totalPasswords = state.stats.totalPasswords
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Quick Stats Grid
                QuickStatsGrid(state.stats)
                
                Spacer(Modifier.height(16.dp))
                
                // Tab Selector
                TabSelector(
                    selectedTab = state.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Content based on selected tab
                when (state.selectedTab) {
                    DashboardTab.OVERVIEW -> OverviewTab(state.stats)
                    DashboardTab.ISSUES -> IssuesTab(state.stats.securityIssues)
                    DashboardTab.ACHIEVEMENTS -> AchievementsTab(state.stats.achievements)
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, "Back", tint = CyberBlue)
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "SECURITY",
                style = MaterialTheme.typography.headlineSmall,
                color = CyberBlue,
                fontWeight = FontWeight.Bold
            )
            Text(
                "DASHBOARD",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
        
        IconButton(onClick = { /* Refresh */ }) {
            Icon(Icons.Default.Refresh, "Refresh", tint = CyberBlue)
        }
    }
}

@Composable
fun HealthScoreCard(score: Int, totalPasswords: Int) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "score"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val scoreColor = when {
        score >= 80 -> NeonGreen
        score >= 60 -> WarningOrange
        else -> DangerRed
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect background
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(1f + glowAlpha * 0.1f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                scoreColor.copy(alpha = glowAlpha * 0.3f),
                                scoreColor.copy(alpha = 0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Score number
                Text(
                    text = "$animatedScore",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = scoreColor
                )
                
                Text(
                    text = "HEALTH SCORE",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Status indicator
                Surface(
                    color = scoreColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            score >= 80 -> "🛡️ EXCELLENT"
                            score >= 60 -> "⚠️ GOOD"
                            score >= 40 -> "⚠️ FAIR"
                            else -> "🚨 NEEDS ATTENTION"
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = scoreColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "$totalPasswords passwords analyzed",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
fun QuickStatsGrid(stats: PasswordHealthStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickStatCard(
            modifier = Modifier.weight(1f),
            value = stats.strongPasswords.toString(),
            label = "Strong",
            color = NeonGreen,
            icon = "💪"
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            value = stats.weakPasswords.toString(),
            label = "Weak",
            color = DangerRed,
            icon = "⚠️"
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            value = "${stats.averageEntropy.roundToInt()}",
            label = "Avg Entropy",
            color = CyberBlue,
            icon = "🔐"
        )
    }
}

@Composable
fun QuickStatCard(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun TabSelector(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashboardTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Surface(
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
                color = if (isSelected) CyberBlue.copy(alpha = 0.2f) else SurfaceMedium,
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CyberBlue) else null
            ) {
                Text(
                    text = tab.name.replace("_", " "),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) CyberBlue else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun OverviewTab(stats: PasswordHealthStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Strength Distribution
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Password Strength Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                
                StrengthBar(
                    strong = stats.strongPasswords,
                    weak = stats.weakPasswords,
                    total = stats.totalPasswords
                )
            }
        }
        
        // Average Strength
        InfoCard(
            title = "Average Strength",
            value = stats.averageStrength.displayName,
            icon = "📊",
            color = when(stats.averageStrength) {
                PasswordStrength.VERY_STRONG, PasswordStrength.STRONG -> NeonGreen
                PasswordStrength.FAIR -> WarningOrange
                else -> DangerRed
            }
        )
    }
}

@Composable
fun StrengthBar(strong: Int, weak: Int, total: Int) {
    if (total == 0) {
        Text("No passwords yet", color = TextTertiary)
        return
    }
    
    val strongRatio = strong.toFloat() / total
    val weakRatio = weak.toFloat() / total
    val mediumRatio = 1f - strongRatio - weakRatio
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (strongRatio > 0) {
                Box(
                    modifier = Modifier
                        .weight(strongRatio)
                        .fillMaxHeight()
                        .background(NeonGreen)
                )
            }
            if (mediumRatio > 0) {
                Box(
                    modifier = Modifier
                        .weight(mediumRatio)
                        .fillMaxHeight()
                        .background(WarningOrange)
                )
            }
            if (weakRatio > 0) {
                Box(
                    modifier = Modifier
                        .weight(weakRatio)
                        .fillMaxHeight()
                        .background(DangerRed)
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem("Strong", strong, NeonGreen)
            LegendItem("Medium", (total - strong - weak), WarningOrange)
            LegendItem("Weak", weak, DangerRed)
        }
    }
}

@Composable
fun LegendItem(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "$label: $count",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun IssuesTab(issues: List<SecurityIssue>) {
    if (issues.isEmpty()) {
        EmptyState(
            icon = "✅",
            title = "All Clear!",
            description = "No security issues detected"
        )
    } else {
        LazyColumn(
            modifier = Modifier.height(400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(issues) { issue ->
                IssueCard(issue)
            }
        }
    }
}

@Composable
fun IssueCard(issue: SecurityIssue) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DangerRed.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("⚠️", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when(issue) {
                        is SecurityIssue.WeakPassword -> "Weak Password"
                        is SecurityIssue.OldPassword -> "Old Password (${issue.ageInDays} days)"
                        is SecurityIssue.LowEntropyPassword -> "Low Entropy (${issue.entropy.roundToInt()} bits)"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = DangerRed,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when(issue) {
                        is SecurityIssue.WeakPassword -> issue.recommendation
                        is SecurityIssue.OldPassword -> issue.recommendation
                        is SecurityIssue.LowEntropyPassword -> issue.recommendation
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun AchievementsTab(achievements: List<Achievement>) {
    LazyColumn(
        modifier = Modifier.height(400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(achievements) { achievement ->
            AchievementCard(achievement)
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val alpha = if (achievement.isUnlocked) 1f else 0.4f
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) 
                NeonGreen.copy(alpha = 0.1f) else CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                achievement.iconName,
                fontSize = 32.sp,
                modifier = Modifier.alpha(alpha)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (achievement.isUnlocked) NeonGreen else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(alpha)
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.alpha(alpha)
                )
                
                if (!achievement.isUnlocked && achievement.progress > 0) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { achievement.progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberBlue,
                        trackColor = SurfaceMedium,
                    )
                    Text(
                        "${achievement.currentValue} / ${achievement.requirement}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
            
            if (achievement.isUnlocked) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyState(icon: String, title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = CyberBlue)
    }
}

