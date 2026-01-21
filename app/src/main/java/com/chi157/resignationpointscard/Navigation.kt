package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chi157.resignationpointscard.ui.theme.DarkBlueBackground

sealed class Screen(val route: String) {
    object CompanyName : Screen("company_name")
    object StampCountSelection : Screen("stamp_count_selection")
    object ThemeSelection : Screen("theme_selection")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // 等待數據加載
    if (isLoading) {
        // 可以顯示載入畫面
        return
    }
    
    // 決定起始畫面 (新順序：公司名稱 -> 選擇風格 -> 選擇格數)
    val startDestination = when {
        settings?.isOnboardingCompleted == true -> Screen.Main.route
        settings?.companyName?.isEmpty() == true -> Screen.CompanyName.route
        settings?.selectedTheme?.isEmpty() == true -> Screen.ThemeSelection.route
        (settings?.targetStamps ?: 0) == 0 -> Screen.StampCountSelection.route
        else -> Screen.Main.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. 公司名稱畫面
        composable(Screen.CompanyName.route) {
            SetupScreen(
                onConfirm = { companyName ->
                    viewModel.saveCompanyName(companyName)
                    navController.navigate(Screen.ThemeSelection.route) {
                        popUpTo(Screen.CompanyName.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 2. 主題選擇畫面
        composable(Screen.ThemeSelection.route) {
            ThemeSelectionScreen(
                onThemeSelected = { theme ->
                    viewModel.saveTheme(theme)
                    navController.navigate(Screen.StampCountSelection.route) {
                        popUpTo(Screen.ThemeSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 3. 格數選擇畫面
        composable(Screen.StampCountSelection.route) {
            StampCountSelectionScreen(
                onStampCountSelected = { count ->
                    viewModel.saveTargetStamps(count)
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.StampCountSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 主畫面（暫時用文字顯示）
        composable(Screen.Main.route) {
            MainPlaceholder(
                settings = settings,
                onReset = { 
                    viewModel.resetAllData()
                }
            )
        }
    }
}

@Composable
fun MainPlaceholder(
    settings: com.chi157.resignationpointscard.data.AppSettings?,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlueBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 顯示目前的設定資訊
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "--- 測試資訊 ---",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(text = "公司：${settings?.companyName}", color = Color.White)
                Text(text = "格數：${settings?.targetStamps}", color = Color.White)
                Text(text = "主題：${settings?.selectedTheme}", color = Color.White)
            }
        }

        Text(
            text = "暫時的主畫面",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(64.dp))

        // 重置按鈕區塊 (參考設計稿樣式)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(text = "🔄", modifier = Modifier.padding(end = 8.dp))
                Text(text = "重置", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Surface(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(1.dp, Color(0xFFE57373), RoundedCornerShape(4.dp)),
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🗑️", modifier = Modifier.padding(end = 12.dp))
                        Text(
                            text = "全部重來",
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Text(text = "〉", color = Color(0xFFD32F2F))
                }
            }
        }
    }
}
