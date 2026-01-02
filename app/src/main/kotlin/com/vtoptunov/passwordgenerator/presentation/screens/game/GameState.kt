package com.vtoptunov.passwordgenerator.presentation.screens.game

import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import com.vtoptunov.passwordgenerator.domain.model.GamePhase
import com.vtoptunov.passwordgenerator.domain.model.GameResult
import com.vtoptunov.passwordgenerator.domain.model.GameSession
import com.vtoptunov.passwordgenerator.domain.model.PlayerStats

data class GameState(
    val session: GameSession? = null,
    val playerStats: PlayerStats = PlayerStats(),
    val isLoading: Boolean = false,
    val showDifficultySelector: Boolean = true,
    val showAdPrompt: Boolean = false,
    val canWatchAd: Boolean = true,
    val attemptsRemaining: Int = 3,
    val selectedPassword: String? = null,
    val isCheckingAnswer: Boolean = false,
    val showResult: Boolean = false
)

sealed class GameEvent {
    data class StartGame(val difficulty: GameDifficulty, val customPassword: String? = null) : GameEvent()
    object MemorizeTimeUp : GameEvent()
    data class SelectPassword(val password: String) : GameEvent()
    object ConfirmSelection : GameEvent()
    object WatchAdForExtraAttempt : GameEvent()
    object PlayAgain : GameEvent()
    object BackToDifficultySelector : GameEvent()
    object ExitGame : GameEvent()
}

