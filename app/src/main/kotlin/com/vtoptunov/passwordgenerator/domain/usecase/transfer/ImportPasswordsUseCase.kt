package com.vtoptunov.passwordgenerator.domain.usecase.transfer

import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.model.PasswordCategory
import com.vtoptunov.passwordgenerator.domain.model.PasswordExport
import com.vtoptunov.passwordgenerator.domain.model.PasswordExportEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportPasswordsUseCase @Inject constructor() {
    
    // CRITICAL: Must match ExportPasswordsUseCase serialization settings
    // to ensure checksum verification works correctly
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    
    operator fun invoke(jsonString: String): Result<List<Password>> {
        return try {
            val export = json.decodeFromString<PasswordExport>(jsonString)
            
            // BUG FIX #17: Use the same serialization method as ExportPasswordsUseCase
            // to ensure checksum verification works correctly
            val exportWithoutChecksum = export.copy(checksum = "")
            // Must use inline reified version (without explicit serializer) to match Export
            val calculatedChecksum = calculateChecksum(json.encodeToString(exportWithoutChecksum))
            
            if (export.checksum != calculatedChecksum) {
                return Result.failure(SecurityException("Checksum verification failed. Data may be corrupted or tampered."))
            }
            
            val passwords = export.passwords.map { entry ->
                Password(
                    id = entry.id,
                    password = entry.password,
                    category = try {
                        PasswordCategory.valueOf(entry.category)
                    } catch (e: Exception) {
                        PasswordCategory.UNCATEGORIZED
                    },
                    createdAt = entry.createdAt,
                    lastModified = entry.lastModified,
                    isFavorite = entry.isFavorite,
                    note = entry.note
                )
            }
            
            Result.success(passwords)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun decodeFromBase64(base64String: String): Result<List<Password>> {
        return try {
            val jsonString = String(Base64.getDecoder().decode(base64String))
            invoke(jsonString)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun calculateChecksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}

