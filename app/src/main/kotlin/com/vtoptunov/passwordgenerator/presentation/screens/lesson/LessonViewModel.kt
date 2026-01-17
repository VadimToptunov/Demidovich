package com.vtoptunov.passwordgenerator.presentation.screens.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.datastore.AcademyProgressDataStore
import com.vtoptunov.passwordgenerator.domain.model.LessonLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val academyProgressDataStore: AcademyProgressDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state.asStateFlow()
    
    init {
        val lessonId = savedStateHandle.get<String>("lessonId")
        lessonId?.let { loadLesson(it) }
    }
    
    private fun loadLesson(lessonId: String) {
        val lesson = LessonLibrary.getAllLessons().find { it.id == lessonId }
        _state.update { it.copy(lesson = lesson) }
    }
    
    fun onEvent(event: LessonEvent) {
        when (event) {
            LessonEvent.NextTopic -> nextTopic()
            LessonEvent.StartQuiz -> startQuiz()
            is LessonEvent.SelectAnswer -> selectAnswer(event.index)
            LessonEvent.CheckAnswer -> checkAnswer()
            LessonEvent.NextQuestion -> nextQuestion()
            LessonEvent.CompleteLesson -> completeLesson()
            LessonEvent.BackPressed -> {
                // Handle back navigation
            }
        }
    }
    
    private fun nextTopic() {
        _state.update { currentState ->
            val lesson = currentState.lesson ?: return
            val nextIndex = currentState.currentTopicIndex + 1
            
            if (nextIndex >= lesson.topics.size) {
                // All topics done, start quiz if available
                if (lesson.quiz.isNotEmpty()) {
                    currentState.copy(isQuizMode = true, currentQuizIndex = 0)
                } else {
                    // No quiz, complete lesson
                    currentState.copy(
                        isCompleted = true,
                        xpEarned = lesson.xpReward
                    )
                }
            } else {
                currentState.copy(currentTopicIndex = nextIndex)
            }
        }
    }
    
    private fun startQuiz() {
        _state.update { it.copy(isQuizMode = true, currentQuizIndex = 0) }
    }
    
    private fun selectAnswer(index: Int) {
        _state.update { it.copy(selectedAnswerIndex = index) }
    }
    
    private fun checkAnswer() {
        _state.update { currentState ->
            val lesson = currentState.lesson ?: return
            val question = lesson.quiz.getOrNull(currentState.currentQuizIndex) ?: return
            val isCorrect = currentState.selectedAnswerIndex == question.correctAnswerIndex
            
            currentState.copy(
                isAnswerChecked = true,
                correctAnswers = if (isCorrect) currentState.correctAnswers + 1 else currentState.correctAnswers
            )
        }
    }
    
    private fun nextQuestion() {
        _state.update { currentState ->
            val lesson = currentState.lesson ?: return
            val nextIndex = currentState.currentQuizIndex + 1
            
            if (nextIndex >= lesson.quiz.size) {
                // Quiz complete
                val totalQuestions = lesson.quiz.size
                val correctPercentage = (currentState.correctAnswers.toFloat() / totalQuestions * 100).toInt()
                val xpBonus = if (correctPercentage >= 80) lesson.xpReward else (lesson.xpReward * 0.5f).toInt()
                
                currentState.copy(
                    isCompleted = true,
                    xpEarned = xpBonus
                )
            } else {
                currentState.copy(
                    currentQuizIndex = nextIndex,
                    selectedAnswerIndex = null,
                    isAnswerChecked = false
                )
            }
        }
    }
    
    private fun completeLesson() {
        viewModelScope.launch {
            val currentState = _state.value
            val lesson = currentState.lesson ?: return@launch
            val xpEarned = currentState.xpEarned
            
            // Save lesson completion to datastore and award XP
            academyProgressDataStore.completeLessonAndAwardXP(
                lessonId = lesson.id,
                xpEarned = xpEarned
            )
        }
    }
}
