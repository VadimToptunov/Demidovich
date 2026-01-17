package com.vtoptunov.passwordgenerator.presentation.screens.lesson

import com.vtoptunov.passwordgenerator.domain.model.Lesson

data class LessonState(
    val lesson: Lesson? = null,
    val currentTopicIndex: Int = 0,
    val isQuizMode: Boolean = false,
    val currentQuizIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isAnswerChecked: Boolean = false,
    val correctAnswers: Int = 0,
    val isCompleted: Boolean = false,
    val xpEarned: Int = 0
)

sealed class LessonEvent {
    object NextTopic : LessonEvent()
    object StartQuiz : LessonEvent()
    data class SelectAnswer(val index: Int) : LessonEvent()
    object CheckAnswer : LessonEvent()
    object NextQuestion : LessonEvent()
    object CompleteLesson : LessonEvent()
    object BackPressed : LessonEvent()
}
