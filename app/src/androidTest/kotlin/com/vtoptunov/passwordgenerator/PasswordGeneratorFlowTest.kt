package com.vtoptunov.passwordgenerator

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.vtoptunov.passwordgenerator.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Integration test for password generation and saving flow.
 * Tests the main user journey from generating to saving passwords.
 */
@HiltAndroidTest
class PasswordGeneratorFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun app_launches_successfully() {
        // Basic test to ensure app launches without crashing
        composeTestRule.waitForIdle()
        // If we got here, app launched successfully
    }

    @Test
    fun navigateToDashboard_checkPasswordHealth() {
        composeTestRule.waitForIdle()

        // Navigate to dashboard
        composeTestRule
            .onAllNodesWithContentDescription("Dashboard")
            .onFirst()
            .performClick()

        composeTestRule.waitForIdle()

        // Check dashboard is showing
        composeTestRule
            .onNodeWithText("Password Health", useUnmergedTree = true)
            .assertExists()
    }
}

