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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chi157.resignationpointscard.ui.theme.*

@Composable
fun StampCountSelectionScreen(onStampCountSelected: (Int) -> Unit) {
    var selectedCount by remember { mutableStateOf(10) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        
        // 標題
        Text(
            text = "簽訂離職契約",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 副標題
        Text(
            text = "集滿多少點數以達成目標？",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        // 格數選項
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StampCountOption(
                count = 10,
                isSelected = selectedCount == 10,
                onClick = { selectedCount = 10 }
            )
            
            StampCountOption(
                count = 20,
                isSelected = selectedCount == 20,
                onClick = { selectedCount = 20 }
            )
            
            StampCountOption(
                count = 30,
                isSelected = selectedCount == 30,
                onClick = { selectedCount = 30 }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 開始集點按鈕
        Button(
            onClick = { onStampCountSelected(selectedCount) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4ECDC4)
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text(
                text = "開始集點 ✨",
                color = Color(0xFF2C3E50),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun StampCountOption(
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFFFD89C) else Color.White
    val textColor = if (isSelected) Color(0xFFFF8C42) else Color(0xFF2C3E50)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFFFF8C42) else Color(0xFFCCCCCC),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$count 格",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (isSelected) {
                Text(
                    text = "✓",
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StampCountSelectionScreenPreview() {
    ResignationPointsCardTheme {
        StampCountSelectionScreen(onStampCountSelected = {})
    }
}
