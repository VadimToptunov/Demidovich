package com.vtoptunov.passwordgenerator.presentation.screens.socialengineering
import com.vtoptunov.passwordgenerator.R

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vtoptunov.passwordgenerator.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialEngineeringScreen(
    navController: NavController,
    viewModel: SocialEngineeringViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Social Engineering 🎭", color = TextPrimary)
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
                            contentDescription = stringResource(R.string.back),
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSpaceMedium
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
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
                .padding(LocalDimensions.current.spacingMedium)
        ) {
            // Stats Bar
            StatsBar(
                timeRemaining = state.timeRemaining,
                streak = state.streak
            )

            Spacer(modifier = Modifier.height(LocalDimensions.current.spacingMedium))

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
                    onNextLevel = { viewModel.onEvent(SocialEngineeringEvent.NextLevel) }
                )
            }
        }
    }
}

@Composable
fun StatsBar(
    timeRemaining: Int,
    streak: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard(
            icon = Icons.Default.Timer,
            label = stringResource(R.string.time),
            value = "${timeRemaining}s",
            color = if (timeRemaining <= 10) DangerRed else NeonGreen,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
        
        StatCard(
            icon = Icons.Default.Whatshot,
            label = stringResource(R.string.streak),
            value = "$streak",
            color = ElectricPurple,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalDimensions.current.spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(LocalDimensions.current.iconSmall))
            Spacer(modifier = Modifier.width(LocalDimensions.current.spacingSmall))
            Column(horizontalAlignment = Alignment.Start) {
                Text(label, fontSize = 10.sp, color = TextSecondary)
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun GameContent(
    state: SocialEngineeringState,
    onEvent: (SocialEngineeringEvent) -> Unit
) {
    val scenario = state.currentScenario ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                    Text(
                        "🎭 Scenario:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        "How would you respond to this situation?",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Scenario Type Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = getTypeColor(scenario.scenarioType).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            scenario.scenarioType.name.replace("_", " "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = getTypeColor(scenario.scenarioType)
                        )
                    }
                }
            }
        }

        // Conversation Messages
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium)
            ) {
                Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                    Text(
                        "💬 Conversation:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    scenario.conversationMessages.forEach { message ->
                        ConversationBubble(message, state)
                        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    }
                }
            }
        }

        // Tactics Used
        if (scenario.tactics.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningOrange.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                        Text(
                            "⚠️ Tactics Being Used:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                        scenario.tactics.forEach { tactic ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text("• ", color = WarningOrange, fontWeight = FontWeight.Bold)
                                Text(
                                    tactic.name.replace("_", " "),
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Response Options
        item {
            Text(
                "Choose your response:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Compute responses once
        val allResponses = (listOf(scenario.correctAnswer) + scenario.wrongAnswers).shuffled()
        
        itemsIndexed(allResponses) { index, response ->
            ResponseCard(
                response = response,
                index = index,
                isSelected = state.selectedResponse == index,
                isCorrect = response == scenario.correctAnswer,
                showResult = state.isAnswered,
                onClick = {
                    if (!state.isAnswered) {
                        onEvent(SocialEngineeringEvent.ResponseSelected(index))
                    }
                }
            )
        }
    }
}

@Composable
fun ConversationBubble(
    message: com.vtoptunov.passwordgenerator.domain.model.ConversationMessage,
    state: SocialEngineeringState
) {
    val isOther = message.sender == com.vtoptunov.passwordgenerator.domain.model.MessageSender.ATTACKER
    val alignment = if (isOther) Alignment.Start else Alignment.End
    val bubbleColor = if (isOther) ElectricPurple.copy(alpha = 0.2f) else CyberBlue.copy(alpha = 0.2f)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            if (isOther) "Them" else "You",
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingExtraSmall))
        Surface(
            shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                message.text,
                modifier = Modifier.padding(LocalDimensions.current.spacingSmall),
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 18.sp
            )
        }
        if (message.isSuspicious && state.isAnswered) {
            Text(
                "🚩 Suspicious",
                fontSize = 10.sp,
                color = WarningOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ResponseCard(
    response: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showResult && isCorrect -> NeonGreen.copy(alpha = 0.2f)
        showResult && isSelected -> DangerRed.copy(alpha = 0.2f)
        isSelected && !showResult -> CyberBlue.copy(alpha = 0.2f)
        else -> DeepSpaceMedium
    }
    
    val borderColor = when {
        showResult && isCorrect -> NeonGreen
        showResult && isSelected -> DangerRed
        isSelected -> CyberBlue
        else -> TextTertiary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
        enabled = !showResult,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, borderColor, RoundedCornerShape(LocalDimensions.current.cardCornerRadius))
                .padding(LocalDimensions.current.spacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(LocalDimensions.current.iconLarge)
                    .clip(RoundedCornerShape(LocalDimensions.current.spacingSmall))
                    .background(borderColor),
                contentAlignment = Alignment.Center
            ) {
                if (showResult && isCorrect) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = DeepSpace,
                        modifier = Modifier.size(LocalDimensions.current.iconSmall)
                    )
                } else if (showResult && isSelected) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Wrong",
                        tint = Color.White,
                        modifier = Modifier.size(LocalDimensions.current.iconSmall)
                    )
                } else {
                    Text(
                        "${index + 1}",
                        color = if (isSelected) DeepSpace else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                response,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ExplanationContent(
    state: SocialEngineeringState,
    onNextLevel: () -> Unit
) {
    val scenario = state.currentScenario ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium)
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
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LocalDimensions.current.spacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (state.isCorrect) "✅ Correct Response!" else "❌ Incorrect",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isCorrect) NeonGreen else DangerRed
                    )
                    if (state.isCorrect) {
                        Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
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
                Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                    Text(
                        "🔍 Explanation:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        scenario.explanation,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = NeonGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                    Text(
                        "✅ Best Response:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        scenario.correctAnswer,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DeepSpaceMedium)
            ) {
                Column(modifier = Modifier.padding(LocalDimensions.current.spacingMedium)) {
                    Text(
                        "💡 Key Takeaway:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBlue
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.spacingSmall))
                    Text(
                        getKeyTakeaway(scenario.scenarioType),
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
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

fun getTypeColor(type: com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType): Color {
    return when (type) {
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.PRETEXTING -> ElectricPurple
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.BAITING -> WarningOrange
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.QUID_PRO_QUO -> CyberBlue
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.TAILGATING -> NeonGreen
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.IMPERSONATION -> DangerRed
    }
}

fun getKeyTakeaway(type: com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType): String {
    return when (type) {
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.PRETEXTING -> 
            "Always verify the identity of people requesting information through alternative channels."
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.BAITING -> 
            "Never use unknown USB drives or download files from untrusted sources."
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.QUID_PRO_QUO -> 
            "Be suspicious of unsolicited offers. If it seems too good to be true, it probably is."
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.TAILGATING -> 
            "Never let strangers follow you into secure areas, even if they seem legitimate."
        com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType.IMPERSONATION -> 
            "Verify identities through official channels, not through contact information they provide."
    }
}

