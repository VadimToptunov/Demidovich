package com.vtoptunov.passwordgenerator.presentation.screens.passwordcracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.usecase.game.GeneratePasswordCrackerLevelUseCase
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
class PasswordCrackerViewModel @Inject constructor(
    private val generateLevelUseCase: GeneratePasswordCrackerLevelUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordCrackerState())
    val state: StateFlow<PasswordCrackerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        startLevel(1)
    }

    fun onEvent(event: PasswordCrackerEvent) {
        when (event) {
            is PasswordCrackerEvent.InputChanged -> {
                _state.update { it.copy(userInput = event.input) }
            }
            PasswordCrackerEvent.UseHint -> {
                if (_state.value.hintsUsed < _state.value.currentLevel!!.hintsAvailable) {
                    _state.update { it.copy(showHintConfirmation = true) }
                }
            }
            PasswordCrackerEvent.ConfirmUseHint -> {
                revealHint()
                _state.update { it.copy(showHintConfirmation = false) }
            }
            PasswordCrackerEvent.CancelHint -> {
                _state.update { it.copy(showHintConfirmation = false) }
            }
            PasswordCrackerEvent.SubmitPassword -> {
                checkPassword()
            }
            PasswordCrackerEvent.NextLevel -> {
                val nextLevel = _state.value.currentLevelNumber + 1
                startLevel(nextLevel)
            }
            PasswordCrackerEvent.BackTapped -> {
                // Navigation handled by screen
            }
        }
    }

    private fun startLevel(levelNumber: Int) {
        timerJob?.cancel()
        
        val level = generateLevelUseCase(levelNumber)
        
        _state.update {
            it.copy(
                currentLevel = level,
                currentLevelNumber = levelNumber,
                userInput = "",
                hintsUsed = 0,
                timeRemaining = level.timeLimit,
                isComplete = false,
                isCorrect = false,
                revealedWeaknesses = emptyList(),
                showExplanation = false,
                explanation = ""
            )
        }
        
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.timeRemaining > 0 && !_state.value.isComplete) {
                delay(1000)
                _state.update { it.copy(timeRemaining = it.timeRemaining - 1) }
            }
            
            if (!_state.value.isComplete) {
                // Time's up - wrong answer
                checkPassword(timeUp = true)
            }
        }
    }

    private fun revealHint() {
        val level = _state.value.currentLevel ?: return
        val revealedWeaknesses = _state.value.revealedWeaknesses
        
        if (revealedWeaknesses.size < level.weaknesses.size) {
            val nextWeakness = level.weaknesses[revealedWeaknesses.size]
            _state.update {
                it.copy(
                    hintsUsed = it.hintsUsed + 1,
                    revealedWeaknesses = revealedWeaknesses + nextWeakness
                )
            }
        }
    }

    private fun checkPassword(timeUp: Boolean = false) {
        timerJob?.cancel()
        
        val level = _state.value.currentLevel ?: return
        val isCorrect = if (timeUp) false else 
            _state.value.userInput.equals(level.weakPassword, ignoreCase = true)
        
        val explanation = buildExplanation(level, isCorrect)
        
        _state.update {
            it.copy(
                isComplete = true,
                isCorrect = isCorrect,
                revealedWeaknesses = level.weaknesses, // Show all weaknesses
                showExplanation = true,
                explanation = explanation,
                streak = if (isCorrect) it.streak + 1 else 0,
                totalXp = if (isCorrect) it.totalXp + level.xpReward else it.totalXp
            )
        }
    }

    private fun buildExplanation(level: com.vtoptunov.passwordgenerator.domain.model.PasswordCrackerLevel, isCorrect: Boolean): String {
        return buildString {
            if (isCorrect) {
                appendLine("✅ Correct! The password was: ${level.weakPassword}")
                appendLine()
                appendLine("Why this password is weak:")
            } else {
                appendLine("❌ Time's up or incorrect!")
                appendLine("The password was: ${level.weakPassword}")
                appendLine()
                appendLine("Why this password is weak:")
            }
            
            level.weaknesses.forEachIndexed { index, weakness ->
                appendLine("${index + 1}. ${weakness.displayName}: ${weakness.description}")
            }
            
            appendLine()
            appendLine("💡 Security Tip:")
            appendLine(getSecurityTip(level.weaknesses))
        }
    }

    private fun getSecurityTip(weaknesses: List<com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness>): String {
        return when {
            weaknesses.any { it == com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness.TOO_SHORT } ->
                "Use at least 12 characters. Longer passwords are exponentially harder to crack."
            weaknesses.any { it == com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness.DICTIONARY_WORD } ->
                "Avoid dictionary words. Hackers use wordlists with millions of common passwords."
            weaknesses.any { it == com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness.COMMON_PATTERN } ->
                "Avoid predictable patterns like 123 or abc. They're the first things attackers try."
            weaknesses.any { it == com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness.CONTAINS_YEAR } ->
                "Don't use years or dates. They're easily guessed through social engineering."
            else ->
                "Mix uppercase, lowercase, numbers, and symbols for maximum security."
        }
    }
}

