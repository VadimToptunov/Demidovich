package com.vtoptunov.passwordgenerator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vtoptunov.passwordgenerator.presentation.screens.generator.GeneratorScreen

sealed class Screen(val route: String) {
    object Generator : Screen("generator")
    object SavedPasswords : Screen("saved_passwords")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object Transfer : Screen("transfer")
    object Premium : Screen("premium")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
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
                }
            )
        }
        
        // TODO: Add other screens
        composable(Screen.SavedPasswords.route) {
            // SavedPasswordsScreen()
        }
        
        composable(Screen.Dashboard.route) {
            // DashboardScreen()
        }
        
        composable(Screen.Settings.route) {
            // SettingsScreen()
        }
        
        composable(Screen.Transfer.route) {
            // TransferScreen()
        }
        
        composable(Screen.Premium.route) {
            // PremiumScreen()
        }
    }
}

