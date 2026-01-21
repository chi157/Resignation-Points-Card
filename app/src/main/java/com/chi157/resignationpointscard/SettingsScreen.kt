package com.chi157.resignationpointscard

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chi157.resignationpointscard.data.AppSettings
import com.chi157.resignationpointscard.data.CommonReason
import com.chi157.resignationpointscard.ui.theme.DarkBlueBackground

@Composable
fun SettingsScreen(
    settings: AppSettings?,
    viewModel: AppViewModel,
    onReset: () -> Unit,
    navController: NavHostController
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showSaveSuccessDialog by remember { mutableStateOf(false) }
    
    var showReasonDialog by remember { mutableStateOf(false) }
    var editingReason by remember { mutableStateOf<CommonReason?>(null) }
    
    val allCommonReasons by viewModel.allCommonReasons.collectAsState()
    val focusManager = LocalFocusManager.current
    
    // 暫存狀態，讓用戶修改但還沒按下儲存前可以反應 (實際上這裡我們做即時儲存，或是按下 TopBar 的儲存按鈕)
    // 根據設計稿 TopBar 有 "儲存"，所以我們可能需要一個暫存機制，但為了簡化，我們先做即時儲存，
    // 或者我們只讓 "儲存" 按鈕負責公司名稱的修改確認，其他切換類型的直接生效 (比較符合手機操作習慣)
    // 但既然有 "儲存"，我們就把公司名稱的修改跟儲存綁定。
    
    var tempCompanyName by remember(settings) { mutableStateOf(settings?.companyName ?: "") }
    var tempTargetStamps by remember(settings) { mutableStateOf(settings?.targetStamps ?: 30) }
    var tempSelectedTheme by remember(settings) { mutableStateOf(settings?.selectedTheme ?: CardTheme.VACATION_MODE.name) }
    var tempQuoteRefreshRate by remember(settings) { mutableStateOf(settings?.quoteRefreshRate ?: 1) }
    var tempFundIncrementPresets by remember(settings) { mutableStateOf(settings?.fundIncrementPresets ?: "500,1000,3000") }
    
    var tempWidgetColor1 by remember(settings) { mutableStateOf(settings?.widgetColor1 ?: "#2C3E50") }
    var tempWidgetColor2 by remember(settings) { mutableStateOf(settings?.widgetColor2 ?: "#E74C3C") }
    var tempWidgetColor3 by remember(settings) { mutableStateOf(settings?.widgetColor3 ?: "#27AE60") }
    var tempWidgetTextColor1 by remember(settings) { mutableStateOf(settings?.widgetTextColor1 ?: "#FFFFFF") }
    var tempWidgetTextColor2 by remember(settings) { mutableStateOf(settings?.widgetTextColor2 ?: "#FFFFFF") }
    var tempWidgetTextColor3 by remember(settings) { mutableStateOf(settings?.widgetTextColor3 ?: "#FFFFFF") }
    
    // 開發模式彩蛋 - 連續點擊計數器
    var devModeClickCount by remember { mutableStateOf(0) }
    var devModeUnlocked by remember { mutableStateOf(false) }
    var showDevModeToast by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // 顯示開發模式解鎖提示
    LaunchedEffect(showDevModeToast) {
        if (showDevModeToast) {
            android.widget.Toast.makeText(
                context,
                "🎉 開發模式已解鎖！現在可以選擇 30 秒刷新頻率了！",
                android.widget.Toast.LENGTH_LONG
            ).show()
            showDevModeToast = false
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(60.dp)) // 佔位
                Text(
                    text = "設定",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Button(
                    onClick = { 
                        viewModel.saveCompanyName(tempCompanyName)
                        viewModel.saveTargetStamps(tempTargetStamps)
                        
                        // 尋找對應的 Enum
                        val themeEnum = try { CardTheme.valueOf(tempSelectedTheme) } catch(e: Exception) { CardTheme.VACATION_MODE }
                        viewModel.saveTheme(themeEnum)
                        
                        viewModel.saveQuoteRefreshRate(tempQuoteRefreshRate)
                        viewModel.saveFundIncrementPresets(tempFundIncrementPresets)
                        viewModel.saveWidgetColors(tempWidgetColor1, tempWidgetColor2, tempWidgetColor3)
                        viewModel.saveWidgetTextColors(tempWidgetTextColor1, tempWidgetTextColor2, tempWidgetTextColor3)
                        
                        focusManager.clearFocus()
                        showSaveSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD166),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("儲存", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            MainBottomNavigation(currentRoute = Screen.Settings.route, onNavigate = { route ->
                navController.navigate(route)
            })
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. 公司資訊
            SettingsSection(title = "公司資訊", icon = Icons.Default.Info)
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("公司名稱", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    BasicTextField(
                        value = tempCompanyName,
                        onValueChange = { tempCompanyName = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                            .padding(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 2. 集點卡設定
            SettingsSection(title = "集點卡設定", icon = Icons.Default.List)
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 卡片風格
                    Text("卡片風格", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ThemeOption(
                            name = "經典 RPG",
                            isSelected = tempSelectedTheme == CardTheme.CLASSIC_RPG.name,
                            color = Color(0xFF2C3E50),
                            icon = "🛡️",
                            onClick = { tempSelectedTheme = CardTheme.CLASSIC_RPG.name }
                        )
                        ThemeOption(
                            name = "系統錯誤",
                            isSelected = tempSelectedTheme == CardTheme.SYSTEM_ERROR.name,
                            color = Color(0xFF000000),
                            icon = "👾",
                            onClick = { tempSelectedTheme = CardTheme.SYSTEM_ERROR.name }
                        )
                        ThemeOption(
                            name = "度假模式",
                            isSelected = tempSelectedTheme == CardTheme.VACATION_MODE.name,
                            color = Color(0xFFFFE082),
                            icon = "☀️",
                            onClick = { tempSelectedTheme = CardTheme.VACATION_MODE.name }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3. 小工具設定
            SettingsSection(title = "小工具設定", icon = Icons.Default.DateRange)
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("語錄刷新頻率", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                            .clickable {
                               // 彩蛋計數器邏輯
                               devModeClickCount++
                               if (devModeClickCount >= 15 && !devModeUnlocked) {
                                   devModeUnlocked = true
                                   showDevModeToast = true
                               }
                               
                               val nextRate = when(tempQuoteRefreshRate) {
                                   0 -> 1  // 30秒 -> 1小時
                                   1 -> 2
                                   2 -> 4
                                   4 -> 8
                                   8 -> 12
                                   12 -> 24
                                   else -> if (devModeUnlocked) 0 else 1  // 只有解鎖後才能回到 30秒
                               }
                               tempQuoteRefreshRate = nextRate
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tempQuoteRefreshRate == 0) "30 秒 ↕ (開發)" else "$tempQuoteRefreshRate 小時 ↕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "注意：實際刷新時間受 Android 系統限制，可能會有些許延遲。",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("桌面小工具背景顏色 (樣式會輪流出現)", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    var showColorPicker1 by remember { mutableStateOf(false) }
                    var showColorPicker2 by remember { mutableStateOf(false) }
                    var showColorPicker3 by remember { mutableStateOf(false) }
                    var showTextColorPicker1 by remember { mutableStateOf(false) }
                    var showTextColorPicker2 by remember { mutableStateOf(false) }
                    var showTextColorPicker3 by remember { mutableStateOf(false) }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // 樣式 1
                        Column {
                            Text("樣式 1", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("背景顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetColor1)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showColorPicker1 = true }
                                    )
                                    Text(tempWidgetColor1, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("文字顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetTextColor1)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showTextColorPicker1 = true }
                                    )
                                    Text(tempWidgetTextColor1, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                        
                        // 樣式 2
                        Column {
                            Text("樣式 2", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("背景顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetColor2)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showColorPicker2 = true }
                                    )
                                    Text(tempWidgetColor2, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("文字顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetTextColor2)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showTextColorPicker2 = true }
                                    )
                                    Text(tempWidgetTextColor2, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                        
                        // 樣式 3
                        Column {
                            Text("樣式 3", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("背景顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetColor3)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showColorPicker3 = true }
                                    )
                                    Text(tempWidgetColor3, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("文字顏色", fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(android.graphics.Color.parseColor(tempWidgetTextColor3)), RoundedCornerShape(8.dp))
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                            .clickable { showTextColorPicker3 = true }
                                    )
                                    Text(tempWidgetTextColor3, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                    
                    // 背景調色盤對話框
                    if (showColorPicker1) {
                        ColorPickerDialog(
                            initialColor = tempWidgetColor1,
                            onConfirm = { tempWidgetColor1 = it; showColorPicker1 = false },
                            onDismiss = { showColorPicker1 = false }
                        )
                    }
                    if (showColorPicker2) {
                        ColorPickerDialog(
                            initialColor = tempWidgetColor2,
                            onConfirm = { tempWidgetColor2 = it; showColorPicker2 = false },
                            onDismiss = { showColorPicker2 = false }
                        )
                    }
                    if (showColorPicker3) {
                        ColorPickerDialog(
                            initialColor = tempWidgetColor3,
                            onConfirm = { tempWidgetColor3 = it; showColorPicker3 = false },
                            onDismiss = { showColorPicker3 = false }
                        )
                    }
                    
                    // 文字調色盤對話框
                    if (showTextColorPicker1) {
                        ColorPickerDialog(
                            initialColor = tempWidgetTextColor1,
                            onConfirm = { tempWidgetTextColor1 = it; showTextColorPicker1 = false },
                            onDismiss = { showTextColorPicker1 = false }
                        )
                    }
                    if (showTextColorPicker2) {
                        ColorPickerDialog(
                            initialColor = tempWidgetTextColor2,
                            onConfirm = { tempWidgetTextColor2 = it; showTextColorPicker2 = false },
                            onDismiss = { showTextColorPicker2 = false }
                        )
                    }
                    if (showTextColorPicker3) {
                        ColorPickerDialog(
                            initialColor = tempWidgetTextColor3,
                            onConfirm = { tempWidgetTextColor3 = it; showTextColorPicker3 = false },
                            onDismiss = { showTextColorPicker3 = false }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. 預備金點選金額設定
            SettingsSection(title = "預備金點選金額設定", icon = Icons.Default.Add)
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("設定計畫頁面中的快速增加金額 (最多三個)", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    val presetList = tempFundIncrementPresets.split(",").map { it.trim() }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { index ->
                            val value = presetList.getOrNull(index) ?: ""
                            OutlinedTextField(
                                value = value,
                                onValueChange = { newVal ->
                                    if (newVal.all { it.isDigit() }) {
                                        val newList = presetList.toMutableList()
                                        while (newList.size <= index) newList.add("")
                                        newList[index] = newVal
                                        tempFundIncrementPresets = newList.joinToString(",")
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("金額 ${index + 1}") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // 5. 常用蓋章原因
            SettingsSection(title = "常用蓋章原因", icon = Icons.Default.Edit)
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("管理蓋章時可選擇的常用理由\n點擊原因可編輯", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    
                    allCommonReasons.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(4.dp))
                                .clickable { 
                                    editingReason = reason
                                    showReasonDialog = true
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = reason.text, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            IconButton(
                                onClick = { viewModel.deleteCommonReason(reason) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    
                    if (allCommonReasons.isEmpty()) {
                        Text("尚未建立任何常用原因", color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    
                    Button(
                        onClick = { 
                            editingReason = null
                            showReasonDialog = true 
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3498DB)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("新增常用原因", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
             // 6. 重置
            SettingsSection(title = "重置", icon = Icons.Default.Refresh)
            
            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(56.dp)
                    .border(2.dp, Color(0xFFE74C3C), RoundedCornerShape(4.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE74C3C))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("全部重來", color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFE74C3C))
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("離職集點卡 ver1.0", color = Color.Gray, fontSize = 12.sp)
                Text("© 2026 Neil尼歐 · Cynthia Chang(chi157) · AGPL v3", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("確定要全部重來嗎？") },
                text = { Text("這個操作無法復原，所有紀錄和設定都會被清空。") },
                confirmButton = {
                    Button(
                        onClick = { 
                            onReset()
                            showResetDialog = false 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C))
                    ) {
                        Text("確定重置", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
        
        if (showSaveSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* 禁止點擊外部關閉，強制按確定 */ },
                title = { Text("儲存成功") },
                text = { Text("設定已成功更新。") },
                confirmButton = {
                    Button(
                        onClick = { 
                            showSaveSuccessDialog = false
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color.White
                        )
                    ) {
                        Text("確定")
                    }
                }
            )
        }

        if (showReasonDialog) {
            var reasonText by remember { mutableStateOf(editingReason?.text ?: "") }
            AlertDialog(
                onDismissRequest = { showReasonDialog = false },
                title = { Text(if (editingReason == null) "新增常用原因" else "編輯常用原因") },
                text = {
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("原因內容") },
                        placeholder = { Text("例如：主管太機車") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (reasonText.isNotBlank()) {
                                if (editingReason == null) {
                                    viewModel.addCommonReason(reasonText)
                                } else {
                                    viewModel.updateCommonReason(editingReason!!.copy(text = reasonText))
                                }
                                showReasonDialog = false
                            }
                        }
                    ) {
                        Text("儲存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReasonDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFFD166), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ThemeOption(name: String, isSelected: Boolean, color: Color, icon: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .border(if (isSelected) 3.dp else 0.dp, if (isSelected) Color(0xFF3498DB) else Color.Transparent, RoundedCornerShape(4.dp))
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 24.sp)
            }
        }
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF3498DB) else Color.Gray,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}
