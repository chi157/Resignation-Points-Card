package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chi157.resignationpointscard.ui.theme.*

@Composable
fun SetupScreen(onConfirm: (String) -> Unit) {
    var companyName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "輸入那個...",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "讓你靈魂枯竭的地方",
            color = SoftPink,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 警告圖示
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(80.dp))

        // 公司名稱輸入框
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White)
                .border(2.dp, SoftPink)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (companyName.isEmpty()) {
                Text(
                    text = "公司名稱...",
                    color = Color.LightGray,
                    fontSize = 18.sp
                )
            }
            BasicTextField(
                value = companyName,
                onValueChange = { companyName = it },
                textStyle = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 18.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 確認按鈕
        Button(
            onClick = { if (companyName.isNotBlank()) onConfirm(companyName) },
            colors = ButtonDefaults.buttonColors(containerColor = GrayButton),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .border(2.dp, DarkGrayBorder, RoundedCornerShape(4.dp))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "確認目標",
                    color = DarkGrayBorder,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➔",
                    color = DarkGrayBorder,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    ResignationPointsCardTheme {
        SetupScreen(onConfirm = {})
    }
}
