package com.vtoptunov.passwordgenerator.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun EnhancedMatrixSplashScreen(onSplashComplete: () -> Unit) {
    var animationPhase by remember { mutableStateOf(SplashPhase.MATRIX_RAIN) }
    var titleLetters by remember { mutableStateOf(listOf<TitleLetter>()) }
    
    val appName = "CYBERSAFE"
    
    LaunchedEffect(Unit) {
        // Phase 1: Matrix rain (1.5s)
        delay(1500)
        
        // Phase 2: Collect red letters (1s)
        animationPhase = SplashPhase.COLLECTING
        
        // Initialize title letters with random positions
        titleLetters = appName.mapIndexed { index, char ->
            TitleLetter(
                char = char,
                targetIndex = index,
                currentX = Random.nextFloat(),
                currentY = Random.nextFloat()
            )
        }
        
        delay(1000)
        
        // Phase 3: Show title (0.5s)
        animationPhase = SplashPhase.TITLE_SHOWN
        
        delay(500)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Matrix background
        MatrixBackground(
            isActive = animationPhase == SplashPhase.MATRIX_RAIN,
            modifier = Modifier.fillMaxSize()
        )
        
        // Title assembly animation
        if (animationPhase != SplashPhase.MATRIX_RAIN) {
            TitleAssembly(
                letters = titleLetters,
                phase = animationPhase,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

enum class SplashPhase {
    MATRIX_RAIN,
    COLLECTING,
    TITLE_SHOWN
}

data class TitleLetter(
    val char: Char,
    val targetIndex: Int,
    val currentX: Float,
    val currentY: Float
)

@Composable
fun MatrixBackground(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    var time by remember { mutableStateOf(0f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                delay(50)
                time += 0.1f
            }
        }
    }
    
    Box(
        modifier = modifier.drawBehind {
            val columns = 30
            val columnWidth = size.width / columns
            
            repeat(columns) { col ->
                val x = col * columnWidth
                val baseY = (time * 50f + col * 100f) % (size.height + 200f) - 100f
                
                // Draw falling characters (simplified as dots)
                repeat(15) { row ->
                    val y = baseY + row * 25f
                    if (y in 0f..size.height) {
                        val alpha = when {
                            row == 0 -> 1f
                            row < 3 -> 0.7f
                            row < 7 -> 0.4f
                            else -> 0.2f
                        }
                        
                        // Some are red (future title letters)
                        val color = if (Random.nextFloat() < 0.05f) {
                            Color(0xFFFF4757).copy(alpha = alpha)
                        } else {
                            Color(0xFF00FF88).copy(alpha = alpha)
                        }
                        
                        drawCircle(
                            color = color,
                            radius = 4f,
                            center = Offset(x + columnWidth / 2, y)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TitleAssembly(
    letters: List<TitleLetter>,
    phase: SplashPhase,
    modifier: Modifier = Modifier
) {
    val animationProgress = animateFloatAsState(
        targetValue = if (phase == SplashPhase.TITLE_SHOWN) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "title_assembly"
    )
    
    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            letters.forEach { letter ->
                val progress = animationProgress.value
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + scaleIn(initialScale = 0.3f)
                ) {
                    Text(
                        text = letter.char.toString(),
                        fontSize = (32 + progress * 16).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFF4757).copy(alpha = 0.7f + progress * 0.3f),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
        
        // Subtitle
        if (phase == SplashPhase.TITLE_SHOWN) {
            Column(
                modifier = Modifier.offset(y = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500))
                ) {
                    Text(
                        text = "Password Generator",
                        fontSize = 14.sp,
                        color = Color(0xFF00FF88),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

