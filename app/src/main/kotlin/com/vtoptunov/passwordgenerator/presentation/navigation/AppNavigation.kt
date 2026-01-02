package com.vtoptunov.passwordgenerator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vtoptunov.passwordgenerator.presentation.screens.dashboard.DashboardScreen
import com.vtoptunov.passwordgenerator.presentation.screens.generator.GeneratorScreen
import com.vtoptunov.passwordgenerator.presentation.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Generator : Screen("generator")
    object SavedPasswords : Screen("saved")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Generator.route
    ) {
        composable(Screen.Generator.route) {
            GeneratorScreen(
                onNavigateToSaved = {
                    navController.navigate(Screen.SavedPasswords.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.SavedPasswords.route) {
            // TODO: Implement Saved Passwords Screen
        }
    }
}

