package com.vtoptunov.passwordgenerator.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MatrixSplashScreen(onSplashComplete: () -> Unit) {
    var showTitle by remember { mutableStateOf(false) }
    var columnsCount by remember { mutableStateOf(20) }
    
    val columns = remember {
        List(columnsCount) { MatrixColumn(it) }
    }
    
    LaunchedEffect(Unit) {
        // After 2.5 seconds, show title and complete
        delay(2500)
        showTitle = true
        delay(1500)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Matrix rain effect
        MatrixRain(columns = columns, showTitle = showTitle)
        
        // Title animation
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(1000)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CYBERSAFE",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF4757)
                )
                Text(
                    text = "Password Generator",
                    fontSize = 16.sp,
                    color = Color(0xFF00FF88)
                )
            }
        }
    }
}

@Composable
fun MatrixRain(columns: List<MatrixColumn>, showTitle: Boolean) {
    var frame by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(50) // 20 FPS
            frame++
        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val columnWidth = size.width / columns.size
        val charHeight = 20f
        
        columns.forEachIndexed { index, column ->
            val x = index * columnWidth + columnWidth / 2
            
            // Update column position
            column.update(size.height)
            
            // Draw characters in this column
            column.chars.forEachIndexed { charIndex, char ->
                val y = column.y + charIndex * charHeight
                
                if (y > 0 && y < size.height) {
                    // Determine color based on position in column
                    val alpha = when {
                        charIndex == 0 -> 1f // Brightest at the head
                        charIndex < 3 -> 0.8f
                        charIndex < 6 -> 0.5f
                        else -> 0.3f
                    }
                    
                    // Some characters are red (for title effect)
                    val color = if (showTitle && Random.nextFloat() < 0.1f) {
                        Color(0xFFFF4757).copy(alpha = alpha)
                    } else {
                        Color(0xFF00FF88).copy(alpha = alpha)
                    }
                    
                    // Draw character (simplified - actual drawing would need text paint)
                    drawCircle(
                        color = color,
                        radius = 3f,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

class MatrixColumn(private val index: Int) {
    var y = Random.nextFloat() * -500f
    private val speed = Random.nextFloat() * 5f + 5f
    val chars = List(20) { randomChar() }
    
    private var framesSinceReset = 0
    
    fun update(screenHeight: Float) {
        y += speed
        framesSinceReset++
        
        // Occasionally change characters
        if (framesSinceReset % 3 == 0 && Random.nextFloat() < 0.3f) {
            chars.toMutableList()[Random.nextInt(chars.size)] = randomChar()
        }
        
        // Reset when off screen
        if (y > screenHeight + 100) {
            y = -100f
            framesSinceReset = 0
        }
    }
    
    private fun randomChar(): Char {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%^&*()_+-=[]{}|;:,.<>?"
        return chars.random()
    }
}

