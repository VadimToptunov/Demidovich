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
import com.vtoptunov.passwordgenerator.presentation.screens.lessons.LessonsListScreen
import com.vtoptunov.passwordgenerator.presentation.screens.lessons.LessonScreen
import com.vtoptunov.passwordgenerator.presentation.screens.passwordcracker.PasswordCrackerScreen
import com.vtoptunov.passwordgenerator.presentation.screens.phishinghunter.PhishingHunterScreen
import com.vtoptunov.passwordgenerator.presentation.screens.premium.PremiumScreen
import com.vtoptunov.passwordgenerator.presentation.screens.saved.SavedPasswordsScreen
import com.vtoptunov.passwordgenerator.presentation.screens.settings.SettingsScreen
import com.vtoptunov.passwordgenerator.presentation.screens.socialengineering.SocialEngineeringScreen
import com.vtoptunov.passwordgenerator.presentation.screens.transfer.TransferScreen

sealed class Screen(val route: String) {
    object Generator : Screen("generator")
    object SavedPasswords : Screen("saved")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object AcademyHome : Screen("academy")
    object Lessons : Screen("lessons")
    object LessonDetail : Screen("lesson_detail/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson_detail/$lessonId"
    }
    object Premium : Screen("premium")
    object Transfer : Screen("transfer")
    object Game : Screen("game?password={password}") {
        fun createRoute(password: String? = null): String {
            return if (password != null) {
                "game?password=$password"
            } else {
                "game"
            }
        }
    }
    object PasswordCrackerGame : Screen("password_cracker_game")
    object PhishingHunterGame : Screen("phishing_hunter_game")
    object SocialEngineeringGame : Screen("social_engineering_game")
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
                },
                onNavigateToAcademy = {
                    navController.navigate(Screen.AcademyHome.route)
                },
                onNavigateToPremium = {
                    navController.navigate(Screen.Premium.route)
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
                },
                onNavigateToTransfer = {
                    navController.navigate(Screen.Transfer.route)
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
                    when (game) {
                        com.vtoptunov.passwordgenerator.domain.model.AcademyGame.MEMORY_MATCH -> {
                            navController.navigate(Screen.Game.createRoute())
                        }
                        com.vtoptunov.passwordgenerator.domain.model.AcademyGame.PASSWORD_CRACKER -> {
                            navController.navigate(Screen.PasswordCrackerGame.route)
                        }
                        com.vtoptunov.passwordgenerator.domain.model.AcademyGame.PHISHING_HUNTER -> {
                            navController.navigate(Screen.PhishingHunterGame.route)
                        }
                        com.vtoptunov.passwordgenerator.domain.model.AcademyGame.SOCIAL_ENGINEERING -> {
                            navController.navigate(Screen.SocialEngineeringGame.route)
                        }
                        com.vtoptunov.passwordgenerator.domain.model.AcademyGame.NETWORK_DEFENSE -> {
                            // TODO: Implement Network Defense game screen
                            // navController.navigate(Screen.NetworkDefenseGame.route)
                        }
                    }
                },
                onLessonsClick = {
                    navController.navigate(Screen.Lessons.route)
                }
            )
        }
        
        composable(Screen.Lessons.route) {
            LessonsListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                }
            )
        }
        
        composable(
            route = Screen.LessonDetail.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            if (lessonId != null) {
                LessonScreen(
                    lessonId = lessonId,
                    onBack = { navController.popBackStack() }
                )
            }
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

        // Password Cracker Game
        composable(Screen.PasswordCrackerGame.route) {
            PasswordCrackerScreen(navController = navController)
        }
        
        // Phishing Hunter Game
        composable(Screen.PhishingHunterGame.route) {
            PhishingHunterScreen(navController = navController)
        }
        
        // Social Engineering Game
        composable(Screen.SocialEngineeringGame.route) {
            SocialEngineeringScreen(navController = navController)
        }
        
        // Premium Screen
        composable(Screen.Premium.route) {
            PremiumScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Transfer Screen (QR Code Export/Import)
        composable(Screen.Transfer.route) {
            TransferScreen(navController = navController)
        }
    }
}

