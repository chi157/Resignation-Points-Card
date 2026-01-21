package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorPickerDialog(
    initialColor: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // 解析初始顏色並轉換為 HSV
    val color = try {
        android.graphics.Color.parseColor(initialColor)
    } catch (e: Exception) {
        android.graphics.Color.parseColor("#2C3E50")
    }
    
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color, hsv)
    
    var hue by remember { mutableStateOf(hsv[0]) }
    var saturation by remember { mutableStateOf(hsv[1] * 100f) }
    var value by remember { mutableStateOf(hsv[2] * 100f) }
    
    val currentColorHex = remember(hue, saturation, value) {
        val hsvArray = floatArrayOf(hue, saturation / 100f, value / 100f)
        val rgb = android.graphics.Color.HSVToColor(hsvArray)
        String.format("#%02X%02X%02X", 
            android.graphics.Color.red(rgb),
            android.graphics.Color.green(rgb),
            android.graphics.Color.blue(rgb)
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("選擇顏色", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顏色預覽
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(android.graphics.Color.parseColor(currentColorHex)), RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentColorHex,
                        color = if (value > 50) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(
                                if (value > 50) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f), 
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // 色相 (H) 滑桿
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("色相 (H)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${hue.toInt()}°", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))),
                            activeTrackColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
                        )
                    )
                }
                
                // 飽和度 (S) 滑桿
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("飽和度 (S)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${saturation.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = saturation,
                        onValueChange = { saturation = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF9B59B6),
                            activeTrackColor = Color(0xFF9B59B6)
                        )
                    )
                }
                
                // 明度/亮度 (V) 滑桿
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("亮度 (V)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("${value.toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = value,
                        onValueChange = { value = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF34495E),
                            activeTrackColor = Color(0xFF34495E)
                        )
                    )
                }
                
                // 快速預設顏色
                Text("快速選擇", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 白色
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                            .clickable { 
                                hue = 0f
                                saturation = 0f
                                value = 100f
                            }
                    )
                    // 黑色
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .clickable { 
                                value = 0f
                            }
                    )
                    // 紅色
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE74C3C), RoundedCornerShape(4.dp))
                            .clickable { 
                                hue = 0f
                                saturation = 100f
                                value = 90f
                            }
                    )
                    // 綠色
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF27AE60), RoundedCornerShape(4.dp))
                            .clickable { 
                                hue = 145f
                                saturation = 77f
                                value = 68f
                            }
                    )
                    // 藍色
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF3498DB), RoundedCornerShape(4.dp))
                            .clickable { 
                                hue = 204f
                                saturation = 76f
                                value = 86f
                            }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentColorHex) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
            ) {
                Text("確定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
