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
    object Plan : Screen("plan")
    object Record : Screen("record")
    object Settings : Screen("settings")
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
        
        // 4. 正式主畫面 (集點卡)
        composable(Screen.Main.route) {
            MainPointsCardScreen(
                viewModel = viewModel,
                onNavigateToPlan = { navController.navigate(Screen.Plan.route) },
                onNavigateToRecord = { navController.navigate(Screen.Record.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        // 5. 離職計畫
        composable(Screen.Plan.route) {
            ResignationPlanScreen(viewModel = viewModel, navController = navController)
        }
        
        // 6. 離職紀錄 (暫時 Placeholder)
        composable(Screen.Record.route) {
            TabPlaceholder(route = Screen.Record.route, title = "離職紀錄", viewModel = viewModel, navController = navController)
        }
        
        // 7. 設定畫面
        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onReset = { 
                    viewModel.resetAllData()
                    navController.navigate(Screen.CompanyName.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    }
}

@Composable
fun SettingsScreen(
    settings: com.chi157.resignationpointscard.data.AppSettings?,
    onReset: () -> Unit,
    navController: NavHostController
) {
    Scaffold(
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
                .padding(24.dp)
        ) {
            Text(
                text = "設定",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 重置按鈕區塊
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
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "離職集點卡 ver 1.0", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun TabPlaceholder(route: String, title: String, viewModel: AppViewModel, navController: NavHostController) {
    Scaffold(
        bottomBar = {
            MainBottomNavigation(currentRoute = route, onNavigate = { targetRoute ->
                navController.navigate(targetRoute)
            })
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = title, color = Color.White, fontSize = 24.sp)
        }
    }
}
