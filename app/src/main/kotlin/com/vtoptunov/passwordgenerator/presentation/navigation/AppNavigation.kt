package com.vtoptunov.passwordgenerator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vtoptunov.passwordgenerator.presentation.screens.academy.AcademyHomeScreen
import com.vtoptunov.passwordgenerator.presentation.screens.dashboard.DashboardScreen
import com.vtoptunov.passwordgenerator.presentation.screens.game.GameScreen
import com.vtoptunov.passwordgenerator.presentation.screens.generator.GeneratorScreen
import com.vtoptunov.passwordgenerator.presentation.screens.saved.SavedPasswordsScreen
import com.vtoptunov.passwordgenerator.presentation.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Generator : Screen("generator")
    object SavedPasswords : Screen("saved")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object AcademyHome : Screen("academy")
    object Game : Screen("game?password={password}") {
        fun createRoute(password: String? = null): String {
            return if (password != null) {
                "game?password=$password"
            } else {
                "game"
            }
        }
    }
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
                },
                onNavigateToGame = { password ->
                    navController.navigate(Screen.Game.createRoute(password))
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
            SavedPasswordsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AcademyHome.route) {
            AcademyHomeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onGameSelected = { game ->
                    // Navigate to specific game
                    navController.navigate(Screen.Game.createRoute())
                }
            )
        }
        
        composable(
            route = "game?password={password}",
            arguments = listOf(
                navArgument("password") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val password = backStackEntry.arguments?.getString("password")
            GameScreen(
                navController = navController,
                customPassword = password
            )
        }
    }
}

