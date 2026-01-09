package com.vtoptunov.passwordgenerator.domain.usecase.transfer

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.vtoptunov.passwordgenerator.domain.model.Password
import com.vtoptunov.passwordgenerator.domain.model.PasswordExport
import com.vtoptunov.passwordgenerator.domain.model.PasswordExportEntry
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportPasswordsUseCase @Inject constructor() {
    
    // CRITICAL: Must match ImportPasswordsUseCase serialization settings
    // to ensure checksum verification works correctly
    private val json = Json { 
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true  // Must match import for checksum consistency
    }
    
    operator fun invoke(passwords: List<Password>): PasswordExport {
        val entries = passwords.map { password ->
            PasswordExportEntry(
                id = password.id,
                password = password.password,
                category = password.category.name,
                createdAt = password.createdAt,
                lastModified = password.lastModified,
                isFavorite = password.isFavorite,
                note = password.note
            )
        }
        
        val exportWithoutChecksum = PasswordExport(
            passwords = entries,
            exportDate = System.currentTimeMillis(),
            version = "1.0"
        )
        
        // Calculate checksum for data integrity
        val jsonString = json.encodeToString(exportWithoutChecksum)
        val checksum = calculateChecksum(jsonString)
        
        return exportWithoutChecksum.copy(checksum = checksum)
    }
    
    fun encodeToJson(export: PasswordExport): String {
        return json.encodeToString(export)
    }
    
    fun encodeToBase64(export: PasswordExport): String {
        val jsonString = encodeToJson(export)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray())
    }
    
    fun generateQRCode(export: PasswordExport, width: Int = 512, height: Int = 512): Bitmap? {
        return try {
            val data = encodeToBase64(export)
            
            // QR Code has size limits, so we might need to split for large datasets
            if (data.length > 2953) { // QR Code max capacity
                throw IllegalArgumentException("Data too large for QR code. Please export fewer passwords.")
            }
            
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, 1)
                put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H)
            }
            
            val writer = MultiFormatWriter()
            val bitMatrix: BitMatrix = writer.encode(
                data,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
            
            createBitmapFromBitMatrix(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createBitmapFromBitMatrix(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }
    
    private fun calculateChecksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}

