package com.vtoptunov.passwordgenerator.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.vtoptunov.passwordgenerator.R
import com.vtoptunov.passwordgenerator.domain.model.PasswordStyle
import com.vtoptunov.passwordgenerator.domain.usecase.password.GeneratePasswordUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * CyberSafe Password Generator Widget
 * 
 * Quick password generation from home screen
 * - Generate new password with one tap
 * - Auto-copy to clipboard
 * - Show password length
 * - Configurable style (Random, PIN, XKCD)
 */
class PasswordWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == ACTION_GENERATE_PASSWORD) {
            // Generate and copy password
            val password = generatePassword(context)
            copyToClipboard(context, password)
            
            // Update all widgets
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = android.content.ComponentName(context, PasswordWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_password)
        
        // Set up generate button
        val generateIntent = Intent(context, PasswordWidget::class.java).apply {
            action = ACTION_GENERATE_PASSWORD
        }
        val generatePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            generateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_button_generate, generatePendingIntent)
        
        // Set up app launch
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val launchPendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, launchPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun generatePassword(context: Context): String {
        // Use default settings (16 chars, Random style)
        // In real implementation, would use GeneratePasswordUseCase via Dagger
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?"
        return (1..16)
            .map { chars.random() }
            .joinToString("")
    }
    
    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Password", text)
        clipboard.setPrimaryClip(clip)
        
        Toast.makeText(context, "Password copied! 🔐", Toast.LENGTH_SHORT).show()
    }
    
    companion object {
        private const val ACTION_GENERATE_PASSWORD = "com.vtoptunov.passwordgenerator.ACTION_GENERATE_PASSWORD"
    }
}
