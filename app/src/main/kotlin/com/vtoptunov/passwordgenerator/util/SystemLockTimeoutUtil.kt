package com.vtoptunov.passwordgenerator.util

import android.content.Context
import android.provider.Settings

object SystemLockTimeoutUtil {
    /**
     * Get the system's screen timeout in minutes.
     * Returns the timeout configured in Settings > Display > Screen timeout
     */
    fun getSystemScreenTimeoutMinutes(context: Context): Int {
        return try {
            val timeoutMillis = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                30000 // Default 30 seconds
            )
            // Convert milliseconds to minutes, minimum 1 minute
            val minutes = (timeoutMillis / 60000).coerceAtLeast(1)
            // Cap at 15 minutes for reasonable auto-lock
            minutes.coerceAtMost(15)
        } catch (e: Exception) {
            5 // Default to 5 minutes if unable to read
        }
    }
}
