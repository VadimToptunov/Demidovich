package com.vtoptunov.passwordgenerator.presentation.screens.phishinghunter

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun PhishingHunterScreen(
    navController: NavController,
    viewModel: PhishingHunterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Phishing Hunter 🎣", color = TextPrimary)
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
                hintsUsed = state.hintsUsed
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
                    onNextLevel = { viewModel.onEvent(PhishingHunterEvent.NextLevel) }
                )
            }
        }
    }
}

@Composable
fun StatsBar(
    timeRemaining: Int,
    streak: Int,
    hintsUsed: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard(
            icon = Icons.Default.Timer,
            label = "Time",
            value = "${timeRemaining}s",
            color = if (timeRemaining <= 10) DangerRed else NeonGreen
        )
        
        StatCard(
            icon = Icons.Default.Whatshot,
            label = "Streak",
            value = "$streak",
            color = ElectricPurple
        )
        
        StatCard(
            icon = Icons.Default.Lightbulb,
            label = "Hints",
            value = "$hintsUsed",
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
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun GameContent(
    state: PhishingHunterState,
    onEvent: (PhishingHunterEvent) -> Unit
) {
    val scenario = state.currentScenario ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
                shape = RoundedCornerShape(dimensions.cardCornerRadius)
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Text(
                        "🎯 Investigate This:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        "Is this legitimate or a phishing attempt?",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // URL Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (scenario.isPhishing) 
                        DangerRed.copy(alpha = 0.1f) 
                    else 
                        NeonGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Link,
                            contentDescription = "URL",
                            tint = CyberBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Website URL",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        scenario.url,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }

        // Email Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium)
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = ElectricPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Email Details",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("From:", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        scenario.emailFrom,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    
                    Text("Subject:", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        scenario.emailSubject,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    
                    Text("Message:", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        scenario.emailBody,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Revealed Red Flags
        if (state.revealedRedFlags.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningOrange.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                        Text(
                            "🚩 Red Flags:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                        state.revealedRedFlags.forEach { flag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text("• ", color = WarningOrange, fontWeight = FontWeight.Bold)
                                Text(
                                    flag.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                OutlinedButton(
                    onClick = { onEvent(PhishingHunterEvent.UseHint) },
                    modifier = Modifier.weight(1f),
                    enabled = state.revealedRedFlags.size < scenario.redFlags.size && !state.isAnswered,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberBlue
                    )
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = "Hint", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hint")
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                Button(
                    onClick = { onEvent(PhishingHunterEvent.AnswerSubmitted(false)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !state.isAnswered,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Legitimate", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✅ Legitimate", color = DeepSpace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Button(
                    onClick = { onEvent(PhishingHunterEvent.AnswerSubmitted(true)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !state.isAnswered,
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Phishing", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🎣 Phishing!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ExplanationContent(
    state: PhishingHunterState,
    onNextLevel: () -> Unit
) {
    val scenario = state.currentScenario ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isCorrect) 
                        NeonGreen.copy(alpha = 0.1f) 
                    else 
                        DangerRed.copy(alpha = 0.1f)
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
                        if (state.isCorrect) "✅ Correct!" else "❌ Incorrect",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isCorrect) NeonGreen else DangerRed
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                    Text(
                        if (scenario.isPhishing) "This was a PHISHING attempt" else "This was LEGITIMATE",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    if (state.isCorrect) {
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                        Text(
                            "+${scenario.xpReward} XP",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberBlue
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium)
            ) {
                Column(modifier = Modifier.padding(dimensions.spacingMedium)) {
                    Text(
                        "🔍 Explanation:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (scenario.isPhishing && scenario.redFlags.isNotEmpty()) {
                        Text(
                            "Red flags in this scenario:",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
                        scenario.redFlags.forEach { flag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text("🚩 ", fontSize = 14.sp)
                                Column {
                                    Text(
                                        flag.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                        fontWeight = FontWeight.Bold,
                                        color = WarningOrange,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        getRedFlagDescription(flag),
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            "This scenario appears legitimate with no obvious red flags. Always verify through official channels when in doubt!",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text(
                    "Next Scenario →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepSpace
                )
            }
        }
    }
}

fun getRedFlagDescription(flag: com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag): String {
    return when (flag) {
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.NO_HTTPS -> "Website doesn't use HTTPS encryption"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.SUSPICIOUS_DOMAIN -> "Domain name looks suspicious"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.TYPO_IN_URL -> "URL contains typos"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.NUMBERS_IN_DOMAIN -> "Domain has suspicious numbers"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.SUSPICIOUS_EMAIL -> "Email address is suspicious"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.FALSE_URGENCY -> "Creates false urgency"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.GENERIC_GREETING -> "Uses generic greeting"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.SPELLING_ERRORS -> "Contains spelling errors"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.SUSPICIOUS_ATTACHMENT -> "Has suspicious attachment"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.REQUESTS_PASSWORD -> "Asks for password"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.TOO_GOOD_TO_BE_TRUE -> "Offer seems too good to be true"
        com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag.MISMATCHED_URL -> "URL doesn't match claimed sender"
    }
}
