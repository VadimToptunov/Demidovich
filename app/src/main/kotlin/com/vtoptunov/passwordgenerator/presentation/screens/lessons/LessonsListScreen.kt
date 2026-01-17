package com.vtoptunov.passwordgenerator.presentation.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vtoptunov.passwordgenerator.domain.model.Lesson
import com.vtoptunov.passwordgenerator.domain.model.LessonDifficulty
import com.vtoptunov.passwordgenerator.domain.model.LessonLibrary
import com.vtoptunov.passwordgenerator.presentation.theme.*
import java.util.Locale

@Composable
fun LessonsListScreen(
    onNavigateBack: () -> Unit = {},
    onLessonClick: (String) -> Unit = {}
) {
    val lessons = remember { LessonLibrary.getAllLessons() }
    val isRussian = Locale.getDefault().language == "ru"
    
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
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberBlue
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (isRussian) "📚 УРОКИ" else "📚 LESSONS",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isRussian) "Изучай кибербезопасность" else "Learn cybersecurity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                Spacer(Modifier.width(48.dp))
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Lessons List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lessons) { lesson ->
                    LessonCard(
                        lesson = lesson,
                        isRussian = isRussian,
                        onClick = { onLessonClick(lesson.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isRussian: Boolean,
    onClick: () -> Unit
) {
    val isUnlocked = true // For now, all lessons unlocked
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SurfaceDark else SurfaceDark.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon + Title
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        lesson.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Column {
                        Text(
                            if (isRussian) lesson.titleRu else lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) TextPrimary else TextSecondary
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Difficulty
                            val (diffColor, diffLabel) = when (lesson.difficulty) {
                                LessonDifficulty.BEGINNER -> Pair(
                                    NeonGreen,
                                    if (isRussian) lesson.difficulty.displayNameRu else lesson.difficulty.displayName
                                )
                                LessonDifficulty.INTERMEDIATE -> Pair(
                                    WarningOrange,
                                    if (isRussian) lesson.difficulty.displayNameRu else lesson.difficulty.displayName
                                )
                                LessonDifficulty.ADVANCED -> Pair(
                                    DangerRed,
                                    if (isRussian) lesson.difficulty.displayNameRu else lesson.difficulty.displayName
                                )
                            }
                            
                            Surface(
                                color = diffColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    diffLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = diffColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            
                            // Time
                            Text(
                                "⏱ ${lesson.durationMinutes} ${if (isRussian) "мин" else "min"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
                
                // XP Badge
                Surface(
                    color = ElectricPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "+${lesson.xpReward}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Progress or Status
            if (lesson.isCompleted) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (isRussian) "Пройдено!" else "Completed!",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonGreen
                    )
                }
            } else if (!isUnlocked) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (isRussian) "Заблокировано" else "Locked",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            } else {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberBlue,
                        contentColor = DeepSpace
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isRussian) "Начать урок" else "Start Lesson")
                }
            }
        }
    }
}
