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

    val backgroundlmage = painterResource(id = R.drawable.tree)
    var isGameRunning by remember { mutableStateOf(false) }
    var isGameCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(isGameRunning, isGameCompleted) {
        while (isGameRunning && !isGameCompleted) {
            if (ballPosition.y > screenHeight) {
                // 碰到底部，重置位置並更換顏色
                ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                ballColor = randomBallColor() // 隨機更換顏色
            } else {
                delay(16)
                ballPosition = ballPosition.copy(y = ballPosition.y + ballDropSpeed)

                if (
                    ballPosition.y > screenHeight - basketHeight - 20 &&
                    ballPosition.x in basketX..(basketX + basketWidth)
                ) {
                    // 捕捉到球，計分邏輯
                    when (ballColor) {
                        Color.Magenta -> score *= 2
                        Color.Gray -> score += 4
                        Color.Yellow -> score -= 1
                        else -> score++
                    }

                    // 重置球的位置與顏色
                    ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                    ballColor = randomBallColor()

                    // 判斷是否通關
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
            // 通關畫面
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
                    Text(
                        text = "🎉 恭喜通關！ 🎉",
                        color = Color.Green,
                        fontSize = 24.sp
                    )
                    Button(
                        onClick = {
                            // 重置遊戲狀態
                            isGameCompleted = false
                            score = 0
                            ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                            ballColor = randomBallColor()
                            isGameRunning = true
                        }
                    ) {
                        Text(text = "再來一次")
                    }
                }
            }
        }

        else {
            // 遊戲畫面
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "分數: $score", color = Color.Black, fontSize = 18.sp)
                Text(text = "說明:獲得20分為通關成功", color = Color.Blue, fontSize = 16.sp)
                Text(text = "紅色+1, 灰色+4, 黃色-1, 紫色*2", color = Color.Blue, fontSize = 14.sp)
            }
        }



            // 控制按鈕
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isGameRunning = true
                            score = 0
                            ballPosition = Offset(Random.nextFloat() * screenWidth, 0f)
                            ballColor = randomBallColor()
                        }
                    ) {
                        Text(text = "開始")
                    }

                    Button(
                        onClick = {
                            isGameRunning = false
                            score = 0
                        }
                    ) {
                        Text(text = "結束")
                    }
                }
            }
        }
    }


// 隨機顏色生成
fun randomBallColor(): Color {
    return when (Random.nextInt(4)) {
        0 -> Color.Red
        1 -> Color.Magenta
        2 -> Color.Gray
        3 -> Color.Yellow
        else -> Color.Red
    }
}












