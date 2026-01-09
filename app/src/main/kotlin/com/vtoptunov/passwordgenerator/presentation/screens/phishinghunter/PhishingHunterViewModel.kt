package com.vtoptunov.passwordgenerator.presentation.screens.phishinghunter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.usecase.game.GeneratePhishingScenarioUseCase
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
class PhishingHunterViewModel @Inject constructor(
    private val generateScenarioUseCase: GeneratePhishingScenarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PhishingHunterState())
    val state: StateFlow<PhishingHunterState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        startLevel(1)
    }

    fun onEvent(event: PhishingHunterEvent) {
        when (event) {
            is PhishingHunterEvent.AnswerSubmitted -> {
                checkAnswer(event.isPhishing)
            }
            PhishingHunterEvent.UseHint -> {
                revealHint()
            }
            PhishingHunterEvent.NextLevel -> {
                val nextLevel = _state.value.currentLevelNumber + 1
                startLevel(nextLevel)
            }
            PhishingHunterEvent.BackTapped -> {
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
                userAnswer = null,
                isAnswered = false,
                isCorrect = false,
                revealedRedFlags = emptyList(),
                showExplanation = false,
                timeRemaining = 60, // 60 seconds per scenario
                hintsUsed = 0
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
                checkAnswer(false)
            }
        }
    }

    private fun revealHint() {
        val scenario = _state.value.currentScenario ?: return
        val revealedFlags = _state.value.revealedRedFlags
        
        if (revealedFlags.size < scenario.redFlags.size) {
            val nextFlag = scenario.redFlags[revealedFlags.size]
            _state.update {
                it.copy(
                    revealedRedFlags = revealedFlags + nextFlag,
                    hintsUsed = it.hintsUsed + 1
                )
            }
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        timerJob?.cancel()
        
        val scenario = _state.value.currentScenario ?: return
        val isCorrect = userAnswer == scenario.isPhishing
        
        val newStreak = if (isCorrect) _state.value.streak + 1 else 0
        val xpEarned = if (isCorrect) scenario.xpReward else 0
        
        _state.update {
            it.copy(
                userAnswer = userAnswer,
                isAnswered = true,
                isCorrect = isCorrect,
                revealedRedFlags = scenario.redFlags, // Show all red flags
                showExplanation = true,
                streak = newStreak,
                totalXp = it.totalXp + xpEarned
            )
        }
    }
}

