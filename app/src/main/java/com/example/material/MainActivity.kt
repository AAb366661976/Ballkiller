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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    var basketX by remember { mutableStateOf(screenWidth / 2 - 50f) }
    var basketWidth by remember { mutableStateOf(100f) }
    var ballPosition by remember { mutableStateOf(Offset(Random.nextFloat() * screenWidth, 0f)) }
    var score by remember { mutableStateOf(0) }
    var ballColor by remember { mutableStateOf(randomBallColor()) }

    val ballDropSpeed = 5f
    val basketHeight = 30f

    var isGameRunning by remember { mutableStateOf(false) }
    var isGameCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isGameRunning, isGameCompleted) {
        while (isGameRunning && !isGameCompleted) {
            if (ballPosition.y > screenHeight) {
                ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                ballColor = randomBallColor() // 碰到底部，隨機更換顏色
            } else {
                delay(16)
                ballPosition = ballPosition.copy(y = ballPosition.y + ballDropSpeed)

                if (
                    ballPosition.y > screenHeight - basketHeight - 20 &&
                    ballPosition.x in basketX..(basketX + basketWidth)
                ) {
                    // 捕捉到球，計分
                    when (ballColor) {
                        Color.Magenta -> score *= 2
                        Color.Gray -> score += 4
                        Color.Yellow -> score -= 1
                        else -> score++
                    }

                    ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                    ballColor = randomBallColor()

                    if (score >= 20) {
                        isGameRunning = false
                        isGameCompleted = true
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isGameCompleted) {
            GameOverScreen(
                onRetry = {
                    isGameCompleted = false
                    score = 0
                    ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                    ballColor = randomBallColor()
                    isGameRunning = true
                }
            )
        } else {
            GameScreen(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                basketX = basketX,
                basketWidth = basketWidth,
                ballPosition = ballPosition,
                ballColor = ballColor,
                score = score,
                onDrag = { dx ->
                    basketX = (basketX + dx).coerceIn(0f, screenWidth - basketWidth)
                },
                onGameStart = {
                    isGameRunning = true
                    score = 0
                    ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                    ballColor = randomBallColor()
                },
                onGameStop = {
                    isGameRunning = false
                    score = 0
                }
            )
        }
    }
}

@Composable
fun GameScreen(
    screenWidth: Float,
    screenHeight: Float,
    basketX: Float,
    basketWidth: Float,
    ballPosition: Offset,
    ballColor: Color,
    score: Int,
    onDrag: (Float) -> Unit,
    onGameStart: () -> Unit,
    onGameStop: () -> Unit
) {
    Image(
        painter = painterResource(id = R.drawable.tree),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color = ballColor, radius = 20f, center = ballPosition)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x)
                }
            }
    ) {
        drawRect(
            color = Color.Blue,
            topLeft = Offset(basketX, screenHeight - 30f),
            size = Size(basketWidth, 30f)
        )
    }

    ScoreDisplay(score = score)
    GameControls(onGameStart = onGameStart, onGameStop = onGameStop)
}

@Composable
fun GameOverScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.win), // 替換成你的圖片資源 ID
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 讓圖片填滿螢幕
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🎉 恭喜通關！ 🎉", color = Color.Green, fontSize = 24.sp)
            Button(onClick = onRetry) {
                Text(text = "再來一次")
            }
        }
    }
}

@Composable
fun ScoreDisplay(score: Int) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "分數: $score", color = Color.Black, fontSize = 18.sp)
        Text(text = "說明:", color = Color.Blue, fontSize = 16.sp)
        Text(text = "紅色+1, 灰色+4, 黃色-1, 紫色*2", color = Color.Blue, fontSize = 14.sp)
    }
}

@Composable
fun GameControls(onGameStart: () -> Unit, onGameStop: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onGameStart) {
                Text(text = "開始")
            }
            Button(onClick = onGameStop) {
                Text(text = "結束")
            }
        }
    }
}

fun randomBallColor(): Color {
    val colors = listOf(Color.Red, Color.Magenta, Color.Gray, Color.Yellow)
    return colors.random()
}




