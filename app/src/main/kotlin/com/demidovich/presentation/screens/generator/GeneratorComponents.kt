package com.demidovich.presentation.screens.generator

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demidovich.domain.model.PasswordStrength
import com.demidovich.domain.model.PasswordStyle
import com.demidovich.presentation.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CrackingSimulatorCard(simulation: CrackingSimulationState) {
    // Blinking cursor effect
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Hacker's View",
                    style = MaterialTheme.typography.titleMedium,
                    color = DangerRed,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Simulated terminal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "$ bruteforce attack --target password",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = NeonGreen
                    )
                    Text(
                        "> Cracking...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Row {
                        Text(
                            simulation.crackedChars,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                        if (!simulation.isComplete) {
                            Text(
                                "█",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 18.sp,
                                color = DangerRed.copy(alpha = cursorAlpha),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = simulation.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = DangerRed,
                trackColor = SurfaceDark
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatsItem("Attempts", NumberFormat.getInstance(Locale.US).format(simulation.attempts))
                StatsItem("Time", "${simulation.timeElapsedMs}ms")
                StatsItem("Progress", "${(simulation.progress * 100).toInt()}%")
            }
        }
    }
}

@Composable
fun PasswordStatsCard(
    entropy: Double,
    crackTime: String,
    strength: PasswordStrength
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Security Analysis",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricCard(
                    label = "Entropy",
                    value = "${entropy.toInt()} bits",
                    icon = Icons.Default.Calculate,
                    color = CyberBlue
                )
                
                MetricCard(
                    label = "Crack Time",
                    value = crackTime,
                    icon = Icons.Default.Timer,
                    color = when (strength) {
                        PasswordStrength.VERY_WEAK, PasswordStrength.WEAK -> DangerRed
                        PasswordStrength.FAIR -> WarningOrange
                        else -> NeonGreen
                    }
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun StyleSelectorCard(
    selectedStyle: PasswordStyle,
    onStyleSelected: (PasswordStyle) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Password Style",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(12.dp))
            
            val styles = listOf(
                PasswordStyle.Random,
                PasswordStyle.XKCD,
                PasswordStyle.Phonetic,
                PasswordStyle.Story,
                PasswordStyle.Pronounceable
            )
            
            styles.forEach { style ->
                StyleItem(
                    style = style,
                    isSelected = style == selectedStyle,
                    onClick = { onStyleSelected(style) }
                )
                if (style != styles.last()) {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun StyleItem(
    style: PasswordStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) ElectricPurple.copy(alpha = 0.2f) else SurfaceDark
    val borderColor = if (isSelected) ElectricPurple else Color.Transparent
    
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = borderColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    style.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) ElectricPurple else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ElectricPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(4.dp))
            
            Text(
                style.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                style.example,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = if (isSelected) CyberBlue else TextTertiary
            )
        }
    }
}

@Composable
fun PasswordOptionsCard(
    length: Int,
    includeUppercase: Boolean,
    includeLowercase: Boolean,
    includeNumbers: Boolean,
    includeSymbols: Boolean,
    onLengthChanged: (Int) -> Unit,
    onOptionToggled: (PasswordOption) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Options",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Length Slider
            Text(
                "Length: $length",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Slider(
                value = length.toFloat(),
                onValueChange = { onLengthChanged(it.toInt()) },
                valueRange = 8f..32f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = CyberBlue,
                    activeTrackColor = CyberBlue,
                    inactiveTrackColor = SurfaceDark
                )
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Character Type Toggles
            OptionToggle(
                label = "Uppercase (A-Z)",
                checked = includeUppercase,
                onCheckedChange = { onOptionToggled(PasswordOption.UPPERCASE) }
            )
            OptionToggle(
                label = "Lowercase (a-z)",
                checked = includeLowercase,
                onCheckedChange = { onOptionToggled(PasswordOption.LOWERCASE) }
            )
            OptionToggle(
                label = "Numbers (0-9)",
                checked = includeNumbers,
                onCheckedChange = { onOptionToggled(PasswordOption.NUMBERS) }
            )
            OptionToggle(
                label = "Symbols (!@#$)",
                checked = includeSymbols,
                onCheckedChange = { onOptionToggled(PasswordOption.SYMBOLS) }
            )
        }
    }
}

@Composable
fun OptionToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberBlue,
                checkedTrackColor = CyberBlue.copy(alpha = 0.5f),
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = SurfaceDark
            )
        )
    }
}

@Composable
fun StatsItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DangerRed,
            fontFamily = FontFamily.Monospace
        )
        Text(
            label,
            fontSize = 11.sp,
            color = TextTertiary
        )
    }
}

