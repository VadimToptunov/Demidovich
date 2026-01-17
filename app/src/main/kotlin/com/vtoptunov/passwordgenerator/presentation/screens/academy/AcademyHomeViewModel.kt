package com.vtoptunov.passwordgenerator.presentation.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.data.datastore.AcademyProgressDataStore
import com.vtoptunov.passwordgenerator.domain.model.AcademyGame
import com.vtoptunov.passwordgenerator.domain.model.AcademyProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademyHomeViewModel @Inject constructor(
    private val academyProgressDataStore: AcademyProgressDataStore
) : ViewModel() {
    
    private val _state = MutableStateFlow(AcademyHomeState())
    val state: StateFlow<AcademyHomeState> = _state.asStateFlow()
    
    init {
        loadProgress()
    }
    
    fun onEvent(event: AcademyHomeEvent) {
        when (event) {
            is AcademyHomeEvent.GameSelected -> {
                // Navigation handled in screen
            }
            AcademyHomeEvent.ViewProgress -> {
                // Navigation handled in screen
            }
            AcademyHomeEvent.ViewLessons -> {
                // Navigation handled in screen
            }
        }
    }
    
    private fun loadProgress() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Combine player stats and unlocked games from DataStore
            combine(
                academyProgressDataStore.playerStats,
                academyProgressDataStore.unlockedGames,
                academyProgressDataStore.completedLessons
            ) { playerStats, unlockedGames, completedLessons ->
                // Auto-unlock games based on player level
                val gamesUnlockedByLevel = AcademyGame.values().filter { game ->
                    playerStats.level >= game.unlockLevel
                }.toSet()
                
                // Unlock games that should be unlocked but aren't yet
                gamesUnlockedByLevel.forEach { game ->
                    if (game !in unlockedGames) {
                        academyProgressDataStore.unlockGame(game)
                    }
                }
                
                AcademyProgress(
                    totalXp = playerStats.totalXP,
                    level = playerStats.level,
                    memoryMatchBestStreak = playerStats.bestStreak,
                    passwordCrackerBestStreak = 0, // TODO: Track per-game stats
                    phishingHunterAccuracy = 0f, // TODO: Track accuracy
                    socialEngineeringCompleted = 0, // TODO: Track completions
                    gamesUnlocked = gamesUnlockedByLevel.union(unlockedGames),
                    lessonsCompleted = completedLessons.size
                )
            }.collect { progress ->
                _state.update {
                    it.copy(
                        progress = progress,
                        isLoading = false
                    )
                }
            }
        }
    }
}

