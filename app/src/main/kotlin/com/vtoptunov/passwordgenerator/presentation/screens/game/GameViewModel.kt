package com.vtoptunov.passwordgenerator.presentation.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.datastore.AcademyProgressDataStore
import com.vtoptunov.passwordgenerator.data.repository.PremiumRepository
import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import com.vtoptunov.passwordgenerator.domain.model.GamePhase
import com.vtoptunov.passwordgenerator.domain.model.GameResult
import com.vtoptunov.passwordgenerator.domain.model.GameSession
import com.vtoptunov.passwordgenerator.domain.model.PlayerStats
import com.vtoptunov.passwordgenerator.domain.usecase.game.GenerateMemoryGameUseCase
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
class GameViewModel @Inject constructor(
    private val generateMemoryGameUseCase: GenerateMemoryGameUseCase,
    private val premiumRepository: PremiumRepository,
    private val academyProgressDataStore: AcademyProgressDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()
    
    private var memorizeTimerJob: Job? = null
    private var isPremium = false
    
    init {
        // Observe premium status
        viewModelScope.launch {
            premiumRepository.premiumStatus.collect { premiumStatus ->
                isPremium = premiumStatus.isPremium
            }
        }
        
        // Load player stats from DataStore
        viewModelScope.launch {
            academyProgressDataStore.playerStats.collect { stats ->
                _state.update { it.copy(playerStats = stats) }
            }
        }
    }
    
    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.StartGame -> startGame(event.difficulty, event.customPassword)
            GameEvent.MemorizeTimeUp -> onMemorizeTimeUp()
            is GameEvent.SelectPassword -> selectPassword(event.password)
            GameEvent.ConfirmSelection -> confirmSelection()
            GameEvent.WatchAdForExtraAttempt -> watchAdForExtraAttempt()
            GameEvent.PlayAgain -> playAgain()
            GameEvent.BackToDifficultySelector -> backToDifficultySelector()
            GameEvent.ExitGame -> exitGame()
        }
    }
    
    private fun startGame(difficulty: GameDifficulty, customPassword: String?) {
        val game = generateMemoryGameUseCase(difficulty, customPassword)
        val session = GameSession(
            game = game,
            phase = GamePhase.MEMORIZING,
            remainingMemorizeTime = game.memorizeTimeSeconds
        )
        
        // Shuffle passwords ONCE at game start
        val shuffledPasswords = (listOf(game.correctPassword) + game.decoyPasswords).shuffled()
        
        _state.update {
            it.copy(
                session = session,
                showDifficultySelector = false,
                attemptsRemaining = game.maxAttempts,
                selectedPassword = null,
                showResult = false,
                isCheckingAnswer = false,
                shuffledPasswords = shuffledPasswords,
                lastWrongPassword = null
            )
        }
        
        startMemorizeTimer()
    }
    
    private fun startMemorizeTimer() {
        memorizeTimerJob?.cancel()
        memorizeTimerJob = viewModelScope.launch {
            val memorizeTime = _state.value.session?.game?.memorizeTimeSeconds ?: return@launch
            
            for (i in memorizeTime downTo 0) {
                _state.update { 
                    it.copy(
                        session = it.session?.copy(remainingMemorizeTime = i)
                    )
                }
                delay(1000)
            }
            
            onMemorizeTimeUp()
        }
    }
    
    private fun onMemorizeTimeUp() {
        memorizeTimerJob?.cancel()
        _state.update {
            it.copy(
                session = it.session?.copy(
                    phase = GamePhase.SELECTING,
                    remainingMemorizeTime = null
                )
            )
        }
    }
    
    private fun selectPassword(password: String) {
        _state.update {
            it.copy(selectedPassword = password)
        }
    }
    
    private fun confirmSelection() {
        val currentState = _state.value
        val selectedPassword = currentState.selectedPassword ?: return
        val session = currentState.session ?: return
        val game = session.game
        
        _state.update { it.copy(isCheckingAnswer = true) }
        
        viewModelScope.launch {
            delay(500) // Brief delay for animation
            
            val isCorrect = selectedPassword == game.correctPassword
            val newAttempts = currentState.attemptsRemaining - 1
            
            if (isCorrect) {
                // Success!
                val timeSpent = ((System.currentTimeMillis() - session.startTime) / 1000).toInt()
                val newStreak = currentState.playerStats.currentStreak + 1
                val result = GameResult(
                    isCorrect = true,
                    isSuccess = true,
                    attemptsUsed = game.maxAttempts - newAttempts,
                    timeSpentSeconds = timeSpent,
                    xpEarned = game.xpReward,
                    newStreak = newStreak,
                    difficulty = game.difficulty,
                    password = game.correctPassword
                )
                
                // Save stats to DataStore
                academyProgressDataStore.updateStats(
                    xpEarned = result.xpEarned,
                    isWin = true,
                    difficulty = result.difficulty
                )
                
                _state.update {
                    it.copy(
                        session = session.copy(
                            phase = GamePhase.RESULT,
                            result = result
                        ),
                        showResult = true,
                        isCheckingAnswer = false
                    )
                }
            } else if (newAttempts <= 0 && !isPremium) {
                // Failed - no more attempts (unless premium)
                val timeSpent = ((System.currentTimeMillis() - session.startTime) / 1000).toInt()
                val result = GameResult(
                    isCorrect = false,
                    isSuccess = false,
                    attemptsUsed = game.maxAttempts,
                    timeSpentSeconds = timeSpent,
                    xpEarned = 0,
                    newStreak = 0,
                    difficulty = game.difficulty,
                    password = game.correctPassword
                )
                
                // Save failed attempt to DataStore (no XP, but count the game)
                academyProgressDataStore.updateStats(
                    xpEarned = 0,
                    isWin = false,
                    difficulty = result.difficulty
                )
                
                _state.update {
                    it.copy(
                        session = session.copy(
                            phase = GamePhase.RESULT,
                            result = result
                        ),
                        attemptsRemaining = 0,
                        showResult = true,
                        isCheckingAnswer = false,
                        showAdPrompt = true // Show option to watch ad for extra attempt
                    )
                }
            } else {
                // Wrong answer, but has attempts remaining
                // Premium users get unlimited attempts
                val newAttemptsValue = if (isPremium) game.maxAttempts else newAttempts
                
                _state.update {
                    it.copy(
                        attemptsRemaining = newAttemptsValue,
                        selectedPassword = null,
                        isCheckingAnswer = false,
                        lastWrongPassword = selectedPassword // Save wrong password for red display
                    )
                }
            }
        }
    }
    
    private fun watchAdForExtraAttempt() {
        // In real implementation, this would show an ad
        // For now, just grant an extra attempt
        _state.update {
            it.copy(
                attemptsRemaining = 1,
                showAdPrompt = false,
                canWatchAd = false, // Only one ad per game
                showResult = false,
                session = it.session?.copy(phase = GamePhase.SELECTING)
            )
        }
    }
    
    private fun playAgain() {
        val currentDifficulty = _state.value.session?.game?.difficulty ?: GameDifficulty.EASY
        startGame(currentDifficulty, null)
    }
    
    private fun backToDifficultySelector() {
        memorizeTimerJob?.cancel()
        _state.update {
            GameState(
                playerStats = it.playerStats,
                showDifficultySelector = true
            )
        }
    }
    
    private fun exitGame() {
        memorizeTimerJob?.cancel()
    }
}

