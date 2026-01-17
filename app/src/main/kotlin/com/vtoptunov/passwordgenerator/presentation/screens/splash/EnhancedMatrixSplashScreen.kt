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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Matrix-style splash screen with falling characters
 * Features:
 * - Falling green characters (Matrix Rain effect)
 * - Random red characters that will form the app name
 * - Fade effect from bright to dark
 * - PASSFORGE title assembly from red characters
 */
@Composable
fun EnhancedMatrixSplashScreen(onTimeout: () -> Unit) {
    var animationPhase by remember { mutableStateOf(SplashPhase.MATRIX_RAIN) }
    var showTitle by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        // Phase 1: Matrix rain (2 seconds) - LONGER
        delay(2000)
        
        // Phase 2: Show title
        animationPhase = SplashPhase.TITLE_SHOWN
        showTitle = true
        
        delay(1500) // LONGER: 1500ms for better visibility
        
        // Complete
        onTimeout()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Matrix Rain Background
        MatrixRainBackground(
            modifier = Modifier.fillMaxSize()
        )
        
        // Title
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(animationSpec = tween(800)) + 
                    scaleIn(initialScale = 0.8f, animationSpec = tween(800)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            MatrixTitle()
        }
    }
}

enum class SplashPhase {
    MATRIX_RAIN,
    TITLE_SHOWN
}

/**
 * Matrix rain effect with falling columns of characters
 */
@Composable
fun MatrixRainBackground(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    
    // Characters pool (Katakana, Latin, digits - Matrix style)
    val matrixChars = remember {
        "ｦｱｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%^&*".toList()
    }
    
    // Initialize falling columns
    val columns = remember {
        val columnCount = 50 // MORE columns
        List(columnCount) { index ->
            MatrixColumn(
                id = index,
                speed = Random.nextFloat() * 3f + 2f, // FASTER: 2-5 instead of 1-3
                startDelay = Random.nextInt(0, 50), // LESS delay
                length = Random.nextInt(10, 20), // SHORTER columns
                isRedColumn = Random.nextFloat() < 0.2f // MORE red columns (20%)
            )
        }
    }
    
    // Animation tick
    var tick by remember { mutableStateOf(0L) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(30) // FASTER: 33 FPS instead of 20 FPS
            tick++
        }
    }
    
    Canvas(modifier = modifier) {
        val columnWidth = size.width / columns.size
        val fontSize = with(density) { 16.sp.toPx() }
        val charHeight = fontSize * 1.2f
        
        columns.forEachIndexed { index, column ->
            drawMatrixColumn(
                column = column,
                tick = tick,
                x = index * columnWidth + columnWidth / 2,
                columnWidth = columnWidth,
                screenHeight = size.height,
                charHeight = charHeight,
                matrixChars = matrixChars,
                textMeasurer = textMeasurer,
                fontSize = fontSize
            )
        }
    }
}

/**
 * Draw a single falling column of Matrix characters
 */
private fun DrawScope.drawMatrixColumn(
    column: MatrixColumn,
    tick: Long,
    x: Float,
    columnWidth: Float,
    screenHeight: Float,
    charHeight: Float,
    matrixChars: List<Char>,
    textMeasurer: TextMeasurer,
    fontSize: Float
) {
    // Calculate column position
    val adjustedTick = (tick - column.startDelay).coerceAtLeast(0)
    val y = (adjustedTick * column.speed * charHeight) % (screenHeight + column.length * charHeight)
    
    // Draw characters in column
    for (i in 0 until column.length) {
        val charY = y - i * charHeight
        
        // Skip if off screen - more strict bounds check
        if (charY < 0 || charY > screenHeight) continue
        
        // Calculate alpha (fade from bright to dark)
        val alpha = when (i) {
            0 -> 1.0f // Head is brightest
            in 1..3 -> 0.8f
            in 4..7 -> 0.5f
            in 8..12 -> 0.3f
            else -> 0.15f
        }
        
        // Choose color (red for special columns, green for normal)
        val color = if (column.isRedColumn && i < 5) {
            Color(0xFFFF4757).copy(alpha = alpha)
        } else {
            Color(0xFF00FF88).copy(alpha = alpha)
        }
        
        // Random character
        val char = matrixChars.random()
        
        // Draw character safely with bounds check
        try {
            drawText(
                textMeasurer = textMeasurer,
                text = char.toString(),
                style = TextStyle(
                    color = color,
                    fontSize = with(this) { fontSize.toSp() },
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal
                ),
                topLeft = Offset(
                    x = (x - columnWidth / 4).coerceAtLeast(0f),
                    y = charY.coerceIn(0f, screenHeight)
                )
            )
        } catch (e: IllegalArgumentException) {
            // Skip if constraints are invalid
        }
    }
}

/**
 * PASSFORGE title with Matrix effect
 */
@Composable
fun MatrixTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main title with letter-by-letter animation
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            "PASSFORGE".forEachIndexed { index, char ->
                var visible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    delay(index * 80L)
                    visible = true
                }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(300)) + 
                            scaleIn(initialScale = 0.3f, animationSpec = tween(300))
                ) {
                    Text(
                        text = char.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFF4757),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
        
        // Subtitle
        var showSubtitle by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(800)
            showSubtitle = true
        }
        
        AnimatedVisibility(
            visible = showSubtitle,
            enter = fadeIn(animationSpec = tween(600))
        ) {
            Text(
                text = "Password Generator",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF88)
            )
        }
    }
}

/**
 * Data class for a falling column
 */
data class MatrixColumn(
    val id: Int,
    val speed: Float,
    val startDelay: Int,
    val length: Int,
    val isRedColumn: Boolean
)
