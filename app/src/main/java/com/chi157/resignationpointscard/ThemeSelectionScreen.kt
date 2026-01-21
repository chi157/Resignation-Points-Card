package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.chi157.resignationpointscard.ui.theme.*

enum class CardTheme(
    val displayName: String,
    val icon: String,
    val description: String,
    val borderColor: Color,
    val buttonColor: Color,
    val cardBackground: Color,
    val iconColor: Color,
    // 新增 UI 顏色定義
    val screenBackgroundColor: Color,
    val primaryTextColor: Color,
    val dateBackgroundColor: Color,
    val progressSectionBackgroundColor: Color, // 進度條區域背景
    val countBadgeColor: Color, // 0/10 那個框框的顏色
    val emptySlotColor: Color // 未蓋章格子的顏色
) {
    CLASSIC_RPG(
        displayName = "經典 RPG",
        icon = "🛡️",
        description = "把職場當作一場冒險遊戲，\n用像素創斬斷舊命運！",
        borderColor = Color.White, // 卡片外框白色
        buttonColor = Color(0xFF3498DB), // 按鈕藍色
        cardBackground = Color(0xFF2C3E50), // 卡片內部深藍色
        iconColor = Color(0xFFFFD700),
        screenBackgroundColor = Color(0xFF2C3E50), // 背景深藍
        primaryTextColor = Color.White,
        dateBackgroundColor = Color(0xFF3498DB), // 日期藍色
        progressSectionBackgroundColor = Color.White,
        countBadgeColor = Color(0xFFFFD700), // 計數黃色
        emptySlotColor = Color.LightGray
    ),
    SYSTEM_ERROR(
        displayName = "系統錯誤",
        icon = "🐛",
        description = "覺醒的駭客，\n打破這個名為公司的矩陣。",
        borderColor = Color(0xFF00FF9F), // 螢光綠外框
        buttonColor = Color(0xFF00FF9F), // 按鈕螢光綠
        cardBackground = Color.Black, // 卡片內部黑
        iconColor = Color(0xFF00FF9F), // 螢光綠
        screenBackgroundColor = Color.Black, // 背景黑
        primaryTextColor = Color.White,
        dateBackgroundColor = Color(0xFF00FF9F), // 日期螢光綠
        progressSectionBackgroundColor = Color.White,
        countBadgeColor = Color(0xFF00FF9F), // 計數螢光綠
        emptySlotColor = Color.LightGray
    ),
    VACATION_MODE(
        displayName = "度假模式",
        icon = "☀️",
        description = "心已經飛去海島退休了，\n只差肉體還在這裡。",
        borderColor = Color(0xFFCD853F), // 棕色外框
        buttonColor = Color(0xFF4ECDC4),
        cardBackground = Color(0xFFFFF8DC), // 米黃色卡片背景
        iconColor = Color(0xFF4ECDC4),
        screenBackgroundColor = Color(0xFFD2B48C), // 羊皮紙背景
        primaryTextColor = Color(0xFF2C3E50),
        dateBackgroundColor = Color(0xFF2C3E50),
        progressSectionBackgroundColor = Color.Transparent, // 保持原樣
        countBadgeColor = Color(0xFF4ECDC4),
        emptySlotColor = Color(0xFFEEEEEE)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemeSelectionScreen(onThemeSelected: (CardTheme) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { CardTheme.values().size })
    val currentTheme = CardTheme.values()[pagerState.currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // 標題
        Text(
            text = "選擇你的集點卡風格",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 可滑動的卡片
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) { page ->
            val theme = CardTheme.values()[page]
            ThemeCard(theme = theme)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 指示點
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(CardTheme.values().size) { index ->
                val color = if (index == pagerState.currentPage) {
                    currentTheme.buttonColor
                } else {
                    Color.Gray
                }
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(12.dp)
                        .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 決定風格按鈕
        Button(
            onClick = { onThemeSelected(currentTheme) },
            colors = ButtonDefaults.buttonColors(
                containerColor = currentTheme.buttonColor
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        ) {
            Text(
                text = "決定風格 →",
                color = Color(0xFF2C3E50),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ThemeCard(theme: CardTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, theme.borderColor, RoundedCornerShape(8.dp))
                .background(Color(0xFF2C3E50), RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 內部卡片
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(3.dp, Color.White, RoundedCornerShape(4.dp))
                    .background(theme.cardBackground, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 圖示
                    Text(
                        text = theme.icon,
                        fontSize = 72.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 風格名稱
                    Text(
                        text = theme.displayName,
                        color = if (theme == CardTheme.VACATION_MODE) Color(0xFFFF8C42) else theme.iconColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 描述文字
            Text(
                text = theme.description,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSelectionScreenPreview() {
    ResignationPointsCardTheme {
        ThemeSelectionScreen(onThemeSelected = {})
    }
}
