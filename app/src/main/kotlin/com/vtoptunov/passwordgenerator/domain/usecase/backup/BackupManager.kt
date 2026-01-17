package com.vtoptunov.passwordgenerator.domain.usecase.backup

import android.content.Context
import android.net.Uri
import com.vtoptunov.passwordgenerator.domain.repository.PasswordRepository
import com.vtoptunov.passwordgenerator.domain.usecase.transfer.ExportPasswordsUseCase
import com.vtoptunov.passwordgenerator.domain.usecase.transfer.ImportPasswordsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Automatic Backup Manager
 * 
 * Features:
 * - Auto-backup on password changes
 * - Keep last N backups (configurable)
 * - Restore from any backup
 * - Export backup to external storage
 * - Import backup from external storage
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val passwordRepository: PasswordRepository,
    private val exportPasswordsUseCase: ExportPasswordsUseCase,
    private val importPasswordsUseCase: ImportPasswordsUseCase
) {
    
    private val backupDir = File(context.filesDir, "backups")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    
    init {
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
    }
    
    /**
     * Create automatic backup
     * Called after password add/delete/update
     */
    suspend fun createAutoBackup(): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Get all passwords
            var allPasswords = listOf<com.vtoptunov.passwordgenerator.domain.model.Password>()
            passwordRepository.getAllPasswords().collect { passwords ->
                allPasswords = passwords
            }
            
            // Export all passwords
            val passwordExport = exportPasswordsUseCase.invoke(allPasswords)
            
            // Serialize to JSON
            val json = Json {
                prettyPrint = true
                encodeDefaults = true
                ignoreUnknownKeys = true
            }
            val jsonString = json.encodeToString(passwordExport)
            
            // Create backup file
            val timestamp = dateFormat.format(Date())
            val backupFile = File(backupDir, "backup_$timestamp.json")
            
            // Write to file
            FileOutputStream(backupFile).use { output ->
                output.write(jsonString.toByteArray())
            }
            
            // Clean old backups (keep last 10)
            cleanOldBackups(maxBackups = 10)
            
            Result.success(backupFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get list of all backups
     */
    fun getBackups(): List<BackupFile> {
        if (!backupDir.exists()) return emptyList()
        
        return backupDir.listFiles { file ->
            file.isFile && file.name.startsWith("backup_") && file.name.endsWith(".json")
        }?.mapNotNull { file ->
            try {
                val timestamp = file.name
                    .removePrefix("backup_")
                    .removeSuffix(".json")
                val date = dateFormat.parse(timestamp)
                BackupFile(
                    file = file,
                    name = file.name,
                    date = date ?: Date(file.lastModified()),
                    sizeBytes = file.length()
                )
            } catch (e: Exception) {
                null
            }
        }?.sortedByDescending { it.date } ?: emptyList()
    }
    
    /**
     * Restore from backup file
     */
    suspend fun restoreFromBackup(backupFile: File): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // Read backup file
            val json = FileInputStream(backupFile).use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            }
            
            // Import passwords
            val importResult = importPasswordsUseCase.invoke(json)
            if (importResult.isFailure) {
                return@withContext Result.failure(importResult.exceptionOrNull()!!)
            }
            
            val passwords = importResult.getOrNull()!!
            Result.success(passwords.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export backup to external storage (Downloads folder)
     */
    suspend fun exportBackupToExternal(backupFile: File, uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(backupFile).use { input ->
                    input.copyTo(output)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Import backup from external storage
     */
    suspend fun importBackupFromExternal(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            } ?: return@withContext Result.failure(Exception("Failed to read file"))
            
            // Import passwords
            val importResult = importPasswordsUseCase.invoke(json)
            if (importResult.isFailure) {
                return@withContext Result.failure(importResult.exceptionOrNull()!!)
            }
            
            val passwords = importResult.getOrNull()!!
            Result.success(passwords.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete old backups, keep only maxBackups most recent
     */
    private fun cleanOldBackups(maxBackups: Int) {
        val backups = getBackups()
        if (backups.size <= maxBackups) return
        
        backups.drop(maxBackups).forEach { backup ->
            backup.file.delete()
        }
    }
    
    /**
     * Delete specific backup
     */
    fun deleteBackup(backupFile: BackupFile): Boolean {
        return backupFile.file.delete()
    }
    
    /**
     * Get total backup size
     */
    fun getTotalBackupSize(): Long {
        return getBackups().sumOf { it.sizeBytes }
    }
}

data class BackupFile(
    val file: File,
    val name: String,
    val date: Date,
    val sizeBytes: Long
)
