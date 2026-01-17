package com.vtoptunov.passwordgenerator.presentation.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vtoptunov.passwordgenerator.R
import com.vtoptunov.passwordgenerator.domain.model.Lesson
import com.vtoptunov.passwordgenerator.domain.model.LessonTopic
import com.vtoptunov.passwordgenerator.domain.model.QuizQuestion
import com.vtoptunov.passwordgenerator.presentation.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onBack: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val dimensions = LocalDimensions.current
    
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }
    
    val isRussian = Locale.getDefault().language == "ru"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isRussian) state.lesson?.titleRu ?: "" else state.lesson?.title ?: "",
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = CyberBlue
                )
            )
        },
        containerColor = CardBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = CyberBlue
                    )
                }
                state.lesson == null -> {
                    Text(
                        stringResource(R.string.lesson_not_found),
                        modifier = Modifier.align(Alignment.Center),
                        color = TextSecondary
                    )
                }
                else -> {
                    LessonContent(
                        lesson = state.lesson!!,
                        currentTopicIndex = state.currentTopicIndex,
                        isQuizMode = state.isQuizMode,
                        currentQuizIndex = state.currentQuizIndex,
                        selectedAnswerIndex = state.selectedAnswerIndex,
                        isAnswerRevealed = state.isAnswerRevealed,
                        correctAnswers = state.correctAnswers,
                        isRussian = isRussian,
                        onNextTopic = { viewModel.nextTopic() },
                        onPreviousTopic = { viewModel.previousTopic() },
                        onStartQuiz = { viewModel.startQuiz() },
                        onSelectAnswer = { viewModel.selectAnswer(it) },
                        onConfirmAnswer = { viewModel.confirmAnswer() },
                        onNextQuestion = { viewModel.nextQuestion() },
                        onFinishLesson = { viewModel.finishLesson(); onBack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonContent(
    lesson: Lesson,
    currentTopicIndex: Int,
    isQuizMode: Boolean,
    currentQuizIndex: Int,
    selectedAnswerIndex: Int?,
    isAnswerRevealed: Boolean,
    correctAnswers: Int,
    isRussian: Boolean,
    onNextTopic: () -> Unit,
    onPreviousTopic: () -> Unit,
    onStartQuiz: () -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onConfirmAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onFinishLesson: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(LocalDimensions.current.spacingMedium),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium)
    ) {
        item {
            ProgressBar(
                currentStep = if (isQuizMode) lesson.topics.size + currentQuizIndex + 1 else currentTopicIndex + 1,
                totalSteps = lesson.topics.size + lesson.quiz.size,
                isRussian = isRussian
            )
        }
        
        if (!isQuizMode && currentTopicIndex < lesson.topics.size) {
            val topic = lesson.topics[currentTopicIndex]
            item {
                TopicCard(topic = topic, isRussian = isRussian)
            }
            
            item {
                NavigationButtons(
                    canGoPrevious = currentTopicIndex > 0,
                    canGoNext = currentTopicIndex < lesson.topics.size - 1,
                    onPrevious = onPreviousTopic,
                    onNext = onNextTopic,
                    onStartQuiz = if (currentTopicIndex == lesson.topics.size - 1) onStartQuiz else null,
                    isRussian = isRussian
                )
            }
        } else if (isQuizMode && currentQuizIndex < lesson.quiz.size) {
            val question = lesson.quiz[currentQuizIndex]
            item {
                QuizCard(
                    question = question,
                    questionNumber = currentQuizIndex + 1,
                    totalQuestions = lesson.quiz.size,
                    selectedAnswerIndex = selectedAnswerIndex,
                    isAnswerRevealed = isAnswerRevealed,
                    isRussian = isRussian,
                    onSelectAnswer = onSelectAnswer,
                    onConfirm = onConfirmAnswer,
                    onNext = onNextQuestion
                )
            }
        } else if (isQuizMode && currentQuizIndex >= lesson.quiz.size) {
            item {
                ResultsCard(
                    correctAnswers = correctAnswers,
                    totalQuestions = lesson.quiz.size,
                    xpEarned = lesson.xpReward,
                    isRussian = isRussian,
                    onFinish = onFinishLesson
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(
    currentStep: Int,
    totalSteps: Int,
    isRussian: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
    ) {
        Text(
            if (isRussian) "Прогресс: $currentStep / $totalSteps" else "Progress: $currentStep / $totalSteps",
            fontSize = 14.sp,
            color = TextSecondary
        )
        
        LinearProgressIndicator(
            progress = currentStep.toFloat() / totalSteps.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalDimensions.current.spacingSmall)
                .clip(RoundedCornerShape(4.dp)),
            color = CyberBlue,
            trackColor = SurfaceDark
        )
    }
}

@Composable
private fun TopicCard(
    topic: LessonTopic,
    isRussian: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingMedium)
        ) {
            Text(
                if (isRussian) topic.titleRu else topic.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CyberBlue
            )
            
            Text(
                if (isRussian) topic.contentRu else topic.content,
                fontSize = 16.sp,
                color = TextPrimary,
                lineHeight = 24.sp
            )
            
            topic.example?.let { example ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CardBackground,
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
                ) {
                    Column(
                        modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(LocalDimensions.current.iconSmall)
                            )
                            Text(
                                if (isRussian) "Пример" else "Example",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricPurple
                            )
                        }
                        Text(
                            if (isRussian) topic.exampleRu ?: example else example,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            
            topic.tip?.let { tip ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = NeonGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(LocalDimensions.current.iconMedium)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                if (isRussian) "Совет" else "Tip",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                if (isRussian) topic.tipRu ?: tip else tip,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCard(
    question: QuizQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswerIndex: Int?,
    isAnswerRevealed: Boolean,
    isRussian: Boolean,
    onSelectAnswer: (Int) -> Unit,
    onConfirm: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isRussian) "Вопрос $questionNumber из $totalQuestions" else "Question $questionNumber of $totalQuestions",
                    fontSize = 14.sp,
                    color = CyberBlue,
                    fontWeight = FontWeight.Bold
                )
                
                if (isAnswerRevealed && selectedAnswerIndex != null) {
                    val isCorrect = selectedAnswerIndex == question.correctAnswerIndex
                    Surface(
                        color = if (isCorrect) SuccessGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(LocalDimensions.current.spacingSmall)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isCorrect) SuccessGreen else DangerRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                if (isCorrect) {
                                    if (isRussian) "Правильно!" else "Correct!"
                                } else {
                                    if (isRussian) "Неправильно" else "Incorrect"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) SuccessGreen else DangerRed
                            )
                        }
                    }
                }
            }
            
            Text(
                if (isRussian) question.questionRu else question.question,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 26.sp
            )
            
            val answers = if (isRussian) question.answersRu else question.answers
            answers.forEachIndexed { index, answer ->
                AnswerOption(
                    answer = answer,
                    index = index,
                    isSelected = selectedAnswerIndex == index,
                    isCorrect = isAnswerRevealed && index == question.correctAnswerIndex,
                    isWrong = isAnswerRevealed && selectedAnswerIndex == index && index != question.correctAnswerIndex,
                    isRevealed = isAnswerRevealed,
                    onClick = { if (!isAnswerRevealed) onSelectAnswer(index) }
                )
            }
            
            if (isAnswerRevealed) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
                    border = BorderStroke(1.dp, CyberBlue.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
                        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
                    ) {
                        Text(
                            if (isRussian) "Объяснение" else "Explanation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberBlue
                        )
                        Text(
                            if (isRussian) question.explanationRu else question.explanation,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
                
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberBlue,
                        contentColor = CardBackground
                    ),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
                ) {
                    Text(
                        if (isRussian) "Далее" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswerIndex != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricPurple,
                        contentColor = Color.White,
                        disabledContainerColor = SurfaceDark,
                        disabledContentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
                ) {
                    Text(
                        if (isRussian) "Проверить" else "Check Answer",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerOption(
    answer: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isCorrect -> SuccessGreen
        isWrong -> DangerRed
        isSelected -> CyberBlue
        else -> SurfaceDark.copy(alpha = 0.5f)
    }
    
    val backgroundColor = when {
        isCorrect -> SuccessGreen.copy(alpha = 0.15f)
        isWrong -> DangerRed.copy(alpha = 0.15f)
        isSelected -> CyberBlue.copy(alpha = 0.1f)
        else -> SurfaceDark
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed, onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(LocalDimensions.current.spacingMedium),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCorrect -> SuccessGreen
                            isWrong -> DangerRed
                            isSelected -> CyberBlue
                            else -> SurfaceDark.copy(alpha = 0.5f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isRevealed && (isCorrect || isWrong)) {
                    Icon(
                        if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        ('A' + index).toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
            
            Text(
                answer,
                fontSize = 15.sp,
                color = TextPrimary,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavigationButtons(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onStartQuiz: (() -> Unit)?,
    isRussian: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
    ) {
        if (canGoPrevious) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyberBlue
                ),
                border = BorderStroke(1.dp, CyberBlue),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(LocalDimensions.current.iconSmall)
                )
                Spacer(Modifier.width(LocalDimensions.current.spacingSmall))
                Text(
                    if (isRussian) "Назад" else "Previous",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (onStartQuiz != null) {
            Button(
                onClick = onStartQuiz,
                modifier = Modifier.weight(if (canGoPrevious) 1f else 1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(LocalDimensions.current.iconSmall)
                )
                Spacer(Modifier.width(LocalDimensions.current.spacingSmall))
                Text(
                    if (isRussian) "Начать тест" else "Start Quiz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (canGoNext) {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = CardBackground
                ),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Text(
                    if (isRussian) "Далее" else "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(LocalDimensions.current.spacingSmall))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(LocalDimensions.current.iconSmall)
                )
            }
        }
    }
}

@Composable
private fun ResultsCard(
    correctAnswers: Int,
    totalQuestions: Int,
    xpEarned: Int,
    isRussian: Boolean,
    onFinish: () -> Unit
) {
    val percentage = (correctAnswers.toFloat() / totalQuestions.toFloat() * 100).toInt()
    val isPerfect = correctAnswers == totalQuestions
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(LocalDimensions.current.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                if (isPerfect) Icons.Default.EmojiEvents else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isPerfect) NeonGreen else CyberBlue,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                if (isPerfect) {
                    if (isRussian) "Отлично!" else "Perfect!"
                } else {
                    if (isRussian) "Урок завершен!" else "Lesson Complete!"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPerfect) NeonGreen else CyberBlue,
                textAlign = TextAlign.Center
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
            ) {
                Text(
                    if (isRussian) "Ваш результат" else "Your Score",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    "$correctAnswers / $totalQuestions ($percentage%)",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            Divider(color = SurfaceDark)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = Icons.Default.Stars,
                    value = "+$xpEarned",
                    label = "XP",
                    color = ElectricPurple
                )
                
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    value = correctAnswers.toString(),
                    label = if (isRussian) "Правильно" else "Correct",
                    color = SuccessGreen
                )
            }
            
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = CardBackground
                ),
                shape = RoundedCornerShape(LocalDimensions.current.cardCornerRadius)
            ) {
                Text(
                    if (isRussian) "Завершить" else "Finish",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.spacingSmall)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(LocalDimensions.current.iconLarge)
        )
        Text(
            value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
