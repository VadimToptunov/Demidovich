package com.vtoptunov.passwordgenerator.presentation.screens.phishinghunter

import com.vtoptunov.passwordgenerator.domain.model.PhishingScenario
import com.vtoptunov.passwordgenerator.domain.model.PhishingRedFlag

data class PhishingHunterState(
    val currentScenario: PhishingScenario? = null,
    val userAnswer: Boolean? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val revealedRedFlags: List<PhishingRedFlag> = emptyList(),
    val streak: Int = 0,
    val totalXp: Int = 0,
    val currentLevelNumber: Int = 1,
    val showExplanation: Boolean = false,
    val timeRemaining: Int = 0,
    val hintsUsed: Int = 0
)

sealed class PhishingHunterEvent {
    data class AnswerSubmitted(val isPhishing: Boolean) : PhishingHunterEvent()
    object UseHint : PhishingHunterEvent()
    object NextLevel : PhishingHunterEvent()
    object BackTapped : PhishingHunterEvent()
}

