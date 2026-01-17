package com.vtoptunov.passwordgenerator.util

import android.content.Context
import android.provider.Settings

object SystemLockTimeoutUtil {
    /**
     * Get the system's screen timeout in seconds.
     * Returns the timeout configured in Settings > Display > Screen timeout
     */
    fun getSystemScreenTimeoutSeconds(context: Context): Int {
        return try {
            val timeoutMillis = Settings.System.getLong(
                context.contentResolver,
                Settings.System.SCREEN_OFF_TIMEOUT,
                30000L // Default 30 seconds
            )
            // Convert milliseconds to seconds
            val seconds = (timeoutMillis / 1000).toInt()
            // Return actual system timeout (no minimum/maximum)
            seconds
        } catch (e: Exception) {
            300 // Default to 5 minutes (300 seconds) if unable to read
        }
    }
    
    /**
     * Get the system's screen timeout formatted for display (e.g., "30s", "2m")
     */
    fun getSystemScreenTimeoutFormatted(context: Context): String {
        val seconds = getSystemScreenTimeoutSeconds(context)
        return when {
            seconds < 60 -> "${seconds}s"
            seconds % 60 == 0 -> "${seconds / 60}m"
            else -> "${seconds / 60}m ${seconds % 60}s"
        }
    }
}
