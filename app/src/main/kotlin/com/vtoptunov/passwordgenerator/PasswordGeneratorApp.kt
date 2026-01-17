package com.vtoptunov.passwordgenerator

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PasswordGeneratorApp : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        // App initialization
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}

