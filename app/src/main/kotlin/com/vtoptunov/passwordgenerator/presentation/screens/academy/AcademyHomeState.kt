package com.vtoptunov.passwordgenerator.presentation.screens.academy

import com.vtoptunov.passwordgenerator.domain.model.AcademyGame
import com.vtoptunov.passwordgenerator.domain.model.AcademyProgress

data class AcademyHomeState(
    val progress: AcademyProgress = AcademyProgress(),
    val isLoading: Boolean = true
)

sealed class AcademyHomeEvent {
    data class GameSelected(val game: AcademyGame) : AcademyHomeEvent()
    object ViewProgress : AcademyHomeEvent()
    object ViewLessons : AcademyHomeEvent()
}

