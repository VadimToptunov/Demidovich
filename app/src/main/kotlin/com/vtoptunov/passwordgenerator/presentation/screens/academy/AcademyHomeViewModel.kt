package com.vtoptunov.passwordgenerator.presentation.screens.academy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.model.AcademyProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademyHomeViewModel @Inject constructor() : ViewModel() {
    
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
            
            // Load from DataStore persistence
            // Note: Full integration with AcademyProgressDataStore.playerStats
            // can be added when gameplay stats are actively tracked
            val mockProgress = AcademyProgress(
                totalXp = 450,
                level = 5,
                memoryMatchBestStreak = 12,
                passwordCrackerBestStreak = 8,
                phishingHunterAccuracy = 0.85f,
                socialEngineeringCompleted = 5
            )
            
            _state.update {
                it.copy(
                    progress = mockProgress,
                    isLoading = false
                )
            }
        }
    }
}

