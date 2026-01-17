package com.vtoptunov.passwordgenerator.presentation.screens.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.vtoptunov.passwordgenerator.data.security.BiometricAuthManager
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun BiometricLockScreen(
    activity: FragmentActivity,
    biometricAuthManager: BiometricAuthManager,
    onAuthenticated: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        // Automatically trigger biometric authentication on screen load
        biometricAuthManager.authenticate(
            activity = activity,
            title = "Unlock PassForge",
            subtitle = "Verify your identity",
            description = "Use your fingerprint or face to continue",
            onSuccess = {
                onAuthenticated()
            },
            onError = { error ->
                errorMessage = error
            },
            onFailed = {
                errorMessage = "Authentication failed. Please try again."
            }
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric",
                tint = CyberBlue,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                text = "🔒 PASSFORGE LOCKED",
                style = MaterialTheme.typography.headlineMedium,
                color = CyberBlue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Use your fingerprint or face to unlock",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(32.dp))
            
            Button(
                onClick = {
                    errorMessage = null
                    biometricAuthManager.authenticate(
                        activity = activity,
                        title = "Unlock PassForge",
                        subtitle = "Verify your identity",
                        description = "Use your fingerprint or face to continue",
                        onSuccess = onAuthenticated,
                        onError = { error -> errorMessage = error },
                        onFailed = { errorMessage = "Authentication failed. Please try again." }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBlue,
                    contentColor = DeepSpace
                )
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Unlock")
            }
            
            errorMessage?.let { error ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
