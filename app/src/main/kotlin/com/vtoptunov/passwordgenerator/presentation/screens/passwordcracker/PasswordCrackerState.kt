package com.vtoptunov.passwordgenerator.presentation.screens.passwordcracker

import com.vtoptunov.passwordgenerator.domain.model.PasswordCrackerLevel
import com.vtoptunov.passwordgenerator.domain.model.PasswordWeakness

data class PasswordCrackerState(
    val currentLevel: PasswordCrackerLevel? = null,
    val userInput: String = "",
    val hintsUsed: Int = 0,
    val timeRemaining: Int = 0,
    val isComplete: Boolean = false,
    val isCorrect: Boolean = false,
    val revealedWeaknesses: List<PasswordWeakness> = emptyList(),
    val streak: Int = 0,
    val totalXp: Int = 0,
    val currentLevelNumber: Int = 1,
    val showHintConfirmation: Boolean = false,
    val showExplanation: Boolean = false,
    val explanation: String = ""
)

sealed class PasswordCrackerEvent {
    data class InputChanged(val input: String) : PasswordCrackerEvent()
    object UseHint : PasswordCrackerEvent()
    object ConfirmUseHint : PasswordCrackerEvent()
    object CancelHint : PasswordCrackerEvent()
    object SubmitPassword : PasswordCrackerEvent()
    object NextLevel : PasswordCrackerEvent()
    object BackTapped : PasswordCrackerEvent()
}

