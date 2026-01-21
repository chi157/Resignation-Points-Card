package com.chi157.resignationpointscard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun ResignationSuccessScreen(
    viewModel: AppViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToRecord: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val allStamps by viewModel.allStamps.collectAsState()
    val totalStamps = allStamps.size
    val targetStamps = settings?.targetStamps?.takeIf { it > 0 } ?: 30
    val completedCardsCount = totalStamps / targetStamps

    val infiniteTransition = rememberInfiniteTransition(label = "success")
    
    // 主喇叭動畫
    val popperScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "popperScale"
    )
    val popperRotate by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "popperRotate"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD166), // 暖黃
                        Color(0xFFF3722C), // 活力橘
                        Color(0xFFF94144)  // 熱情紅
                    )
                )
            )
    ) {
        // 背景大量隨機彩帶
        ConfettiBackground(infiniteTransition)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 1. 強烈噴發感的喇叭
                Box(contentAlignment = Alignment.Center) {
                    // 噴射出的噴發粒子 (固定動畫)
                    repeat(8) { i ->
                        val angle = (i * 45).toFloat()
                        val distance by infiniteTransition.animateFloat(
                            initialValue = 20f,
                            targetValue = 120f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = i * 50, easing = FastOutLinearInEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "spray"
                        )
                        val opacity by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = i * 50, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "opacity"
                        )
                        
                        Box(
                            modifier = Modifier
                                .rotate(angle)
                                .offset(y = (-distance).dp)
                                .size(8.dp)
                                .alpha(opacity)
                                .background(
                                    color = listOf(Color.Yellow, Color.Cyan, Color.White, Color.Magenta).random(),
                                    shape = CircleShape
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(popperScale)
                            .rotate(popperRotate),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎉", fontSize = 80.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "恭喜你！",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "祝你奔向自由！",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "再見了，", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                
                Surface(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .border(2.dp, Color.Black, RoundedCornerShape(4.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${settings?.companyName ?: "XXX"} ！",
                        modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "你的離職決心",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SuccessStatCard(
                        modifier = Modifier.weight(1f),
                        label = "完成卡片",
                        value = "$completedCardsCount",
                        icon = { 
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(3) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        repeat(3) {
                                            Box(modifier = Modifier.size(8.dp).background(Color.Black))
                                        }
                                    }
                                }
                            }
                        }
                    )
                    SuccessStatCard(
                        modifier = Modifier.weight(1f),
                        label = "總蓋章數",
                        value = "$totalStamps",
                        icon = { 
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "願你找到更好的歸屬",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "人生苦短，不要委屈自己",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToRecord,
                    modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "查看離職紀錄", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        viewModel.resetAllData()
                        onNavigateToMain()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71)), // 綠色
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔄", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "重新集點 (新公司)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessStatCard(modifier: Modifier, label: String, value: String, icon: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(4.dp)),
        color = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                icon()
            }
            Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ConfettiBackground(transition: InfiniteTransition) {
    val particles = remember { List(25) { RandomParticle() } }

    particles.forEach { particle ->
        val yOffset by transition.animateFloat(
            initialValue = -100f,
            targetValue = 1400f,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, delayMillis = particle.delay, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "confettiY"
        )
        val xOffset by transition.animateFloat(
            initialValue = particle.startX,
            targetValue = particle.startX + (Random.nextInt(-100, 100)),
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "confettiX"
        )
        val rotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 720f,
            animationSpec = infiniteRepeatable(
                animation = tween(particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "confettiRotation"
        )

        Box(
            modifier = Modifier
                .offset(x = xOffset.dp, y = yOffset.dp)
                .rotate(rotation)
                .size(particle.size.dp)
                .alpha(0.7f)
                .background(particle.color, if (particle.isCircle) CircleShape else RoundedCornerShape(2.dp))
        )
    }
}

data class RandomParticle(
    val startX: Float = Random.nextFloat() * 500 - 100,
    val duration: Int = Random.nextInt(2500, 5000),
    val delay: Int = Random.nextInt(0, 3000),
    val color: Color = listOf(Color.White, Color.Yellow, Color.Cyan, Color.Magenta, Color.Green).random(),
    val size: Int = Random.nextInt(6, 14),
    val isCircle: Boolean = Random.nextBoolean()
)
