package com.example.material

import android.media.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.material.ui.theme.MaterialTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme{
                CatchBallGame()
            }
            }
        }
    }


@Composable
fun CatchBallGame() {
    val screenWidth = 950f
    val screenHeight = 1000f

    var basketX by remember { mutableStateOf(screenWidth / 2 - 50f) }
    var basketWidth by remember { mutableStateOf(100f) }
    var ballPosition by remember { mutableStateOf(Offset(Random.nextFloat() * screenWidth, 0f)) }
    var score by remember { mutableStateOf(0) }

    val ballDropSpeed = 10f
    val basketHeight = 40f

    val backgroundlmage=painterResource(id = R.drawable.tree)


    LaunchedEffect(ballPosition) {
        if (ballPosition.y > screenHeight) {
            ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
        } else {
            delay(16)
            ballPosition = ballPosition.copy(y = ballPosition.y + ballDropSpeed)

            // Check if the ball is caught
            if (
                ballPosition.y > screenHeight - basketHeight - 20 &&
                ballPosition.x in basketX..(basketX + basketWidth)
            ) {
                score++
                ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {   //background Image
        val backgroundImage: Painter=painterResource(id = R.drawable.tree)
        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Ball
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = Color.Red,
                radius = 20f,
                center = ballPosition
            )
        }

        // Basket
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        basketX = (basketX + dragAmount.x).coerceIn(0f, screenWidth - basketWidth)
                    }
                }
        ) {
            drawRect(
                color = Color.Blue,
                topLeft = Offset(basketX, screenHeight - basketHeight),
                size = androidx.compose.ui.geometry.Size(basketWidth, basketHeight)
            )
        }


        // Score
        Box(
            modifier = Modifier
                .padding(16.dp)

        ) {
            androidx.compose.material3.Text(
                text = "Score: $score",
                color = Color.White
            )
        }
    }
}

// Function to draw an individual box with a number inside it.
fun DrawScope.drawBoxWithNumber(number: Int, x: Int, y: Int, cellSize: Float, padding: Dp) {
    val boxSize = cellSize - padding.toPx()
    val left = x * cellSize + padding.toPx()
    val top = y * cellSize + padding.toPx()

    drawRoundRect(
        color = Color.Green.copy(0.5f),
        topLeft = Offset(left, top),
        size = Size(boxSize, boxSize),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
    )
    drawContext.canvas.nativeCanvas.drawText(
        number.toString(),
        left + boxSize / 2,
        top + boxSize / 1.5f,
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = 40.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            color = android.graphics.Color.BLACK
            typeface = android.graphics.Typeface.create("", android.graphics.Typeface.BOLD)
        }
    )
}




