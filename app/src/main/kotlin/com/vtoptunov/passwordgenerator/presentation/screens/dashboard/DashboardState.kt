package com.vtoptunov.passwordgenerator.presentation.screens.dashboard

import com.vtoptunov.passwordgenerator.domain.model.PasswordHealthStats

data class DashboardState(
    val stats: PasswordHealthStats = PasswordHealthStats(),
    val isLoading: Boolean = true,
    val selectedTab: DashboardTab = DashboardTab.OVERVIEW
)

enum class DashboardTab {
    OVERVIEW,
    ISSUES,
    ACHIEVEMENTS
}
