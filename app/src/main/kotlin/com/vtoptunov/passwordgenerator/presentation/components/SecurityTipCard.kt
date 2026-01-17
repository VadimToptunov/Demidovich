package com.vtoptunov.passwordgenerator.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vtoptunov.passwordgenerator.domain.model.SecurityTip
import com.vtoptunov.passwordgenerator.domain.model.TipImportance
import com.vtoptunov.passwordgenerator.presentation.theme.*

@Composable
fun SecurityTipCard(
    tip: SecurityTip,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (tip.importance) {
        TipImportance.CRITICAL -> DangerRed.copy(alpha = 0.15f)
        TipImportance.HIGH -> WarningOrange.copy(alpha = 0.15f)
        TipImportance.MEDIUM -> CyberBlue.copy(alpha = 0.15f)
        TipImportance.LOW -> NeonGreen.copy(alpha = 0.15f)
    }
    
    val iconColor = when (tip.importance) {
        TipImportance.CRITICAL -> DangerRed
        TipImportance.HIGH -> WarningOrange
        TipImportance.MEDIUM -> CyberBlue
        TipImportance.LOW -> NeonGreen
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "Tip",
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = tip.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = tip.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingSecurityTip(
    tip: SecurityTip?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = tip != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(500)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        if (tip != null) {
            SecurityTipCard(
                tip = tip,
                onDismiss = onDismiss
            )
        }
    }
}

