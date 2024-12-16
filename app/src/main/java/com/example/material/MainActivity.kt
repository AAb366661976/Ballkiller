package com.example.material


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
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    val screenWidth = 1000f
    val screenHeight = 1000f

    var basketX by remember { mutableStateOf(screenWidth / 2 - 50f) }
    var basketWidth by remember { mutableStateOf(100f) }
    var ballPosition by remember { mutableStateOf(Offset(Random.nextFloat() * screenWidth, 0f)) }
    var score by remember { mutableStateOf(0) }
    var ballColor by remember { mutableStateOf(randomBallColor()) }

    val ballDropSpeed = 20f
    val basketHeight = 30f

    val backgroundlmage = painterResource(id = R.drawable.tree)
    var isGameRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isGameRunning) {
        while (isGameRunning) { // 當遊戲進行中時，進入循環
            if (ballPosition.y > screenHeight) {
                ballPosition = Offset(Random.nextFloat() * screenWidth, 0f) // 重置球位置

            } else {
                delay(16)
                ballPosition = ballPosition.copy(y = ballPosition.y + ballDropSpeed)

                // 判斷是否捕捉到球
                if (
                    ballPosition.y > screenHeight - basketHeight - 20 &&
                    ballPosition.x in basketX..(basketX + basketWidth)
                ) {

                    when (ballColor) {
                        Color.Magenta -> score *= 2
                        Color.Gray -> score += 4
                        Color.Yellow -> score -= 1
                        else -> score++
                        // 增加分數

                    }
                    ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                    ballColor = randomBallColor()
                }
            }
        }
    }

        Box(modifier = Modifier.fillMaxSize())
        {   //background Image
            val backgroundImage: Painter = painterResource(id = R.drawable.tree)
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
                    color = ballColor,
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
                            basketX =
                                (basketX + dragAmount.x).coerceIn(0f, screenWidth - basketWidth)
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
                Text(
                    text = "Score: $score",
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd // 確保按鈕放在右上角
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 開始按鈕
                    androidx.compose.material3.Button(
                        onClick = {
                            isGameRunning = true
                            score = 0
                            ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                            ballColor = randomBallColor()
                        }
                    ) {
                        Text(text = "開始")
                    }

                    // 結束按鈕
                    Button(
                        onClick = {
                            isGameRunning = false
                            ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                            score = 0
                        }
                    ) {
                        Text(text = "結束")
                    }
                }
            }
        }
    }

    fun randomBallColor(): Color {
        return when (Random.nextInt(4)) {
            0 -> Color.Red
            1 -> Color.Magenta
            2 -> Color.Gray
            3 -> Color.Yellow
            else -> Color.Red
        }
    }













