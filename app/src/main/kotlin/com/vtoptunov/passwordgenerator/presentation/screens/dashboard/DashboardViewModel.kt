package com.vtoptunov.passwordgenerator.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtoptunov.passwordgenerator.domain.usecase.analytics.CalculatePasswordHealthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val calculatePasswordHealthUseCase: CalculatePasswordHealthUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        loadHealthStats()
    }
    
    private fun loadHealthStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            calculatePasswordHealthUseCase().collectLatest { stats ->
                _state.update { 
                    it.copy(
                        stats = stats,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun selectTab(tab: DashboardTab) {
        _state.update { it.copy(selectedTab = tab) }
    }
    
    fun refresh() {
        loadHealthStats()
    }
}

