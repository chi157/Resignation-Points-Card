package com.chi157.resignationpointscard

import androidx.compose.foundation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.chi157.resignationpointscard.data.AppSettings
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
    val focusManager = LocalFocusManager.current
    
    // 暫存狀態，讓用戶修改但還沒按下儲存前可以反應 (實際上這裡我們做即時儲存，或是按下 TopBar 的儲存按鈕)
    // 根據設計稿 TopBar 有 "儲存"，所以我們可能需要一個暫存機制，但為了簡化，我們先做即時儲存，
    // 或者我們只讓 "儲存" 按鈕負責公司名稱的修改確認，其他切換類型的直接生效 (比較符合手機操作習慣)
    // 但既然有 "儲存"，我們就把公司名稱的修改跟儲存綁定。
    
    var tempCompanyName by remember(settings) { mutableStateOf(settings?.companyName ?: "") }
    var tempTargetStamps by remember(settings) { mutableStateOf(settings?.targetStamps ?: 30) }
    var tempSelectedTheme by remember(settings) { mutableStateOf(settings?.selectedTheme ?: CardTheme.VACATION_MODE.name) }
    var tempQuoteRefreshRate by remember(settings) { mutableStateOf(settings?.quoteRefreshRate ?: 1) }

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
                    // 目標格數
                    Text("目標格數", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val options = listOf(10, 20, 30)
                        val current = tempTargetStamps
                        
                        options.forEach { option ->
                            val isSelected = current == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(2.dp)
                                    .background(
                                        if (isSelected) Color(0xFFAAB8C2) else Color.Transparent, 
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { tempTargetStamps = option },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$option 格",
                                    color = if (isSelected) Color.White else Color(0xFF3498DB),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
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
                            .clickable { /* 這裡可以做一個選單，暫時僅作切換示範 */ 
                               val nextRate = when(tempQuoteRefreshRate) {
                                   1 -> 2
                                   2 -> 4
                                   4 -> 8
                                   8 -> 12
                                   12 -> 24
                                   else -> 1
                               }
                               tempQuoteRefreshRate = nextRate
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$tempQuoteRefreshRate 小時 ↕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "注意：實際刷新時間受 iOS/Android 系統限制，可能會有些許延遲。",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
             // 4. 重置
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
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFE74C3C))
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
