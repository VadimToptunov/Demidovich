package com.vtoptunov.passwordgenerator.presentation.screens.socialengineering

import com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringScenario
import com.vtoptunov.passwordgenerator.domain.model.SocialEngineeringType

data class SocialEngineeringState(
    val currentScenario: SocialEngineeringScenario? = null,
    val selectedResponse: Int? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val streak: Int = 0,
    val totalXp: Int = 0,
    val currentLevelNumber: Int = 1,
    val showExplanation: Boolean = false,
    val timeRemaining: Int = 0
)

sealed class SocialEngineeringEvent {
    data class ResponseSelected(val responseIndex: Int) : SocialEngineeringEvent()
    object NextLevel : SocialEngineeringEvent()
    object BackTapped : SocialEngineeringEvent()
}

