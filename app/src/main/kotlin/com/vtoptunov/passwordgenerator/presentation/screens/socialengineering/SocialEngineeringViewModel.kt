package com.vtoptunov.passwordgenerator.presentation.screens.socialengineering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.usecase.game.GenerateSocialEngineeringScenarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialEngineeringViewModel @Inject constructor(
    private val generateScenarioUseCase: GenerateSocialEngineeringScenarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SocialEngineeringState())
    val state: StateFlow<SocialEngineeringState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        startLevel(1)
    }

    fun onEvent(event: SocialEngineeringEvent) {
        when (event) {
            is SocialEngineeringEvent.ResponseSelected -> {
                checkResponse(event.responseIndex)
            }
            SocialEngineeringEvent.NextLevel -> {
                val nextLevel = _state.value.currentLevelNumber + 1
                startLevel(nextLevel)
            }
            SocialEngineeringEvent.BackTapped -> {
                // Navigation handled by screen
            }
        }
    }

    private fun startLevel(levelNumber: Int) {
        timerJob?.cancel()
        
        val scenario = generateScenarioUseCase(levelNumber)
        
        _state.update {
            it.copy(
                currentScenario = scenario,
                currentLevelNumber = levelNumber,
                selectedResponse = null,
                isAnswered = false,
                isCorrect = false,
                showExplanation = false,
                timeRemaining = 45 // 45 seconds per scenario
            )
        }
        
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.timeRemaining > 0 && !_state.value.isAnswered) {
                delay(1000)
                _state.update { it.copy(timeRemaining = it.timeRemaining - 1) }
            }
            
            if (!_state.value.isAnswered) {
                // Time's up - count as wrong answer
                checkResponse(-1)
            }
        }
    }

    private fun checkResponse(responseIndex: Int) {
        timerJob?.cancel()
        
        val scenario = _state.value.currentScenario ?: return
        
        val allResponses = listOf(scenario.correctAnswer) + scenario.wrongAnswers
        val selectedResponse = if (responseIndex >= 0 && responseIndex < allResponses.size) {
            allResponses[responseIndex]
        } else {
            ""
        }
        
        val isCorrect = selectedResponse == scenario.correctAnswer
        
        val newStreak = if (isCorrect) _state.value.streak + 1 else 0
        val xpEarned = if (isCorrect) scenario.xpReward else 0
        
        _state.update {
            it.copy(
                selectedResponse = responseIndex,
                isAnswered = true,
                isCorrect = isCorrect,
                showExplanation = true,
                streak = newStreak,
                totalXp = it.totalXp + xpEarned
            )
        }
    }
}

