package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chi157.resignationpointscard.data.AppSettings
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import com.chi157.resignationpointscard.data.StampRecord
import com.chi157.resignationpointscard.ui.theme.DarkBlueBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainPointsCardScreen(
    viewModel: AppViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToRecord: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val allStamps by viewModel.allStamps.collectAsState()
    val isStampedToday by viewModel.isStampedToday.collectAsState()
    val angryCounter by viewModel.angryCounter.collectAsState()

    
    var showStampDialog by remember { mutableStateOf(false) }
    var showAngryDialog by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var showFullCardDialog by remember { mutableStateOf(false) }

    // 計算當前卡片資訊
    val targetStamps = settings?.targetStamps?.takeIf { it > 0 } ?: 30
    val totalStamps = allStamps.size
    
    // 計算已完成的完整卡片數 (例如 10 stamps, target 10 -> completed 1)
    val completedCardsCount = totalStamps / targetStamps
    val lastCompletedIndex = settings?.lastCompletedCardIndex ?: 0
    
    // 判斷是否還有「已完成但尚未檢視/確認」的卡片
    // 如果 completedCardsCount > lastCompletedIndex，表示有一張新滿的卡還沒被"翻頁"
    // 但只有當剛好整除時才算是"剛滿"的狀態需要處理。
    // 如果 totalStamps % targetStamps == 0 且 totalStamps > 0，表示當前卡片剛好滿了。
    
    val isJustFull = (totalStamps > 0 && totalStamps % targetStamps == 0)
    
    // 決定要顯示哪張卡片
    // 如果剛滿，且用戶還沒按過「再來一次」(lastCompletedIndex < completedCardsCount)，則顯示滿的那張 (Index = completedCardsCount)
    // 否則顯示下一張 (Index = completedCardsCount + 1)
    
    val showFullCardReview = isJustFull && (lastCompletedIndex < completedCardsCount)
    
    val currentCardIndex = if (showFullCardReview) completedCardsCount else completedCardsCount + 1
    
    // 根據顯示的卡片 Index 過濾印章
    // 如果是看滿的那張，就是該張的所有印章。如果是看新卡，就是新卡的印章(通常是空，除非已開始蓋)
    // 這裡邏輯要小心：allStamps 裡面的 cardIndex 是當下蓋的時候決定的。
    // 我們的 addStamp 邏輯是：val cardIndex = (totalStamps / targetStamps) + 1。
    // 所以第 1~10 個章，cardIndex 都是 1。
    // 當 total = 10，showFullCardReview = true，我們想看 cardIndex = 1 的章。
    // 當 total = 10，showFullCardReview = false (已按過)，我們想看 cardIndex = 2 的章 (目前 0 個)。
    
    val currentCardStamps = allStamps.filter { it.cardIndex == currentCardIndex }
    val stampsOnThisCard = currentCardStamps.size
    
    // 解析主題
    val currentTheme = try {
        if (settings?.selectedTheme.isNullOrEmpty()) {
            CardTheme.VACATION_MODE
        } else {
            CardTheme.valueOf(settings!!.selectedTheme)
        }
    } catch (e: Exception) {
        CardTheme.VACATION_MODE
    }

    Scaffold(
        bottomBar = {
            MainBottomNavigation(
                currentRoute = Screen.Main.route,
                onNavigate = { route ->
                    when (route) {
                        "settings" -> onNavigateToSettings()
                        "plan" -> onNavigateToPlan()
                        "record" -> onNavigateToRecord()
                    }
                }
            )
        },
        containerColor = currentTheme.screenBackgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 標題
            Text(
                text = "${settings?.companyName} 離職集點卡",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.primaryTextColor
            )
            
            // 日期
            val dateFormat = SimpleDateFormat("yyyy.MM.dd (E)", Locale.TAIWAN)
            Text(
                text = dateFormat.format(Date()),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(currentTheme.dateBackgroundColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = Color.White, // 日期文字固定白色
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 卡片進度欄
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(currentTheme.progressSectionBackgroundColor, RoundedCornerShape(4.dp))
                    .border(
                        width = if (currentTheme == CardTheme.VACATION_MODE) 2.dp else 0.dp, 
                        color = if (currentTheme == CardTheme.VACATION_MODE) Color(0xFF8B4513) else Color.Transparent, 
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "第 $currentCardIndex 張卡片",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (currentTheme == CardTheme.VACATION_MODE) Color.Black else Color.Black // 確保在白色背景上也是黑色
                )
                
                Box(
                    modifier = Modifier
                        .background(currentTheme.countBadgeColor, RoundedCornerShape(4.dp)) // 黃色或綠色或藍綠色
                        .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$stampsOnThisCard / $targetStamps",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 集點卡本體
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(3.dp, currentTheme.borderColor, RoundedCornerShape(16.dp))
                    .background(currentTheme.cardBackground, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                StampGrid(
                    targetStamps = targetStamps,
                    stampedPositions = currentCardStamps.map { it.stampPosition }.toSet(),
                    theme = currentTheme
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // 蓋章按鈕區塊
            if (showFullCardReview) {
                // 顯示集滿通知與操作
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1C40F), RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("★ 集滿啦！ ★", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { showFullCardDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD35400), // 橘色
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "👍 開始新卡片 👍", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                
            } else {
                // 一般蓋章邏輯
                if (isStampedToday && angryCounter < 5) {
                    Button(
                        onClick = { viewModel.incrementAngryCounter() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF536162),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = "✓ 今日已蓋章 ✓", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Text(
                        text = "明天再來吧",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    // 蓋章按鈕 (或觸發彩蛋後的按鈕)
                    Button(
                        onClick = { 
                            if (angryCounter >= 5) showAngryDialog = true else showStampDialog = true 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (angryCounter >= 5) Color(0xFFE74C3C) else Color(0xFF2C3E50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        val btnText = if (angryCounter >= 5) "蓋章發洩！" else "✔ 我要蓋章"
                        Text(text = btnText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 彈窗處理 ---
        
        // 0. 集滿卡片彈窗
        if (showFullCardDialog) {
            AlertDialog(
                onDismissRequest = { showFullCardDialog = false },
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Text("🎉", fontSize = 24.sp)
                         Spacer(modifier = Modifier.width(8.dp))
                         Text("${settings?.companyName} 集滿了！", fontWeight = FontWeight.Bold)
                    }
                },
                text = { Text("要再給公司一次機會嗎？") },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. 不要，我要離職
                        Button(
                            onClick = { 
                                // TODO: 跳轉到離職畫面
                                showFullCardDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C))
                        ) {
                            Text("不要，我要離職", color = Color.White)
                        }
                        
                        // 2. 再給一次機會
                        Button(
                            onClick = { 
                                // 更新已完成卡片索引，進入下一張卡
                                viewModel.updateLastCompletedCardIndex(completedCardsCount)
                                showFullCardDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
                        ) {
                            Text("再給一次機會", color = Color.White)
                        }
                        
                         // 3. Cancel
                        Button(
                            onClick = { showFullCardDialog = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("取消", color = Color.Black)
                        }
                    }
                }
            )
        }

        // 1. 蓋章原因彈窗
        if (showStampDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showStampDialog = false 
                },
                title = { Text("今日離職值 +1", fontWeight = FontWeight.Bold) },
                text = {
                    val focusManager = LocalFocusManager.current
                    val context = LocalContext.current
                    val view = LocalView.current
                    val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
                    
                    fun hideKeyboard() {
                        focusManager.clearFocus()
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = { hideKeyboard() })
                            }
                    ) {
                        Text("為什麼今日想離職？", fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        var reasonText by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = { reasonText = it },
                            placeholder = { Text("例：老闆又在畫大餅...") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { hideKeyboard() })
                        )
                        
                        Row(modifier = Modifier.padding(top = 16.dp)) {
                            Button(
                                onClick = {
                                    hideKeyboard()
                                    viewModel.addStamp(reasonText)
                                    showStampDialog = false
                                    showSuccessAnimation = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2C3E50),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("確認蓋章", color = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        // 2. 「真的很生氣」彩蛋彈窗
        if (showAngryDialog) {
            AlertDialog(
                onDismissRequest = { showAngryDialog = false },
                icon = { Text("💢", fontSize = 40.sp) },
                title = { Text("真的很生氣！！！", fontWeight = FontWeight.Bold, color = Color.Red) },
                text = { Text("受不了了，今天想蓋幾章就蓋幾章！\n不要阻止我！", textAlign = TextAlign.Center) },
                confirmButton = {
                    Button(
                        onClick = { 
                            showAngryDialog = false
                            showStampDialog = true // 接著去輸入原因
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("現在就蓋！")
                    }
                }
            )
        }

        // 3. 蓋章成功遮罩 (簡單實作)
        if (showSuccessAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("☀️", fontSize = 80.sp)
                        Text(
                            text = "蓋章成功！",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Text(text = "離自由又更近一步了", color = Color.Gray)
                        
                        Button(
                            onClick = { showSuccessAnimation = false },
                            modifier = Modifier.padding(top = 24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2C3E50),
                                contentColor = Color.White
                            )
                        ) {
                            Text("返回")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StampGrid(targetStamps: Int, stampedPositions: Set<Int>, theme: CardTheme) {
    val columns = if (targetStamps <= 10) 5 else 6
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(targetStamps) { index ->
            val position = index + 1
            val isStamped = stampedPositions.contains(position)
            
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(
                        if (isStamped) Color(0xFFFFD89C) else theme.emptySlotColor,
                        RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        if (isStamped) Color(0xFFFF8C42) else Color.Transparent, // 未蓋章時無邊框
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isStamped) {
                    Text(text = "☀️", fontSize = 20.sp)
                } else {
                    Text(text = "$position", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MainBottomNavigation(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        BottomNavItem(
            label = "集點卡",
            icon = Icons.Default.Home,
            isSelected = currentRoute == Screen.Main.route,
            onClick = { onNavigate(Screen.Main.route) }
        )
        BottomNavItem(
            label = "離職計畫",
            icon = Icons.Default.Check,
            isSelected = currentRoute == Screen.Plan.route,
            onClick = { onNavigate(Screen.Plan.route) }
        )
        BottomNavItem(
            label = "離職紀錄",
            icon = Icons.Default.Info,
            isSelected = currentRoute == Screen.Record.route,
            onClick = { onNavigate(Screen.Record.route) }
        )
        BottomNavItem(
            label = "設定",
            icon = Icons.Default.Settings,
            isSelected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) }
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label, fontSize = 10.sp) },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFFFD700),
            selectedTextColor = Color(0xFFFFD700),
            unselectedIconColor = Color.White,
            unselectedTextColor = Color.White,
            indicatorColor = Color.Transparent
        )
    )
}
