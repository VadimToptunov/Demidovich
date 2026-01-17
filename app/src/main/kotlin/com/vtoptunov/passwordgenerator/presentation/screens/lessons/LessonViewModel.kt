package com.vtoptunov.passwordgenerator.presentation.screens.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.model.Lesson
import com.vtoptunov.passwordgenerator.domain.model.LessonLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonState(
    val lesson: Lesson? = null,
    val isLoading: Boolean = true,
    val currentTopicIndex: Int = 0,
    val isQuizMode: Boolean = false,
    val currentQuizIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctAnswers: Int = 0
)

@HiltViewModel
class LessonViewModel @Inject constructor(
) : ViewModel() {
    
    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state.asStateFlow()
    
    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val lesson = LessonLibrary.getAllLessons().find { it.id == lessonId }
            
            _state.update {
                it.copy(
                    lesson = lesson,
                    isLoading = false,
                    currentTopicIndex = 0,
                    isQuizMode = false
                )
            }
        }
    }
    
    fun nextTopic() {
        _state.update {
            val newIndex = (it.currentTopicIndex + 1).coerceAtMost((it.lesson?.topics?.size ?: 1) - 1)
            it.copy(currentTopicIndex = newIndex)
        }
    }
    
    fun previousTopic() {
        _state.update {
            val newIndex = (it.currentTopicIndex - 1).coerceAtLeast(0)
            it.copy(currentTopicIndex = newIndex)
        }
    }
    
    fun startQuiz() {
        _state.update {
            it.copy(
                isQuizMode = true,
                currentQuizIndex = 0,
                selectedAnswerIndex = null,
                isAnswerRevealed = false,
                correctAnswers = 0
            )
        }
    }
    
    fun selectAnswer(answerIndex: Int) {
        _state.update {
            it.copy(selectedAnswerIndex = answerIndex)
        }
    }
    
    fun confirmAnswer() {
        val currentState = _state.value
        val lesson = currentState.lesson ?: return
        val quiz = lesson.quiz
        if (currentState.currentQuizIndex >= quiz.size) return
        
        val currentQuestion = quiz[currentState.currentQuizIndex]
        val isCorrect = currentState.selectedAnswerIndex == currentQuestion.correctAnswerIndex
        
        _state.update {
            it.copy(
                isAnswerRevealed = true,
                correctAnswers = if (isCorrect) it.correctAnswers + 1 else it.correctAnswers
            )
        }
    }
    
    fun nextQuestion() {
        _state.update {
            it.copy(
                currentQuizIndex = it.currentQuizIndex + 1,
                selectedAnswerIndex = null,
                isAnswerRevealed = false
            )
        }
    }
    
    fun finishLesson() {
        // TODO: Save lesson completion and XP to DataStore
        // This will be implemented when we integrate with AcademyProgressDataStore
    }
}
