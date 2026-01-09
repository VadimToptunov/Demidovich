package com.vtoptunov.passwordgenerator

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.vtoptunov.passwordgenerator.presentation.screens.academy.AcademyHomeScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for CyberSafe Academy games.
 * Tests the game selection and UI components.
 */
@HiltAndroidTest
class AcademyGamesUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun academyHomeScreen_displaysGameCards() {
        composeTestRule.setContent {
            AcademyHomeScreen(
                onNavigateBack = {},
                onNavigateToGame = { _, _ -> }
            )
        }

        composeTestRule.waitForIdle()

        // Check that game card is displayed (at least one)
        composeTestRule
            .onNodeWithText("Memory Match", useUnmergedTree = true, ignoreCase = true)
            .assertExists()
    }
}

